/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.rushhourgame.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.StepForHuman;

/**
 * 経路計算機. 経路探索処理中は経路にしたがった移動動作をしないこと (HashMapを使っているため)
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class RouteSearcher extends AbstractController implements Callable<Boolean> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RouteSearcher.class.getName());

    @Inject
    protected StepForHumanController sCon;

    @Inject
    protected ResidenceController rCon;

    @Inject
    protected CompanyController cCon;

    @Inject
    protected StationController stCon;

    transient protected final Map<Company, List<RouteNode>> routes = new HashMap<>();

    /**
     * 経路情報をすべて破棄する.
     */
    public void flush() {
        routes.clear();
    }

    public boolean isReachable(@NotNull Residence r, @NotNull Company c) {
        return routes.get(c).stream().anyMatch(node -> node.getOriginal().equals(r));
    }
    
    public RouteNode getStart(@NotNull Residence r, @NotNull Company c) {
        return routes.get(c).stream().filter(node -> node.getOriginal().equals(r)).findFirst().get();
    }

    @Override
    public Boolean call() {
        LOG.log(Level.INFO, "{0}#call start.", this.getClass().getSimpleName());

        // 今まで保持していた情報をすべて破棄
        flush();

        // すべての会社への行き方を求める
        // 探索すると、全開始点からgoalまでの行き方が分かる
        // したがって、すべての住宅からすべての会社へ行く道を検索する必要はない。
        List<StepForHuman> originalEdges = sCon.findAll();
        List<Company> dests = cCon.findAll();
        List<RelayPointForHuman> originalNodes = findRelayPointAll();

        dests.forEach((company) -> {
            // nodeの用意
            List<RouteNode> nodes = buildRouteNodes(originalNodes);
            // edgeの用意
            List<RouteEdge> edges = buildRouteEdges(originalEdges, nodes);

            // goal の取得
            RouteNode goal = nodes.stream().filter(node -> node.getOriginal().equals(company)).findFirst().get();
            
            search(nodes, goal);
            
            // 経路探索結果を登録
            routes.put(company, nodes);
        });
        
        LOG.log(Level.INFO, "{0}#call end", this.getClass().getSimpleName());
        return true;
    }

    protected List<RelayPointForHuman> findRelayPointAll() {
        return Stream.concat(
                rCon.findAll().stream(),
                Stream.concat(
                        cCon.findAll().stream(),
                        Stream.concat(
                                stCon.findPlatformAll().stream(),
                                stCon.findTicketGate().stream()
                        )
                )
        ).collect(Collectors.toList());
    }
    
    protected List<RouteNode> buildRouteNodes(List<RelayPointForHuman> originalNodes) {
        return originalNodes.stream().map(original -> new RouteNode(original)).collect(Collectors.toList());
    }
    
    /**
     * StepForHuman から RouteEdge を作成する
     * @param originalEdges StepForHuman
     * @param nodes RouteEdge
     * @return List&gt;RouteEdge&lt;
     */
    protected List<RouteEdge> buildRouteEdges(List<StepForHuman> originalEdges, List<RouteNode> nodes) {
        return originalEdges.stream().map(original -> {
                // from に対応する node の取得
                RouteNode from = nodes.stream().filter(node -> 
                        node.getOriginal().equals(original.getFrom())
                ).findFirst().get();
                
                // to に対応する node の取得
                RouteNode to = nodes.stream().filter(node -> 
                        node.getOriginal().equals(original.getTo())
                ).findFirst().get();
                
                RouteEdge edge = new RouteEdge(original, from, to);
                
                // Node へのリンクを追加
                from.getOutEdges().add(edge);
                to.getInEdges().add(edge);
                
                return edge;
            }).collect(Collectors.toList());
    }

    /**
     * nodesの要素にviaとcostをセットして終える. ダイクストラ法で探す
     *
     * @param nodes 出発地一覧
     * @param goal 目的地
     */
    protected void search(List<RouteNode> nodes, RouteNode goal) {
        
        nodes.forEach(node -> node.setCost(Double.MAX_VALUE));

        goal.setCost(0);

        PriorityQueue<RouteNode> queue = new PriorityQueue<>();
        queue.add(goal);

        while (!queue.isEmpty()) {
            RouteNode x = queue.poll();

            x.getInEdges().forEach(link -> {
                RouteNode y = link.getFrom();
                double newValue = x.getCost() + link.getCost();
                if (newValue < y.getCost()) {
                    y.setCost(newValue);
                    y.setVia(x);
                    queue.offer(y);
                }
            });
        }
    }
}

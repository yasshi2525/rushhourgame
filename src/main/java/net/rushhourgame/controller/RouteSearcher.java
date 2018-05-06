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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import net.rushhourgame.GameMaster;
import net.rushhourgame.controller.route.PermanentRouteEdge;
import net.rushhourgame.controller.route.PermanentRouteNode;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.controller.route.TemporaryHumanPoint;
import net.rushhourgame.controller.route.TemporaryHumanRouteEdge;
import net.rushhourgame.controller.route.TemporaryHumanRouteNode;
import net.rushhourgame.controller.route.TemporaryHumanStep;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Identifiable;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.TicketGate;

/**
 * 経路計算機. 経路探索処理中は経路にしたがった移動動作をしないこと (HashMapを使っているため). これを使う ExecutorService
 * のスレッドプールサイズは1にすること (同時に実行されることを想定していないため)
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

    @Inject
    protected GameMaster gm;

    protected Map<Long, List<RouteNode>> routes;

    protected Lock lock;

    protected boolean isAvailble;

    @PostConstruct
    public void init() {
        routes = new HashMap<>();
        lock = new ReentrantReadWriteLock().writeLock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    /**
     * 経路情報をすべて破棄する.
     *
     * @param humans 人
     */
    protected void flush(List<Human> humans) {
        humans.forEach(h -> h.flushCurrent());
        routes.clear();
    }

    public boolean isAvailable() {
        return isAvailble;
    }

    public void notifyUpdate() {
        isAvailble = false;
    }

    public boolean isReachable(@NotNull Identifiable start, @NotNull Company c) {
        return routes.containsKey(c.getId()) && routes.get(c.getId()).stream()
                .anyMatch(node -> node.getOriginal().equalsId(start));
    }

    public RouteNode getStart(@NotNull Identifiable start, @NotNull Company c) {
        return routes.get(c.getId()).stream()
                .filter(node -> node.getOriginal().equalsId(start))
                .findFirst().get();
    }
    
    public double getCost(@NotNull Identifiable start, @NotNull Company c) {
        return getStart(start, c).getCost();
    }

    @Override
    @Transactional
    public Boolean call() {
        LOG.log(Level.INFO, "{0}#call start by {1}", new Object[]{this.getClass().getSimpleName(), this});
        lock.lock();
        try {
            BaseObjPack bPack = this.new BaseObjPack();

            // 今まで保持していた情報をすべて破棄
            flush(bPack.humans);

            // すべての会社への行き方を求める
            // すべての住宅からすべての会社へ行く道を検索する必要はない。
            // なぜなら探索すると、全開始点からgoalまでの行き方が分かるから
            cCon.findAll().forEach(company -> {
                PermanentObjPack pPack = this.new PermanentObjPack(bPack);
                TemporaryObjPack tPack = this.new TemporaryObjPack(pPack, bPack.humanMap.get(company.getId()));

                // 移動中の人座標を経路に追加
                List<RouteNode> nodes = constructRouteNodes(pPack, tPack);
                List<RouteEdge> edges = constructRouteEdges(pPack, tPack);

                search(nodes, pPack.companyNodes.get(company));

                edges.forEach(edge -> {
                    LOG.log(Level.FINE, edge.toString());
                });
                nodes.forEach((node) -> {
                    LOG.log(Level.FINE, node.toStringAsRoute());
                });
                
                // 経路探索結果を登録
                routes.put(company.getId(), nodes);

                // 移動中の人に移動経路をセット
                // 車内の場合、行先がなくなるため、h.currentがnullになるようにする
                tPack.humanNodes.stream().filter(node -> !node.isEnd())
                        .forEach(node -> node.registerCurrentTaskToHuman());
            });

            LOG.log(Level.INFO, "{0}#call end", this.getClass().getSimpleName());
        } finally {
            lock.unlock();
        }
        isAvailble = true;
        return true;
    }

    protected List<RouteNode> constructRouteNodes(PermanentObjPack pPack, TemporaryObjPack tPack) {
        return Stream.concat(pPack.allNodes.stream(), tPack.humanNodes.stream()).collect(Collectors.toList());
    }

    protected List<RouteEdge> constructRouteEdges(PermanentObjPack pPack, TemporaryObjPack tPack) {
        return Stream.concat(pPack.allEdges.stream(), tPack.humanEdges.stream()).collect(Collectors.toList());
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
        
        nodes.forEach(node -> node.fix());
    }

    protected final class BaseObjPack {

        protected List<Residence> residences;
        protected List<Company> companies;
        protected List<TicketGate> ticketGates;
        protected List<Platform> platforms;

        protected List<StepForHuman> steps;

        protected List<Human> humans;
        protected Map<Long, Set<Human>> humanMap;

        public BaseObjPack() {
            fetchObj();
            categorizeHumans();
        }

        protected void fetchObj() {
            residences = rCon.findAll();
            companies = cCon.findAll();
            ticketGates = stCon.findTicketGateAll();
            platforms = stCon.findPlatformAll();
            steps = sCon.findAll();
            humans = gm.getHumans();
        }

        protected void categorizeHumans() {
            humanMap = new HashMap<>();

            humans.forEach(h -> {
                if (!humanMap.containsKey(h.getDest().getId())) {
                    humanMap.put(h.getDest().getId(), new HashSet<>());
                }
                humanMap.get(h.getDest().getId()).add(h);
            });
        }
    }

    protected final class PermanentObjPack {

        protected Map<Residence, RouteNode> residenceNodes = new HashMap<>();
        protected Map<Company, RouteNode> companyNodes = new HashMap<>();
        protected Map<TicketGate, RouteNode> ticketGateNodes = new HashMap<>();
        protected Map<Platform, RouteNode> platformNodes = new HashMap<>();
        protected List<RouteNode> allNodes;

        protected List<RouteEdge> allEdges;

        public PermanentObjPack(BaseObjPack base) {
            buildRouteNode(base);
            buildRouteEdge(base);
        }

        protected void buildRouteNode(BaseObjPack base) {
            base.residences.stream().forEach(o -> residenceNodes.put(o, new PermanentRouteNode(o)));
            base.companies.stream().forEach(o -> companyNodes.put(o, new PermanentRouteNode(o)));
            base.ticketGates.stream().forEach(o -> ticketGateNodes.put(o, new PermanentRouteNode(o)));
            base.platforms.stream().forEach(o -> platformNodes.put(o, new PermanentRouteNode(o)));

            allNodes = new ArrayList<>();
            allNodes.addAll(residenceNodes.values());
            allNodes.addAll(companyNodes.values());
            allNodes.addAll(ticketGateNodes.values());
            allNodes.addAll(platformNodes.values());
        }

        protected void buildRouteEdge(BaseObjPack base) {
            allEdges = base.steps.stream().map(original -> {
                // from に対応する node の取得
                RouteNode from = allNodes.stream()
                        .filter(node -> node.getOriginal().equalsId(original.getFrom())
                        ).findFirst().get();

                // to に対応する node の取得
                RouteNode to = allNodes.stream()
                        .filter(node -> node.getOriginal().equalsId(original.getTo())
                        ).findFirst().get();

                RouteEdge edge = new PermanentRouteEdge(original, from, to);

                // Node へのリンクを追加
                from.getOutEdges().add(edge);
                to.getInEdges().add(edge);

                return edge;
            }).collect(Collectors.toList());
        }
    }

    protected final class TemporaryObjPack {

        protected List<TemporaryHumanRouteNode> humanNodes;
        protected List<TemporaryHumanRouteEdge> humanEdges;

        public TemporaryObjPack(PermanentObjPack pPack, Set<Human> humans) {
            buildHumanNodes(humans);
            buildHumanEdges(pPack);
        }

        protected void buildHumanNodes(Set<Human> humans) {
            humanNodes = humans != null
                    ? humans.stream().map(h -> new TemporaryHumanRouteNode(new TemporaryHumanPoint(h))).collect(Collectors.toList())
                    : Collections.emptyList();
        }

        protected void buildHumanEdges(PermanentObjPack pPack) {
            humanEdges = new ArrayList<>();

            humanNodes.forEach(humanN -> {
                switch (humanN.getHuman().getStandingOn()) {
                    case GROUND:
                        // 人(改札外) -> 会社
                        // 人(改札外) -> 改札
                        pPack.companyNodes.values().forEach(companyN -> addHumanEdges(humanN, companyN));
                        pPack.ticketGateNodes.values().forEach(ticketGateN -> addHumanEdges(humanN, ticketGateN));
                        break;
                    case PLATFORM:
                        // 人(プラットフォーム上) -> そのプラットフォーム
                        addHumanEdges(humanN, pPack.platformNodes.get(humanN.getHuman().getOnPlatform()));
                        break;
                    case TRAIN:
                        // 人(電車内) -> (何もしない)
                        break;
                }
            });
        }

        protected void addHumanEdges(TemporaryHumanRouteNode humanN, RouteNode to) {
            TemporaryHumanRouteEdge edge = new TemporaryHumanRouteEdge(humanN, to);

            humanN.getOutEdges().add(edge);
            to.getInEdges().add(edge);

            humanEdges.add(edge);
        }
    }
}

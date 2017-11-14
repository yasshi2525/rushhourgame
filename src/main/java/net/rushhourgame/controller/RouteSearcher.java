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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@Singleton
public class RouteSearcher implements Callable<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RouteSearcher.class.getName());

    @PersistenceContext
    protected EntityManager em;

    @Inject
    protected StepForHumanController sCon;
    
    @Inject
    protected ResidenceController rCon;
    
    @Inject
    protected CompanyController cCon;

    protected final Map<RelayPointForHuman, Stream<RouteNode>> routes = new HashMap<>();

    /**
     * 経路情報をすべて破棄する.
     */
    public void flush() {
        routes.clear();
    }

    @Override
    public Object call() {
        LOG.log(Level.INFO, "{0}#call start.", this.getClass().getSimpleName());
        
        flush();
               
        Stream<StepForHuman> edges = sCon.findAll();
        List<Residence> srcs = rCon.findAll();
        List<Company> dests = cCon.findAll();
        
        /*LOG.log(Level.INFO, "{0}#call collected {1} RelayPointForHuman instances.",
                new Object[]{this.getClass().getSimpleName(), nodes.size()});
        
        */
        return null;
    }

    /**
     * nodesの要素にviaとcostをセットして終える. ダイクストラ法で探す
     *
     * @param nodes
     * @param goal
     */
    protected void search(Stream<RouteNode> nodes, RouteNode goal) {
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

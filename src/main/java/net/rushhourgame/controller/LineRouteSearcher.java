/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.controller.route.LineRouteEdge;
import net.rushhourgame.controller.route.LineRouteNode;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class LineRouteSearcher extends AbstractController {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LineRouteSearcher.class.getName());

    public void persist(@NotNull Line completedLine) {
        em.refresh(completedLine);

        double costrate = Double.parseDouble(prop.get(RushHourProperties.GAME_DEF_TRAIN_COSTRATE));
        List<Platform> originalNodes = extractPlatform(completedLine);
        List<PlatformEdge> originalEdges = new ArrayList<>();
        
        
        // LineStep から、路線中の駅間同士の距離を求める
        completedLine.getSteps().stream()
                .filter(step -> step.getDeparture() != null)
                .forEach(step -> {
                    originalEdges.add(new PlatformEdge(step.getDeparture()));
                });

        originalNodes.forEach(goalPlt -> {
            // 各駅に対して、他の駅からの距離を求める
            List<LineRouteNode> nodes = wrapNode(originalNodes);

            LineRouteNode goal = nodes.stream().filter(
                    n -> n.getOriginal().equals(goalPlt)
            ).findFirst().get();

            List<LineRouteEdge> edges = wrapEdge(originalEdges, nodes);

            search(nodes, goal);
            
            edges.forEach(edge -> {
                LOG.log(Level.FINE, "{0}#persist {1}", new Object[]{LineRouteSearcher.class, edge.toString()});
            });
            nodes.forEach(node -> {
                LOG.log(Level.FINE, "{0}#persist {1}", new Object[]{LineRouteSearcher.class, node.toStringAsRoute()});
            });

            // goal から goal は同じ地点なので、永続化しない。
            nodes.stream().filter(
                    n -> !n.equals(goal) && n.getCost() != Double.MAX_VALUE
            ).forEach(n -> {
                persistStepForHuman(createThroughTrain(
                        completedLine,
                        n.getOriginal(),
                        goal.getOriginal(),
                        n.getCost() * costrate));
            });
        });
    }

    protected List<Platform> extractPlatform(Line line) {
        return em.createNamedQuery("Platform.findInLine", Platform.class)
                .setParameter("line", line).getResultList();
    }

    protected List<LineRouteNode> wrapNode(List<Platform> originalNodes) {
        return originalNodes.stream().map(original -> new LineRouteNode(original))
                .collect(Collectors.toList());
    }

    protected List<LineRouteEdge> wrapEdge(List<PlatformEdge> originalEdges, List<LineRouteNode> nodes) {
        return originalEdges.stream().map(original -> {
            // from に対応する node の取得
            LineRouteNode from = nodes.stream().filter(node
                    -> node.getOriginal().equals(original.getFrom())
            ).findFirst().get();

            // to に対応する node の取得
            LineRouteNode to = nodes.stream().filter(node
                    -> node.getOriginal().equals(original.getTo())
            ).findFirst().get();

            LineRouteEdge edge = new LineRouteEdge(original.getCost(), from, to);

            // Node へのリンクを追加
            from.getOutEdges().add(edge);
            to.getInEdges().add(edge);

            return edge;
        }).collect(Collectors.toList());
    }

    protected void search(List<LineRouteNode> nodes, LineRouteNode goal) {

        nodes.forEach(node -> node.setCost(Double.MAX_VALUE));

        goal.setCost(0);

        PriorityQueue<LineRouteNode> queue = new PriorityQueue<>();
        queue.add(goal);

        while (!queue.isEmpty()) {
            LineRouteNode x = queue.poll();

            x.getInEdges().forEach(link -> {
                LineRouteNode y = link.getFrom();
                double newValue = x.getCost() + link.getCost();
                if (newValue < y.getCost()) {
                    y.setCost(newValue);
                    y.setVia(x);
                    queue.offer(y);
                }
            });
        }
    }

    protected StepForHumanThroughTrain createThroughTrain(Line line, Platform from, Platform to, double cost) {
        StepForHumanThroughTrain inst = new StepForHumanThroughTrain();
        inst.setLine(line);
        inst.setFrom(from);
        inst.setTo(to);
        inst.setCost(cost);
        LOG.log(Level.INFO, "{0}#createThroughTrain created {1}", new Object[]{LineRouteSearcher.class, inst});
        return inst;
    }

    protected void persistStepForHuman(StepForHumanThroughTrain step) {
        em.persist(step);
    }

    protected static class PlatformEdge {

        final protected Platform from;
        final protected Platform to;
        protected double cost;

        public PlatformEdge(LineStepDeparture dpt) {
            from = dpt.getStaying();

            LineStep step = dpt.getParent().getNext();

            while (step.getDeparture() == null) {
                cost += step.getDist();
                step = step.getNext();
            }

            to = step.getDeparture().getStaying();
        }

        public Platform getFrom() {
            return from;
        }

        public Platform getTo() {
            return to;
        }

        public double getCost() {
            return cost;
        }
    }
}

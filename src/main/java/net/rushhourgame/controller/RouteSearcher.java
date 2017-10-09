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

import java.util.List;
import java.util.PriorityQueue;
import net.rushhourgame.entity.Link;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RouteSearcher {

    public synchronized void search(List<RoutingInfo> network, List<Link> links, List<Node> nodes, Node goal) {
        for (Node node : nodes) {
            node.setCost(Double.MAX_VALUE);
        }

        goal.setCost(0);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(goal);

        while (!queue.isEmpty()) {
            Node x = queue.poll();

            for (Link link : x.getInEdges()) {
                Node y = link.getFrom();
                double newValue = x.getCost() + link.getCost();
                if (newValue < y.getCost()) {
                    y.setCost(newValue);
                    y.setVia(x);
                    queue.offer(y);
                }
            }
        }

        for (RoutingInfo r : network) {
            Node start = r.getStart();
            Link link = links.stream().filter(
                    obj -> obj.getFrom().equals(start) && obj.getTo().equals(start.getVia())
            ).findFirst().get();
            r.setNext(link);
            r.setCost(start.getCost());
        }
    }

}

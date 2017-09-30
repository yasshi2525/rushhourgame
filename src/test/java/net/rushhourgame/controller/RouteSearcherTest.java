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
import net.rushhourgame.entity.Link;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RouteSearcherTest extends AbstractControllerTest {

    @Test
    public void testSearch1() throws RushHourException {
        Node node1 = NCON.create(0, 0);
        EM.flush();
        
        List<Node> nodes = NCON.findIn(0, 0, 1);
        List<RoutingInfo> network = RCON.findNetwork(node1);
        List<Link> links = LCON.findIn(0, 0, 1);
        
        RouteSearcher instance = new RouteSearcher();
        instance.search(network, links, nodes, node1);
        
        assertTrue(node1.getCost() == 0.0);
    }

    
    @Test
    public void testSearch2() throws RushHourException {
        Node node1 = NCON.create(0, 0);
        Node node2 = NCON.create(1, 1);
        LCON.create(node1, node2, 1, Link.Type.WALK);
        EM.flush();
        EM.clear();
        List<Node> nodes = NCON.findIn(0, 0, 2);
        
        Node start = nodes.get(0);
        Node goal = nodes.get(1);
        List<RoutingInfo> network = RCON.findNetwork(goal);
        List<Link> links = LCON.findIn(0, 0, 2);
        
        RouteSearcher instance = new RouteSearcher();
        instance.search(network, links, nodes, goal);
        
        assertEquals(links.get(0), network.get(0).getNext());
        assertTrue(start.getCost() == 1);
        assertEquals(goal, start.getVia());
    }
    
    @Test
    public void testSearch2Loop() throws RushHourException {
        Node node1 = NCON.create(0, 0);
        Node node2 = NCON.create(1, 1);
        LCON.create(node1, node2, 1, Link.Type.WALK);
        LCON.create(node2, node1, 1, Link.Type.WALK);
        EM.flush();
        EM.clear();
        List<Node> nodes = NCON.findIn(0, 0, 2);
        
        Node start = nodes.get(0);
        Node goal = nodes.get(1);
        List<RoutingInfo> startlNetwork = RCON.findNetwork(start);
        List<RoutingInfo> goalNetwork = RCON.findNetwork(goal);
        List<Link> links = LCON.findIn(0, 0, 2);
        
        RouteSearcher instance = new RouteSearcher();
        instance.search(startlNetwork, links, nodes, start);
        instance.search(goalNetwork, links, nodes, goal);
        
        assertEquals(links.get(1), startlNetwork.get(0).getNext());
        assertEquals(links.get(0), goalNetwork.get(0).getNext());
    }
}

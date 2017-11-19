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
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.entity.Company;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import org.junit.Before;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RouteSearcherTest extends AbstractControllerTest {

    protected RouteSearcher inst;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createRouteSearcher();
    }
    
    @Test
    public void testFindRelayPointAllEmptyWorld() {
        assertEquals(0, inst.findRelayPointAll().size());
    }
    
    @Test
    public void testFindRelayPointAllOneCompany() throws RushHourException {
        CCON.create(0, 0);
        EM.flush();
        assertEquals(1, inst.findRelayPointAll().size());
    }
    
    @Test
    public void testFindRelayPointAllRsdCmp() throws RushHourException {
        RCON.create(0, 0);
        CCON.create(0, 0);
        EM.flush();
        assertEquals(2, inst.findRelayPointAll().size());
    }
    
    @Test
    public void testBuildRouteNodesEmptyWorld() {
        assertEquals(0, inst.buildRouteNodes(inst.findRelayPointAll()).size());
    }
    
    @Test
    public void testBuildRouteNodesRsdCmp() throws RushHourException {
        RCON.create(0, 0);
        CCON.create(0, 0);
        EM.flush();
        assertEquals(2, inst.buildRouteNodes(inst.findRelayPointAll()).size());
    }
    
    @Test
    public void testBuildRouteEdgesEmptyWorld() {
        List<RouteNode> nodes = inst.buildRouteNodes(inst.findRelayPointAll());
        assertEquals(0, inst.buildRouteEdges(SCON.findAll(), nodes).size());
    }
    
    @Test
    public void testBuildRouteEdgesRsdCmp() throws RushHourException {
        RCON.create(0, 0);
        CCON.create(0, 0);
        EM.flush();
        
        List<RouteNode> nodes = inst.buildRouteNodes(inst.findRelayPointAll());
        assertEquals(1L, inst.buildRouteEdges(SCON.findAll(), nodes).size());
    }
    
    @Test
    public void testSearchEmptyWorld() {
        List<RelayPointForHuman> originalEdges = inst.findRelayPointAll();
        inst.search(inst.buildRouteNodes(originalEdges), new RouteNode(new Company()));
    }
    
    @Test
    public void testSearchOnlyCmp() throws RushHourException {
        CCON.create(0, 0);
        EM.flush();
        
        List<RelayPointForHuman> originalEdges = inst.findRelayPointAll();
        List<RouteNode> nodes = inst.buildRouteNodes(inst.findRelayPointAll());
        
        RouteNode goal = nodes.get(0);
        inst.search(inst.buildRouteNodes(originalEdges), goal);
        
        assertTrue(0 == goal.getCost());
        assertNull(goal.getVia());
    }
    
    /**
     * 到達できない場合。Cmp2を例に
     * @throws RushHourException 
     */
    @Test
    public void testSearchUnreach() throws RushHourException {
        CCON.create(10, 10);
        CCON.create(20, 20);
        EM.flush();
        
        List<RelayPointForHuman> originalEdges = inst.findRelayPointAll();
        List<RouteNode> nodes = inst.buildRouteNodes(originalEdges);
        
        RouteNode begin = nodes.get(0);
        RouteNode goal = nodes.get(1);
        inst.search(nodes, goal);
        
        assertTrue(0 == goal.getCost());
        assertTrue(Double.MAX_VALUE == begin.getCost());
        assertNull(goal.getVia());
        assertNull(begin.getVia());
    }
    
    @Test
    public void testSearch() throws RushHourException {
        RCON.create(10, 10);
        CCON.create(10, 20);
        EM.flush();
        
        List<RelayPointForHuman> originalEdges = inst.findRelayPointAll();
        List<RouteNode> nodes = inst.buildRouteNodes(originalEdges);
        inst.buildRouteEdges(SCON.findAll(), nodes);
        
        RouteNode begin = nodes.get(0);
        RouteNode goal = nodes.get(1);
        inst.search(nodes, goal);
        
        assertTrue(0 == goal.getCost());
        assertTrue(10 == begin.getCost());
        assertNull(goal.getVia());
        assertEquals(goal, begin.getVia());
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsReachableEmptyWorld() {
        assertFalse(inst.isReachable(new Residence(), new Company()));
    }
    
    @Test
    public void testIsReachableRsdCmp() throws RushHourException {
        Residence r = RCON.create(10, 10);
        Company c = CCON.create(10, 20);
        EM.flush();
        assertTrue(inst.call());
        assertTrue(inst.isReachable(r, c));
    }
    
    @Test
    public void testCall() throws RushHourException {
        Residence r1 = RCON.create(10, 10);
        Company c1 = CCON.create(10, 20);
        Residence r2 = RCON.create(100, 100);
        Company c2 = CCON.create(200, 200);
        EM.flush();
        
        assertTrue(inst.call());
        
        RouteNode start1_1 = inst.getStart(r1, c1);
        RouteNode start1_2 = inst.getStart(r1, c2);
        RouteNode start2_1 = inst.getStart(r2, c1);
        RouteNode start2_2 = inst.getStart(r2, c2);
        
        assertEquals(c1, start1_1.getVia().getOriginal());
        assertEquals(c2, start1_2.getVia().getOriginal());
        assertEquals(c1, start2_1.getVia().getOriginal());
        assertEquals(c2, start2_2.getVia().getOriginal());
    }
}

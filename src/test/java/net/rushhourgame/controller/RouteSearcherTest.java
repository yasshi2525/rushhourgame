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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.rushhourgame.GameMaster;
import net.rushhourgame.controller.route.PermanentRouteEdge;
import net.rushhourgame.controller.route.PermanentRouteNode;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.controller.route.TemporaryHumanRouteEdge;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.entity.hroute.StepForHumanTransfer;
import net.rushhourgame.json.SimpleUserData;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RouteSearcherTest extends AbstractControllerTest {

    protected RouteSearcher inst;
    protected static final Pointable TEST_POS = new SimplePoint();
    protected static final Pointable ORIGIN = new SimplePoint();
    protected static final Pointable FAR = new SimplePoint(10, 20);
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createRouteSearcher();
        inst.hCon = spy(inst.hCon);
        inst.init();
    }
    
    @Test
    public void testFlush() {
        List<Human> humans = new ArrayList<>();
        Human human = new Human();
        humans.add(human);
        
        RouteNode node = mock(RouteNode.class);
        RouteEdge edge = mock(RouteEdge.class);
        doReturn(edge).when(node).getViaEdge();
        
        human.setCurrent(node);
        
        inst.flush(humans);
        
        assertNull(human.getCurrent());
    }
    
    @Test
    public void testRefreshNode() {
        List<RouteNode> nodes = new ArrayList<>();
        
        Residence oldR = mock(Residence.class);
        Platform oldP = mock(Platform.class);
        TicketGate oldTg = mock(TicketGate.class);
        
        RouteNode r = new PermanentRouteNode(oldR);
        RouteNode p = new PermanentRouteNode(oldP);
        RouteNode tg = new PermanentRouteNode(oldTg);
        
        nodes.add(r);
        nodes.add(p);
        nodes.add(tg);
        
        inst.routes.put(1L, nodes);
        
        Residence newR = mock(Residence.class);
        Platform newP = mock(Platform.class);
        TicketGate newTg = mock(TicketGate.class);
        
        inst.rCon = mock(ResidenceController.class);
        inst.stCon = mock(StationController.class);
        
        doReturn(newR).when(inst.rCon).find(any(Residence.class));
        doReturn(newP).when(inst.stCon).find(any(Platform.class));
        doReturn(newTg).when(inst.stCon).find(any(TicketGate.class));
        
        inst.refresh();
        
        assertEquals(newR, r.getOriginal());
        assertEquals(newP, p.getOriginal());
        assertEquals(newTg, tg.getOriginal());
    }
    
    @Test
    public void testRefreshEdge() {
        inst.rCon = mock(ResidenceController.class);
        inst.stCon = mock(StationController.class);
        
        Residence oldR = mock(Residence.class);
        Platform oldP = mock(Platform.class);
        TicketGate oldTg = mock(TicketGate.class);
        
        StepForHumanDirectly directly = new StepForHumanDirectly();
        StepForHumanIntoStation into = new StepForHumanIntoStation();
        StepForHumanOutOfStation out = new StepForHumanOutOfStation();
        StepForHumanResidenceToStation from = new StepForHumanResidenceToStation();
        StepForHumanStationToCompany to = new StepForHumanStationToCompany();
        StepForHumanThroughTrain train = new StepForHumanThroughTrain();
        StepForHumanTransfer transfer = new StepForHumanTransfer();
        
        directly.setFrom(oldR);
        into.setFrom(oldTg);
        into.setTo(oldP);
        out.setFrom(oldP);
        out.setTo(oldTg);
        from.setFrom(oldR);
        from.setTo(oldTg);
        to.setFrom(oldTg);
        train.setFrom(oldP);
        train.setTo(oldP);
        transfer.setFrom(oldTg);
        transfer.setTo(oldTg);
        
        Residence newR = mock(Residence.class);
        Platform newP = mock(Platform.class);
        TicketGate newTg = mock(TicketGate.class);
        doReturn(newR).when(inst.rCon).find(any(Residence.class));
        doReturn(newP).when(inst.stCon).find(any(Platform.class));
        doReturn(newTg).when(inst.stCon).find(any(TicketGate.class));
        
        inst.refresh(directly);
        inst.refresh(into);
        inst.refresh(out);
        inst.refresh(from);
        inst.refresh(to);
        inst.refresh(train);
        inst.refresh(transfer);
        
        assertNotEquals(oldR, directly.getFrom());
        assertNotEquals(oldTg, into.getFrom());
        assertNotEquals(oldP, into.getTo());
        assertNotEquals(oldP, out.getFrom());
        assertNotEquals(oldTg, out.getTo());
        assertNotEquals(oldR, from.getFrom());
        assertNotEquals(oldTg, from.getTo());
        assertNotEquals(oldTg, to.getFrom());
        assertNotEquals(oldP, train.getFrom());
        assertNotEquals(oldP, train.getTo());
        assertNotEquals(oldTg, transfer.getFrom());
        assertNotEquals(oldTg, transfer.getTo());
        
    }
    
    public void testIsReachableEmptyWorld() {
        assertFalse(inst.isReachable(new Residence(), new Company()));
    }
    
    @Test
    public void testIsReachableRsdCmp() throws RushHourException {
        Residence r = RCON.create(new SimplePoint(10, 10));
        Company c = CCON.create(new SimplePoint(10, 2));
        doReturn(new ArrayList<>()).when(inst.hCon).findAll();
        assertTrue(inst.call());
        assertTrue(inst.isReachable(r, c));
    }
    
    @Test
    public void testSearchEmptyWorld() {
        inst.search(new ArrayList<>(), new PermanentRouteNode(new Company()));
    }
    
    @Test
    public void testSearchOnlyCmp() throws RushHourException {
        Company cmp = CCON.create(TEST_POS);
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteNode goal = pPack.companyNodes.get(cmp.getId());
        
        inst.search(pPack.allNodes, goal);
        
        assertTrue(0 == goal.getCost());
        assertNull(goal.getVia());
    }
    
    /**
     * 到達できない場合。Cmp2を例に
     * @throws RushHourException 
     */
    @Test
    public void testSearchUnreach() throws RushHourException {
        Company cmp1 = CCON.create(new SimplePoint(10, 10));
        Company cmp2 = CCON.create(new SimplePoint(20, 20));
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteNode begin = pPack.companyNodes.get(cmp1.getId());
        RouteNode goal = pPack.companyNodes.get(cmp2.getId());
        
        inst.search(pPack.allNodes, goal);
        
        assertTrue(0 == goal.getCost());
        assertTrue(Double.MAX_VALUE == begin.getCost());
        assertNull(goal.getVia());
        assertNull(begin.getVia());
    }
    
    @Test
    public void testSearch() throws RushHourException {
        Residence rsd = RCON.create(new SimplePoint(10, 10));
        Company cmp = CCON.create(new SimplePoint(10, 20));
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
              
        RouteNode begin = pPack.residenceNodes.get(rsd.getId());
        RouteNode goal = pPack.companyNodes.get(cmp.getId());
        
        inst.search(pPack.allNodes, goal);
        
        assertTrue(0 == goal.getCost());
        assertTrue(10 == begin.getCost());
        assertNull(goal.getVia());
        assertEquals(goal, begin.getVia());
        
        try {
            begin.getInEdges();
            fail();
        } catch (IllegalStateException e) {
            // OK
        }
        
        try {
            begin.getOutEdges();
            fail();
        } catch (IllegalStateException e) {
            // OK
        }
        
        assertTrue(rsd.distTo(cmp) == begin.getCost());
    }
    
    /**
     * detourは迂回という意味. 駅まで遠回りすることで最短時間でつく
     * @throws RushHourException 
     */
    @Test
    public void testSearchDetour() throws RushHourException {
        Residence r = RCON.create(new SimplePoint(-10, -10));
        Company c = CCON.create(new SimplePoint(10, 1000));
        Player p = createPlayer();
        AssistanceController.Result result = ACON.startWithStation(p, new SimplePoint(), Locale.JAPANESE);
        AssistanceController.Result extend = ACON.extendWithStation(p, result.node, new SimplePoint(0, 990), Locale.JAPANESE);
        
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        for (RouteEdge edge : pPack.allEdges) {
            System.out.println(edge);
        }
        
        RouteNode goal = pPack.companyNodes.get(c.getId());
        inst.search(pPack.allNodes, goal);
        
        RouteNode start = pPack.residenceNodes.get(r.getId());
        assertEquals(start.getOriginal(), r);
        assertEquals(start.getVia().getOriginal(), result.station.getTicketGate());
        assertEquals(start.getVia().getVia().getOriginal(), result.station.getPlatform());
        assertEquals(start.getVia().getVia().getVia().getOriginal(), extend.station.getPlatform());
        assertEquals(start.getVia().getVia().getVia().getVia().getOriginal(), extend.station.getTicketGate());
        assertEquals(start.getVia().getVia().getVia().getVia().getVia().getOriginal(), c);
        
        inst.routes.put(c.getId(), pPack.allNodes);
        
        assertTrue(inst.isReachable(r, c));
        assertTrue(inst.isReachable(result.station.getPlatform(), c));
        assertTrue(inst.isReachable(extend.station.getPlatform(), c));
        
        assertTrue(r.distTo(c) > start.getCost());
    }
    
    @Test
    public void testCallNoHuman() throws RushHourException {
        Residence r1 = RCON.create(new SimplePoint(10, 10));
        Company c1 = CCON.create(new SimplePoint(10, 20));
        Residence r2 = RCON.create(new SimplePoint(100, 100));
        Company c2 = CCON.create(new SimplePoint(200, 200));
        
        doReturn(new ArrayList<>()).when(inst.hCon).findAll();
        
        assertTrue(inst.call());
        
        RouteNode start1_1 = inst.getStart(r1, c1);
        RouteNode start1_2 = inst.getStart(r1, c2);
        RouteNode start2_1 = inst.getStart(r2, c1);
        RouteNode start2_2 = inst.getStart(r2, c2);
        
        assertEquals(c1, start1_1.getVia().getOriginal());
        assertEquals(c2, start1_2.getVia().getOriginal());
        assertEquals(c1, start2_1.getVia().getOriginal());
        assertEquals(c2, start2_2.getVia().getOriginal());
        
        
        RouteEdge edge = inst.getStart(r1, c1).getViaEdge();
        assertNotNull(edge.getTo());
        assertNotNull(edge.getOriginal());
    }
    
    @Test
    public void testCallWithHuman() throws RushHourException {
        Residence r = RCON.create(new SimplePoint(10, 10));
        Company c = CCON.create(new SimplePoint(10, 20));
        Human h = HCON.create(TEST_POS, r, c);
        List<Human> list = new ArrayList<>();
        list.add(h);
        doReturn(list).when(inst.hCon).findAll();
        
        assertTrue(inst.call());
        
        h.setCurrent(inst.getStart(h.getSrc(), h.getDest()));
        
        assertTrue(inst.call());
        
        assertTrue(h.getCurrent() instanceof TemporaryHumanRouteEdge);
        assertTrue(inst.isAvailable());
    }
    
    @Test
    public void testBaseObjPackSmallWorld() throws RushHourException {
        WorldPack world = createSmallWorld();
        List<Human> humans = new ArrayList<>();
        humans.add(world.h);
        doReturn(humans).when(inst.hCon).findAll();
        
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        
        assertEquals(1, bPack.residences.size());
        assertEquals(1, bPack.companies.size());
        assertEquals(2, bPack.ticketGates.size());
        assertEquals(2, bPack.platforms.size());
        assertEquals(13, bPack.steps.size());
        assertEquals(1, bPack.humans.size());
        
        assertTrue(bPack.humanMap.containsKey(world.cmp.getId()));
        assertEquals(1, bPack.humanMap.get(world.cmp.getId()).size());
    }
    
    @Test
    public void testPermanentObjSmallWorld() throws RushHourException {
        WorldPack world = createSmallWorld();
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteNode rN = pPack.residenceNodes.get(world.rsd.getId());
        assertNotNull(rN);
        assertEquals(0, rN.getInEdges().size());
        assertEquals(3, rN.getOutEdges().size());
        
        RouteNode cN = pPack.companyNodes.get(world.cmp.getId());
        assertNotNull(cN);
        assertEquals(3, cN.getInEdges().size());
        assertEquals(0, cN.getOutEdges().size());
        
        RouteNode tg1N = pPack.ticketGateNodes.get(world.st1.getTicketGate().getId());
        assertNotNull(tg1N);
        assertEquals(3, tg1N.getInEdges().size());
        assertEquals(3, tg1N.getOutEdges().size());
        
        RouteNode tg2N = pPack.ticketGateNodes.get(world.st2.getTicketGate().getId());
        assertNotNull(tg2N);
        assertEquals(3, tg2N.getInEdges().size());
        assertEquals(3, tg2N.getOutEdges().size());
        
        RouteNode p1N = pPack.platformNodes.get(world.st1.getPlatform().getId());
        assertNotNull(p1N);
        assertEquals(2, p1N.getInEdges().size());
        assertEquals(2, p1N.getOutEdges().size());
        
        RouteNode p2N = pPack.platformNodes.get(world.st2.getPlatform().getId());
        assertNotNull(p2N);
        assertEquals(2, p2N.getInEdges().size());
        assertEquals(2, p2N.getOutEdges().size());
        
        assertEquals(6, pPack.allNodes.size());
        assertEquals(13, pPack.allEdges.size());
    }
    
    @Test
    public void testTemporaryObjPackEmptyWorld() {
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteSearcher.TemporaryObjPack tPack = inst.new TemporaryObjPack(pPack, null);

        assertNotNull(tPack.humanNodes);
        assertTrue(tPack.humanNodes.isEmpty());
        assertNotNull(tPack.humanEdges);
        assertTrue(tPack.humanEdges.isEmpty());
    }
    
    @Test
    public void testTemporaryObjPackSmallWorld() throws RushHourException {
        WorldPack world = createSmallWorld();
        Set<Human> hs = new HashSet<>();
        hs.add(world.h);
        
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteSearcher.TemporaryObjPack tPack = inst.new TemporaryObjPack(pPack, hs);

        assertEquals(1, tPack.humanNodes.size());
        assertEquals(3, tPack.humanEdges.size());
        assertEquals(0, tPack.humanNodes.get(0).getInEdges().size());
        assertEquals(3, tPack.humanNodes.get(0).getOutEdges().size());
        
        RouteNode cN = pPack.companyNodes.get(world.cmp.getId());
        assertNotNull(cN);
        assertEquals(4, cN.getInEdges().size());
        assertEquals(0, cN.getOutEdges().size());
        
        RouteNode tg1N = pPack.ticketGateNodes.get(world.st1.getTicketGate().getId());
        assertNotNull(tg1N);
        assertEquals(4, tg1N.getInEdges().size());
        assertEquals(3, tg1N.getOutEdges().size());
        
        RouteNode tg2N = pPack.ticketGateNodes.get(world.st2.getTicketGate().getId());
        assertNotNull(tg2N);
        assertEquals(4, tg2N.getInEdges().size());
        assertEquals(3, tg2N.getOutEdges().size());
    }
    
    @Test
    public void testTemporaryObjPackSmallWorldOnPlatform() throws RushHourException {
        WorldPack world = createSmallWorld();
        Set<Human> hs = new HashSet<>();
        world.h.enterIntoPlatform(world.st1.getTicketGate(), world.st1.getPlatform());
        hs.add(world.h);
        
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteSearcher.TemporaryObjPack tPack = inst.new TemporaryObjPack(pPack, hs);

        assertEquals(1, tPack.humanNodes.size());
        assertEquals(1, tPack.humanEdges.size());
        assertEquals(0, tPack.humanNodes.get(0).getInEdges().size());
        assertEquals(1, tPack.humanNodes.get(0).getOutEdges().size());
        
        RouteNode p1N = pPack.platformNodes.get(world.st1.getPlatform().getId());
        assertNotNull(p1N);
        assertEquals(3, p1N.getInEdges().size());
        assertEquals(2, p1N.getOutEdges().size());
    }
    
    @Test
    public void testTemporaryObjPackSmallWorldOnTrain() throws RushHourException {
        WorldPack world = createSmallWorld();
        Set<Human> hs = new HashSet<>();
        world.h.setOnTrain(world.t.getDeployed());
        hs.add(world.h);
        
        RouteSearcher.BaseObjPack bPack = inst.new BaseObjPack();
        RouteSearcher.PermanentObjPack pPack = inst.new PermanentObjPack(bPack);
        
        RouteSearcher.TemporaryObjPack tPack = inst.new TemporaryObjPack(pPack, hs);

        assertEquals(1, tPack.humanNodes.size());
        assertEquals(0, tPack.humanEdges.size());
        assertEquals(0, tPack.humanNodes.get(0).getInEdges().size());
        assertEquals(0, tPack.humanNodes.get(0).getOutEdges().size());
    } 
    
    /**
     * R St--St C
     * @return WorldPack
     * @throws RushHourException 
     */
    protected WorldPack createSmallWorld() throws RushHourException {
        WorldPack pack = new WorldPack();
        pack.rsd = RCON.create(ORIGIN);
        pack.cmp = CCON.create(FAR);
        pack.owner = PCON.upsertPlayer("admin", "admin", "admin", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
        
        AssistanceController.Result start = ACON.startWithStation(pack.owner, ORIGIN, Locale.getDefault());
        pack.st1 = start.station;
        pack.l = start.line;
        
        AssistanceController.Result end = ACON.extendWithStation(pack.owner, start.node, FAR, Locale.getDefault());
        pack.st2 = end.station;
        
        pack.h = HCON.create(ORIGIN, pack.rsd, pack.cmp);
        
        pack.t = TRAINCON.create(pack.owner);
        TRAINCON.deploy(pack.t, pack.owner, pack.l.findTop());
        
        EM.flush();
        return pack;
    }
    
    protected static class WorldPack {
        public Residence rsd;
        public Company cmp;
        public Player owner;
        public Station st1;
        public Station st2;
        public Human h;
        public Train t;
        public Line l;
    }
}

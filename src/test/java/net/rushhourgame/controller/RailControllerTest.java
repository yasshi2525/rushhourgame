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
import java.util.List;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RailControllerTest extends AbstractControllerTest {
    protected RailController inst;
    private static final double TEST_X = 10;
    private static final double TEST_Y = 10;
    private static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    private static final double TEST_X2 = 20;
    private static final double TEST_Y2 = 10;
    private static final Pointable TEST_POS2 = new SimplePoint(TEST_X2, TEST_Y2);
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_INTERVAL = 3;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = railCon;
    }

    @Test
    public void testCreate() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(player, created.getOwner());
        assertTrue(created.isOwnedBy(player));
        assertTrue(created.isPrivilegedBy(player));

        em.flush();
        em.refresh(created);

        assertEquals(0, created.getInEdges().size());
        assertEquals(0, created.getOutEdges().size());
    }

    @Test
    public void testCreateDuplication() throws RushHourException {
        Player player = createPlayer();
        inst.create(player, TEST_POS);
        try {
            inst.create(player, TEST_POS);
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testCreateOtherDuplication() throws RushHourException {
        Player player = createPlayer();
        inst.create(player, TEST_POS);

        Player other = createOther();
        inst.create(other, TEST_POS);
    }

    @Test
    public void testExtend() throws RushHourException {
        Player player = createPlayer();
        RailNode created1 = inst.create(player, TEST_POS);
        RailNode created2 = inst.extend(player, created1, TEST_POS2);

        em.flush();
        em.refresh(created1);
        em.refresh(created2);

        // 1 --> 2
        RailEdge edge1 = created1.getOutEdges().get(0);
        // 1 <-- 2
        RailEdge edge2 = created1.getInEdges().get(0);

        assertTrue(created1.distTo(created2) == 10);
        assertTrue(created2.distTo(created1) == 10);
        assertTrue(edge1.getDist() == 10);
        assertTrue(edge2.getDist() == 10);

        assertNotNull(created2);
        assertEquals(player, edge1.getOwner());
        assertEquals(player, edge2.getOwner());
        assertTrue(edge1.isOwnedBy(player));
        assertTrue(edge2.isOwnedBy(player));
        assertTrue(edge1.isPrivilegedBy(player));
        assertTrue(edge2.isPrivilegedBy(player));

        // check 1 --> (2)
        assertEquals(1, created2.getInEdges().size());
        assertEquals(created1, created2.getInEdges().get(0).getFrom());
        assertEquals(created2, created2.getInEdges().get(0).getTo());

        // check 1 <-- (2) 
        assertEquals(1, created2.getOutEdges().size());
        assertEquals(created2, created2.getOutEdges().get(0).getFrom());
        assertEquals(created1, created2.getOutEdges().get(0).getTo());

        // check (1) <-- 2
        assertEquals(1, created1.getInEdges().size());
        assertEquals(created2, created1.getInEdges().get(0).getFrom());
        assertEquals(created1, created1.getInEdges().get(0).getTo());

        // check (1) --> 2 
        assertEquals(1, created1.getOutEdges().size());
        assertEquals(created1, created1.getOutEdges().get(0).getFrom());
        assertEquals(created2, created1.getOutEdges().get(0).getTo());
    }

    @Test
    public void testExtendDuplicate() throws RushHourException {
        Player player = createPlayer();
        RailNode create = inst.create(player, TEST_POS);
        try {
            inst.extend(player, create, TEST_POS);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }

    /**
     * 違う人が線路を伸ばそうとした
     *
     * @throws RushHourException
     */
    @Test
    public void testExtendOther() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode create = inst.create(player, TEST_POS);
        try {
            inst.extend(other, create, TEST_POS);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testConnect() throws RushHourException {
        Player player = createPlayer();
        RailNode r1 = inst.create(player, TEST_POS);
        RailNode r2 = inst.create(player, TEST_POS2);

        inst.connect(player, r1, r2);
    }

    @Test
    public void testConncetOtherOwnerFrom() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode r1 = inst.create(player, TEST_POS);
        RailNode r2 = inst.extend(player, r1, TEST_POS2);

        try {
            inst.connect(other, r1, r2);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testConncetOtherOwnerTo() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode r1 = inst.create(player, TEST_POS);
        RailNode r2 = inst.create(other, TEST_POS);

        try {
            inst.connect(player, r1, r2);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testConnectLoop() throws RushHourException {
        Player player = createPlayer();
        RailNode r1 = inst.create(player, TEST_POS);

        try {
            inst.connect(player, r1, r1);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testRemoveEmptyEdges () throws RushHourException {
        Player player = createPlayer();
        try {
            inst.remove(player, new ArrayList<>());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testRemoveIndependentEdges () throws RushHourException {
        Player player = createPlayer();
        RailNode created1 = inst.create(player, TEST_POS);
        inst.extend(player, created1, TEST_POS2);
        RailNode created2 = inst.create(player, new SimplePoint(1000, 1000));
        inst.extend(player, created2, new SimplePoint(2000, 1000));
        
        em.flush();
        em.refresh(created1);
        em.refresh(created2);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created1.getOutEdges().get(0));
        edges.add(created2.getOutEdges().get(0));
        
        try {
            inst.remove(player, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testRemoveOther() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created.getOutEdges().get(0));
        edges.add(created.getInEdges().get(0));
        
        try {
            inst.remove(other, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testRemoveUsedByStopping() throws RushHourException {
        // st1 -- st2
        Station station = createStation();
        Player player = station.getOwner();
        RailNode goal = inst.extend(player, station.getPlatform().getRailNode(), TEST_POS);
        stCon.create(player, goal, "hoge");
        em.flush();
        em.refresh(goal);

        Line line = lCon.create(player, "hoge");
        LineStep s1 = lCon.start(line, player, station);
        lCon.extend(s1, player, goal.getInEdges().get(0));
        em.flush();
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(goal.getOutEdges().get(0));
        edges.add(goal.getInEdges().get(0));

        try {
            inst.remove(player, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testRemoveUsedByPassing() throws RushHourException {
        // st1 -- st2
        Station station = createStation();
        Player player = station.getOwner();
        RailNode goal = inst.extend(player, station.getPlatform().getRailNode(), TEST_POS);
        stCon.create(player, goal, "hoge");
        em.flush();
        em.refresh(goal);

        Line line = lCon.create(player, "hoge");
        LineStep s1 = lCon.start(line, player, station);
        lCon.extend(s1, player, goal.getInEdges().get(0), true);
        em.flush();

        List<RailEdge> edges = new ArrayList<>();
        edges.add(goal.getOutEdges().get(0));
        edges.add(goal.getInEdges().get(0));
        
        try {
            inst.remove(player, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testRemoveUsedByMoving() throws RushHourException {
        // st1 --
        Station station = createStation();
        Player player = station.getOwner();
        RailNode goal = inst.extend(player, station.getPlatform().getRailNode(), TEST_POS);
 
        em.flush();
        em.refresh(goal);

        Line line = lCon.create(player, "hoge");
        LineStep s1 = lCon.start(line, player, station);
        lCon.extend(s1, player, goal.getInEdges().get(0), true);
        em.flush();

        List<RailEdge> edges = new ArrayList<>();
        edges.add(goal.getOutEdges().get(0));
        edges.add(goal.getInEdges().get(0));
        
        try {
            inst.remove(player, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testRemove() throws RushHourException {
        Player player = createPlayer();
        RailNode start = inst.create(player, TEST_POS);
        inst.extend(player, start, TEST_POS2);
        
        em.flush();
        em.refresh(start);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(start.getOutEdges().get(0));
        edges.add(start.getInEdges().get(0));
        
        inst.remove(player, edges);
        
        assertTrue(tableCon.findAll("RailNode", RailNode.class).isEmpty());
        assertTrue(tableCon.findAll("RailEdge", RailEdge.class).isEmpty());
    }
    
    @Test
    public void testRemoveWhenStation() throws RushHourException {
        // st1 --
        Station station = createStation();
        Player player = station.getOwner();
        RailNode goal = inst.extend(player, station.getPlatform().getRailNode(), TEST_POS);
        
        em.flush();
        em.refresh(goal);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(goal.getOutEdges().get(0));
        edges.add(goal.getInEdges().get(0));
        
        inst.remove(player, edges);
        
        assertEquals(1, tableCon.findAll("RailNode", RailNode.class).size());
        assertTrue(tableCon.findAll("RailEdge", RailEdge.class).isEmpty());
    }
    
    @Test
    public void testCanRemoveEmptyEdges () throws RushHourException {
        Player player = createPlayer();
        try {
            inst.canRemove(player, new ArrayList<>());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testCanRemoveIndependentEdges () throws RushHourException {
        Player player = createPlayer();
        RailNode created1 = inst.create(player, TEST_POS);
        inst.extend(player, created1, TEST_POS2);
        RailNode created2 = inst.create(player, new SimplePoint(1000, 1000));
        inst.extend(player, created2, new SimplePoint(2000, 1000));
        
        em.flush();
        em.refresh(created1);
        em.refresh(created2);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created1.getOutEdges().get(0));
        edges.add(created2.getOutEdges().get(0));
        
        try {
            inst.canRemove(player, edges);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testCanRemoveOther() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created.getOutEdges().get(0));
        edges.add(created.getInEdges().get(0));
        
        assertFalse(inst.canRemove(other, edges));
    }
    
    @Test
    public void testCanRemoveInStep() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created.getOutEdges().get(0));
        edges.add(created.getInEdges().get(0));
        
        doReturn(true).when(inst).isInSteps(any(RailEdge.class));
        
        assertFalse(inst.canRemove(player, edges));
        
        verify(inst, times(1)).isInSteps(any(RailEdge.class));
    }
    
    @Test
    public void testCanRemove() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        List<RailEdge> edges = new ArrayList<>();
        edges.add(created.getOutEdges().get(0));
        edges.add(created.getInEdges().get(0));
        
        doReturn(false).when(inst).isInSteps(any(RailEdge.class));
        
        assertTrue(inst.canRemove(player, edges));
        
        verify(inst, times(2)).isInSteps(any(RailEdge.class));
    }
    
    @Test
    public void testRemoveIsolatedRailNodeInvalidNode() throws RushHourException {
        Player player = createPlayer();
        RailNode r1 = inst.create(player, TEST_POS);
        RailNode r2 = inst.create(player, TEST_POS2);
        RailEdge edge = new RailEdge();
        edge.setOwner(player);
        edge.setFrom(r1);
        edge.setTo(r2);
        em.persist(edge);
        em.flush();
        inst.removeIfIsolatedRailNode(r1);
        inst.removeIfIsolatedRailNode(r2);
    }
    
    @Test
    public void testRemoveIsolatedRailNodeStationNode() throws RushHourException {
        Station st = createStation();
        
        inst.removeIfIsolatedRailNode(st.getPlatform().getRailNode());
    }
    
    @Test
    public void testFindEdgeOther() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        try {
            inst.findEdge(createOther(), 
                    created.getInEdges().get(0).getId(),
                    created.getOutEdges().get(0).getId());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testFindEdgeHalfOther() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        
        Player other = createOther();
        RailNode createdOther = inst.create(other, TEST_POS);
        inst.extend(other, createdOther, TEST_POS2);
        em.flush();
        em.refresh(created);
        em.refresh(createdOther);
        
        try {
            inst.findEdge(createOther(), 
                    created.getInEdges().get(0).getId(),
                    createdOther.getOutEdges().get(0).getId());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
        try {
            inst.findEdge(createOther(), 
                    createdOther.getOutEdges().get(0).getId(),
                    created.getInEdges().get(0).getId());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testFindEdgeIndependent() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        
        try {
            inst.findEdge(player, 
                    created.getInEdges().get(0).getId(),
                    created.getInEdges().get(0).getId());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testFindEdgeUnexisted() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_POS);
        inst.extend(player, created, TEST_POS2);
        em.flush();
        em.refresh(created);
        try {
            inst.findEdge(player, 
                    created.getInEdges().get(0).getId(),
                    9999);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testFindNodeIn() {
        assertTrue(inst.findNodeIn(TEST_POS, 6).isEmpty());
    }
}

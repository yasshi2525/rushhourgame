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

import java.util.List;
import java.util.Locale;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.exception.RushHourException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class LineControllerTest extends AbstractControllerTest {

    protected static final String TEST_NAME = "_test";

    protected LineController inst;
    protected static final Pointable TEST_POS = new SimplePoint(10, 10);
    protected static final Pointable TEST_POS2 = new SimplePoint(20, 20);

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = lCon;
        inst.writeLock = spy(inst.writeLock);
        inst.readLock = spy(inst.readLock);
    }

    @Test
    public void testCreate() throws RushHourException {
        Player owner = createPlayer();
        Line created = inst.create(owner, TEST_NAME);
        
        verify(inst.writeLock, times(2)).lock();
        verify(inst.writeLock, times(2)).unlock();
        
        assertNotNull(created);
        assertEquals(owner, created.getOwner());
        assertTrue(created.isOwnedBy(owner));
        assertTrue(created.isPrivilegedBy(owner));
        assertEquals(TEST_NAME, created.getName());
    }

    @Test
    public void testCreateDuplicateName() throws RushHourException {
        Player owner = createPlayer();
        inst.create(owner, TEST_NAME);
        try {
            inst.create(owner, TEST_NAME);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testStart() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Line line = inst.create(owner, TEST_NAME);

        LineStep parent = inst.start(line, owner, st);
        em.flush();
        em.refresh(line);
        em.refresh(parent);
        LineStepDeparture child = parent.getDeparture();

        verify(inst.writeLock, times(3)).lock();
        verify(inst.writeLock, times(3)).unlock();
        
        assertEquals(owner, parent.getOwner());
        assertEquals(line, parent.getParent());
        assertTrue(0 == parent.getDist());
        assertNull(parent.getMoving());
        assertNull(parent.getPassing());
        assertNull(parent.getStopping());
        assertTrue(parent.isOwnedBy(owner));
        assertTrue(parent.isPrivilegedBy(owner));
        assertNull(parent.getNext());
        assertNotNull(child);
        assertEquals(1, line.getSteps().size());
        assertEquals(parent, child.getParent());
        assertEquals(st.getPlatform(), child.getStaying());
    }

    @Test
    public void testStartOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();

        Line line = inst.create(owner, TEST_NAME);

        try {
            inst.start(line, other, st);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    /**
     * 駅しかないとき、nextはない
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testFindNextOnlyStation() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Line line = inst.create(owner, TEST_NAME);

        // lineをINSERTする
        em.flush();

        LineStep current = inst.start(line, owner, st);
        assertTrue(inst.findNext(current, owner).isEmpty());
    }

    /**
     * 駅から1本線路エッジがあるとき、nextが1つ
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testFindNext() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode extend = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        em.flush();
        em.refresh(extend);

        assertEquals(extend.getInEdges().get(0), inst.findNext(current, owner).get(0));
        
        verify(inst.writeLock, times(3)).lock();
        verify(inst.writeLock, times(3)).unlock();
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }

    @Test
    public void testFindNextOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();
        RailNode extend = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        em.flush();
        em.refresh(extend);

        try {
            inst.findNext(current, other);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testFindNextInvalidLineStep() throws RushHourException {
        Player owner = createPlayer();

        // 何もchildをもたないlinestepをつくる
        LineStep invalidLineStep = spy(LineStep.class);
        doReturn(true).when(invalidLineStep).isOwnedBy(owner);

        try {
            inst.findNext(invalidLineStep, owner);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testFindNextInvalidLine() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode extend = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        // lineにchildのいないlinestepを追加する。
        LineStep invalidLineStep = new LineStep();
        invalidLineStep.setParent(line);
        line.getSteps().add(invalidLineStep);
        em.persist(invalidLineStep);
        em.flush();

        try {
            inst.findNext(current, owner);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testExtendMoving() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);

        RailEdge edge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);

        LineStep extended = inst.extend(start, owner, edge);
        
        verify(inst.writeLock, times(4)).lock();
        verify(inst.writeLock, times(4)).unlock();

        em.flush();
        em.refresh(line);

        assertNotNull(extended);
        assertEquals(extended, start.getNext());
        assertEquals(2, line.getSteps().size());

        assertNull(extended.getDeparture());
        assertNotNull(extended.getMoving());
        assertEquals(extended, extended.getMoving().getParent());
        assertTrue(edge.getDist() == extended.getDist());
        assertNull(extended.getStopping());
        assertNull(extended.getPassing());
    }

    @Test
    public void testExtendStopping() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, TEST_POS);
        Station st2 = stCon.create(owner, n2, "_test2");

        em.flush();
        em.refresh(n2);
        RailEdge edge = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edge);

        em.flush();
        em.refresh(line);

        assertNotNull(extended);
        assertEquals(extended, start.getNext());
        assertEquals(2, line.getSteps().size());

        assertNull(extended.getDeparture());
        assertNull(extended.getMoving());
        assertNotNull(extended.getStopping());
        assertEquals(extended, extended.getStopping().getParent());
        assertTrue(edge.getDist() == extended.getDist());
        assertNull(extended.getPassing());
    }

    @Test
    public void testExtendDeparture() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, TEST_POS);
        Station st2 = stCon.create(owner, n2, "_test2");

        em.flush();
        em.refresh(n2);

        RailEdge edgeGo = n2.getInEdges().get(0);
        RailEdge edgeBack = n2.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edgeGo);
        LineStep extended2 = inst.extend(extended, owner, edgeBack);

        em.flush();
        em.refresh(line);

        assertNotNull(extended2);
        assertEquals(extended, start.getNext());
        assertEquals(extended2, extended.getNext().getNext());
        assertEquals(4, line.getSteps().size());

        assertNotNull(extended.getNext().getDeparture());
        assertNull(extended.getNext().getMoving());
        assertNull(extended.getNext().getStopping());
        assertNull(extended.getNext().getPassing());
    }

    @Test
    public void testExtendPassing() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, TEST_POS);
        Station st2 = stCon.create(owner, n2, "_test2");

        em.flush();
        em.refresh(n2);
        RailEdge edge = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edge, true);

        em.flush();
        em.refresh(line);

        assertNotNull(extended);
        assertEquals(2, line.getSteps().size());

        assertEquals(start.getNext(), extended);
        assertNull(extended.getDeparture());
        assertNull(extended.getMoving());
        assertNull(extended.getStopping());
        assertNotNull(extended.getPassing());
        assertEquals(extended, extended.getPassing().getParent());
        assertTrue(edge.getDist() == extended.getDist());
        assertEquals(edge, extended.getPassing().getRunning());
        assertEquals(st2.getPlatform(), extended.getPassing().getGoal());
    }

    @Test
    public void testExtendOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();

        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);

        RailEdge extend = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep base = inst.start(line, owner, st);

        try {
            inst.extend(base, other, extend);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExtendInvalidLineStepOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);

        RailEdge extend = node.getInEdges().get(0);

        Line line = inst.create(other, TEST_NAME);
        LineStep base = inst.start(line, other, st);

        try {
            inst.extend(base, other, extend);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExtendInvalidEdgeOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();

        RailEdge extend = mock(RailEdge.class);
        doReturn(false).when(extend).isOwnedBy(any(Player.class));

        Line line = inst.create(other, TEST_NAME);
        LineStep base = inst.start(line, other, st);

        try {
            inst.extend(base, other, extend);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExtendUnconnectedRailEdge() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();

        RailNode unconnectedNode1 = railCon.create(owner, new SimplePoint(5, 5));
        RailNode unconnectedNode2 = railCon.extend(owner, unconnectedNode1, new SimplePoint(8, 8));

        em.flush();
        em.refresh(unconnectedNode2);
        RailEdge unconnectedEdge = unconnectedNode2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep base = inst.start(line, owner, st);

        try {
            inst.extend(base, owner, unconnectedEdge);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testExtendNextRegisterted() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge edge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);

        // startにnextをセットする
        inst.extend(start, owner, edge);

        try {
            inst.extend(start, owner, edge);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testInsertOtherFrom() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode n1 = railCon.create(other, TEST_POS);

        try {
            inst.insert(n1, n1, player);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testInsertOtherTo() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode n1 = railCon.create(player, TEST_POS);
        RailNode n2 = railCon.create(other, TEST_POS);

        try {
            inst.insert(n1, n2, player);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testInsertUnconnected() throws RushHourException {
        Player player = createPlayer();
        RailNode n1 = railCon.create(player, TEST_POS);
        RailNode n2 = railCon.create(player, TEST_POS2);

        try {
            inst.insert(n1, n2, player);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testInsertEmpty() throws RushHourException {
        Player player = createPlayer();
        RailNode n1 = railCon.create(player, TEST_POS);
        RailNode n2 = railCon.extend(player, n1, TEST_POS2);

        try {
            inst.insert(n1, n2, player);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
        
        verify(inst.writeLock, times(1)).lock();
        verify(inst.writeLock, times(1)).unlock();
    }

    /**
     * DepartureからDepartureはつなげられない.
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndOnlyStation() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);

        assertNotNull(start.getDeparture());
        assertFalse(inst.canEnd(start, owner));
        
        verify(inst.writeLock, times(3)).lock();
        verify(inst.writeLock, times(3)).unlock();
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }

    /**
     * MovingからDepartureはつなげられない.
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndMovingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge edge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep tail = inst.extend(start, owner, edge);

        assertNotNull(tail.getMoving());
        assertFalse(inst.canEnd(tail, owner));
    }

    /**
     * PassingからDepartureはつなげられない.
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndPassingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);
        RailEdge backEdge = node.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep next = inst.extend(start, owner, goEdge);
        LineStep tail = inst.extend(next, owner, backEdge, true);

        assertNotNull(tail.getPassing());
        assertFalse(inst.canEnd(tail, owner));
    }

    /**
     * StoppingからDepartureはつなげられる.
     *
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndStoppingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);
        RailEdge backEdge = node.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep next = inst.extend(start, owner, goEdge);
        LineStep tail = inst.extend(next, owner, backEdge);

        assertNotNull(tail.getStopping());
        assertTrue(inst.canEnd(tail, owner));
    }

    @Test
    public void testCanEndUnconnected() throws RushHourException {
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode node = railCon.extend(owner, st1.getPlatform().getRailNode(), TEST_POS);
        Station st2 = stCon.create(owner, node, "_test2");
        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep tail = inst.extend(start, owner, goEdge);

        assertNotNull(tail.getStopping());
        assertFalse(inst.canEnd(tail, owner));
    }

    @Test
    public void testCanEndOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        Player other = createOther();

        try {
            inst.canEnd(start, other);
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testCanEndHavingNext() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node2 = railCon.extend(owner, st.getPlatform().getRailNode(), new SimplePoint(21.0, 5.0));
        em.flush();
        em.refresh(node2);
        RailEdge goEdge = node2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep step1 = inst.start(line, owner, st);
        LineStep step2 = inst.extend(step1, owner, goEdge);

        try {
            inst.canEnd(step1, owner);
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testEnd() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);
        RailEdge backEdge = node.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep next = inst.extend(start, owner, goEdge);
        LineStep tail = inst.extend(next, owner, backEdge);

        assertNotNull(tail.getStopping());
        assertTrue(inst.canEnd(tail, owner));
        assertFalse(line.isCompleted());

        inst.end(tail, owner);

        assertEquals(start, tail.getNext());
        assertTrue(line.isCompleted());
        
        verify(inst.writeLock, times(6)).lock();
        verify(inst.writeLock, times(6)).unlock();
    }

    @Test
    public void testEndOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        Player other = createOther();

        try {
            inst.end(start, other);
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testEndHavingNext() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node2 = railCon.extend(owner, st.getPlatform().getRailNode(), new SimplePoint(21.0, 5.0));
        em.flush();
        em.refresh(node2);
        RailEdge goEdge = node2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep step1 = inst.start(line, owner, st);
        LineStep step2 = inst.extend(step1, owner, goEdge);

        try {
            inst.end(step1, owner);
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testEndUnconnected() throws RushHourException {
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode node = railCon.extend(owner, st1.getPlatform().getRailNode(), TEST_POS);
        Station st2 = stCon.create(owner, node, "_test2");
        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep tail = inst.extend(start, owner, goEdge);

        assertNotNull(tail.getStopping());
        assertFalse(inst.canEnd(tail, owner));

        try {
            inst.end(tail, owner);
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testEndTwoStation() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, TEST_POS);
        Station st2 = stCon.create(owner, n2, "_test2");

        em.flush();
        em.refresh(n2);

        RailEdge edgeGo = n2.getInEdges().get(0);
        RailEdge edgeBack = n2.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edgeGo);
        LineStep extended2 = inst.extend(extended, owner, edgeBack);

        em.flush();
        em.refresh(line);

        inst.end(extended2, owner);

        List<StepForHumanThroughTrain> throughs = sCon.findThroughTrainAll();
        assertEquals(2, throughs.size());

        // 徒歩よりコストが安い
        StepForHumanThroughTrain through = throughs.get(0);
        assertTrue(st2.distTo(st1) > through.getCost());
        assertEquals(line, through.getLine());
        assertNotEquals(through.getTo(), through.getFrom());
        // st1かst2は不定？
        assertTrue(through.getUid().startsWith("train"));
    }

    @Test
    public void testEndDoubleLine() throws RushHourException {
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, TEST_POS);

        em.flush();
        em.refresh(n2);

        RailEdge edgeGo1 = n2.getInEdges().get(0);
        RailEdge edgeBack1 = n2.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start1 = inst.start(line, owner, st1);
        LineStep extended1 = inst.extend(start1, owner, edgeGo1);
        LineStep extended2 = inst.extend(extended1, owner, edgeBack1);
        inst.end(extended2, owner);

        RailNode n3 = railCon.create(owner, TEST_POS2);
        RailNode n4 = railCon.extend(owner, n3, new SimplePoint(100, 100));

        em.flush();
        em.refresh(n4);

        RailEdge edgeGo2 = n4.getInEdges().get(0);
        RailEdge edgeBack2 = n4.getOutEdges().get(0);

        Station st2 = stCon.create(owner, n3, "test2");

        try {
            LineStep start2 = inst.start(line, owner, st2);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testRemovePlatformOtherOwner() throws RushHourException {
        Station st = createStation();
        Player other = createOther();
        
        try {
            inst.remove(st.getPlatform(), other);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testRemovePlatformStopped() throws RushHourException {
        Player owner = createPlayer();
        AssistanceController.Result res = aCon.startWithStation(owner, TEST_POS, Locale.JAPANESE);
        aCon.extendWithStation(owner, res.node, TEST_POS2, Locale.JAPANESE);
        
        inst.remove(res.station.getPlatform(), owner);
        
        assertEquals(3, res.line.getSteps().size());
    }
    
    @Test
    public void testRemovePlatformPassed() throws RushHourException {
        Player owner = createPlayer();
        
        RailNode r1 = railCon.create(owner, TEST_POS);

        RailNode r2 = railCon.extend(owner, r1, TEST_POS2);
        em.refresh(r1);
        RailEdge e1 = r1.getOutEdges().get(0);
        RailEdge e2 = r1.getInEdges().get(0);
        
        Station st1 = stCon.create(owner, r1, TEST_NAME);
        Station st2 = stCon.create(owner, r2, TEST_NAME + "2");
        
        Line line = inst.create(owner, TEST_NAME);
        LineStep dpt = inst.start(line, owner, st1);
        LineStep pass = inst.extend(dpt, owner, e1, true);
        LineStep tail = inst.extend(pass, owner, e2);
        
        inst.end(tail, owner);
        
        inst.remove(st2.getPlatform(), owner);
        
        assertEquals(3, line.getSteps().size());
    }
    
    @Test
    public void testRemoveLonelyPlatform() throws RushHourException {
        Player owner = createPlayer();
        AssistanceController.Result res = aCon.startWithStation(owner, TEST_POS, Locale.JAPANESE);
        
        inst.remove(res.station.getPlatform(), owner);
        
        assertTrue(res.line.getSteps().isEmpty());
    }
    
    @Test
    public void testRemovePlatformWithOnlyOneStationLine() throws RushHourException {
        Player owner = createPlayer();
        AssistanceController.Result res = aCon.startWithStation(owner, TEST_POS, Locale.JAPANESE);
        aCon.extend(owner, res.node, TEST_POS2);
        
        inst.remove(res.station.getPlatform(), owner);
        
        assertEquals(2, res.line.getSteps().size());
    }
    

    @Test
    public void testIsAreaIn() throws RushHourException {
        Station st1 = createStation();
        Player owner = st1.getOwner();

        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, new SimplePoint(100, 100));

        em.flush();
        em.refresh(n2);

        RailEdge edgeGo = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extend = inst.extend(start, owner, edgeGo);

        assertTrue(extend.isAreaIn(new SimplePoint(0, 0), 2));
        assertTrue(extend.isAreaIn(new SimplePoint(100, 100), 2));
        assertFalse(extend.isAreaIn(new SimplePoint(50, 50), 2));
    }

    @Test
    public void testFindIn() throws RushHourException {
        Station st1 = createStation();
        Player owner = st1.getOwner();

        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = railCon.extend(owner, n1, new SimplePoint(100, 100));

        em.flush();
        em.refresh(n2);

        RailEdge edgeGo = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extend = inst.extend(start, owner, edgeGo);

        em.flush();
        List<Line> scopedLine = inst.findIn(new SimplePoint(0, 0), 2);
        assertEquals(1, scopedLine.size());
        assertEquals(2, scopedLine.get(0).getSteps().size());

        scopedLine = inst.findIn(new SimplePoint(100, 100), 2);
        assertEquals(1, scopedLine.size());
        assertEquals(2, scopedLine.get(0).getSteps().size());

        assertTrue(inst.findIn(new SimplePoint(50, 50), 2).isEmpty());

        assertEquals(1, inst.findAll().size());
        assertEquals(2, inst.findAll().get(0).getSteps().size());
    }
    
    @Test
    public void testFindTopAlreadyCompleted() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = railCon.extend(owner, st.getPlatform().getRailNode(), TEST_POS);

        em.flush();
        em.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);
        RailEdge backEdge = node.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep next = inst.extend(start, owner, goEdge);
        LineStep tail = inst.extend(next, owner, backEdge);

        inst.end(tail, owner);

        try {
            inst.findTop(line);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testAutocreateNotEnd() throws RushHourException {
        Station st1 = createStation();
        Line line = inst.autocreate(st1.getOwner(), st1, TEST_NAME);
        
        verify(inst.writeLock, times(4)).lock();
        verify(inst.writeLock, times(4)).unlock();
        
        assertFalse(line.isCompleted());
    }
    
    @Test
    public void testFindNull() {
        assertNull(inst.find((LineStep)null));
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }

    @Test
    public void testInheritEntity() throws RushHourException {
        Station station = createStation();
        Player owner = station.getOwner();
        RailNode start = station.getPlatform().getRailNode();
        RailNode extended = railCon.extend(owner, start, TEST_POS);
        Line line = inst.create(station.getOwner(), TEST_NAME);
        inst.start(line, owner, station);
        inst.insert(start, extended, owner);

        inst.synchronizeDatabase();
        inst.entities = null;
        inst.synchronizeDatabase();

        assertEquals(3, inst.findAll().get(0).getSteps().size());
    }
}

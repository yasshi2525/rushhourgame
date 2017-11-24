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

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.controller.AbstractControllerTest.createPlayer;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.exception.RushHourException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class LineControllerTest extends AbstractControllerTest {

    protected static final String TEST_NAME = "_test";

    protected LineController inst;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createLineController();
    }

    @Test
    public void testCreate() throws RushHourException {
        Player owner = createPlayer();
        Line created = inst.create(owner, TEST_NAME);
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
        EM.flush();
        EM.refresh(line);
        EM.refresh(parent);
        LineStepDeparture child = parent.getDeparture();

        assertEquals(owner, parent.getOwner());
        assertEquals(line, parent.getParent());
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
        EM.flush();

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
        RailNode extend = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        EM.flush();
        EM.refresh(extend);

        assertEquals(extend.getInEdges().get(0), inst.findNext(current, owner).get(0));
    }

    @Test
    public void testFindNextOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();
        RailNode extend = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        EM.flush();
        EM.refresh(extend);

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
        RailNode extend = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        Line line = inst.create(owner, TEST_NAME);
        LineStep current = inst.start(line, owner, st);

        // lineにchildのいないlinestepを追加する。
        LineStep invalidLineStep = new LineStep();
        invalidLineStep.setParent(line);
        EM.persist(invalidLineStep);
        EM.flush();

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
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);

        RailEdge edge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);

        LineStep extended = inst.extend(start, owner, edge);

        EM.flush();
        EM.refresh(line);

        assertNotNull(extended);
        assertEquals(extended, start.getNext());
        assertEquals(2, line.getSteps().size());

        assertNull(extended.getDeparture());
        assertNotNull(extended.getMoving());
        assertNull(extended.getStopping());
        assertNull(extended.getPassing());
    }

    @Test
    public void testExtendStopping() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = RAILCON.extend(owner, n1, 10, 10);
        Station st2 = STCON.create(owner, n2, "_test2");

        EM.flush();
        EM.refresh(n2);
        RailEdge edge = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edge);

        EM.flush();
        EM.refresh(line);

        assertNotNull(extended);
        assertEquals(extended, start.getNext());
        assertEquals(2, line.getSteps().size());

        assertNull(extended.getDeparture());
        assertNull(extended.getMoving());
        assertNotNull(extended.getStopping());
        assertNull(extended.getPassing());
    }

    @Test
    public void testExtendDeparture() throws RushHourException {
        // 駅 - 線路 - 駅
        Station st1 = createStation();
        Player owner = st1.getOwner();
        RailNode n1 = st1.getPlatform().getRailNode();
        RailNode n2 = RAILCON.extend(owner, n1, 10, 10);
        Station st2 = STCON.create(owner, n2, "_test2");

        EM.flush();
        EM.refresh(n2);

        RailEdge edgeGo = n2.getInEdges().get(0);
        RailEdge edgeBack = n2.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edgeGo);
        LineStep extended2 = inst.extend(extended, owner, edgeBack);

        EM.flush();
        EM.refresh(line);

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
        RailNode n2 = RAILCON.extend(owner, n1, 10, 10);
        Station st2 = STCON.create(owner, n2, "_test2");

        EM.flush();
        EM.refresh(n2);
        RailEdge edge = n2.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st1);
        LineStep extended = inst.extend(start, owner, edge, true);

        EM.flush();
        EM.refresh(line);

        assertNotNull(extended);
        assertEquals(2, line.getSteps().size());

        assertEquals(start.getNext(), extended);
        assertNull(extended.getDeparture());
        assertNull(extended.getMoving());
        assertNull(extended.getStopping());
        assertNotNull(extended.getPassing());
    }

    @Test
    public void testExtendOtherOwner() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        Player other = createOther();

        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);

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
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);

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

        RailNode unconnectedNode1 = RAILCON.create(owner, 5, 5);
        RailNode unconnectedNode2 = RAILCON.extend(owner, unconnectedNode1, 8, 8);

        EM.flush();
        EM.refresh(unconnectedNode2);
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
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);
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
    
    /**
     * DepartureからDepartureはつなげられない.
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
    }
    
    /**
     * MovingからDepartureはつなげられない.
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndMovingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);
        RailEdge edge = node.getInEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep tail = inst.extend(start, owner, edge);
        
        assertNotNull(tail.getMoving());
        assertFalse(inst.canEnd(tail, owner));
    }
    
    /**
     * PassingからDepartureはつなげられない.
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndPassingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);
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
     * @throws RushHourException 例外
     */
    @Test
    public void testCanEndStoppingToDeparture() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);
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
        RailNode node = RAILCON.extend(owner, st1.getPlatform().getRailNode(), 10, 10);
        Station st2 = STCON.create(owner, node, "_test2");
        EM.flush();
        EM.refresh(node);
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
        RailNode node2 = RAILCON.extend(owner, st.getPlatform().getRailNode(), 21.0, 5.0);
        EM.flush();
        EM.refresh(node2);
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
        RailNode node = RAILCON.extend(owner, st.getPlatform().getRailNode(), 10, 10);

        EM.flush();
        EM.refresh(node);
        RailEdge goEdge = node.getInEdges().get(0);
        RailEdge backEdge = node.getOutEdges().get(0);

        Line line = inst.create(owner, TEST_NAME);
        LineStep start = inst.start(line, owner, st);
        LineStep next = inst.extend(start, owner, goEdge);
        LineStep tail = inst.extend(next, owner, backEdge);
        
        assertNotNull(tail.getStopping());
        assertTrue(inst.canEnd(tail, owner));
        assertTrue(EM.createNamedQuery("Line.isImcompleted", Number.class)
                .setParameter("line", line)
                .getSingleResult().longValue() == 1L);
        
        inst.end(tail, owner);
        
        assertEquals(start, tail.getNext());
        assertFalse(EM.createNamedQuery("Line.isImcompleted", Number.class)
                .setParameter("line", line)
                .getSingleResult().longValue() == 1L);
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
        RailNode node2 = RAILCON.extend(owner, st.getPlatform().getRailNode(), 21.0, 5.0);
        EM.flush();
        EM.refresh(node2);
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
        RailNode node = RAILCON.extend(owner, st1.getPlatform().getRailNode(), 10, 10);
        Station st2 = STCON.create(owner, node, "_test2");
        EM.flush();
        EM.refresh(node);
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
}

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
}

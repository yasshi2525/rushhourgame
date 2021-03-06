/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.controller.AssistanceController.Result;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Nameable;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AssistanceControllerTest extends AbstractControllerTest {

    protected AssistanceController inst;
    protected Player player;
    protected static final Pointable ORGIN = new SimplePoint(10, 10);
    protected static final Pointable EXTENDED = new SimplePoint(20, 20);
    protected static final Pointable EXTENDED2 = new SimplePoint(5, 30);
    protected static final Pointable EXTENDED3 = new SimplePoint(40, 15);

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = aCon;
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(AssistanceControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testStartWithStation() throws Exception {
        inst.writeLock = spy(inst.writeLock);
        
        Result res = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        
        verify(inst.writeLock,times(1)).lock();
        verify(inst.writeLock,times(1)).unlock();
        
        em.flush();
        em.refresh(res.node);

        assertTrue(res.node.distTo(ORGIN) == 0);
        assertEquals("駅1", res.station.getName());
        Line line = res.line;
        em.refresh(line);
        assertEquals("路線1", line.getName());
        assertEquals(1, line.getSteps().size());
    }

    @Test
    public void testGetDefaultNameEmpty() {
        String name = inst.getDefaultName(new ArrayList<>(), "hoge");
        assertEquals("hoge1", name);
    }

    @Test
    public void testGetDefaultNameUnique() throws RushHourException {
        List<Nameable> list = new ArrayList<>();
        Nameable obj = mock(Nameable.class);
        list.add(obj);
        doReturn("unique").when(obj).getName();

        String name = inst.getDefaultName(list, "hoge");
        assertEquals("hoge1", name);
    }

    @Test
    public void testGetDefaultStationNameDuplicated() throws RushHourException {
        List<Nameable> list = new ArrayList<>();
        Nameable obj = mock(Nameable.class);
        list.add(obj);
        doReturn("hoge1").when(obj).getName();

        String name = inst.getDefaultName(list, "hoge");
        assertEquals("hoge2", name);
    }

    @Test
    public void testExtend() throws RushHourException {
        inst.writeLock = spy(inst.writeLock);
        
        // start -- goal        
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result goal = inst.extend(player, start.node, EXTENDED);
        
        verify(inst.writeLock,times(2)).lock();
        verify(inst.writeLock,times(2)).unlock();

        Line line = start.line;
        assertTrue(line.isCompleted());

        em.refresh(line);

        LineStep dep = findTop(line);
        assertDeparture(dep, start.node);

        // start -> goal
        LineStep moving = dep.getNext();
        assertMoving(moving, start.node, goal.node);

        // start <- goal
        LineStep stop = moving.getNext();
        assertStopping(stop, goal.node, start.node);

        assertEquals(dep, stop.getNext());
    }

    @Test
    public void testExtendForkV() throws RushHourException {
        // n2 - start - n1
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result n1 = inst.extend(player, start.node, EXTENDED);
        Result n2 = inst.extend(player, start.node, EXTENDED2);

        Line line = start.line;
        assertTrue(line.isCompleted());

        em.refresh(line);

        // n2 -- [start] -- n1        
        LineStep dep2 = findTop(line);
        assertDeparture(dep2, start.node);

        // n2 <- start -- n1
        LineStep moving2 = dep2.getNext();
        assertMoving(moving2, start.node, n2.node);

        // n2 -> start -- n1
        LineStep stop2 = moving2.getNext();
        assertStopping(stop2, n2.node, start.node);

        // n2 - [start] -- n1
        LineStep dep1 = stop2.getNext();
        assertDeparture(dep1, start.node);

        // n2 -- start -> n1
        LineStep moving1 = dep1.getNext();
        assertMoving(moving1, start.node, n1.node);

        // n2 -- start <- n1
        LineStep stop1 = moving1.getNext();
        assertStopping(stop1, n1.node, start.node);

        assertEquals(dep2, stop1.getNext());
    }

    @Test
    public void testExtendForkI() throws RushHourException {
        // start - n1 - n2
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result n1 = inst.extend(player, start.node, EXTENDED);
        Result n2 = inst.extend(player, n1.node, EXTENDED2);
        em.flush();

        assertEquals(1, lCon.findAll(player).size());
        Line line = lCon.findAll(player).get(0);
        assertTrue(line.isCompleted());

        em.refresh(line);

        //[start]-- n1 -- n2        
        LineStep top = findTop(line);
        assertDeparture(top, start.node);

        // start -> n1 -- n2
        LineStep step = top.getNext();
        assertMoving(step, start.node, n1.node);

        // start -- n1 -> n2
        step = step.getNext();
        assertMoving(step, n1.node, n2.node);

        // start -- n1 <- n2
        step = step.getNext();
        assertMoving(step, n2.node, n1.node);

        // start <- n1 -- n2
        step = step.getNext();
        assertStopping(step, n1.node, start.node);

        assertEquals(top, step.getNext());
    }

    @Test
    public void testExtendForkY() throws RushHourException {
        //         n3
        //         |
        // start - n1 - n2
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result n1 = inst.extend(player, start.node, EXTENDED);
        Result n2 = inst.extend(player, n1.node, EXTENDED2);
        Result n3 = inst.extend(player, n1.node, EXTENDED3);

        Line line = start.line;
        assertTrue(line.isCompleted());

        em.refresh(line);
        // start -> n1 -> n3 -> n1 -> n2 -> n1 -> start
        LineStep top = findTop(line);
        assertDeparture(top, start.node);
        LineStep step = top.getNext();
        assertMoving(step, start.node, n1.node);
        assertMoving(step = step.getNext(), n1.node, n3.node);
        assertMoving(step = step.getNext(), n3.node, n1.node);
        assertMoving(step = step.getNext(), n1.node, n2.node);
        assertMoving(step = step.getNext(), n2.node, n1.node);
        assertStopping(step = step.getNext(), n1.node, start.node);

        assertEquals(top, step.getNext());
    }

    @Test
    public void testExtendForkT() throws RushHourException {
        //       n3
        //        |
        // n2 - start - n1
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result n1 = inst.extend(player, start.node, EXTENDED);
        Result n2 = inst.extend(player, start.node, EXTENDED2);
        Result n3 = inst.extend(player, start.node, EXTENDED3);

        Line line = start.line;
        assertTrue(line.isCompleted());

        em.refresh(line);

        // start -> n3 -> start -> n2 -> start -> n1 -> start
        LineStep top = findTop(line);
        assertDeparture(top, start.node);
        LineStep step = top.getNext();
        assertMoving(step, start.node, n3.node);
        assertStopping(step = step.getNext(), n3.node, start.node);

        assertDeparture(step = step.getNext(), start.node);
        assertMoving(step = step.getNext(), start.node, n2.node);
        assertStopping(step = step.getNext(), n2.node, start.node);

        assertDeparture(step = step.getNext(), start.node);
        assertMoving(step = step.getNext(), start.node, n1.node);
        assertStopping(step = step.getNext(), n1.node, start.node);

        assertEquals(top, step.getNext());
    }

    @Test
    public void testExtendWithLineController() throws RushHourException {
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        RailNode n2 = railCon.extend(player, start.node, EXTENDED);
        em.flush();
        em.refresh(n2);
        em.refresh(start.line);
        lCon.extend(start.line.findTop(), player, n2.getInEdges().get(0));
        inst.extend(player, n2, EXTENDED2);
    }

    @Test
    public void testExtendToCompletedLine() throws RushHourException {
        // before : st -- st
        // after  : st -- st -- st
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result extended = inst.extendWithStation(player, start.node, EXTENDED, Locale.JAPANESE);
        em.flush();
        
        assertEquals(2, tableCon.findAll("StepForHumanThroughTrain", StepForHumanThroughTrain.class).size());
        
        Result extended2 = inst.extendWithStation(player, extended.node, EXTENDED2, Locale.JAPANESE);
        em.flush();
        
        // 古いのが残っていない
        assertEquals(6, tableCon.findAll("StepForHumanThroughTrain", StepForHumanThroughTrain.class).size());
    }

    protected static LineStep findTop(Line line) {
        return line.getSteps().stream()
                .filter(step -> step.getDeparture() != null)
                .min((s1, s2) -> (int) (s1.getId() - s2.getId())).get();
    }

    protected static void assertDeparture(LineStep step, RailNode on) {
        assertNotNull(step.getDeparture());
        assertEquals(on, step.getDeparture().getStaying().getRailNode());
    }

    protected void assertMoving(LineStep step, RailNode from, RailNode to) {
        from = em.merge(from);
        to = em.merge(to);
        RailEdge edge = em.merge(step.getMoving().getRunning());
        assertNotNull(step.getMoving());
        assertEquals(from, edge.getFrom());
        assertEquals(to, edge.getTo());
    }

    protected void assertStopping(LineStep step, RailNode from, RailNode to) {
        from = em.merge(from);
        to = em.merge(to);
        RailEdge edge = em.merge(step.getStopping().getRunning());
        assertNotNull(step.getStopping());
        assertEquals(from, edge.getFrom());
        assertEquals(to, edge.getTo());
        assertEquals(to, step.getStopping().getGoal().getRailNode());
    }
}

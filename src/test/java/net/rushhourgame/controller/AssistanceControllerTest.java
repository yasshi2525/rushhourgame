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
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
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
        inst = ControllerFactory.createAssistanceController();
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(AssistanceControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testStartWithStation() throws Exception {
        Result res = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        EM.flush();
        EM.refresh(res.node);

        assertTrue(res.node.distTo(ORGIN) == 0);
        assertEquals("駅1", res.node.getPlatform().getStation().getName());
        Line line = res.line;
        EM.refresh(line);
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
        // start -- goal        
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result goal = inst.extend(player, start.node, EXTENDED);

        Line line = start.line;
        assertTrue(LCON.isCompleted(line));

        EM.refresh(line);

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
        assertTrue(LCON.isCompleted(line));

        EM.refresh(line);

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
        EM.flush();

        assertEquals(1, LCON.findAll(player).size());
        Line line = LCON.findAll(player).get(0);
        assertTrue(LCON.isCompleted(line));

        EM.refresh(line);

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
        assertTrue(LCON.isCompleted(line));

        EM.refresh(line);
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
        assertTrue(LCON.isCompleted(line));

        EM.refresh(line);

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
        RailNode n2 = RAILCON.extend(player, start.node, EXTENDED);
        EM.flush();
        EM.refresh(n2);
        EM.refresh(start.line);
        LCON.extend(start.line.findTop(), player, n2.getInEdges().get(0));
        inst.extend(player, n2, EXTENDED2);
    }

    @Test
    public void testExtendToCompletedLine() throws RushHourException {
        // before : st -- st
        // after  : st -- st -- st
        Result start = inst.startWithStation(player, ORGIN, Locale.JAPANESE);
        Result extended = inst.extendWithStation(player, start.node, EXTENDED, Locale.JAPANESE);
        EM.flush();
        
        assertEquals(2, TCON.findAll("StepForHumanThroughTrain", StepForHumanThroughTrain.class).size());
        
        Result extended2 = inst.extendWithStation(player, extended.node, EXTENDED2, Locale.JAPANESE);
        EM.flush();
        
        // 古いのが残っていない
        assertEquals(6, TCON.findAll("StepForHumanThroughTrain", StepForHumanThroughTrain.class).size());
    }

    protected static LineStep findTop(Line line) {
        return line.getSteps().stream()
                .filter(step -> step.getDeparture() != null)
                .min((s1, s2) -> (int) (s1.getId() - s2.getId())).get();
    }

    protected static void assertDeparture(LineStep step, RailNode on) {
        assertNotNull(step.getDeparture());
        assertEquals(on.getPlatform(), step.getDeparture().getStaying());
    }

    protected static void assertMoving(LineStep step, RailNode from, RailNode to) {
        from = EM.merge(from);
        to = EM.merge(to);
        RailEdge edge = EM.merge(step.getMoving().getRunning());
        assertNotNull(step.getMoving());
        assertEquals(from, edge.getFrom());
        assertEquals(to, edge.getTo());
    }

    protected static void assertStopping(LineStep step, RailNode from, RailNode to) {
        from = EM.merge(from);
        to = EM.merge(to);
        RailEdge edge = EM.merge(step.getStopping().getRunning());
        assertNotNull(step.getStopping());
        assertEquals(from, edge.getFrom());
        assertEquals(to, edge.getTo());
        assertEquals(to.getPlatform(), step.getStopping().getGoal());
    }
}

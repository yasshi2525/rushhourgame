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
package net.rushhourgame.entity;

import java.util.ArrayList;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepMoving;
import net.rushhourgame.entity.troute.LineStepPassing;
import net.rushhourgame.entity.troute.LineStepStopping;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TrainDeployedTest extends AbstractEntityTest {
    @Spy
    protected TrainDeployed inst;
    
    @Spy
    protected LineStep dep;
    @Spy
    protected LineStep stop;
    @Spy
    protected LineStep pass;
    @Spy
    protected LineStep moving;
    @Spy
    protected Train train;
    @Mock
    protected LineStepDeparture _dep;
    @Mock
    protected LineStepStopping _stop;
    @Mock
    protected LineStepPassing _pass;
    @Mock
    protected LineStepMoving _moving;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.train = train;
        doReturn(_dep).when(dep).getDeparture();
        doReturn(_stop).when(stop).getStopping();
        doReturn(_pass).when(pass).getPassing();
        doReturn(_moving).when(moving).getMoving();
    }
    
    @Test
    public void testGetTrain() {
        inst.setTrain(train);
        assertEquals(train, inst.getTrain());
    }
    
    @Test
    public void testSetCurrent() {
        LineStep step = mock(LineStep.class);
        doNothing().when(inst).registerPoint();
        inst.setCurrent(step);
        assertEquals(step, inst.getCurrent());
        assertTrue(0.0 == inst.process);
    }

    @Test
    public void testConsumeTimeDoNothing() {
        inst.consumeTime(0.0);
    }
    
    @Test
    public void testConsumeTimeRunning() {
        doReturn(true).when(inst).shouldRun();
        doReturn(0.5).when(inst).consumeTimeByRunning(anyDouble());
        doReturn(false).when(inst).shouldShiftStep();
        doReturn(false).when(inst).shouldStay();
        
        inst.consumeTime(1.0);
        
        verify(inst, times(2)).consumeTimeByRunning(anyDouble());
    }
    
    @Test
    public void testConsumeTimeStaying() {
        doReturn(false).when(inst).shouldRun();
        doReturn(false).when(inst).shouldShiftStep();
        doReturn(true).when(inst).shouldStay();
        doReturn(0.5).when(inst).consumeTimeByStaying(anyDouble());
        
        inst.consumeTime(1.0);
        
        verify(inst, times(2)).consumeTimeByStaying(anyDouble());
    }
    
    @Test
    public void testConsumeTimeShiftStep() {
        doReturn(true).when(inst).shouldRun();
        doReturn(0.5).when(inst).consumeTimeByRunning(anyDouble());
        doReturn(true).when(inst).shouldShiftStep();
        doReturn(true).when(inst).shouldStay();
        doReturn(0.5).when(inst).consumeTimeByStaying(anyDouble());
        doNothing().when(inst).shiftStep();
        
        inst.consumeTime(1.0);
        
        verify(inst, times(2)).shiftStep();
    }


    @Test
    public void testShouldRun() {
        inst.current = dep;
        assertFalse(inst.shouldRun());
        
        inst.current = moving;
        assertTrue(inst.shouldRun());
        
        inst.current = stop;
        assertTrue(inst.shouldRun());
        
        inst.current = pass;
        assertTrue(inst.shouldRun());
    }

    @Test
    public void testShouldStay() {
        inst.current = dep;
        assertTrue(inst.shouldStay());
        
        inst.current = moving;
        assertFalse(inst.shouldStay());
        
        inst.current = stop;
        assertFalse(inst.shouldStay());
        
        inst.current = pass;
        assertFalse(inst.shouldStay());
    }

    @Test
    public void testConsumeTimeByRunning() {
        inst.current = moving;
        doReturn(10.0).when(inst).calcMovableDist(anyDouble());
        doReturn(5.0).when(inst).calcRemainDist();
        doReturn(1.0).when(inst).run(anyDouble());
        inst.consumeTimeByRunning(1.0);
        verify(inst, times(1)).run(5.0);
    }

    @Test
    public void testCalcRemainDist() {
        inst.current = moving;
        doReturn(10.0).when(moving).getDist();
        inst.process = 1.0;
        assertTrue(0.0 ==inst.calcRemainDist());
        
        inst.process = 0.0;
        assertTrue(10.0 ==inst.calcRemainDist());
        
        inst.process = 0.5;
        assertTrue(5.0 ==inst.calcRemainDist());
    }

    @Test
    public void testCalcMovableDist() {
        doReturn(0.0).when(train).getSpeed();
        assertTrue(0.0 == inst.calcMovableDist(0.0));
        assertTrue(0.0 == inst.calcMovableDist(1.0));
        
        doReturn(10.0).when(train).getSpeed();
        assertTrue(0.0 == inst.calcMovableDist(0.0));
        assertTrue(10.0 == inst.calcMovableDist(1.0));
        assertTrue(100.0 == inst.calcMovableDist(10.0));
    }

    @Test
    public void testRun() {
        inst.current = moving;
        doReturn(10.0).when(moving).getDist();
        doReturn(10.0).when(train).getSpeed();
        doNothing().when(inst).registerPoint();
        
        assertTrue(0.0 == inst.run(0));
        assertTrue(0.0 == inst.process);
    }

    @Test
    public void testConsumeTimeByStaying() {
        doReturn(0.5).when(inst).calcStayableTime();
        inst.consumeTimeByStaying(1.0);
        
        verify(inst, times(1)).stay(0.5);
    }

    @Test
    public void testCalcStayableTime() {
        doReturn(10.0).when(train).getMobility();
        
        inst.process = 0.0;
        assertTrue(10.0 == inst.calcStayableTime());
        
        inst.process = 0.5;
        assertTrue(5.0 == inst.calcStayableTime());
        
        inst.process = 1.0;
        assertTrue(0.0 == inst.calcStayableTime());
    }

    @Test
    public void testStay() {
        inst.process = 0.0;
        doReturn(10.0).when(train).getMobility();
        
        assertTrue(0.0 == inst.stay(0.0));
        
        assertTrue(0.0 == inst.process);
        
        assertTrue(10.0 == inst.stay(10.0));
        assertTrue(1.0 == inst.process);
    }

    @Test
    public void testRegisterPoint_0args() {
        inst.current = moving;
        RailNode start = mock(RailNode.class);
        RailNode goal = mock(RailNode.class);
        doReturn(start).when(moving).getStartRailNode();
        doReturn(goal).when(moving).getGoalRailNode();
        doReturn(20.0).when(start).getX();
        doReturn(40.0).when(goal).getX();
        doReturn(20.0).when(start).getY();
        doReturn(40.0).when(goal).getY();
        
        inst.process = 0.0;
        inst.registerPoint();
        assertTrue(20.0 == inst.getX());
        assertTrue(20.0 == inst.getY());
        
        inst.process = 0.5;
        inst.registerPoint();
        assertTrue(30.0 == inst.getX());
        assertTrue(30.0 == inst.getY());
        
        inst.process = 1.0;
        inst.registerPoint();
        assertTrue(40.0 == inst.getX());
        assertTrue(40.0 == inst.getY());
    }

    @Test
    public void testRegisterPoint_double_double() {
        inst.registerPoint(10.0, 10.0);
        assertTrue(10.0 == inst.getX());
        assertTrue(10.0 == inst.getY());
    }

    @Test
    public void testShouldShiftStep() {
        assertFalse(inst.shouldShiftStep());
        
        inst.process = 1.0;
        assertTrue(inst.shouldShiftStep());
    }

    @Test
    public void testShiftStep() {
        inst.current = moving;
        doReturn(stop).when(moving).getNext();
        
        inst.shiftStep();
        assertEquals(stop, inst.current);
        assertTrue(0.0 == inst.process);
    }

    @Test
    public void testFreeHuman() {
        inst.freeHuman(new ArrayList<>());
    }

    @Test
    public void testCollectHuman() {
        inst.collectHuman(new ArrayList<>());
    }
    
}

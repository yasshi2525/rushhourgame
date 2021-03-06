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
import java.util.List;
import javax.persistence.EntityManager;
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
        doNothing().when(inst).registerPoint(null);
        
        inst.registerCurrent(step);
        
        assertEquals(step, inst.getCurrent());
        assertTrue(0.0 == inst.progress);
    }

    @Test
    public void testConsumeTimeDoNothing() {
        inst.consumeTime(new ArrayList<>(), 0);
    }
    
    @Test
    public void testConsumeTimeRunning() {
        doReturn(true).when(inst).shouldRun();
        doReturn(500L).when(inst).consumeTimeByRunning(anyLong(), anyList());
        doReturn(false).when(inst).shouldShiftStep();
        doReturn(false).when(inst).shouldStay();
        
        inst.consumeTime(new ArrayList<>(), 1000);
        
        verify(inst, times(2)).consumeTimeByRunning(anyLong(), anyList());
    }
    
    @Test
    public void testConsumeTimeStaying() {
        doReturn(false).when(inst).shouldRun();
        doReturn(false).when(inst).shouldShiftStep();
        doReturn(true).when(inst).shouldStay();
        doReturn(500L).when(inst).consumeTimeByStaying(anyLong());
        
        inst.consumeTime(new ArrayList<>(), 1000);
        
        verify(inst, times(2)).consumeTimeByStaying(anyLong());
    }
    
    @Test
    public void testConsumeTimeShiftStep() {
        doReturn(true).when(inst).shouldRun();
        doReturn(500L).when(inst).consumeTimeByRunning(anyLong(), anyList());
        doReturn(true).when(inst).shouldShiftStep();
        doReturn(true).when(inst).shouldStay();
        doReturn(500L).when(inst).consumeTimeByStaying(anyLong());
        doNothing().when(inst).shiftStep(anyList());
        
        inst.consumeTime(new ArrayList<>(), 1000);
        
        verify(inst, times(2)).shiftStep(anyList());
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
        doReturn(10.0).when(inst).calcMovableDist(anyLong());
        doReturn(5.0).when(inst).calcRemainDist();
        doReturn(1000L).when(inst).run(anyDouble(), anyList());
        inst.consumeTimeByRunning(1000, new ArrayList<>());
        
        verify(inst, times(1)).run(eq(5.0), anyList());
    }

    @Test
    public void testCalcRemainDist() {
        inst.current = moving;
        doReturn(10.0).when(moving).getDist();
        inst.progress = 1.0;
        assertTrue(0.0 ==inst.calcRemainDist());
        
        inst.progress = 0.0;
        assertTrue(10.0 ==inst.calcRemainDist());
        
        inst.progress = 0.5;
        assertTrue(5.0 ==inst.calcRemainDist());
    }

    @Test
    public void testCalcMovableDist() {
        doReturn(0.0).when(train).getSpeed();
        assertTrue(0.0 == inst.calcMovableDist(0));
        assertTrue(0.0 == inst.calcMovableDist(1));
        
        doReturn(10.0).when(train).getSpeed();
        assertTrue(0.0 == inst.calcMovableDist(0));
        assertTrue(10.0 == inst.calcMovableDist(1));
        assertTrue(100.0 == inst.calcMovableDist(10));
    }

    @Test
    public void testRun() {
        inst.current = moving;
        doReturn(10.0).when(moving).getDist();
        doReturn(10.0).when(train).getSpeed();
        doNothing().when(inst).registerPoint(anyList());
        
        assertTrue(0.0 == inst.run(0, new ArrayList<>()));
        assertTrue(0.0 == inst.progress);
    }

    @Test
    public void testConsumeTimeByStaying() {
        doReturn(5L).when(inst).calcStayableTime();
        doReturn(5L).when(inst).stay(anyLong());
        inst.consumeTimeByStaying(10);
        
        verify(inst, times(1)).stay(5);
    }

    @Test
    public void testCalcStayableTime() {
        doReturn(100L).when(train).getMobility();
        
        inst.progress = 0.0;
        assertEquals(100, inst.calcStayableTime());
        
        inst.progress = 0.5;
        assertEquals(50, inst.calcStayableTime());
        
        inst.progress = 1.0;
        assertEquals(0, inst.calcStayableTime());
    }

    @Test
    public void testStay() {
        inst.progress = 0.0;
        doReturn(10L).when(train).getMobility();
        
        assertTrue(0.0 == inst.stay(0));
        
        assertTrue(0.0 == inst.progress);
        
        assertTrue(10.0 == inst.stay(10));
        assertTrue(1.0 == inst.progress);
    }

    @Test
    public void testRegisterPoint() {
        inst.current = moving;
        RailNode start = mock(RailNode.class);
        RailNode goal = mock(RailNode.class);
        doReturn(start).when(moving).getStartRailNode();
        doReturn(goal).when(moving).getGoalRailNode();
        doReturn(20.0).when(start).getX();
        doReturn(40.0).when(goal).getX();
        doReturn(20.0).when(start).getY();
        doReturn(40.0).when(goal).getY();
        
        inst.progress = 0.0;
        inst.registerPoint(null);
        assertTrue(20.0 == inst.getX());
        assertTrue(20.0 == inst.getY());
        
        inst.progress = 0.5;
        inst.registerPoint(null);
        assertTrue(30.0 == inst.getX());
        assertTrue(30.0 == inst.getY());
        
        inst.progress = 1.0;
        inst.registerPoint(null);
        assertTrue(40.0 == inst.getX());
        assertTrue(40.0 == inst.getY());
    }

    @Test
    public void testRegisterPointWithHuman() {
        inst.current = moving;
        RailNode start = mock(RailNode.class);
        RailNode goal = mock(RailNode.class);
        doReturn(start).when(moving).getStartRailNode();
        doReturn(goal).when(moving).getGoalRailNode();
        doReturn(20.0).when(start).getX();
        doReturn(40.0).when(goal).getX();
        doReturn(20.0).when(start).getY();
        doReturn(40.0).when(goal).getY();
        
        Human human = mock(Human.class);
        TrainDeployed deployed = mock(TrainDeployed.class);
        doReturn(true).when(inst).equalsId(eq(deployed));
        doReturn(deployed).when(human).getOnTrain();
        
        List<Human> humans = new ArrayList<>();
        humans.add(human);
        inst.progress = 0.5;
        
        inst.registerPoint(humans);
        
        assertTrue(30.0 == inst.getX());
        assertTrue(30.0 == inst.getY());
        
        verify(human, times(1)).setX(eq(30.0));
        verify(human, times(1)).setY(eq(30.0));
    }

    @Test
    public void testShouldShiftStep() {
        assertFalse(inst.shouldShiftStep());
        
        inst.progress = 1.0;
        assertTrue(inst.shouldShiftStep());
    }

    @Test
    public void testShiftStep() {
        inst.current = moving;
        doReturn(stop).when(moving).getNext();
        
        inst.shiftStep(new ArrayList<>());
        
        verify(inst, never()).freeHuman(anyList(), nullable(Platform.class));
        verify(inst, never()).collectHuman(anyList(), nullable(Platform.class));
        
        assertEquals(stop, inst.current);
        assertTrue(0.0 == inst.progress);
    }

    @Test
    public void testShiftStepWhenDeparture() {
        inst.current = dep;
        doNothing().when(inst).collectHuman(anyList(), any(Platform.class));
        doReturn(mock(Platform.class)).when(_dep).getStaying();
        
        inst.shiftStep(new ArrayList<>());
        
        verify(inst, never()).freeHuman(anyList(), nullable(Platform.class));
        verify(inst, times(1)).collectHuman(anyList(), nullable(Platform.class));
        verify(_dep, times(1)).getStaying();
        verify(dep, times(2)).getNext();
        assertTrue(0.0 == inst.progress);
    }
    
    @Test
    public void testShiftStepWhenStopping() {
        inst.current = stop;
        doNothing().when(inst).freeHuman(anyList(), any(Platform.class));
        doReturn(mock(Platform.class)).when(_stop).getGoal();
        
        inst.shiftStep(new ArrayList<>());
        
        verify(inst, times(1)).freeHuman(anyList(), nullable(Platform.class));
        verify(inst, never()).collectHuman(anyList(), nullable(Platform.class));
        verify(_stop, times(1)).getGoal();
        verify(stop, times(2)).getNext();
        assertTrue(0.0 == inst.progress);
    }
    
    @Test
    public void testCanRide() {
        doReturn(3).when(train).getCapacity();
        inst.occupied = 1;
        
        assertTrue(inst.canRide());
    }
    
    @Test
    public void testCanRideBorder() {
        doReturn(2).when(train).getCapacity();
        inst.occupied = 1;
        
        assertTrue(inst.canRide());
    }
    
    @Test
    public void testCanRideFull() {
        doReturn(1).when(train).getCapacity();
        inst.occupied = 1;
        
        assertFalse(inst.canRide());
    }
    
    @Test
    public void testFreeHuman() {
        Human keeper = mock(Human.class);
        doReturn(false).when(keeper).shouldGetOff(eq(inst), any(Platform.class));
        Human off = mock(Human.class);
        doReturn(true).when(off).shouldGetOff(eq(inst), any(Platform.class));
        
        List<Human> passengers = new ArrayList<>();
        passengers.add(keeper);
        passengers.add(off);
        
        Platform platform = mock(Platform.class);
        inst.occupied = 2;
        
        inst.freeHuman(passengers, platform);
        
        verify(off, times(1)).getOffTrain(eq(platform));
        verify(keeper, never()).getOffTrain(any(Platform.class));
        assertEquals(1, inst.occupied);
    }
    
    @Test
    public void testCollectHuman() {
        Human keeper = mock(Human.class);
        doReturn(false).when(keeper).shouldRide(any(Platform.class), any(TrainDeployed.class));
        Human on = mock(Human.class);
        doReturn(true).when(on).shouldRide(any(Platform.class), any(TrainDeployed.class));
        
        Platform platform = mock(Platform.class);
        
        List<Human> waiters = new ArrayList<>();
        waiters.add(keeper);
        waiters.add(on);
        doReturn(2).when(train).getCapacity();
        
        inst.occupied = 0;
        
        inst.collectHuman(waiters, platform);
        
        verify(on, times(1)).shouldRide(any(Platform.class), any(TrainDeployed.class));
        verify(on, times(1)).getInTrain(eq(inst));
        verify(keeper, times(1)).shouldRide(any(Platform.class), any(TrainDeployed.class));
        verify(keeper, never()).getInTrain(any(TrainDeployed.class));
        verify(inst, times(1)).canRide();
        assertEquals(1, inst.occupied);
    }
    
    @Test
    public void testCollectHumanIgnoreFar() {
        Human far = mock(Human.class);
        Platform platform = mock(Platform.class);
        
        doReturn(false).when(far).shouldRide(any(Platform.class), any(TrainDeployed.class));
        
        List<Human> waiters = new ArrayList<>();
        waiters.add(far);
        inst.occupied = 0;
        
        inst.collectHuman(waiters, platform);
        
        verify(far, times(1)).shouldRide(any(Platform.class), any(TrainDeployed.class));
        verify(inst, never()).canRide();
        assertEquals(0, inst.occupied);
    }
    
    @Test
    public void testCollectHumanFull() {
        Human on1 = mock(Human.class);
        doReturn(true).when(on1).shouldRide(any(Platform.class), any(TrainDeployed.class));
        Human on2 = mock(Human.class);
        doReturn(true).when(on2).shouldRide(any(Platform.class), any(TrainDeployed.class));
        
        Platform platform = mock(Platform.class);
        
        List<Human> waiters = new ArrayList<>();
        waiters.add(on1);
        waiters.add(on2);
        
        doReturn(1).when(train).getCapacity();
        inst.occupied = 0;
        
        inst.collectHuman(waiters, platform);
        
        verify(on1, times(1)).shouldRide(any(Platform.class), any(TrainDeployed.class));
        verify(on1, times(1)).getInTrain(eq(inst));
        
        verify(on2, times(1)).shouldRide(any(Platform.class), any(TrainDeployed.class));
        verify(on2, never()).getInTrain(any(TrainDeployed.class));
        verify(inst, times(2)).canRide();
        assertEquals(1, inst.occupied);
    }
}

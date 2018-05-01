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

import net.rushhourgame.controller.route.PermanentRouteEdge;
import net.rushhourgame.controller.route.PermanentRouteNode;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HumanTest extends AbstractEntityTest {

    protected static final long NO_INTERVAL = 0L;
    protected static final long VALID_INTERVAL = 10L;
    protected static final double VALID_SPEED = 0.001d;

    @Spy
    protected Human inst;

    @Mock
    protected PermanentRouteEdge currentEdge;

    @Mock
    protected PermanentRouteNode currentGoal;

    @Mock
    protected PermanentRouteEdge nextEdge;

    @Mock
    protected StepForHuman neverEndTask;

    @Mock
    protected StepForHuman onetimeTask;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        doReturn(currentGoal).when(currentEdge).getTo();
        doReturn(nextEdge).when(currentGoal).getViaEdge();
        inst.current = currentEdge;

        doAnswer(invocation -> invocation.getArgument(1)).when(neverEndTask).step(any(Human.class), anyLong(), anyDouble());
        doReturn(false).when(neverEndTask).isFinished(any(Human.class));

        doReturn(0L).when(onetimeTask).step(any(Human.class), anyLong(), anyDouble());
        doReturn(true).when(onetimeTask).isFinished(any(Human.class));
    }
    
    @Test
    public void testStep() {
        doReturn(onetimeTask).when(currentEdge).getOriginal();
        doReturn(true).when(currentGoal).isEnd();
        
        inst.step(VALID_INTERVAL, VALID_SPEED);
        
        verify(currentEdge, times(2)).getOriginal();
        verify(onetimeTask, times(1)).isFinished(any(Human.class));
        verify(currentEdge, times(1)).getTo();
        verify(currentGoal, times(1)).isEnd();
        
        assertEquals(inst.current, currentEdge);
        assertTrue(inst.isFinished());
    }

    @Test
    public void testStepNoActionWhenFinished() {
        inst.isFinished = true;

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(0)).getOriginal();
    }

    @Test
    public void testStepNoActionWhenNoRoute() {
        inst.current = null;

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(0)).getOriginal();
    }

    @Test
    public void testStepNothingWhenNoInterval() {
        inst.step(NO_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(0)).getOriginal();
    }

    @Test
    public void testStepProceededButNotEnded() {
        doReturn(neverEndTask).when(currentEdge).getOriginal();

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(2)).getOriginal();
        verify(currentEdge, times(0)).getTo();

        assertFalse(inst.isFinished());
        assertEquals(currentEdge, inst.current);
    }

    @Test
    public void testStepShiftTask() {
        doReturn(onetimeTask).when(currentEdge).getOriginal();
        doReturn(neverEndTask).when(nextEdge).getOriginal();

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(2)).getOriginal();
        verify(onetimeTask, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(onetimeTask, times(1)).isFinished(any(Human.class));
        verify(currentEdge, times(2)).getTo();

        verify(nextEdge, times(2)).getOriginal();
        verify(neverEndTask, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(neverEndTask, times(1)).isFinished(any(Human.class));

        verify(inst, times(1)).shiftEdge();
        verify(currentEdge, times(1)).unreffer(any(Human.class));
        verify(nextEdge, times(1)).reffer(any(Human.class));
        
        assertFalse(inst.isFinished());
        assertEquals(nextEdge, inst.current);
    }

    @Test
    public void testIdle() {
    }

    @Test
    public void testWalk() {
    }

    @Test
    public void testFinishes() {
    }

    @Test
    public void testEnterStation() {
    }

    @Test
    public void testExitStation() {
    }

    @Test
    public void testGetInTrain() {
    }

    @Test
    public void testGetOffTrain() {
    }

    @Test
    public void testShiftTask() {
    }

    @Test
    public void testGetX() {
    }

    @Test
    public void testSetX() {
    }

    @Test
    public void testGetY() {
    }

    @Test
    public void testSetY() {
    }

    @Test
    public void testGetLifespan() {
    }

    @Test
    public void testConsumeLifespan() {
    }

    @Test
    public void testSetLifespan() {
    }

    @Test
    public void testShouldDie() {
    }

    @Test
    public void testGetSrc() {
    }

    @Test
    public void testSetSrc() {
    }

    @Test
    public void testGetDest() {
    }

    @Test
    public void testSetDest() {
    }

    @Test
    public void testGetCurrent() {
    }

    @Test
    public void testSetCurrent() {
    }

    @Test
    public void testDistTo() {
    }

    @Test
    public void testMoveTo() {
    }
}

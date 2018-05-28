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

import javax.persistence.EntityManager;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.route.PermanentRouteEdge;
import net.rushhourgame.controller.route.PermanentRouteNode;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.controller.route.TemporaryHumanRouteEdge;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
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

    @Mock
    protected EntityManager em;

    @Mock
    protected TrainDeployed train;

    @Mock
    protected Platform platform;

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

        verify(inst.current, never()).getOriginal();
    }

    @Test
    public void testStepNoActionWhenNoRoute() {
        inst.current = null;

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, never()).getOriginal();
    }

    @Test
    public void testStepNothingWhenNoInterval() {
        inst.step(NO_INTERVAL, VALID_SPEED);
    }

    @Test
    public void testStepProceededButNotEnded() {
        doReturn(neverEndTask).when(currentEdge).getOriginal();

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(1)).getOriginal();
        verify(neverEndTask, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(neverEndTask, times(1)).isFinished(any(Human.class));
        verify(currentEdge, times(0)).getTo();

        assertFalse(inst.isFinished());
        assertEquals(currentEdge, inst.current);
    }

    @Test
    public void testStepShiftTask() {
        doReturn(onetimeTask).when(currentEdge).getOriginal();
        doReturn(neverEndTask).when(nextEdge).getOriginal();

        inst.step(VALID_INTERVAL, VALID_SPEED);

        verify(currentEdge, times(1)).getOriginal();
        verify(nextEdge, times(1)).getOriginal();

        verify(onetimeTask, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(onetimeTask, times(1)).isFinished(any(Human.class));
        verify(currentEdge, times(2)).getTo();

        verify(neverEndTask, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(neverEndTask, times(1)).isFinished(any(Human.class));
        verify(neverEndTask, times(0)).getTo();

        verify(inst, times(1)).shiftEdge();
        verify(currentEdge, times(1)).unreffer(any(Human.class));
        verify(nextEdge, times(1)).reffer(any(Human.class));

        assertFalse(inst.isFinished());
        assertEquals(nextEdge, inst.current);
    }

    @Test
    public void testEnterIntoPlatform() {
        TicketGate tg = mock(TicketGate.class);
        Platform p = mock(Platform.class);

        inst.enterIntoPlatform(tg, p);

        verify(tg, times(1)).pass();
        verify(p, times(1)).enter();

        assertEquals(p, inst.getOnPlatform());
        assertEquals(Human.StandingOn.PLATFORM, inst.getStandingOn());
    }

    @Test
    public void testExitFromPlatform() {
        Platform p = mock(Platform.class);
        TicketGate tg = mock(TicketGate.class);
        inst.onPlatform = p;

        inst.exitFromPlatform(p, tg);

        verify(p, times(1)).exit();
        verify(tg, times(1)).pass();

        assertNull(inst.getOnPlatform());
        assertEquals(Human.StandingOn.GROUND, inst.getStandingOn());
    }

    @Test
    public void testGetInTrain() {
        inst.onPlatform = platform;

        inst.getInTrain(train);

        verify(platform, times(1)).exit();
        assertNull(inst.onPlatform);
        assertEquals(train, inst.onTrain);
        assertEquals(train, inst.getOnTrain());
        assertEquals(Human.StandingOn.TRAIN, inst.stand);
    }

    @Test
    public void testGetOffTrain() {
        inst.onTrain = train;

        inst.getOffTrain(platform);

        assertNull(inst.onTrain);
        assertEquals(platform, inst.onPlatform);
        verify(platform, times(1)).enter(eq(true));
        verify(inst, times(1)).shiftEdge();
        assertEquals(Human.StandingOn.PLATFORM, inst.stand);
    }

    @Test
    public void testShouldRide() {
        inst.onPlatform = platform;
        doReturn(true).when(platform).equalsId(any(Identifiable.class));

        StepForHumanThroughTrain purpose = mock(StepForHumanThroughTrain.class);
        RouteEdge current = mock(RouteEdge.class);
        Line line = mock(Line.class);

        inst.current = current;
        doReturn(purpose).when(current).getOriginal();
        doReturn(line).when(purpose).getLine();

        LineStep lineStep = mock(LineStep.class);

        doReturn(lineStep).when(train).getCurrent();
        doReturn(line).when(lineStep).getParent();
        doReturn(true).when(line).equalsId(any(Identifiable.class));

        assertTrue(inst.shouldRide(platform, train));

        verify(platform, times(1)).equalsId(any(Identifiable.class));
        verify(current, times(2)).getOriginal();
        verify(train, times(1)).getCurrent();
    }

    @Test
    public void testShouldRideDifferentPlatform() {
        inst.onPlatform = mock(Platform.class);
        doReturn(false).when(platform).equalsId(any(Identifiable.class));

        assertFalse(inst.shouldRide(platform, train));

        verify(platform, times(1)).equalsId(any(Identifiable.class));
        verify(currentEdge, never()).getOriginal();
        verify(train, never()).getCurrent();
    }

    @Test
    public void testShouldRideCurrentNull() {
        inst.onPlatform = platform;
        doReturn(true).when(platform).equalsId(any(Identifiable.class));
        inst.current = null;

        assertFalse(inst.shouldRide(platform, train));
        verify(platform, times(1)).equalsId(any(Identifiable.class));
        verify(currentEdge, never()).getOriginal();
        verify(train, never()).getCurrent();
    }

    @Test
    public void testShouldRideOutOfPurpose() {
        inst.onPlatform = platform;
        doReturn(true).when(platform).equalsId(any(Identifiable.class));

        StepForHumanOutOfStation purpose = mock(StepForHumanOutOfStation.class);
        RouteEdge current = mock(RouteEdge.class);
        doReturn(purpose).when(current).getOriginal();
        inst.current = current;

        assertFalse(inst.shouldRide(platform, train));

        verify(platform, times(1)).equalsId(any(Identifiable.class));
        verify(current, times(1)).getOriginal();
        verify(train, never()).getCurrent();
    }

    @Test
    public void testShouldRideDifferentDestination() {
        inst.onPlatform = platform;
        doReturn(true).when(platform).equalsId(any(Identifiable.class));

        StepForHumanThroughTrain purpose = mock(StepForHumanThroughTrain.class);
        RouteEdge current = mock(RouteEdge.class);
        Line line = mock(Line.class);

        inst.current = current;
        doReturn(purpose).when(current).getOriginal();
        doReturn(line).when(purpose).getLine();

        Line otherline = mock(Line.class);
        LineStep lineStep = mock(LineStep.class);

        doReturn(lineStep).when(train).getCurrent();
        doReturn(otherline).when(lineStep).getParent();

        doReturn(false).when(otherline).equalsId(eq(line));

        assertFalse(inst.shouldRide(platform, train));

        verify(platform, times(1)).equalsId(any(Identifiable.class));
        verify(current, times(2)).getOriginal();
        verify(train, times(1)).getCurrent();
    }

    @Test
    public void testShouldGetOff() {
        inst.onTrain = train;
        doReturn(true).when(train).equalsId(any(Identifiable.class));

        Platform destination = mock(Platform.class);
        StepForHuman purpose = mock(StepForHuman.class);
        RouteEdge current = mock(RouteEdge.class);

        doReturn(purpose).when(current).getOriginal();
        doReturn(destination).when(purpose).getTo();
        doReturn(true).when(platform).equalsId(eq(destination));

        inst.current = current;

        assertTrue(inst.shouldGetOff(train, platform));
    }

    @Test
    public void testShouldGetOffNull() {
        inst.onTrain = null;

        assertFalse(inst.shouldGetOff(train, platform));

        verify(train, never()).equalsId(any(Identifiable.class));
    }

    @Test
    public void testShouldGetOffOtherTrain() {
        inst.onTrain = mock(TrainDeployed.class);

        doReturn(false).when(train).equalsId(any(Identifiable.class));

        assertFalse(inst.shouldGetOff(train, platform));

        verify(train, times(1)).equalsId(any(Identifiable.class));
        verify(platform, never()).equalsId(any(Identifiable.class));
    }

    @Test
    public void testShouldGetOffNoPurpose() {
        inst.onTrain = train;
        doReturn(true).when(train).equalsId(any(Identifiable.class));
        inst.current = null;

        assertTrue(inst.shouldGetOff(train, platform));
        verify(platform, never()).equalsId(any(Identifiable.class));
    }

    @Test
    public void testShouldGetOffOtherPlatform() {
        inst.onTrain = train;
        doReturn(true).when(train).equalsId(any(Identifiable.class));

        Platform destination = mock(Platform.class);
        StepForHuman purpose = mock(StepForHuman.class);
        RouteEdge current = mock(RouteEdge.class);

        doReturn(purpose).when(current).getOriginal();
        doReturn(destination).when(purpose).getTo();
        doReturn(false).when(platform).equalsId(eq(destination));

        inst.current = current;

        assertFalse(inst.shouldGetOff(train, platform));

        verify(train, times(1)).equalsId(any(Identifiable.class));
        verify(platform, times(1)).equalsId(any(Identifiable.class));
    }

    @Test
    public void testMergeCompanyCopy() {
        inst.dest = mock(Company.class);
        Company copy = mock(Company.class);
        doReturn(true).when(inst.dest).equalsId(eq(copy));

        inst.merge(copy);

        assertEquals(inst.dest, copy);
    }

    @Test
    public void testMergeCompanyOther() {
        inst.dest = mock(Company.class);
        Company other = mock(Company.class);
        doReturn(false).when(inst.dest).equalsId(eq(other));

        inst.merge(other);

        assertNotEquals(inst.dest, other);
    }

    @Test
    public void testMergeCompanySame() {
        Company dest = mock(Company.class);
        inst.dest = dest;
        doReturn(true).when(inst.dest).equalsId(eq(dest));

        inst.merge(dest);

        assertEquals(inst.dest, dest);
    }
    
    @Test
    public void testWalkToLong() {
        assertEquals(VALID_INTERVAL,
                inst.walkTo(VALID_INTERVAL, VALID_SPEED, new SimplePoint(100, 100)));
    }

    @Test
    public void testSearchCurrentOnPlatform() {
        RouteSearcher searcher = mock(RouteSearcher.class);
        doReturn(true).when(searcher).isReachable(any(Platform.class), any(Company.class));
        doReturn(mock(RouteNode.class)).when(searcher).getStart(any(Platform.class), any(Company.class));
        doNothing().when(inst).setCurrent(any(RouteNode.class));
        inst.onPlatform = platform;
        inst.dest = mock(Company.class);

        inst.searchCurrent(searcher);

        verify(searcher, times(1)).getStart(any(Platform.class), any(Company.class));
        verify(searcher, never()).getStart(any(Residence.class), any(Company.class));
    }

    @Test
    public void testSearchCurrentLonelyPlatform() {
        inst.onPlatform = mock(Platform.class);
        inst.dest = mock(Company.class);
        inst.src = mock(Residence.class);
        RouteSearcher searcher = mock(RouteSearcher.class);
        doReturn(false).when(searcher).isReachable(nullable(Platform.class), any(Company.class));

        inst.searchCurrent(searcher);

        verify(searcher, never()).getStart(nullable(Platform.class), any(Company.class));
        verify(searcher, times(1)).isReachable(any(Residence.class), any(Company.class));
    }

    @Test
    public void testSearchCurrentOutOfPlatform() {
        RouteSearcher searcher = mock(RouteSearcher.class);
        inst.onPlatform = null;
        inst.src = mock(Residence.class);
        inst.dest = mock(Company.class);
        doReturn(true).when(searcher).isReachable(eq(inst.src), eq(inst.dest));
        doReturn(mock(RouteNode.class)).when(searcher).getStart(eq(inst.src), eq(inst.dest));
        doNothing().when(inst).setCurrent(any(RouteNode.class));

        inst.searchCurrent(searcher);

        verify(searcher, never()).getStart(any(Platform.class), any(Company.class));
        verify(searcher, times(1)).getStart(any(Residence.class), any(Company.class));
    }

    @Test
    public void testSearchNotFound() {
        RouteSearcher searcher = mock(RouteSearcher.class);
        inst.onPlatform = null;
        inst.src = mock(Residence.class);
        inst.dest = mock(Company.class);
        doReturn(false).when(searcher).isReachable(eq(inst.src), eq(inst.dest));
        
        inst.searchCurrent(searcher);

        verify(searcher, never()).getStart(any(Platform.class), any(Company.class));
        verify(searcher, never()).getStart(any(Residence.class), any(Company.class));
    }
    
    @Test
    public void testIsAreaIn() {
        assertTrue(inst.isAreaIn(new SimplePoint(), 10));
    }
    
    @Test
    public void testBean() {
        assertFalse(inst.equalsId(null));
        assertNull(inst.getOwner());
        assertFalse(inst.isOwnedBy(mock(Player.class)));
        assertFalse(inst.isPrivilegedBy(mock(Player.class)));
    }
}

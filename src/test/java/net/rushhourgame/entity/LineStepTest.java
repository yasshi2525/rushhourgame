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
package net.rushhourgame.entity;

import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepMoving;
import net.rushhourgame.entity.troute.LineStepPassing;
import net.rushhourgame.entity.troute.LineStepStopping;
import net.rushhourgame.entity.troute.LineStepType;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class LineStepTest extends AbstractEntityTest {

    protected LineStep inst;
    @Mock
    protected LineStepDeparture departure;
    @Mock
    protected LineStepMoving moving;
    @Mock
    protected LineStepStopping stopping;
    @Mock
    protected LineStepPassing passing;
    @Mock
    protected Platform platform;
    @Mock
    protected RailEdge railEdge;
    @Mock
    protected RailNode railNode;
    @Mock
    protected RailNode railNode2;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = new LineStep();
    }
    
    @Test
    public void testBean() {
        Station st = mock(Station.class);
        inst.setOnStation(st);
        
        assertEquals(st, inst.getOnStation());
    }

    @Test
    public void testGetStartRailNodeDeparture() {
        inst.departure = departure;
        when(departure.getStaying()).thenReturn(platform);
        when(platform.getRailNode()).thenReturn(railNode);

        assertEquals(railNode, inst.getStartRailNode());
    }

    @Test
    public void testGetStartRailNodeMoving() {
        inst.moving = moving;
        when(moving.getRunning()).thenReturn(railEdge);
        when(railEdge.getFrom()).thenReturn(railNode);

        assertEquals(railNode, inst.getStartRailNode());
    }

    @Test
    public void testGetStartRailNodeStopping() {
        inst.stopping = stopping;
        when(stopping.getRunning()).thenReturn(railEdge);
        when(railEdge.getFrom()).thenReturn(railNode);

        assertEquals(railNode, inst.getStartRailNode());
    }

    @Test
    public void testGetStartRailNodePassing() {
        inst.passing = passing;
        when(passing.getRunning()).thenReturn(railEdge);
        when(railEdge.getFrom()).thenReturn(railNode);

        assertEquals(railNode, inst.getStartRailNode());
    }

    @Test
    public void testGetStartRailNodeNoChildren() {
        try {
            inst.getStartRailNode();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
        assertEquals("ls(0){?}", inst.toString());
    }

    @Test
    public void testGetGoalRailNodeDeparture() {
        inst.departure = departure;
        when(departure.getStaying()).thenReturn(platform);
        when(platform.getRailNode()).thenReturn(railNode);

        assertEquals(railNode, inst.getGoalRailNode());
    }

    @Test
    public void testGetGoalRailNodeMoving() {
        inst.moving = moving;
        when(moving.getRunning()).thenReturn(railEdge);
        when(railEdge.getTo()).thenReturn(railNode);

        assertEquals(railNode, inst.getGoalRailNode());
    }

    @Test
    public void testGetGoalRailNodeStopping() {
        inst.stopping = stopping;
        when(stopping.getGoal()).thenReturn(platform);
        when(platform.getRailNode()).thenReturn(railNode);

        assertEquals(railNode, inst.getGoalRailNode());
    }

    @Test
    public void testGetGoalRailNodePassing() {
        inst.passing = passing;
        when(passing.getGoal()).thenReturn(platform);
        when(platform.getRailNode()).thenReturn(railNode);

        assertEquals(railNode, inst.getGoalRailNode());
    }

    @Test
    public void testGetGoalRailNodeNoChildren() {
        try {
            inst.getGoalRailNode();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
    }

    @Test
    public void testGetOnRailEdgeDeparture() {
        inst.departure = departure;

        assertNull(inst.getOnRailEdge());
    }

    @Test
    public void testGetOnRailEdgeMoving() {
        inst.moving = moving;
        when(moving.getRunning()).thenReturn(railEdge);

        assertEquals(railEdge, inst.getOnRailEdge());
    }

    @Test
    public void testGetOnRailEdgeStopping() {
        inst.stopping = stopping;
        when(stopping.getRunning()).thenReturn(railEdge);

        assertEquals(railEdge, inst.getOnRailEdge());
    }

    @Test
    public void testGetOnRailEdgePassing() {
        inst.passing = passing;

        assertNull(inst.getOnRailEdge());
    }

    @Test
    public void testGetOnRailEdgeNoChildren() {
        try {
            inst.getOnRailEdge();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
    }

    @Test
    public void testVerifyUnregisteredDeparture() {
        inst.departure = departure;
        try {
            inst.verifyUnregistered();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("departure was already registered.", e.getMessage());
        }
    }

    @Test
    public void testVerifyUnregisteredMoving() {
        inst.moving = moving;
        try {
            inst.verifyUnregistered();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("moving was already registered.", e.getMessage());
        }
    }

    @Test
    public void testVerifyUnregisteredStopping() {
        inst.stopping = stopping;
        try {
            inst.verifyUnregistered();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("stopping was already registered.", e.getMessage());
        }
    }

    @Test
    public void testVerifyUnregisteredPassing() {
        inst.passing = passing;
        try {
            inst.verifyUnregistered();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("passing was already registered.", e.getMessage());
        }
    }

    @Test
    public void testCanConnectUnconnected() {
        when(platform.getRailNode()).thenReturn(railNode);
        when(railEdge.getFrom()).thenReturn(railNode2);

        inst.registerDeparture(platform);

        LineStep target = new LineStep();
        target.registerMoving(railEdge);

        assertFalse(inst.canConnect(target));
    }

    @Test
    public void testCanConnectHavingNext() {
        inst.next = new LineStep();
        try {
            inst.canConnect(new LineStep());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step was already connected.", e.getMessage());
        }
    }

    @Test
    public void testCanConnectNoChild() {
        try {
            inst.canConnect(new LineStep());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
    }

    @Test
    public void testGetTypeDeparture() {
        inst.departure = departure;

        assertEquals(LineStepType.DEPARTURE, inst.getType());
    }

    @Test
    public void testGetTypeMoving() {
        inst.moving = moving;

        assertEquals(LineStepType.MOVING, inst.getType());
    }

    @Test
    public void testGetTypeStopping() {
        inst.stopping = stopping;

        assertEquals(LineStepType.STOPPING, inst.getType());
    }

    @Test
    public void testGetTypePassing() {
        inst.passing = passing;

        assertEquals(LineStepType.PASSING, inst.getType());
    }

    @Test
    public void testGetTypeNoChildren() {
        try {
            inst.getType();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
    }
    
    @Test
    public void testGetDist() {
        try {
            inst.getDist();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("line step doesn't have any children.", e.getMessage());
        }
    }
}

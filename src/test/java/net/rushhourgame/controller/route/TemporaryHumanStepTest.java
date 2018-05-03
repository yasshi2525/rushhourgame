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
package net.rushhourgame.controller.route;

import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.SimplePoint;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TemporaryHumanStepTest {
    @Mock
    protected TemporaryHumanPoint from;
    @Mock
    protected RelayPointForHuman to;
    @Mock
    protected Human human;
    
    protected final static Pointable ORIGIN = new SimplePoint();
    
    @Test
    public void testBean() {
        TemporaryHumanStep inst = new TemporaryHumanStep(from, to);
        doReturn(1L).when(from).getId();
        
        assertEquals("tmp1", inst.getUid());
        assertEquals(from, inst.getFrom());
        assertEquals(to, inst.getTo());
    }
    
    @Test
    public void testIsAreaInInFrom() {
        TemporaryHumanStep inst = spy(new TemporaryHumanStep(from, to));
        doReturn(true).when(inst)._isAreaIn(eq(from), eq(ORIGIN), eq(0d));
        
        assertTrue(inst.isAreaIn(ORIGIN, 0));
    }
    
    @Test
    public void testIsAreaInInTo() {
        TemporaryHumanStep inst = spy(new TemporaryHumanStep(from, to));
        doReturn(false).when(inst)._isAreaIn(eq(from), eq(ORIGIN), eq(0d));
        doReturn(true).when(inst)._isAreaIn(eq(to), eq(ORIGIN), eq(0d));
        
        assertTrue(inst.isAreaIn(ORIGIN, 0));
    }
    
    @Test
    public void testIsAreaInOut() {
        TemporaryHumanStep inst = spy(new TemporaryHumanStep(from, to));
        doReturn(false).when(inst)._isAreaIn(eq(from), eq(ORIGIN), eq(0d));
        doReturn(false).when(inst)._isAreaIn(eq(to), eq(ORIGIN), eq(0d));
        
        assertFalse(inst.isAreaIn(ORIGIN, 0));
    }
    
    @Test
    public void test_IsAreaIn() {
        TemporaryHumanStep inst = new TemporaryHumanStep(from, to);
        
        assertFalse(inst._isAreaIn(new SimplePoint(-5, 0), ORIGIN, 0));
        assertFalse(inst._isAreaIn(new SimplePoint(5, 0), ORIGIN, 0));
        assertFalse(inst._isAreaIn(new SimplePoint(0, -5), ORIGIN, 0));
        assertFalse(inst._isAreaIn(new SimplePoint(0, 5), ORIGIN, 0));
        assertTrue(inst._isAreaIn(new SimplePoint(0, 0), ORIGIN, 0));
    }
    
    @Test
    public void testStep() {
        TemporaryHumanStep inst = new TemporaryHumanStep(from, to);
        
        inst.step(human, 0L, 0d);
        
        verify(human, times(1)).walkTo(eq(0L), eq(0d), eq(to));
    }
    
    @Test
    public void testIsFinished() {
        TemporaryHumanStep inst = new TemporaryHumanStep(from, to);
        doReturn(0d).when(human).distTo(eq(to));
        
        assertTrue(inst.isFinished(human));
    }
    
    @Test
    public void testIsFinishedFalse() {
        TemporaryHumanStep inst = new TemporaryHumanStep(from, to);
        doReturn(10d).when(human).distTo(eq(to));
        
        assertFalse(inst.isFinished(human));
    }
}

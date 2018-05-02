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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PlatformTest extends AbstractEntityTest {
    
    @Spy
    protected Platform inst;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testCanEnter() {
        inst.capacity = 5;
        inst.occupied = 1;
        
        assertTrue(inst.canEnter());
    }
    
    @Test
    public void testCanEnterBorder() {
        inst.capacity = 5;
        inst.occupied = 4;
        
        assertTrue(inst.canEnter());
    }
    
    @Test
    public void testCanEnterFull() {
        inst.capacity = 5;
        inst.occupied = 5;
        
        assertFalse(inst.canEnter());
    }
    
    @Test
    public void testEnter() {
        inst.capacity = 1;
        inst.occupied = 0;
        
        inst.enter();
        
        assertEquals(1, inst.occupied);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testEnterFail() {
        inst.capacity = 1;
        inst.occupied = 1;
        
        inst.enter();
        
        fail();
    }
    
    @Test
    public void testEnterForce() {
        inst.capacity = 1;
        inst.occupied = 1;
        
        inst.enter(true);
        
        assertEquals(2, inst.occupied);
    }
    
    @Test
    public void testExit() {
        inst.occupied = 1;
        
        inst.exit();
        
        assertEquals(0, inst.occupied);
    }
}

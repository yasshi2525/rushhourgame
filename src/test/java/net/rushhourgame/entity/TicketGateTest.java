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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import org.mockito.Spy;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TicketGateTest extends AbstractEntityTest {
    
    @Spy
    protected TicketGate inst;
    
    @Mock
    protected Station station;
    
    @Mock
    protected Platform platform;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.station = station;
        doReturn(platform).when(station).getPlatform();
    }
    
    @Test
    public void testStep() {
        inst.mobility = 0.0005;
        inst.occupied = 2;
        
        inst.step(1000);
        
        assertTrue(1.5 == inst.occupied);
    }
    
    @Test
    public void testStepFlush() {
        inst.mobility = 1;
        inst.occupied = 2;
        
        inst.step(1000);
        
        assertTrue(0.0 == inst.occupied);
    }
    
    @Test
    public void testCanEnter() {
        doReturn(true).when(inst).isAvailable();
        doReturn(true).when(platform).canEnter();
        
        assertTrue(inst.canEnter());
    }
    
    @Test
    public void testCanEnterGateFull() {
        doReturn(false).when(inst).isAvailable();
        
        assertFalse(inst.canEnter());
    }
    
    @Test
    public void testCanEnterGateAvailableButPlatformFull() {
        doReturn(true).when(inst).isAvailable();
        doReturn(false).when(platform).canEnter();
        
        assertFalse(inst.canEnter());
    }
    
    @Test
    public void testCanExit() {
        doReturn(true).when(inst).isAvailable();
        assertTrue(inst.canExit());
    }
    
    @Test
    public void testCanExitGateFull() {
        doReturn(false).when(inst).isAvailable();
        assertFalse(inst.canExit());
    }
    
    @Test
    public void testPass() {
        inst.gateNum = 1;
        inst.occupied = 0;
        
        inst.pass();
        
        assertTrue(1 == inst.occupied);
    }
    
    @Test
    public void testAvailable() {
        inst.gateNum = 2;
        inst.occupied = 0;
        
        assertTrue(inst.isAvailable());
    }
    
    @Test
    public void testAvailableBorder() {
        inst.gateNum = 2;
        inst.occupied = 1;
        
        assertTrue(inst.isAvailable());
    }
    
    @Test
    public void testAvailableFull() {
        inst.gateNum = 2;
        inst.occupied = 2;
        
        assertFalse(inst.isAvailable());
    }
}

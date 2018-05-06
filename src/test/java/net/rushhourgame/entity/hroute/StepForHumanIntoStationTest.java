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
package net.rushhourgame.entity.hroute;

import net.rushhourgame.entity.AbstractEntityTest;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.TicketGate;
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
public class StepForHumanIntoStationTest extends AbstractEntityTest {
    @Spy
    StepForHumanIntoStation inst;
    
    @Mock
    TicketGate gate;
    
    @Mock
    Platform platform;
    
    @Spy
    Human human;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst._from = gate;
        inst._to = platform;
    }
    
    @Test
    public void testStep() {
        doReturn(true).when(gate).canEnter();
        human.setLifespan(1000);
        
        assertEquals(0, inst.step(human, 1000, 1));
        
        assertEquals(1000L, human.getLifespan());
        verify(human, times(1)).enterIntoPlatform(any(TicketGate.class), any(Platform.class));
    }
    
    @Test
    public void testStepFull() {
        doReturn(false).when(gate).canEnter();
        human.setLifespan(1000);
        
        assertEquals(1000, inst.step(human, 1000, 1));
        
        assertEquals(0L, human.getLifespan());
        verify(human, times(0)).enterIntoPlatform(any(TicketGate.class), any(Platform.class));
    }
}

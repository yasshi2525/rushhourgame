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
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Residence;
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
public class StepForHumanTransferTest extends AbstractEntityTest {

    @Spy
    StepForHumanTransfer inst;

    @Mock
    TicketGate from;

    @Mock
    TicketGate to;

    @Mock
    Human human;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst._from = from;
        inst._to = to;
    }
    
    @Test
    public void testBean() {
        assertTrue(new StepForHumanTransfer().getUid().startsWith("transfer"));
    }

    @Test
    public void testStep() {
        inst.step(human, 1000L, 1d);
        
        verify(human).walkTo(eq(1000L), eq(1d), eq(to));
    }
    
    @Test
    public void testIsFinished() {
        doReturn(0d).when(human).distTo(eq(to));
        
        assertTrue(inst.isFinished(human));
    }
    
    @Test
    public void testIsFinishedFalse() {
        doReturn(1d).when(human).distTo(eq(to));
        
        assertFalse(inst.isFinished(human));
    }
}

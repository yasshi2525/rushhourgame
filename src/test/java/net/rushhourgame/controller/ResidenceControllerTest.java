/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
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
package net.rushhourgame.controller;

import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ResidenceControllerTest extends AbstractControllerTest {

    protected ResidenceController inst;
    private static final double TEST_X = 5.1;
    private static final double TEST_Y = 10.1;
    private static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_INTERVAL = 3;
    private static final double TEST_PRODIST = 10.0;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = rCon;
    }

    @Test
    public void testCreate() throws RushHourException {
        Residence created = inst.create(TEST_POS);
        
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_RSD_CAPACITY)), created.getCapacity());
        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_RSD_INTERVAL)), created.getInterval());

        assertEquals(1, rCon.findAll().size());
    }

    @Test
    public void testCreate4arg() throws RushHourException {
        Residence created = inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST);
        
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(TEST_CAPACITY, created.getCapacity());
        assertEquals(TEST_INTERVAL, created.getInterval());
        assertTrue(TEST_PRODIST == created.getProdist());

        assertEquals(1, rCon.findAll().size());
    }

    @Test
    public void testCreateDuplication() throws RushHourException {
        inst.create(TEST_POS);
        try {
            inst.create(TEST_POS);
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testFindIn() throws RushHourException {
        inst.create(TEST_POS);
        assertFalse(inst.findIn(TEST_POS, 2).isEmpty());
        assertTrue(inst.findIn(new SimplePoint(-100, -100), 2).isEmpty());
    }
    
    @Test
    public void testStepDoNothing() throws RushHourException {
        Residence created = inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST);
        inst.step(0L);
        
        verify(inst.cCon, times(0)).findAll();
    }
    
    @Test
    public void testStepNoCmpWorld() throws RushHourException {
        Residence created = spy(inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST));
        inst.findAll().remove(0);
        inst.findAll().add(created);
        
        inst.step(TEST_INTERVAL);
        
        verify(created, times(0)).getCapacity();
    }
    
    @Test
    public void testStepGenerating() throws RushHourException {
        Residence src = spy(inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST));
        inst.findAll().remove(0);
        inst.findAll().add(src);
        
        Company dest = cCon.create(TEST_POS);
        RouteNode node = mock(RouteNode.class);
        doReturn(node).when(inst.searcher).getStart(eq(src), eq(dest));
        doReturn(src.distTo(dest)).when(node).getCost();
        inst.hCon.em = spy(inst.hCon.em);
        doNothing().when(inst.hCon.em).flush();
        
        inst.step(TEST_INTERVAL);
        
        verify(src, times(2)).expires();
        verify(inst.hCon, times(TEST_CAPACITY)).create(any(Pointable.class), any(Residence.class), any(Company.class));
        assertTrue(src.getCount() < src.getInterval());
    }
    
    @Test
    public void testStepSkipGenerating() throws RushHourException {
        Residence src = spy(inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST));
        inst.findAll().remove(0);
        inst.findAll().add(src);
        
        Company dest = cCon.create(new SimplePoint(10000, 10000));
        RouteNode node = mock(RouteNode.class);
        doReturn(node).when(inst.searcher).getStart(eq(src), eq(dest));
        doReturn(src.distTo(dest)).when(node).getCost();
        
        inst.step(TEST_INTERVAL);
        
        verify(src, times(2)).expires();
        verify(inst.hCon, never()).create(any(Pointable.class), any(Residence.class), any(Company.class));
        assertTrue(src.getCount() < src.getInterval());
    }
    
    @Test
    public void testInheritInterval() throws RushHourException {
        Residence src = inst.create(TEST_POS, TEST_CAPACITY, TEST_INTERVAL, TEST_PRODIST);
        src.setCount(3);
        
        inst.synchronizeDatabase();
        
        assertEquals(3, inst.findAll().get(0).getCount());
    }
}

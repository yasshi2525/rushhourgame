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
package net.rushhourgame.controller;

import java.util.ArrayList;
import static net.rushhourgame.RushHourProperties.GAME_DEF_HUMAN_LIFESPAN;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class HumanControllerTest extends AbstractControllerTest {

    protected HumanController inst;
    protected Pointable origin = new SimplePoint();
    protected static final double TEST_X = 10;
    protected static final double TEST_Y = 20;
    protected static final Pointable TEST = new SimplePoint(TEST_X, TEST_Y);
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createHumanController();
    }
    
    @Test
    public void testCreate() throws RushHourException {
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);
        
        Human h = inst.create(TEST, src, dst);
        
        assertNotNull(h);
        assertTrue(TEST_X == h.getX());
        assertTrue(TEST_Y == h.getY());
        assertEquals(src, h.getSrc());
        assertEquals(dst, h.getDest());
        assertEquals(Long.parseLong(PROP.get(GAME_DEF_HUMAN_LIFESPAN)), h.getLifespan());
        assertEquals(Human.StandingOn.GROUND, h.getStandingOn());
        assertNull(h.getCurrent());
        assertFalse(h.isFinished());
    }
    
    @Test
    public void testStep() throws RushHourException {
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);
        
        Human h = inst.create(origin, src, dst);
        
        inst.step(h, 1000000, 0.00001, new ArrayList<>());
    }
    
}

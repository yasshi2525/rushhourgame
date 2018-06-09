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
package net.rushhourgame.managedbean;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ConsoleBeanTest extends AbstractBeanTest{
    
    protected ConsoleBean inst;
    
    protected Player player;
    
    protected static final double TEST_X = 10.1;
    protected static final double TEST_Y = 20.1;
    protected static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(ConsoleBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        
        inst = new ConsoleBean();
        inst.pCon = pCon;
        inst.cCon = cCon;
        inst.rCon = rCon;
        inst.railCon = railCon;
        inst.stCon = stCon;
        inst.lCon = lCon;
        inst.player = player;
        inst.em = em;
        inst.p = new SimplePoint();
        
        inst.session = session;
        doReturn(player.getToken()).when(session).getToken();
    }

    @Test
    public void testInit() {
        inst.player = null;
        inst.init();
        assertEquals(player, inst.player);
    }

    @Test
    public void testCreateCompany() throws RushHourException {
        inst.setX(TEST_X);
        inst.setY(TEST_Y);
        
        inst.createCompany();
        
        assertEquals(1, cCon.findIn(TEST_POS, 2).size());
        
        assertTrue(TEST_X == inst.getX());
        assertTrue(TEST_Y == inst.getY());
    }
    
    @Test
    public void testCreateResidence() throws RushHourException {
        inst.createResidence();
    }
    
    @Test
    public void testRail() throws RushHourException {
        assertFalse(inst.hasTailRail());
        inst.createRail();
        em.flush();
        assertTrue(inst.hasTailRail());
        assertFalse(inst.canSplit());
        
        inst.setX(20);
        inst.setY(20);
        inst.extendRail();
        em.flush();
        em.refresh(inst.tailRail);
        assertTrue(inst.canSplit());
        
        inst.setX(30);
        inst.setY(30);
        inst.splitRail();
    }
    
    @Test
    public void testCreateStation() throws RushHourException {
        assertFalse(inst.hasTailStation());
        
        inst.createRail();
        inst.setText("_test");
        inst.createStation();
        
        assertEquals("_test", inst.getText());
        assertTrue(inst.hasTailStation());
    }
    
    @Test
    public void testCreateLine() throws RushHourException {
        // 駅 - 駅
        
        inst.createRail();
        inst.setText("_test1");
        inst.createStation();
        inst.setX(20);
        inst.setY(20);
        inst.extendRail();
        inst.setText("_test2");
        inst.createStation();
        
        inst.createLine();
    }
    
    @Test
    public void testCanSplit() {
        assertFalse(inst.canSplit());
    }
}

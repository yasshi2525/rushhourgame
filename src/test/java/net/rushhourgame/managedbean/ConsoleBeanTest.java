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
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class ConsoleBeanTest extends AbstractBeanTest{
    
    protected ConsoleBean inst;
    @Mock
    protected RushHourSession session;
    
    protected Player player;
    
    protected static final double TEST_X = 10.1;
    protected static final double TEST_Y = 20.1;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(ConsoleBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        
        inst = new ConsoleBean();
        inst.pCon = PCON;
        inst.cCon = CCON;
        inst.session = session;
        doReturn(player.getToken()).when(session).getToken();
    }

    @Test
    public void testInit() {
        inst.init();
        assertEquals(player, inst.player);
    }

    @Test
    public void testCreateCompany() throws RushHourException {
        inst.setX(TEST_X);
        inst.setY(TEST_Y);
        
        inst.createCompany();
        
        assertEquals(1, CCON.findIn(TEST_X, TEST_Y, 2).size());
        
        assertTrue(TEST_X == inst.getX());
        assertTrue(TEST_Y == inst.getY());
    }
    
}

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

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import net.rushhourgame.GameMaster;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AdminBeanTest extends AbstractBeanTest{
    
    @Spy
    protected AdminBean inst;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.pCon = PCON;
        inst.session = session;
        inst.gm = mock(GameMaster.class);
        doReturn(Locale.getDefault()).when(session).getLocale();
    }

    @Test
    public void testInitAdminLogin() throws RushHourException {
        inst.init();
        verify(inst.gm, times(1)).constructTemplateWorld();
        verify(inst.gm, times(1)).startGame();
    }
    
    @Test
    public void testInitAdminLoginRushHourException() throws RushHourException {
        doThrow(RushHourException.class).when(inst.gm).constructTemplateWorld();
        
        inst.init();
        verify(inst.gm, times(1)).constructTemplateWorld();
        verify(inst.gm, never()).startGame();
    }
    
    @Test
    public void testInitPlayerLogin() throws RushHourException {
        Player p = createPlayer();
        doReturn(p.getToken()).when(session).getToken();
        inst.init();
        verify(session, times(0)).setToken(anyString());
    }
}

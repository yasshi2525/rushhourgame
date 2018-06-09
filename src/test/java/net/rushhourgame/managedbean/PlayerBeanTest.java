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
package net.rushhourgame.managedbean;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PlayerBeanTest extends AbstractBeanTest {
    
    @Spy
    protected PlayerBean inst;

    protected static final String VALID_PLAIN_ACCESS_TOKEN = "valid";
    protected static final String INVALID_ACCESS_TOKEN = "invalid";
    protected static final String DISPLAY_NAME = "user1";
    private static SimpleUserData emptyUser = new SimpleUserData();
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.rushHourSession = session;
        inst.pCon = pCon;
        inst.prop = PROP;
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        
    }
    
    @Test
    public void testInitNoSignIn() {
        inst.init();
        assertFalse(inst.isSignIn());
        assertEquals(emptyUser.getColor(), inst.getColor());
        assertEquals(emptyUser.getIconUrl(), inst.getIconUrl());
        assertEquals(emptyUser.getName(), inst.getName());;
        assertEquals(emptyUser.getTextColor(), inst.getTextColor());
    }
    
    @Test
    public void testInitSignIn() throws RushHourException {
        Player player = createPlayer();
        doReturn(player.getToken()).when(session).getToken();
        inst.init();
        assertTrue(inst.isSignIn());
        assertEquals(player.getInfo().getColor(), inst.getColor());
        assertEquals(player.getInfo().getIconUrl(), inst.getIconUrl());
        assertEquals(player.getInfo().getName(), inst.getName());
        assertEquals(player.getInfo().getTextColor(), inst.getTextColor());
    }
    
    @Test
    public void testLogOut() throws RushHourException, IOException {
        inst.logOut();
        verify(externalContext, times(1)).redirect(anyString());
    }
    
    @Test
    public void testGetFacesContext() {
        assertNull(new PlayerBean().getFacesContext());
    }
}

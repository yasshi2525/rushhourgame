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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class PlayerBeanTest extends AbstractBeanTest {

    protected static final String VALID_PLAIN_ACCESS_TOKEN = "valid";
    protected static final String INVALID_ACCESS_TOKEN = "invalid";
    protected static final String DISPLAY_NAME = "user1";
    protected OAuth oAuth;
    protected Player player;
    protected String accessToken;
    
    @Before
    public void setUp() {
        super.setUp();
        try {
            oAuth = oCon.createOAuthBean("foo", "foosec");
            player = pCon.createPlayer("foo", "user1", VALID_PLAIN_ACCESS_TOKEN, DISPLAY_NAME);
            accessToken = player.getToken();
        } catch (RushHourException ex) {
            fail();
        }
    }
    
    @After
    public void tearDown() {
        em.getTransaction().commit();
        tCon.clean();
    }
    
    @Test
    public void testIsSignIn() {
        PlayerBean inst = new LocalPlayerBean(pCon, true, accessToken);
        assertTrue(inst.isSignIn());
    }

    @Test
    public void testIsSignInNoSessionData() {
        PlayerBean inst = new LocalPlayerBean(pCon, false);
        assertFalse(inst.isSignIn());
    }
    
    @Test
    public void testIsSignInAccessTokenNull() {
        PlayerBean inst = new LocalPlayerBean(pCon, true);
        assertFalse(inst.isSignIn());
    }
    
    @Test
    public void testIsSignInInvalidAccessToken() {
        PlayerBean inst = new LocalPlayerBean(pCon, true, INVALID_ACCESS_TOKEN);
        assertFalse(inst.isSignIn());
    }
    
   @Test
    public void testGetDisplayName() throws RushHourException {
        PlayerBean inst = new LocalPlayerBean(pCon, true, accessToken);
        assertEquals(DISPLAY_NAME, inst.getDisplayName());
    }

    @Test
    public void testGetDisplayNameNoSessionData() throws RushHourException {
        PlayerBean inst = new LocalPlayerBean(pCon, false);
        assertNull(inst.getDisplayName());
    }
    
    @Test
    public void testGetDisplayNameAccessTokenNull() throws RushHourException {
        PlayerBean inst = new LocalPlayerBean(pCon, true);
        assertNull(inst.getDisplayName());
    }
    
    @Test
    public void testGetDisplayNameInvalidAccessToken() throws RushHourException {
        PlayerBean inst = new LocalPlayerBean(pCon, true, INVALID_ACCESS_TOKEN);
        assertNull(inst.getDisplayName());
    }
}

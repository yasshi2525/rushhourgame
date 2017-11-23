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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import net.rushhourgame.ErrorMessageBuilder;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterOAuthAccessTokenClient;
import net.rushhourgame.httpclient.TwitterUserShowClient;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.TwitterUserData;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TwitterOAuthAccessTokenBeanTest extends AbstractBeanTest {

    @Spy
    protected TwitterOAuthAccessTokenBean spy;
    @Mock
    protected TwitterOAuthAccessTokenClient client;
    @Mock
    protected TwitterUserShowClient showClient;
    @Mock
    protected TwitterUserData userData;
    @Mock
    protected ExternalContext context;

    public TwitterOAuthAccessTokenBeanTest() {
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        try {
            spy.oAuthController = OCON;
            spy.playerController = PCON;
            spy.rushHourSession = session;
            spy.client = client;
            spy.userShowClient = showClient;
            spy.errMsgBuilder = ErrorMessageBuilder.getInstance();
            doReturn("test_access").when(client).getAccessToken();
            doReturn("test_access_sec").when(client).getAccessTokenSecret();
            doReturn("test_user_id").when(client).getUserId();
            doReturn(context).when(spy).getExternalContext();
            doNothing().when(context).redirect(anyString());
            doReturn("test_name").when(userData).getName();
            doReturn("#123456").when(userData).getColor();
            doReturn("#123456").when(userData).getTextColor();
            doReturn("test_url").when(userData).getIconUrl();
            doReturn(userData).when(showClient).getUserData();
            OCON.createOAuthBean("test", "test_sec");
            PCON.createPlayer("test", "testId", "test_access", new SimpleUserData());
        } catch (IOException | RushHourException ex) {
            Logger.getLogger(TwitterOAuthAccessTokenBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testInit() {
        spy.requestToken = "test";
        spy.oAuthVerifier = "hoge";
        try {
            doReturn(new Player()).when(spy).fetchPlayer(anyString(), anyString(), anyString(), anyString());
            doNothing().when(spy).registerSessionAttribute(any());
            spy.init();
        } catch (RushHourException | IOException ex) {
            fail();
        }
    }

    @Test
    public void testInitRequestTokenNull() {
        spy.oAuthVerifier = "hoge";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN, ex.getErrMsg().getDetailId());
        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void testInitOAuthVerifierNull() {
        spy.requestToken = "test";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN, ex.getErrMsg().getDetailId());
        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void testInitUnexistRequestToken() {
        spy.requestToken = "unexist";
        spy.oAuthVerifier = "hoge";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_UNREGISTERED_REQ_TOKEN, ex.getErrMsg().getDetailId());
        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void testCreatePlayer() throws Exception {
        OCON.createOAuthBean("new", "new_sec");
        Player newPlayer = spy.fetchPlayer("new", "newId", "new_access", "new_access_sec");

        assertNotNull(newPlayer);
        assertEquals("newId", newPlayer.getUserId());
    }

    @Test
    public void testGetPlayer() throws RushHourException {
        Player fetchPlayer = spy.fetchPlayer("test", "testId", "new_access", "new_access_sec");
        assertNotNull(fetchPlayer);
        assertEquals("new_access", fetchPlayer.getToken());
    }

    @Test
    public void testCreatePlayerNull() {
        try {
            spy.fetchPlayer(null, null, null, null);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_USER_ID, ex.getErrMsg().getDetailId());
        }
    }
}

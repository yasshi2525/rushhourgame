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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import net.rushhourgame.ErrorMessageBuilder;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SignInType;
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
            doReturn(facesContext).when(spy).getFacesContext();
            doReturn(externalContext).when(facesContext).getExternalContext();
            doNothing().when(externalContext).redirect(anyString());
            doReturn("test_name").when(userData).getName();
            doReturn("#123456").when(userData).getColor();
            doReturn("#123456").when(userData).getTextColor();
            doReturn("test_url").when(userData).getIconUrl();
            doReturn(userData).when(showClient).getUserData();
            doReturn(Locale.getDefault()).when(session).getLocale();
            OCON.upsertRequestToken("test", "test_sec", SignInType.TWITTER);
        } catch (IOException | RushHourException ex) {
            Logger.getLogger(TwitterOAuthAccessTokenBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testInit() throws RushHourException, IOException {
        spy.requestToken = "test";
        spy.oAuthVerifier = "hoge";
        spy.init();
        verify(externalContext, times(1)).redirect(anyString());
    }

    @Test
    public void testInitRequestTokenNull() throws IOException {
        spy.oAuthVerifier = "hoge";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testInitOAuthVerifierNull() throws IOException {
        spy.requestToken = "test";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testInitUnexistRequestToken() throws IOException {
        spy.requestToken = "unexist";
        spy.oAuthVerifier = "hoge";
        try {
            spy.init();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_UNREGISTERED_REQ_TOKEN, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testFetchPlayer() throws Exception {
        Player newPlayer = spy.fetchPlayer();

        assertNotNull(newPlayer);
        assertEquals("test_user_id", newPlayer.getUserId());
        assertEquals(SignInType.TWITTER, newPlayer.getSignIn());
        assertEquals("#123456", newPlayer.getInfo().getColor());
    }
    
    @Test
    public void testBean() {
        spy.setOAuthVerifier("foo");
        assertEquals("foo", spy.getOAuthVerifier());
        spy.setRequestToken("bar");
        assertEquals("bar", spy.getRequestToken());
    }
}

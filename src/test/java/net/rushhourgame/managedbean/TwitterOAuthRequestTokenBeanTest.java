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
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL_GET_REQ_TOKEN_CALLBACK_NOT_CONFIRMED;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterOAuthRequestTokenClient;
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
public class TwitterOAuthRequestTokenBeanTest extends AbstractBeanTest{
    @Spy
    protected TwitterOAuthRequestTokenBean spy;
    
    @Mock
    TwitterOAuthRequestTokenClient client;
    
    @Mock
    ExternalContext context;
    
    @Before
    public void setUp() {
        super.setUp();
        try {
            doNothing().when(context).redirect(anyString());
        } catch (IOException ex) {
            Logger.getLogger(TwitterOAuthRequestTokenBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        spy.client = client;
        spy.oAuthController = OCON;
        spy.prop = PROP;
        doReturn(context).when(spy).getExternalContext();
    }

    @Test
    public void testRequestRequestToken() throws Exception {
       when(client.isOAuthCallBackConfirmedOK()).thenReturn(Boolean.TRUE);
       when(client.getRequestToken()).thenReturn("test");
       when(client.getRequestTokenSecret()).thenReturn("test_sec");
       
        spy.requestRequestToken();
        
        assertNotNull(OCON.findByRequestToken("test"));
        assertEquals("test", OCON.findByRequestToken("test").getRequestToken());
    }

    public void testRequestRequestTokenUnconfirmed() throws RushHourException{
        when(client.isOAuthCallBackConfirmedOK()).thenReturn(Boolean.FALSE);
        try {
            spy.requestRequestToken();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_REQ_TOKEN_CALLBACK_NOT_CONFIRMED, ex.getErrMsg().getDetailId());
        } catch (IOException ex) {
            fail();
        }
    }
}

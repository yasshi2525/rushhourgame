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
package net.rushhourgame.httpclient;

import java.io.UnsupportedEncodingException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.httpclient.TwitterClient.*;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TwitterClientTest {

    @Spy
    protected TwitterClient inst;
    protected static final String INVALID_URL = "http://127.0.0.1/";

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
        inst.prop = RushHourProperties.getInstance();
        inst.sigBuilder = new TwitterSignatureBuilder();
    }
    
    @Test
    public void testInit() {
        inst.init();
        assertNotNull(inst.authorizationHeaders.get(OAUTH_CONSUMER_KEY));
        assertNotNull(inst.authorizationHeaders.get(OAUTH_NONCE));
        assertNotNull(inst.authorizationHeaders.get(OAUTH_SIGNATURE_METHOD));
        assertNotNull(inst.authorizationHeaders.get(OAUTH_TIMESTAMP));
        assertNotNull(inst.authorizationHeaders.get(OAUTH_VERSION));
        assertEquals(5, inst.authorizationHeaders.size());
        
        assertEquals(5, inst.requestHeaders.get(AUTHORIZATION).size());
        assertNotNull(inst.sigBuilder.getConsumerSecret());
    }
    
    @Test
    public void testBuilderHeader() throws UnsupportedEncodingException{
        inst.init();
        assertNotNull(inst.buildHeader().get("Authorization"));
    }

    @Test
    public void testCreateNonce() {
        String createNonce = inst.createNonce();
        assertNotNull(createNonce);
        assertFalse(createNonce.contains("/"));
        assertFalse(createNonce.contains("+"));
        assertFalse(createNonce.contains("="));
    }
    
    @Test
    public void testBean() {
        inst.init();
        
        inst.setOAuthToken("foo");
        assertEquals("foo", inst.oAuthToken);
        inst.setOAuthTokenSecret("bar");
        assertEquals("bar", inst.oAuthTokenSecret);
    }
}

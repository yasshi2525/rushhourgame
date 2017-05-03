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
package net.rushhourgame.request;

import net.rushhourgame.request.OAuthRequester;
import net.rushhourgame.request.TwitterOAuthRequester;
import java.util.concurrent.Callable;
import org.apache.commons.codec.EncoderException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.SimpleExecutor;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.RushHourResourceBundle;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class TwitterOAuthRequesterTest {

    protected TwitterOAuthRequester inst;
    protected static final String INVALID_URL = "http://127.0.0.1/";
    protected static RushHourProperties prop;
    protected static RushHourResourceBundle resourceBundle;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        prop = RushHourProperties.getInstance();
        resourceBundle = RushHourResourceBundle.getInstance();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        inst = new TwitterOAuthRequester();
        inst.props = RushHourProperties.getInstance();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRequestNullResource() {
        try {
            inst.request();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
            assertEquals("resourceUrl is null", ex.getMessage());
        }
    }

    @Test
    public void testRequestInvalidResource() {
        inst.setResourceUrl(INVALID_URL);
        try {
            inst.request();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_CONNECTION_ERR, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testRequestValidResource() {
        OAuthRequester localInst = new LocalOAuthRequester();
        try {
            localInst.request();
            assertEquals(200, localInst.getResponseStatus());
            assertEquals(1, localInst.getResponseMap().size());
        } catch (RushHourException ex) {
            fail();
        }
    }

    @Test
    public void testRequestRepeat() {
        OAuthRequester localInst = new LocalOAuthRequester();
        try {
            localInst.request();
            assertEquals(200, localInst.getResponseStatus());
            localInst.request();
            assertEquals(200, localInst.getResponseStatus());
        } catch (RushHourException ex) {
            fail();
        }
    }

    @Test
    public void testGetResponseStatus() {
        exception.expect(NullPointerException.class);
        inst.getResponseStatus();

        inst.setResourceUrl(INVALID_URL);
        try {
            inst.request();
        } catch (RushHourException ex) {
            fail();
        }
        assertEquals(404, inst.getResponseStatus());
    }

    @Test
    public void testBuildHeaderURLNull() {
        try {
            inst.buildHeader();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
            assertEquals("unable to build signature because resourceUrl is null", ex.getMessage());
        } catch (EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testBuildHeader() {
        inst.setResourceUrl(INVALID_URL);
        try {
            assertNotNull(inst.buildHeader());
        } catch (RushHourException | EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testBuildSignatureURLNull() {
        try {
            inst.buildSignature();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
            assertEquals("unable to build signature because resourceUrl is null", ex.getMessage());
        } catch (EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testBuildSignatureURL() {
        inst.setResourceUrl(INVALID_URL);
        try {
            assertNotNull(inst.buildSignature());
        } catch (RushHourException | EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testCreateSignatureBaseStringNullURL() {
        try {
            inst.createSignatureBaseString();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
            assertEquals("unable to build signature because resourceUrl is null", ex.getMessage());
        } catch (EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testCreateSignatureBaseString() {
        inst.setResourceUrl(INVALID_URL);
        try {
            assertNotNull(inst.createSignatureBaseString());
        } catch (EncoderException | RushHourException ex) {
            fail();
        }
    }

    @Test
    public void testCreateParamForSignatureBaseString() {
        assertNotNull(inst.createParamForSignatureBaseString());
    }

    @Test
    public void testCreateParamForSignatureBaseStringEmpty() {
        inst.getParameters().clear();
        assertEquals("", inst.createParamForSignatureBaseString());
    }

    @Test
    public void testCreateParamForSignatureBaseStringSingle() {
        inst.getParameters().put("prm1", "val1");
        assertEquals("prm1=val1", inst.createParamForSignatureBaseString());
    }

    @Test
    public void testCreateParamForSignatureBaseStringConcat() {
        inst.getParameters().put("zzz", "xxx");
        inst.getParameters().put("prm1", "val1");
        assertEquals("prm1=val1&zzz=xxx", inst.createParamForSignatureBaseString());
    }
    
    @Test
    public void testCreateParamForSignatureBaseStringEscape() {
        inst.getParameters().put("&#%\\/@=$#'\"", "xxx");
        inst.getParameters().put("あいう", "一二三");
        assertNotNull(inst.createParamForSignatureBaseString());
    }

    @Test
    public void testCreateSigningKey() {
        try {
            assertEquals("dummy&", inst.createSigningKey());
            inst.setOauthTokenSecret("hoge");
            assertEquals("dummy&hoge", inst.createSigningKey());
        } catch (EncoderException ex) {
            fail();
        }
    }

    @Test
    public void testConvertQueryToMap() {
        assertEquals(0, inst.convertQueryToMap(null).size());
        assertEquals(0, inst.convertQueryToMap("").size());
        assertEquals(0, inst.convertQueryToMap("hoge").size());
        assertEquals(1, inst.convertQueryToMap("foo=bar").size());
        assertEquals(1, inst.convertQueryToMap("&foo=bar").size());
        assertEquals(1, inst.convertQueryToMap("&&foo=bar").size());
        assertEquals(1, inst.convertQueryToMap("foo=bar&").size());
        assertEquals(1, inst.convertQueryToMap("foo=bar&&").size());
        assertEquals(1, inst.convertQueryToMap("hoge&foo=bar").size());
        assertEquals(1, inst.convertQueryToMap("hoge&&foo=bar").size());
        assertEquals(1, inst.convertQueryToMap("foo=bar&hoge").size());
        assertEquals(1, inst.convertQueryToMap("hoge&foo=bar&hoge").size());
        assertEquals(2, inst.convertQueryToMap("prm1=val1&prm2=val2").size());
        assertEquals(2, inst.convertQueryToMap("&prm1=val1&prm2=val2").size());
        assertEquals(2, inst.convertQueryToMap("prm1=val1&prm2=val2&").size());
        assertEquals(2, inst.convertQueryToMap("prm1=val1&&prm2=val2").size());
        assertEquals(2, inst.convertQueryToMap("prm1=val1&hoge&prm2=val2").size());
    }
    
    @Test
    public void testCreateNonce(){
        String createNonce = inst.createNonce();
        assertNotNull(createNonce);
        assertFalse(createNonce.contains("/"));
        assertFalse(createNonce.contains("+"));
        assertFalse(createNonce.contains("="));
    }

    protected static class ConcurrentRequestTask implements Callable<Boolean> {

        protected TwitterOAuthRequester requester;

        public ConcurrentRequestTask(TwitterOAuthRequester requester) {
            this.requester = requester;
        }

        public Boolean call() throws InterruptedException {
            try {
                Thread.sleep(1000L);
                requester.request();
                return true;
            } catch (RushHourException ex) {
                return false;
            }
        }
    }
}

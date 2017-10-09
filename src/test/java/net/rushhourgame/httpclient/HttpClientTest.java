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
import java.util.TreeMap;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HttpClientTest {

    protected HttpClient inst;
    protected static final String INVALID_URL = "http://127.0.0.1/";

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Mock
    protected Response response;
    
    public HttpClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        inst = new LocalHttpClient();
        inst.resourceUrl = INVALID_URL;
        inst.httpMethod = HttpClient.HttpMethod.GET;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExecute() {
        try {
            inst.execute();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_CONNECTION_ERR, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExecuteNullResource() {
        try {
            inst.resourceUrl = null;
            inst.execute();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testExecuteNullHttpMethod() {
        try {
            inst.httpMethod = null;
            inst.execute();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_HTTP_METHOD, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testRequest() throws UnsupportedEncodingException, RushHourException {
        try {
            inst.request(null, null, null);
        } catch (ProcessingException e) {
            // OK
        } 
    }

    @Test
    public void testRequestNullURL() throws UnsupportedEncodingException {
        try {
            inst.resourceUrl = null;
            inst.request(null, null, null);
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testRequestNullHttpMethod() throws UnsupportedEncodingException {
        try {
            inst.httpMethod = null;
            inst.request(null, null, null);
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_HTTP_METHOD, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testVerifyResourceUrl(){
        try {
            inst.verifyResourceUrl();
        } catch (RushHourException ex) {
            fail();
        }
        inst.resourceUrl = null;
        try {
            inst.verifyResourceUrl();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testVerifyResponseCode(){
        when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        inst.response = response;
        try {
            inst.verifyResponseCode();
        } catch (RushHourException ex) {
            fail();
        }
        when(response.getStatus()).thenReturn(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        try {
            inst.verifyResponseCode();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_BAD_RES_STATUS, ex.getErrMsg().getDetailId());
            assertEquals(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), ex.getErrMsg().getDetailParams().get(0));
        }
    }
    
    @Test
    public void testVerifyResponseHeaderKey(){
        inst.responseHeaders = new TreeMap<>();
        try {
            inst.verifyResponseHeaderKey("unexist");
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_INVALID_RESPONSE, ex.getErrMsg().getDetailId());
        }
        inst.responseHeaders.put("exist", "ok");
        try {
            inst.verifyResponseHeaderKey("exist");
        } catch (RushHourException ex) {
            fail();
        }
    }
    
    @Test
    public void testBuildGETQuery() throws UnsupportedEncodingException{
        assertEquals("", inst.buildGETQuery());
        inst.getParameters.put("aaa", "bbb");
        assertEquals("?aaa=bbb", inst.buildGETQuery());
        inst.getParameters.put("ccc", "ddd");
        assertEquals("?aaa=bbb&ccc=ddd", inst.buildGETQuery());
    }

    @Test
    @Ignore
    public void testBuildPOSTParams() {
        
    }

    @Test
    public void testParseResponseData() {
        assertTrue(inst.parseResponseData().isEmpty());
    }

    @Test
    public void testParseQueryToMap() {
        assertEquals(0, inst.parseQueryToMap(null).size());
        assertEquals(0, inst.parseQueryToMap("").size());
        assertEquals(0, inst.parseQueryToMap("hoge").size());
        assertEquals(1, inst.parseQueryToMap("foo=bar").size());
        assertEquals(1, inst.parseQueryToMap("&foo=bar").size());
        assertEquals(1, inst.parseQueryToMap("&&foo=bar").size());
        assertEquals(1, inst.parseQueryToMap("foo=bar&").size());
        assertEquals(1, inst.parseQueryToMap("foo=bar&&").size());
        assertEquals(1, inst.parseQueryToMap("hoge&foo=bar").size());
        assertEquals(1, inst.parseQueryToMap("hoge&&foo=bar").size());
        assertEquals(1, inst.parseQueryToMap("foo=bar&hoge").size());
        assertEquals(1, inst.parseQueryToMap("hoge&foo=bar&hoge").size());
        assertEquals(2, inst.parseQueryToMap("prm1=val1&prm2=val2").size());
        assertEquals(2, inst.parseQueryToMap("&prm1=val1&prm2=val2").size());
        assertEquals(2, inst.parseQueryToMap("prm1=val1&prm2=val2&").size());
        assertEquals(2, inst.parseQueryToMap("prm1=val1&&prm2=val2").size());
        assertEquals(2, inst.parseQueryToMap("prm1=val1&hoge&prm2=val2").size());
    }
    
    @Test
    public void testEncodeUrl() throws UnsupportedEncodingException{
        assertEquals("test", inst.encodeURL("test"));
        assertEquals("space%20space", inst.encodeURL("space space"));
        assertEquals("plus%2Bplus", inst.encodeURL("plus+plus"));
    }
}

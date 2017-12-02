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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.RushHourProperties;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HttpClientTest {

    @Spy
    protected HttpClient inst;

    @Mock
    protected Response response;

    @Before
    public void setUp() {
        inst.prop = RushHourProperties.getInstance();
        inst.errMsgBuilder = ErrorMessageBuilder.getInstance();
    }
    
    @Test
    public void testExecute() throws RushHourException, UnsupportedEncodingException {
        doNothing().when(inst).verifyResourceUrl();
        doReturn(mock(SortedMap.class)).when(inst).buildHeader();
        doReturn(null).when(inst).buildPOSTParams();// Entityはfinalでmock化できない
        doReturn(response).when(inst).request(any(), any(), any());
        doNothing().when(inst).verifyResponseCode();
        doReturn(mock(SortedMap.class)).when(inst).parseResponseData();
        inst.execute();
        
        assertTrue(inst.isExecuted());
    }

    @Test
    public void testExecuteUnsupportedEncodingException() throws RushHourException, UnsupportedEncodingException {
        doNothing().when(inst).verifyResourceUrl();
        doThrow(UnsupportedEncodingException.class).when(inst).buildHeader();
        
        try {
            inst.execute();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_ENCODE, ex.getErrMsg().getDetailId());
            assertEquals(SIGNIN_FAIL_ACTION, ex.getErrMsg().getActionId());
        }
    }
    
    @Test
    public void testExecuteProcessingException() throws RushHourException, UnsupportedEncodingException {
        doNothing().when(inst).verifyResourceUrl();
        doReturn(mock(SortedMap.class)).when(inst).buildHeader();
        doReturn(null).when(inst).buildPOSTParams();// Entityはfinalでmock化できない
        doThrow(ProcessingException.class).when(inst).request(any(), any(), any());
        
        try {
            inst.execute();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_CONNECTION_ERR, ex.getErrMsg().getDetailId());
            assertEquals(SIGNIN_FAIL_ACTION, ex.getErrMsg().getActionId());
        }
    }

    @Test
    public void testRequest() throws UnsupportedEncodingException, RushHourException {
        WebTarget target = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        doReturn(target).when(inst).createTarget();
        doReturn(builder).when(target).request(any(MediaType.class));
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("foo", "bar");
        inst.httpMethod = HttpClient.HttpMethod.GET;
        
        inst.request(headers, new TreeMap<>(), null);
        
        verify(builder, times(1)).get();
    }
    
    @Test
    public void testRequestPost() throws UnsupportedEncodingException, RushHourException {
        WebTarget target = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        doReturn(target).when(inst).createTarget();
        doReturn(builder).when(target).request(any(MediaType.class));
        inst.httpMethod = HttpClient.HttpMethod.POST;
        
        inst.request(new TreeMap<>(), new TreeMap<>(), null);
        
        verify(builder, times(1)).post(any());
    }
    
    @Test
    public void testRequestNullHttpMethod() throws UnsupportedEncodingException {
        WebTarget target = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        doReturn(target).when(inst).createTarget();
        doReturn(builder).when(target).request(any(MediaType.class));
        
        try {
            inst.request(new TreeMap<>(), new TreeMap<>(), null);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_NO_HTTP_METHOD, ex.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, ex.getErrMsg().getActionId());
        }
    }
    
    @Test
    public void testCreateTarget() throws UnsupportedEncodingException {
        inst.resourceUrl = "http://127.0.0.1";
        assertNotNull(inst.createTarget());
    }
    
    @Test
    public void testVerifyResourceUrl() throws RushHourException{
        inst.resourceUrl = "hoge";

        inst.verifyResourceUrl();
    }
    
    @Test
    public void testVerifyResourceUrlNull() throws RushHourException{
        try {
            inst.verifyResourceUrl();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_NO_RESOURCE, ex.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, ex.getErrMsg().getActionId());
        }
    }
    
    @Test
    public void testVerifyResponseCode() throws RushHourException{
        when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        inst.response = response;
        
        inst.verifyResponseCode();
    }
    
    @Test
    public void testVerifyResponseCodeNot200() throws RushHourException{
        when(response.getStatus()).thenReturn(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        inst.response = response;
        try {
            inst.verifyResponseCode();
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_BAD_RES_STATUS, ex.getErrMsg().getDetailId());
            assertEquals(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), ex.getErrMsg().getDetailParams().get(0));
            assertEquals(SIGNIN_FAIL_ACTION, ex.getErrMsg().getActionId());
        }
    }
    
    @Test
    public void testVerifyResponseHeaderKey() throws RushHourException{
        inst.responseHeaders = new TreeMap<>();
        inst.responseHeaders.put("exist", "ok");
        inst.verifyResponseHeaderKey("exist");
    }
    
    @Test
    public void testVerifyResponseHeaderKeyUncontained() throws RushHourException{
        inst.responseHeaders = new TreeMap<>();
        try {
            inst.verifyResponseHeaderKey("unexist");
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL, ex.getErrMsg().getTitleId());
            assertEquals(SIGNIN_FAIL_INVALID_RESPONSE, ex.getErrMsg().getDetailId());
            assertEquals(SIGNIN_FAIL_ACTION, ex.getErrMsg().getActionId());
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
    public void testBuildPOSTParams() {
        assertNull(inst.buildPOSTParams());
    }
    
    @Test
    public void testParseResponseData() {
        inst.response = response;
        doReturn(null).when(response).readEntity(String.class);
        assertTrue(inst.parseResponseData().isEmpty());
    }

    @Test
    public void testParseResponseDataNull() {
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
    
    @Test
    public void testBean() {
        inst.setResourceUrl("hoge");
        assertEquals("hoge", inst.getResourceUrl());
        
        assertTrue(inst.getGetParameters().isEmpty());
        assertTrue(inst.getPostParameters().isEmpty());
        assertNull(inst.getResponseHeaders());
    }
}

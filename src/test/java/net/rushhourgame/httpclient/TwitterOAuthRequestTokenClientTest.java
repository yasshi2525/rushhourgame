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
package net.rushhourgame.httpclient;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.httpclient.TwitterOAuthRequestTokenClient.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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
public class TwitterOAuthRequestTokenClientTest {
    
    @Spy
    TwitterOAuthRequestTokenClient inst;
    
    @Spy
    TwitterSignatureBuilder sigBuilder;

    @Before
    public void setUp() throws RushHourException {
        inst.sigBuilder = sigBuilder;
        inst.prop = RushHourProperties.getInstance();
        doNothing().when(inst).verifyResponseHeaderKey(anyString());
    }
    
    @Test
    public void testInit() {
        inst.init();
        
        assertEquals(HttpMethod.POST, inst.httpMethod);
        assertNotNull(inst.resourceUrl);
        assertNotNull(inst.requestHeaders.get(AUTHORIZATION).get(OAUTH_CALLBACK));
        assertEquals("POST", inst.sigBuilder.getHttpMethod());
        assertNotNull(inst.sigBuilder.getBaseUrl());
        
    }

    @Test
    public void testBean() throws RushHourException {
        inst.responseHeaders = new HashMap<>();
        inst.responseHeaders.put(RES_OAUTH_CONFIRM, "true");
        inst.responseHeaders.put(RES_OAUTH_TOKEN, "token");
        inst.responseHeaders.put(RES_OAUTH_TOKEN_SEC, "sec");
        inst.postParameters.put(OAUTH_CALLBACK, "abc");
        assertTrue(inst.isOAuthCallBackConfirmedOK());
        assertEquals("true", inst.getOAuthCallBackConfirmed());
        assertEquals("token", inst.getRequestToken());
        assertEquals("sec", inst.getRequestTokenSecret());
        assertEquals("abc", inst.getOAuthCallBack());
    }
}

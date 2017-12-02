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

import java.util.TreeMap;
import javax.ws.rs.core.MediaType;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.entity.Player;
import static net.rushhourgame.httpclient.TwitterOAuthAccessTokenClient.*;
import net.rushhourgame.json.TwitterUserData;
import net.rushhourgame.json.TwitterUserDataParser;
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
public class TwitterUserShowClientTest {
    @Spy
    TwitterUserShowClient inst;
    
    @Spy
    TwitterSignatureBuilder sigBuilder;
    
    @Mock
    TwitterUserDataParser parser;
    
    @Before
    public void setUp() {
        inst.prop = RushHourProperties.getInstance();
        inst.sigBuilder = sigBuilder;
        inst.parser = parser;
    }

    @Test
    public void testInit() {
        inst.init();
        assertEquals(HttpClient.HttpMethod.GET, inst.httpMethod);
        assertEquals(MediaType.APPLICATION_JSON_TYPE, inst.mediaType);
        assertNotNull(inst.resourceUrl);
        
        assertEquals("GET", inst.sigBuilder.getHttpMethod());
        assertNotNull(inst.sigBuilder.getBaseUrl());
    }
    
    @Test
    public void testSetPlayer() {
        inst.requestHeaders.put(AUTHORIZATION, new TreeMap<>());
        
        Player p = mock(Player.class);
        doReturn("id").when(p).getUserId();
        doReturn("token").when(p).getAccessToken();
        doReturn("sec").when(p).getAccessTokenSecret();
        
        inst.setPlayer(p);
        
        assertEquals("id", inst.getParameters.get(USER_ID));
        assertEquals("token", inst.requestHeaders.get(AUTHORIZATION).get(OAUTH_TOKEN));
        assertEquals("sec", inst.sigBuilder.getOAuthTokenSecret());
    }

    @Test
    public void testGetUserDataCached() {
        doReturn(true).when(parser).isParsed();
        
        inst.getUserData();
    }
    
    @Test
    public void testGetUserDataUncached() {
        inst.responseHeader = "";
        doReturn(false).when(parser).isParsed();
        doReturn(mock(TwitterUserData.class)).when(parser).parse(anyString());
        inst.getUserData();
    }
    
}

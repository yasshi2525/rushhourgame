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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class TwitterOAuthAccessTokenClient extends TwitterClient implements Serializable{
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthAccessTokenClient.class.getName());
    protected static final String OAUTH_VERIFIER = "oauth_verifier";
    
    protected static final String ACCESS_TOKEN = "oauth_token";
    protected static final String ACCESS_TOKEN_SECRET = "oauth_token_secret";
    protected static final String USER_ID = "user_id";
    protected static final String SCREEN_NAME = "screen_name";
  
    protected String oauthTokenSecret;
    protected String oauthVerifier;
    
    @PostConstruct
    public void init(){
        super.init();
        LOG.log(Level.FINE, "{0}#init", this.getClass().getSimpleName());
        
        httpMethod = HttpMethod.POST;
        resourceUrl = prop.get(TWITTER_API_ACCESS_TOKEN);
        
        sigBuilder.setHttpMethod(httpMethod.toString());
        sigBuilder.setBaseUrl(resourceUrl);
    }

    public void setOAuthVerifier(String oauthVerifier) {
        if (oauthVerifier == null) {
            throw new IllegalArgumentException("oauthTokenSecret = null");
        }
        this.oauthVerifier = oauthTokenSecret;
        requestHeaders.get(AUTHIRIZATION).put(OAUTH_VERIFIER, oauthVerifier);
    }
    
    public String getAccessToken() throws RushHourException{
        verifyResponseHeaderKey(ACCESS_TOKEN);
        return responseHeaders.get(ACCESS_TOKEN);
    }
    
    public String getAccessTokenSecret() throws RushHourException{
        verifyResponseHeaderKey(ACCESS_TOKEN_SECRET);
        return responseHeaders.get(ACCESS_TOKEN_SECRET);
    }
    
    public String getUserId() throws RushHourException{
        verifyResponseHeaderKey(USER_ID);
        return responseHeaders.get(USER_ID);
    }
    
    public String getScreenName() throws RushHourException{
        verifyResponseHeaderKey(SCREEN_NAME);
        return responseHeaders.get(SCREEN_NAME);
    }
}

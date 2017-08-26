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
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourProperties.*;

/**
 * (1) リクエストトークンの取得
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class TwitterOAuthRequestTokenClient extends TwitterClient implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthRequestTokenClient.class.getName());

    protected static final String OAUTH_CALLBACK = "oauth_callback";
    protected static final String RES_OAUTH_TOKEN = "oauth_token";
    protected static final String RES_OAUTH_TOKEN_SEC = "oauth_token_secret";
    protected static final String RES_OAUTH_CONFIRM = "oauth_callback_confirmed";

    @PostConstruct
    public void init() {
        super.init();
        LOG.log(Level.FINE, "{0}#init", this.getClass().getSimpleName());
        
        httpMethod = HttpMethod.POST;
        resourceUrl = prop.get(TWITTER_API_REQ_TOKEN);

        requestHeaders.get(AUTHIRIZATION)
                .put(OAUTH_CALLBACK, prop.get(TWITTER_CALLBACK_URL));
        
        sigBuilder.setHttpMethod(httpMethod.toString());
        sigBuilder.setBaseUrl(resourceUrl);
    }

    public String getOAuthCallBack() {
        return postParameters.get(OAUTH_CALLBACK);
    }

    public boolean isOAuthCallBackConfirmedOK() throws RushHourException {
        verifyResponseHeaderKey(RES_OAUTH_CONFIRM);
        return Boolean.valueOf(responseHeaders.get(RES_OAUTH_CONFIRM));
    }

    public String getRequestToken() throws RushHourException {
        verifyResponseHeaderKey(RES_OAUTH_TOKEN);
        return responseHeaders.get(RES_OAUTH_TOKEN);
    }

    public String getRequestTokenSecret() throws RushHourException {
        verifyResponseHeaderKey(RES_OAUTH_TOKEN_SEC);
        return responseHeaders.get(RES_OAUTH_TOKEN_SEC);
    }

    public String getOAuthCallBackConfirmed() throws RushHourException {
        verifyResponseHeaderKey(RES_OAUTH_CONFIRM);
        return responseHeaders.get(RES_OAUTH_CONFIRM);
    }

}

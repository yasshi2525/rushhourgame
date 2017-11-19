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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.entity.Player;
import net.rushhourgame.json.TwitterUserData;
import net.rushhourgame.json.TwitterUserDataParser;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class TwitterUserShowClient extends TwitterClient {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TwitterUserShowClient.class.getName());
    protected static final String USER_ID = "user_id";

    @Inject
    protected TwitterUserDataParser parser;
    protected TwitterUserData data;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        LOG.log(Level.FINE, "{0}#init start", this.getClass().getSimpleName());
        httpMethod = HttpMethod.GET;
        mediaType = MediaType.APPLICATION_JSON_TYPE;
        resourceUrl = prop.get(RushHourProperties.TWITTER_API_USERS_SHOW);

        sigBuilder.setHttpMethod(httpMethod.toString());
        sigBuilder.setBaseUrl(resourceUrl);
    }

    public void setPlayer(String userId, String accessToken, String accessTokenSecret) {

        getParameters.put(USER_ID, userId);
        requestHeaders.get(AUTHIRIZATION).put(OAUTH_TOKEN, accessToken);
        sigBuilder.setOAuthTokenSecret(accessTokenSecret);
    }

    public void setPlayer(Player player) {
        
        setPlayer(player.getUserId(), player.getOauth().getAccessToken(), player.getOauth().getAccessTokenSecret());
    }

    public TwitterUserData getUserData() {
        if (!parser.isParsed()) {
            parser.parse(responseHeader);
        }
        return parser.getCache();
    }
}

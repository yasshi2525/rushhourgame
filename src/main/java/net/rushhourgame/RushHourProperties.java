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
package net.rushhourgame;

import java.util.Arrays;
import java.util.logging.Logger;
import javax.inject.Singleton;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Singleton
public class RushHourProperties extends AbstractProperties{

    private static final Logger LOG = Logger.getLogger(RushHourProperties.class.getName());
    
    protected static RushHourProperties instance = new RushHourProperties();
    
    public static final String DIGEST_SALT = "rushhour.digest.salt";
    public static final String DIGEST_ALGORITHM = "rushhour.digest.algorithm";
    public static final String ENCRYPT_KEY = "rushhour.encrypt.key";
    public static final String ENCRYPT_ALGORITHM = "rushhour.encrypt.algorithm";
    public static final String ENCRYPT_TRANSFORM = "rushhour.encrypt.transform";
    
    public static final String ADMINISTRATOR = "rushhour.administrator";
    public static final String LANG = "rushhour.lang";
    public static final String TWITTER_CONSUMER_KEY = "rushhour.twitter.consumerKey";
    public static final String TWITTER_CONSUMER_SECRET = "rushhour.twitter.consumerSecret";
    public static final String TWITTER_CALLBACK_URL = "rushhour.twitter.callbackUrl";
    public static final String TWITTER_API_REQ_TOKEN = "rushhour.twitter.api.request_token";
    public static final String TWITTER_API_ACCESS_TOKEN = "rushhour.twitter.api.access_token";
    public static final String TWITTER_API_AUTHENTICATE = "rushhour.twitter.api.authenticate";
    public static final String TWITTER_API_USERS_SHOW = "rushhouse.twitter.api.users.show";

    protected RushHourProperties() {
        super(Arrays.asList("config.properties", "rushhour.properties"));
    }
    
    /**
     * CDIが使えない場合
     * @return 
     */
    public static RushHourProperties getInstance(){
        return instance;
    }
}

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

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Singleton
public class RushHourResourceBundle extends AbstractResourceBundle {

    @Inject
    protected RushHourProperties prop;

    protected static RushHourResourceBundle instance;
    
    private static final Logger LOG = Logger.getLogger(RushHourResourceBundle.class.getName());

    public static final String LABEL_RUSHHOUR = "rushhour.label.rushhour";
    public static final String LABEL_RUSHHOUR_VER = "rushhour.label.rushhour.version";
    public static final String LABEL_ERROR = "rushhour.label.error";
    public static final String LABEL_DETAIL = "rushhour.label.detail";
    public static final String LABEL_ACTION = "rushhour.label.action";
    public static final String LABEL_TOP_TITLE = "rushhour.label.topTitle";
    public static final String LABEL_TWITTER_SIGNIN = "rushhour.label.twitterSignIn";
    public static final String LABEL_FETCH_ACCESS_TOKEN = "rushhour.label.fetchAccessToken";
    public static final String LABEL_WELCOME = "rushhour.label.welcome";
    public static final String LABEL_LANG = "rushhour.label.lang";
    public static final String LABEL_LANG_EN = "rushhour.label.lang.en";
    public static final String LABEL_LANG_JP = "rushhour.label.lang.jp";
    public static final String LABEL_LOGOUT = "rushhour.label.logout";

    public static final String SIGNIN_FAIL_ACTION = "rushhour.message.signin.fail.action";
    public static final String SIGNIN_FAIL = "rushhour.message.signin.fail";
    public static final String SIGNIN_FAIL_NO_RESOURCE = "rushhour.message.signin.fail.noResource";
    public static final String SIGNIN_FAIL_NO_HTTP_METHOD = "rushhour.message.signin.fail.noHttpMethod";
    public static final String SIGNIN_FAIL_ENCODE = "rushhour.message.signin.fail.encode";
    public static final String SIGNIN_FAIL_BAD_RES_STATUS = "rushhour.message.signin.fail.badResponseStatus";
    public static final String SIGNIN_FAIL_INVALID_RESPONSE = "rushhour.message.signin.fail.invalidResponse";
    public static final String SIGNIN_FAIL_CONNECTION_ERR = "rushhour.message.signin.fail.connectionError";
    public static final String SIGNIN_FAIL_GET_REQ_TOKEN_BADSTATUS = "rushhour.message.signin.fail.getRequestToken.badStatus";
    public static final String SIGNIN_FAIL_GET_REQ_TOKEN_CALLBACK_NOT_CONFIRMED = "rushhour.message.signin.fail.getRequestToken.invalidCallBack";
    public static final String SIGNIN_FAIL_GET_REQ_TOKEN_DUPLICATE = "rushhour.message.signin.fail.getRequestToken.duplicate";
    public static final String SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN = "rushhour.message.signin.fail.getAccessToken.noRequestToken";
    public static final String SIGNIN_FAIL_GET_ACCESS_TOKEN_UNREGISTERED_REQ_TOKEN = "rushhour.message.signin.fail.getAccessToken.unregisteredRequestToken";
    public static final String SIGNIN_FAIL_GET_ACCESS_TOKEN_BADSTATUS = "rushhour.message.signin.fail.getAccessToken.badStatus";
    public static final String SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_USER_ID = "rushhour.message.signin.fail.getAccessToken.duplicateUserId";
    public static final String SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_ACCESS_TOKEN = "rushhour.message.signin.fail.getAccessToken.duplicateAccessToken";
    public static final String SIGNIN_FAIL_DATA_INCONSISTENCY = "rushhour.message.signin.fail.dataInconsistency";

    public static final String REQUEST_FAIL = "rushhour.message.request.fail";
    public static final String REQUEST_FAIL_INVALID_TOKEN = "rushhour.message.request.fail.invalidToken";
    public static final String ACCOUNT_FAIL = "rushhour.message.account.fail";
    public static final String ACCOUNT_FAIL_UPDATE_ACCESS_TOKEN = "rushhour.message.account.fail.updateAccessToken";

    public static final String SERVER_ERR_ACTION = "rushhour.message.servererror.action";
    public static final String SYSTEM_ERR_ACTION = "rushhour.message.systemerror.action";

    public static final String UNKNOWN = "rushhour.message.unknown";
    public static final String UNKNOWN_DETAIL = "rushhour.message.unknown.detail";
    public static final String UNKNOWN_ACTION = "rushhour.message.unknown.action";

    protected RushHourResourceBundle() {
        super("rushhourmessage");
    }

    @PostConstruct
    protected void init() {
        LOG.log(Level.FINE, "{0}#init start", RushHourResourceBundle.class.getSimpleName());
        String langList = prop.get(RushHourProperties.LANG);
        if (langList == null || "".equals(langList)) {
            LOG.log(Level.FINE, "{0}#init langList equals null or empty, so fetch default locale = {1}", 
                    new Object[]{RushHourResourceBundle.class.getSimpleName(), Locale.getDefault()});
            fetch(Locale.getDefault());
        }else{
            String[] langs = langList.split(",");
            for(String lang : langs){
                LOG.log(Level.FINE, "{0}#init fetch {1} (val = {2})", 
                        new Object[]{RushHourResourceBundle.class.getSimpleName(), new Locale(lang), lang});
                fetch(new Locale(lang));
            }
        }
    }
    
    public static RushHourResourceBundle getInstance(){
        if(instance == null){
            instance = new RushHourResourceBundle();
            instance.prop = RushHourProperties.getInstance();
            instance.init();
        }
        
        return instance;
    }
}

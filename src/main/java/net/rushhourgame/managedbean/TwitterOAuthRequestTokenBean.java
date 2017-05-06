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
package net.rushhourgame.managedbean;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.OAuthController;
import net.rushhourgame.httpclient.TwitterOAuthRequestTokenClient;

/**
 * (1) Twitter のリクエストトークンを取得する
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named("twitterOAuthRequestToken")
@ViewScoped
public class TwitterOAuthRequestTokenBean extends AbstractTwitterOAuthBean implements Serializable {

    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthRequestTokenBean.class.getName());
    protected static final String OAUTH_CALLBACK = "oauth_callback";
    @Inject
    transient protected OAuthController oAuthController;
    @Inject
    transient protected TwitterOAuthRequestTokenClient client;
    protected boolean isPressed;

    /**
     * リクエストトークンを取得し、リダイレクトする.
     *
     * @throws IOException
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Transactional
    public void requestRequestToken() throws IOException, RushHourException {
        // リクエストトークンの取得
        client.execute();

        // コールバックURLの確認完了通知
        if (client.isOAuthCallBackConfirmedOK()) {
            
            // 認証情報の保存
            oAuthController.createOAuthBean(
                    client.getRequestToken(),
                    client.getRequestTokenSecret());
            //アクセストークン取得のためにTwitterにリダイレクト
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(prop.get(TWITTER_API_AUTHENTICATE) + "?oauth_token=" + client.getRequestToken());
      
        } else {
            // コールバックURLの設定値不正
            LOG.log(Level.SEVERE, "{0}#requestRequestToken"
                    + " unable to get request_token because of invalid callback url {1}",
                    new Object[]{this.getClass().getSimpleName(), 
                        client.getOAuthCallBack()
                    });
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL,
                            SIGNIN_FAIL_GET_REQ_TOKEN_CALLBACK_NOT_CONFIRMED,
                            client.getOAuthCallBack()),
                    "oauth_callback_confirmed = " + client.getOAuthCallBackConfirmed()
            );
        }
    }
    
    public void actionListener(){
        isPressed = true;
    }

    public boolean isPressed() {
        return isPressed;
    }
}

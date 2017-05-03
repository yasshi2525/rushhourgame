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

/**
 * Twitter のリクエストトークンを取得する
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named
@ViewScoped
public class TwitterOAuthRequestTokenManagedBean extends AbstractTwitterOAuthManagedBean implements Serializable {

    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthRequestTokenManagedBean.class.getName());
    protected static final String OAUTH_CALLBACK = "oauth_callback";
    @Inject
    transient protected OAuthController oAuthController;

    /**
     * リクエストトークンを取得し、リダイレクトする.
     *
     * @throws IOException
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Transactional
    public void requestRequestToken() throws IOException, RushHourException {
        // リクエストトークンを取得するためのヘッダを作成
        requester.getParameters().put(OAUTH_CALLBACK, prop.get(TWITTER_CALLBACK_URL));
        requester.setResourceUrl(prop.get(TWITTER_API_REQ_TOKEN));

        // リクエストトークンの取得
        requester.request();

        Map<String, String> responseMap = requester.getResponseMap();

        // 必要なパラメータが帰ってこなかった
        if (!responseMap.containsKey("oauth_callback_confirmed")) {
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL, SIGNIN_FAIL_INVALID_RESPONSE),
                    "invalid response : " + responseMap
            );
        }

        if (Boolean.valueOf(responseMap.get("oauth_callback_confirmed"))) {
            // パラメータが存在しない
            if (!responseMap.containsKey("oauth_token") || !responseMap.containsKey("oauth_token_secret")) {
                throw new RushHourException(ErrorMessage.createReSignInError(
                        SIGNIN_FAIL, SIGNIN_FAIL_INVALID_RESPONSE),
                        "invalid response : " + responseMap
                );
            }

            oAuthController.createOAuthBean(
                    responseMap.get("oauth_token"),
                    responseMap.get("oauth_token_secret"));
            //アクセストークン取得のためにリダイレクト
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(prop.get(TWITTER_API_AUTHENTICATE) + "?oauth_token=" + responseMap.get("oauth_token"));
        } else {
            // コールバックURLの設定値不正
            LOG.log(Level.SEVERE, "TwitterOAuthRequestTokenManagedBean#requestRequestToken"
                    + " unable to get request_token because of invalid callback url {0}",
                    requester.getParameters().get(OAUTH_CALLBACK));
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL,
                            SIGNIN_FAIL_GET_REQ_TOKEN_CALLBACK_NOT_CONFIRMED,
                            responseMap.get("oauth_callback_confirmed")),
                    "oauth_callback_confirmed = " + responseMap.get("oauth_callback_confirmed")
            );
        }
    }
}

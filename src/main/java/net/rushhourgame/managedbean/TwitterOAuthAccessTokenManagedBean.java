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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.entity.OAuthController;
import net.rushhourgame.entity.PlayerController;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.Player;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named
@ViewScoped
public class TwitterOAuthAccessTokenManagedBean extends AbstractTwitterOAuthManagedBean implements Serializable {
    
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthAccessTokenManagedBean.class.getName());
    protected static String OAUTH_VERIFIER = "oauth_verifier";
    protected static String OAUTH_TOKEN = "oauth_token";
    
    protected String requestToken;
    protected String oauthVerifier;
    
    @Inject
    transient protected OAuthController oAuthController;
    @Inject
    transient protected PlayerController playerController;
    @Inject
    transient protected RushHourSession rushHourSession;

    /**
     * access tokenを取得する
     *
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Transactional
    public void init() throws RushHourException, IOException {
        if (requestToken == null) {
            // GETパラメータからリクエストトークンを受け取れない
            LOG.log(Level.SEVERE, "TwitterOAuthAccessTokenManagedBean#init requestToken is null");
            
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN
            ), "requestToken is null");
        }
        
        if (!oAuthController.isRegisteredRequestToken(requestToken)) {
            // リクエストトークンがテーブルに存在しない
            LOG.log(Level.SEVERE, "TwitterOAuthAccessTokenManagedBean#init unregistered requestToken = {0}", requestToken);
            
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_UNREGISTERED_REQ_TOKEN
            ), "requestToken " + requestToken + " is not registered in OAuth table");
        }

        // verifier を登録
        OAuth oAuth = oAuthController.findByRequestToken(requestToken);
        oAuth.setOauthVerifier(oauthVerifier);

        // アクセストークンの取得
        Map<String, String> res = requestAccessToken(oAuth);

        // 必要なパラメータが帰ってこなかった
        if(!res.containsKey("oauth_token") || !res.containsKey("oauth_token_secret")
                || !res.containsKey("user_id") || !res.containsKey("screen_name")){
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL, SIGNIN_FAIL_INVALID_RESPONSE),
                    "invalid response : " + res
            );
        }
        
        // OAuthテーブルに登録
        updateOAuthAccessToken(oAuth, res.get("oauth_token"), res.get("oauth_token_secret"));

        // プレイヤーデータの取得 (存在しない場合は取得)
        Player p = fetchPlayer(requestToken, res.get("user_id"), res.get("oauth_token"), res.get("screen_name"));
        
        // セッションへデータを登録
        registerSessionAttribute(p);
        
        getExternalContext().redirect(MYPAGE);
    }

    /**
     * アクセストークンを取得する
     *
     * @param oAuth
     * @throws net.rushhourgame.exception.RushHourException
     * @return
     */
    protected Map<String, String> requestAccessToken(OAuth oAuth) throws RushHourException {
        requester.setResourceUrl(prop.get(TWITTER_API_ACCESS_TOKEN));
        requester.getParameters().put(OAUTH_TOKEN, requestToken);
        requester.getParameters().put(OAUTH_VERIFIER, oauthVerifier);
        requester.setOauthTokenSecret(oAuth.getRequestTokenSecret());
        
        requester.request();
        return requester.getResponseMap();
    }

    /**
     * アクセストークンを oAuth テーブルに登録
     *
     * @param oAuth
     * @param accessToken
     * @param accessTokenSecret
     */
    protected void updateOAuthAccessToken(OAuth oAuth, String accessToken, String accessTokenSecret) {
        oAuth.setAccessToken(accessToken);
        oAuth.setAccessTokenSecret(accessTokenSecret);
    }

    /**
     * プレイヤーデータを取得.
     * 存在しない場合は作成
     *
     * @param requestToken
     * @param userId
     * @param accessToken
     * @param displayName
     * @return
     * @throws net.rushhourgame.exception.RushHourException
     */
    protected Player fetchPlayer(String requestToken, String userId, String accessToken, 
            String displayName) throws RushHourException {
        if (!playerController.existsUserId(userId)) {
            //新規ユーザ登録
            LOG.log(Level.INFO,
                    "TwitterOAuthAccessTokenManagedBean#fetchPlayer create new user : id = {0}", userId);
            return playerController.createPlayer(requestToken, userId, accessToken, displayName, rushHourSession.getLocale());
        } else {
            //既存ユーザを取得
            Player player = playerController.findByUserId(userId);
            //アクセストークンの値を更新
            playerController.updateToken(player, requestToken, accessToken);
            return player;
        }
    }

    /**
     * セッション情報にアクセストークン情報とロケールを追加.
     * セッションよりもDBに登録されているロケールが優先される
     *
     * @param player
     */
    protected void registerSessionAttribute(Player player) {
        // セッションにアクセストークンとロケールを追加
        rushHourSession.setAccessToken(player.getToken());
        rushHourSession.setLocale(player.getLocale());
    }
    
    public String getRequestToken() {
        return requestToken;
    }
    
    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
    
    public String getOauthVerifier() {
        return oauthVerifier;
    }
    
    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }
}

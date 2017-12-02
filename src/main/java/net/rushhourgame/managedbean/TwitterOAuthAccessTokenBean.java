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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterOAuthAccessTokenClient;
import net.rushhourgame.httpclient.TwitterUserShowClient;

/**
 * (2) Twitter のアクセストークンを取得する
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named("twitterOAuthAccessToken")
@ViewScoped
public class TwitterOAuthAccessTokenBean extends AbstractTwitterOAuthBean {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthAccessTokenBean.class.getName());
    protected final static String OAUTH_VERIFIER = "oauth_verifier";
    protected final static String OAUTH_TOKEN = "oauth_token";

    protected String requestToken;
    protected String oAuthVerifier;

    @Inject
    protected OAuthController oAuthController;
    @Inject
    protected PlayerController playerController;
    @Inject
    protected RushHourSession rushHourSession;
    @Inject
    protected TwitterOAuthAccessTokenClient client;
    /**
     * 表示名、アイコンのURLを取得
     */
    @Inject
    protected TwitterUserShowClient userShowClient;

    /**
     * access tokenを取得する
     *
     * @throws net.rushhourgame.exception.RushHourException 例外
     * @throws java.io.IOException 例外
     */
    @Transactional
    public void init() throws RushHourException, IOException {
        if (requestToken == null || oAuthVerifier == null) {
            // GETパラメータからリクエストトークンを受け取れない
            LOG.log(Level.SEVERE, "TwitterOAuthAccessTokenManagedBean#init requestToken is null");

            throw new RushHourException(errMsgBuilder.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_NO_REQ_TOKEN
            ), "requestToken or oAuthVerifier is null");
        }

        // 秘密リクエストトークンの取得
        OAuth oAuth = oAuthController.findByRequestToken(requestToken, SignInType.TWITTER);

        if (oAuth == null) {
            // リクエストトークンがテーブルに存在しない
            LOG.log(Level.SEVERE, "TwitterOAuthAccessTokenManagedBean#init unregistered requestToken = {0}", requestToken);

            throw new RushHourException(errMsgBuilder.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_UNREGISTERED_REQ_TOKEN
            ), "requestToken " + requestToken + " is not registered in OAuth table");
        }

        // アクセストークンの取得
        executeRequest(oAuth);

        // プレイヤーデータの取得 (存在しない場合は取得)
        Player p = fetchPlayer();

        // セッションへデータを登録
        registerSessionAttribute(p);

        getFacesContext().getExternalContext().redirect(MYPAGE);
    }

    protected void executeRequest(OAuth oAuth) throws RushHourException {
        client.setOAuthToken(requestToken);
        client.setOAuthVerifier(oAuthVerifier);
        client.setOAuthTokenSecret(oAuth.getRequestTokenSecret());

        // リクエストの実行
        client.execute();
    }

    /**
     * プレイヤーデータを取得. 存在しない場合は作成
     *
     * @return Player
     * @throws net.rushhourgame.exception.RushHourException 例外
     */
    protected Player fetchPlayer() throws RushHourException {
        //ユーザデータを取得
        userShowClient.setPlayer(
                client.getUserId(),
                client.getAccessToken(),
                client.getAccessTokenSecret());
        userShowClient.execute();

        return playerController.upsertPlayer(
                client.getAccessToken(),
                client.getAccessTokenSecret(),
                client.getUserId(),
                SignInType.TWITTER,
                userShowClient.getUserData(),
                rushHourSession.getLocale());
    }

    /**
     * セッション情報にアクセストークン情報とロケールを追加. セッションよりもDBに登録されているロケールが優先される
     *
     * @param player player
     */
    protected void registerSessionAttribute(Player player) {
        // セッションにアクセストークンとロケールを追加
        rushHourSession.setToken(player.getToken());
        rushHourSession.setLocale(player.getInfo().getLocale());
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getOAuthVerifier() {
        return oAuthVerifier;
    }

    public void setOAuthVerifier(String oauthVerifier) {
        this.oAuthVerifier = oauthVerifier;
    }
}

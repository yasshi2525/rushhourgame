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
package net.rushhourgame.entity;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import net.rushhourgame.ErrorMessage;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class PlayerController extends AbstractController {

    private final int serialVersionUID = 1;
    private final Player dummyInst = new Player();
    private static final Logger LOG = Logger.getLogger(PlayerController.class.getName());
    @Inject
    protected OAuthController oCon;
    @Inject
    protected DigestCalculator calculator;
    
    public Player createPlayer(String requestToken, String plainUserId, 
            String plainAccessToken, String displayName) throws RushHourException {
        return createPlayer(requestToken, plainUserId, plainAccessToken, displayName, Locale.getDefault());
    }
    
    public Player createPlayer(String requestToken, String plainUserId, 
            String plainAccessToken, String displayName, Locale locale) throws RushHourException {
        // idはTwitterのIDのダイジェストを使う
        // tokenはTwitterのaccessTokenのダイジェストを使う
        String userIdDigest;
        String tokenDigest;
        try {
            userIdDigest = calculator.calcDigest(plainUserId);
            tokenDigest = calculator.calcDigest(plainAccessToken);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "PlayerController#createPlayer", ex);
            throw new RushHourException(ErrorMessage.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
        
        if (existsUserId(userIdDigest)) {
            // ユーザID重複
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_USER_ID),
                    "User Id is already registered : " + userIdDigest);
        }
        if (existsToken(tokenDigest)) {
            //アクセストークン重複
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_ACCESS_TOKEN),
                    "User accessToken is already registered : " + tokenDigest);
        }
        Player p = new Player();
        p.setId(userIdDigest);
        p.setUserId(plainUserId);
        p.setToken(tokenDigest);
        p.setDisplayName(displayName);
        if (locale != null) {
            p.setLocale(locale);
        }else{
            p.setLocale(Locale.getDefault());
        }
        p.setOauth(oCon.findByRequestToken(requestToken));
        em.persist(p);
        return p;
    }

    public boolean existsUserId(String userId) {
        return exists("Player.existsId", "id", userId);
    }

    public boolean existsToken(String token) {
        return exists("Player.existsToken", "token", token);
    }

    public Player findByUserId(String userId) {
        return findBy("Player.findById", "id", userId, dummyInst);
    }

    public Player findByToken(String token) {
        return findBy("Player.findByToken", "token", token, dummyInst);
    }

    public boolean isValidToken(String token) {
        return exists("Player.existsToken", "token", token);
    }

    /**
     * OAuthのひもづきも新しくする.
     * @param p
     * @param requestToken
     * @param newPlainAccessToken
     * @throws RushHourException 
     */
    public void updateToken(Player p, String requestToken, String newPlainAccessToken) throws RushHourException {
        p.setOauth(oCon.findByRequestToken(requestToken));
        
        String newAccessToken;
        try {
            newAccessToken = calculator.calcDigest(newPlainAccessToken);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "PlayerController#updateToken", ex);
            throw new RushHourException(ErrorMessage.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
        
        if (existsToken(newAccessToken)) {
            throw new RushHourException(
                    ErrorMessage.createRetryError(ACCOUNT_FAIL, ACCOUNT_FAIL_UPDATE_ACCESS_TOKEN),
                    "accessToken is already existed : " + newAccessToken
            );
        }
        p.setToken(newPlainAccessToken);
    }

    public void clearToken(String token) {
        try {
            Player player = findByToken(token);
            if (player != null) {
                player.setToken(null);
                player.setOauth(null);
                LOG.log(Level.INFO, "PlayerController#clearToken clear access token of {0}", player.getId());
            }
        } catch (NoResultException e) {
            LOG.log(Level.INFO, "PlayerController#clearToken"
                    + " access token have already been remoevd. {0}", token);
        }
    }
}

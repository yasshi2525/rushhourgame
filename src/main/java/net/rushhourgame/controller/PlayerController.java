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
package net.rushhourgame.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RoleType;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.OwnerInfo;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.UserData;

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
    
    public Player createPlayer(
            String requestToken, 
            String plainUserId, 
            String plainAccessToken, 
            UserData userData) throws RushHourException {
        return createPlayer(
                requestToken, 
                plainUserId, 
                plainAccessToken, 
                userData,
                Locale.getDefault());
    }
    
    public Player createPlayer(
            String requestToken, 
            String plainUserId, 
            String plainAccessToken, 
            UserData userData,
            Locale locale) throws RushHourException {
        if(plainUserId == null){
            throw new RushHourException(ErrorMessage.createReSignInError(SIGNIN_FAIL, 
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_USER_ID, "null"));
        }
        if(plainAccessToken == null){
            throw new RushHourException(ErrorMessage.createReSignInError(SIGNIN_FAIL, 
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_ACCESS_TOKEN, "null"));
        }
        
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
        
        if (existsUserId(plainUserId)) {
            // ユーザID重複
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_USER_ID),
                    "User Id is already registered : digest(" + userIdDigest + ")");
        }
        if (existsToken(tokenDigest)) {
            //アクセストークン重複
            throw new RushHourException(ErrorMessage.createReSignInError(
                    SIGNIN_FAIL,
                    SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_ACCESS_TOKEN),
                    "User accessToken is already registered : " + tokenDigest);
        }
        OwnerInfo info = new OwnerInfo();
        info.include(userData);
        if (locale != null) {
            info.setLocale(locale);
        }else{
            info.setLocale(Locale.getDefault());
        }
        
        Player p = new Player();
        p.getRoles().add(RoleType.PLAYER);
        p.setId(userIdDigest);
        p.setUserId(plainUserId);
        p.setToken(tokenDigest);
        
        p.setInfo(info);
        
        p.setOauth(oCon.findByRequestToken(requestToken));
        em.persist(p);
        return p;
    }

    public boolean existsUserId(String userId) throws RushHourException {
        if(userId == null){
            return false;
        }
        try {
            String id = calculator.calcDigest(userId);
            return exists("Player.existsId", "id", id);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, this.getClass().getSimpleName() +  "#existsUserId", ex);
            throw new RushHourException(ErrorMessage.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
    }

    public boolean existsToken(String token) {
        return exists("Player.existsToken", "token", token);
    }

    public Player findByUserId(String userId) throws RushHourException {
        if(userId == null){
            return null;
        }
        try {
            String id = calculator.calcDigest(userId);
            return findBy("Player.findById", "id", id, dummyInst);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, this.getClass().getSimpleName() +  "#findByUserId", ex);
            throw new RushHourException(ErrorMessage.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
    }

    public Player findByToken(String token) {
        if(token == null){
            return null;
        }
        return findBy("Player.findByToken", "token", token, dummyInst);
    }

    public boolean isValidToken(String token) {
        if(token == null){
            return false;
        }
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
        if(newPlainAccessToken == null){
            throw new RushHourException(
                    ErrorMessage.createReSignInError(SIGNIN_FAIL, 
                            SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_ACCESS_TOKEN, "null")
            );
        }
        //値が同じ場合は更新しない
        String oldToken = p.getToken();
        
        p.setOauth(oCon.findByRequestToken(requestToken));
        
        String newToken;
        try {
            newToken = calculator.calcDigest(newPlainAccessToken);
            if(newToken.equals(oldToken)){
            LOG.log(Level.INFO, "PlayerController#updateToken old token = new token");
                return;
            }
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "PlayerController#updateToken", ex);
            throw new RushHourException(ErrorMessage.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
        
        if (existsToken(newToken)) {
            throw new RushHourException(
                    ErrorMessage.createRetryError(ACCOUNT_FAIL, ACCOUNT_FAIL_UPDATE_ACCESS_TOKEN),
                    "token is already existed : " + newToken
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
                LOG.log(Level.FINE, "PlayerController#clearToken clear token : {0}", token);
            }
        } catch (NoResultException e) {
            LOG.log(Level.INFO, "PlayerController#clearToken"
                    + " access token have already been remoevd. {0}", token);
        }
    }
}

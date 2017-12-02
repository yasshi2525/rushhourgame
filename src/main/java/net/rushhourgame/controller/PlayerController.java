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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.Player;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.EncryptConverter;
import net.rushhourgame.entity.PlayerInfo;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.UserData;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class PlayerController extends AbstractController {

    private static final long serialVersionUID = 1L;
    private final Player dummyInst = new Player();
    private static final Logger LOG = Logger.getLogger(PlayerController.class.getName());
    @Inject
    protected OAuthController oCon;

    @Inject
    protected DigestCalculator calculator;

    public Player upsertPlayer(
            @NotNull String accessToken,
            @NotNull String accessTokenSecret,
            @NotNull String userId,
            @NotNull SignInType signIn,
            @NotNull UserData userData,
            @NotNull Locale locale) throws RushHourException {

        Player p = findByUserId(userId, signIn);
        boolean isNew = p == null;

        if (isNew) {
            p = new Player();
            p.setInfo(new PlayerInfo());
        }

        p.setUserId(userId);
        p.setAccessToken(accessToken);
        p.setAccessTokenSecret(accessTokenSecret);
        p.setSignIn(signIn);
        p.getInfo().include(userData);
        p.getInfo().setLocale(locale);

        try {
            p.setUserIdDigest(calculator.calcDigest(userId));
            // tokenはTwitterのaccessTokenのダイジェストを使う
            p.setToken(calculator.calcDigest(accessToken));
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "PlayerController#upsertPlayer", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, UNKNOWN_DETAIL));
        }

        if (isNew) {
            em.persist(p);
        } else {
            p = em.merge(p);
        }
        return p;
    }

    public boolean existsUserId(String userId, @NotNull SignInType type) throws RushHourException {
        return findByUserId(userId, type) != null;
    }

    public boolean existsToken(String token) {
        return exists("Player.existsToken", "token", token);
    }

    public Player findByUserId(String userId, @NotNull SignInType signIn) throws RushHourException {
        if (userId == null) {
            return null;
        }
        try {
            String userIdDigest = calculator.calcDigest(userId);
            return em.createNamedQuery("Player.findByUserIdDigest", Player.class)
                    .setParameter("userIdDigest", userIdDigest)
                    .setParameter("signIn", signIn)
                    .getSingleResult();
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "PlayerController#findByUserId", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, UNKNOWN_DETAIL));
        } catch (NoResultException e) {
            return null;
        }
    }

    public Player findByToken(String token) {
        if (token == null) {
            return null;
        }
        return findBy("Player.findByToken", "token", token, dummyInst);
    }

    public boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        return findByToken(token) != null;
    }

    public void clearToken(String token) {
        Player player = findByToken(token);
        if (player != null) {
            player.setToken(null);
            em.merge(player);
            LOG.log(Level.FINE, "PlayerController#clearToken clear token : {0}", token);
        } else {
            LOG.log(Level.INFO, "PlayerController#clearToken"
                    + " access token have already been remoevd. {0}", token);
        }
    }
}

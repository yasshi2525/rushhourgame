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
import java.util.Calendar;
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
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.EncryptConverter;
import net.rushhourgame.entity.Player;
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
public class OAuthController extends AbstractController {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OAuthController.class.getName());

    private final OAuth dummyInst = new OAuth();

    @Inject
    protected DigestCalculator calculator;

    public OAuth upsertRequestToken(
            @NotNull String requestToken,
            @NotNull String requestTokenSecret,
            @NotNull SignInType signInType) throws RushHourException {

        OAuth obj = findByRequestToken(requestToken, signInType);
        boolean isNew = obj == null;

        if (isNew) {
            obj = new OAuth();
        }

        obj.setRequestToken(requestToken);
        try {
            obj.setRequestTokenDigest(calculator.calcDigest(requestToken));
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "OAuthController#upsertRequestToken", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, UNKNOWN_DETAIL));
        }
        obj.setRequestTokenSecret(requestTokenSecret);
        obj.setSignIn(signInType);

        if (isNew) {
            em.persist(obj);
        } else {
            obj = em.merge(obj);
        }
        return obj;
    }

    /**
     * DBに登録されている requestTokenか
     *
     * @param requestToken requestToken
     * @param signIn signIn
     * @return boolean
     * @throws net.rushhourgame.exception.RushHourException RushHourException
     */
    public boolean isRegisteredRequestToken(@NotNull String requestToken, @NotNull SignInType signIn) throws RushHourException {
        return findByRequestToken(requestToken, signIn) != null;
    }

    public OAuth findByRequestToken(@NotNull String requestToken, @NotNull SignInType signIn) throws RushHourException {
        try {
            String requestTokenDigest = calculator.calcDigest(requestToken);
            return em.createNamedQuery("OAuth.findByRequestTokenDigest", OAuth.class)
                    .setParameter("requestTokenDigest", requestTokenDigest)
                    .setParameter("signIn", signIn)
                    .getSingleResult();
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "OAuthController#findByRequestToken", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, UNKNOWN_DETAIL));
        } catch (NoResultException e) {
            return null;
        }
    }

    public void purgeOld(int purgeDay) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -purgeDay);
        System.out.println(instance.getTime());
        int val = em.createNamedQuery("OAuth.purgeOld")
                .setParameter("threshold", instance.getTime())
                .executeUpdate();
        LOG.log(Level.INFO, "{0}#purgeOld {1} entries",
                new Object[]{this.getClass().getSimpleName(), val});
    }
}

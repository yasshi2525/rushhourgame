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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.entity.OAuth;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.exception.RushHourException;

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

    public OAuth createOAuthBean(String requestToken, String requestTokenSecret) throws RushHourException {
        if(requestToken == null){
            throw new RushHourException(errMsgBuilder.createReSignInError(
                    SIGNIN_FAIL, SIGNIN_FAIL_GET_REQ_TOKEN_INVALID_REQ_TOKEN, "null"));
        }
        try {
            // IDはrequestTokenのダイジェスト
            // requestTokenを主キーにするため。
            String requestTokenDigest = calculator.calcDigest(requestToken);
            
            if(isRegisteredRequestToken(requestToken)){
                throw new RushHourException(errMsgBuilder.createReSignInError(
                        SIGNIN_FAIL,
                        SIGNIN_FAIL_GET_REQ_TOKEN_DUPLICATE,
                        "Your request_token is already registerd."),
                        "already exists : " + requestToken + " ( digest = " + requestTokenDigest + " )");
            }
            
            OAuth oAuth = new OAuth();
            oAuth.setRequestTokenDigest(requestTokenDigest);
            oAuth.setRequestToken(requestToken);
            oAuth.setRequestTokenSecret(requestTokenSecret);
            em.persist(oAuth);
            return oAuth;
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "OAuthController#createOAuthBean", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
    }

    /**
     * DBに登録されている requestTokenか
     * @param requestToken requestToken
     * @return boolean
     * @throws net.rushhourgame.exception.RushHourException RushHourException 
     */
    public boolean isRegisteredRequestToken(String requestToken) throws RushHourException{
        if(requestToken == null){
            return false;
        }
        try {
            String requestTokenDigest = calculator.calcDigest(requestToken);
            return exists("OAuth.isValidRequestTokenDigest", "requestTokenDigest", requestTokenDigest);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "OAuthController#isRegisteredRequestToken", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
    }
    
    /**
     * requestTokenは暗号化されているため、ダイジェスト値の方で探す
     * @param requestToken requestToken
     * @return OAuth
     * @throws RushHourException RushHourException
     */
    public OAuth findByRequestToken(String requestToken) throws RushHourException{
        if(requestToken == null){
            return null;
        }
        try {
            String requestTokenDigest = calculator.calcDigest(requestToken);
            return findBy("OAuth.findByRequestTokenDigest", "requestTokenDigest", requestTokenDigest, dummyInst);
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "OAuthController#findByRequestToken", ex);
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, ex.getMessage()), ex);
        }
    }
    
    public void purgeOld(int purgeDay){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -purgeDay);
        int val = em.createNamedQuery("OAuth.findByThreshold")
                .setParameter("threshold", instance.getTime())
                .executeUpdate();
            LOG.log(Level.INFO, "{0}#purgeOld {1} entries",
                    new Object[]{this.getClass().getSimpleName(), val});
    }
}

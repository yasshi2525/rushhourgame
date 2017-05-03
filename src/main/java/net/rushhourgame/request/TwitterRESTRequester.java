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
package net.rushhourgame.request;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import net.rushhourgame.ErrorMessage;
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL;
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL_CONNECTION_ERR;
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL_ENCODE;
import net.rushhourgame.exception.RushHourException;
import org.apache.commons.codec.EncoderException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class TwitterRESTRequester extends HttpRequester implements Serializable {
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterRESTRequester.class.getName());


    @Override
    public void request() throws RushHourException {
        super.request();
        try {
            response = ClientBuilder
                    .newClient()
                    .target(resourceUrl + buildQuery())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            LOG.log(Level.FINE, response.readEntity(String.class));
        } catch (ProcessingException ex) {
            LOG.log(Level.SEVERE, "TwitterRESTRequester#request", ex);
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_CONNECTION_ERR, 
                            resourceUrl), 
                    "connection error " + resourceUrl + " : " + ex.getMessage());
        } catch (EncoderException ex) {
            // エンコード失敗による、ヘッダー文字列の構築に失敗。
            LOG.log(Level.SEVERE, "TwitterRESTRequester#request", ex);
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_ENCODE, 
                            ex.getMessage(), resourceUrl), 
                    "fail to encode " + ex.getMessage() + " for " + resourceUrl);
        }
    }
    
}

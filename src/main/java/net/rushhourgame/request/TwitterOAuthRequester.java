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
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.digest.HmacUtils;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class TwitterOAuthRequester extends OAuthRequester implements Serializable {
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterOAuthRequester.class.getName());

    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_NONCE = "oauth_nonce";
    public static final String OAUTH_SIGNATURE = "oauth_signature";
    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    public static final String OAUTH_VERSION = "oauth_version";

    @PostConstruct
    public void init() {
        LOG.log(Level.FINE, "TwitterOAuthRequester#init");
        parameters.put(OAUTH_CONSUMER_KEY, props.get(RushHourProperties.TWITTER_CONSUMER_KEY));
        parameters.put(OAUTH_NONCE, createNonce());
        parameters.put(OAUTH_SIGNATURE_METHOD, "HMAC-SHA1");
        parameters.put(OAUTH_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
        parameters.put(OAUTH_VERSION, "1.0");
    }

    /**
     *
     * @throws RushHourException SIGNIN_FAIL_NO_RESOURCE,
 SIGNIN_FAIL_ENCODE, SIGNIN_FAIL_CONNECTION_ERR,
 SIGNIN_FAIL_BAD_RES_STATUS
     */
    public void request() throws RushHourException {
        super.request();

        String header;
        try {
            header = buildHeader();
        } catch (RushHourException ex) {
            LOG.log(Level.SEVERE, "TwitterOAuthRequester#request", ex);
            throw ex;
        } catch (EncoderException ex) {
            // エンコード失敗による、ヘッダー文字列の構築に失敗。
            LOG.log(Level.SEVERE, "TwitterOAuthRequester#request", ex);
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_ENCODE, 
                            ex.getMessage(), resourceUrl), 
                    "fail to encode " + ex.getMessage() + " for " + resourceUrl);
        }

        try {
            response = ClientBuilder
                    .newClient()
                    .target(resourceUrl)
                    .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .header("Authorization", header)
                    .post(null);
            
        } catch (ProcessingException ex) {
            LOG.log(Level.SEVERE, "TwitterOAuthRequester#request", ex);
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_CONNECTION_ERR, 
                            resourceUrl), 
                    "connection error " + resourceUrl + " : " + ex.getMessage());
        }

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            String responseHeader = response.readEntity(String.class);
            responseMap = convertQueryToMap(responseHeader);
        } else {
            LOG.log(Level.SEVERE, "TwitterOAuthRequester#request"
                    + " bad response status = {0} from {1}",
                    new String[]{ String.valueOf(response.getStatus()), resourceUrl});
            throw new RushHourException(
                    ErrorMessage.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_BAD_RES_STATUS, 
                            String.valueOf(response.getStatus()), resourceUrl),
                    "bad response status = " + response.getStatus() + " from " + resourceUrl);
        }
    }

    /**
     *
     * @return @throws RushHourException
     * @throws EncoderException SIGNIN_FAIL_NO_RESOURCE
     */
    protected String buildHeader() throws RushHourException, EncoderException {
        if (!parameters.containsKey(OAUTH_SIGNATURE)) {
            buildSignature();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("OAuth ");
        parameters.forEach((key, val) -> {
            if (!key.equals(parameters.firstKey())) {
                sb.append(", ");
            }
            sb.append(key);
            sb.append("=\"");
            sb.append(val);
            sb.append("\"");
        });
        String ret = sb.toString();
        LOG.log(Level.FINE, "TwitterOAuthRequester#buildHeader header = {0}", ret);
        return ret;
    }

    /**
     *
     * @return @throws EncoderException
     * @throws RushHourException SIGNIN_FAIL_NO_RESOURCE
     */
    protected String buildSignature() throws EncoderException, RushHourException {
        String signingKey = createSigningKey();
        String signatureBaseString = createSignatureBaseString();
        byte[] signatureBin = HmacUtils.hmacSha1(signingKey, signatureBaseString);
        String signatureBase64 = Base64.getEncoder().encodeToString(signatureBin);
        String signature = urlCodec.encode(signatureBase64);
        parameters.put(OAUTH_SIGNATURE, signature);
        return signature;
    }

    /**
     *
     * @return @throws EncoderException
     * @throws RushHourException SIGNIN_FAIL_NO_RESOURCE
     */
    protected String createSignatureBaseString() throws EncoderException, RushHourException {
        if (resourceUrl == null) {
            throw new RushHourException(
                    ErrorMessage.createSystemError(SIGNIN_FAIL, SIGNIN_FAIL_NO_RESOURCE), 
                    "unable to build signature because resourceUrl is null");
        }
        StringBuilder signatureBaseBuilder = new StringBuilder();

        signatureBaseBuilder.append("POST");
        signatureBaseBuilder.append("&");
        signatureBaseBuilder.append(urlCodec.encode(resourceUrl));
        signatureBaseBuilder.append("&");
        signatureBaseBuilder.append(urlCodec.encode(createParamForSignatureBaseString()));

        return signatureBaseBuilder.toString();
    }

    protected String createParamForSignatureBaseString() {
        StringBuilder paramBuilder = new StringBuilder();

        for (Map.Entry entry : parameters.entrySet()) {
            if (!entry.getKey().equals(parameters.firstKey())) {
                paramBuilder.append("&");
            }
            paramBuilder.append(entry.getKey());
            paramBuilder.append("=");
            paramBuilder.append(entry.getValue());
        }

        return paramBuilder.toString();
    }

    protected String createSigningKey() throws EncoderException {
        StringBuilder sb = new StringBuilder();

        sb.append(urlCodec.encode(props.get(RushHourProperties.TWITTER_CONSUMER_SECRET)));
        sb.append("&");
        sb.append(urlCodec.encode(oauthTokenSecret));

        return sb.toString();
    }

    /**
     * word onlyのランダムな文字列を生成
     * @return 
     */
    protected String createNonce(){
        byte[] base = new byte[32];
        new SecureRandom().nextBytes(base);
        
        return Base64.getEncoder().encodeToString(base)
                .replaceAll("\\+", "")
                .replaceAll("/", "")
                .replaceAll("=", "");
    }
}

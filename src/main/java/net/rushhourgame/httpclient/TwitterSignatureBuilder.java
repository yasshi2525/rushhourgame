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
package net.rushhourgame.httpclient;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

/**
 * https://dev.twitter.com/oauth/overview/creating-signatures の実装
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class TwitterSignatureBuilder implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TwitterSignatureBuilder.class.getName());

    protected String httpMethod;
    protected String baseUrl;
    protected final SortedMap<String, String> parameters = new TreeMap<>();
    protected String consumerSecret;
    protected String oAuthTokenSecret;

    public String build() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#build start", this.getClass().getSimpleName());
        //すべてのパラメータを連結する
        String parameterString = createParameterString();
        //メソッド+URL+パラメータの形式で連結
        String signatureBaseString = createSignatureBaseString(parameterString);
        //鍵の作成
        String signingKey = createSigningKey();
        //暗号化
        String sigRaw = calculateSignature(signingKey, signatureBaseString);
        
        LOG.log(Level.FINER, "{0}#build end {1}", 
                new Object[]{this.getClass().getSimpleName(), sigRaw});
        return sigRaw;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public SortedMap<String, String> getParameters() {
        return parameters;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getOAuthTokenSecret() {
        return oAuthTokenSecret;
    }

    public void setOAuthTokenSecret(String oAuthTokenSecret) {
        this.oAuthTokenSecret = oAuthTokenSecret;
    }

    /**
     * パラメータすべてを文字列に連結する
     *
     * @return String
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    protected String createParameterString() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#createParameterString start", this.getClass().getSimpleName());

        StringBuilder paramBuilder = new StringBuilder();

        SortedMap<String, String> encodedParams = new TreeMap<>();
        // パラメータをパーセントエンコード
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            encodedParams.put(urlEncode(entry.getKey()),
                    urlEncode(entry.getValue()).replace("+", "%20"));
        }

        // アルファベット順に連結する
        encodedParams.forEach((key, value) -> {
            if (!key.equals(encodedParams.firstKey())) {
                paramBuilder.append("&");
            }
            paramBuilder.append(key);
            paramBuilder.append("=");
            paramBuilder.append(value);
        });
        
        String ret = paramBuilder.toString();
        LOG.log(Level.FINER, "{0}#createParameterString end {1}", 
                new Object[]{this.getClass().getSimpleName(), ret});
        return ret;
    }

    /**
     * Signature base string (メソッド&amp;URL&amp;パラメータ)をつくる
     *
     * @param parameterString parameterString
     * @return String
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    protected String createSignatureBaseString(String parameterString) throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#createSignatureBaseString start", this.getClass().getSimpleName());

        StringBuilder signatureBaseBuilder = new StringBuilder();

        if (httpMethod != null) {
            signatureBaseBuilder.append(httpMethod);
        }
        signatureBaseBuilder.append("&");
        if (baseUrl != null) {
            signatureBaseBuilder.append(urlEncode(baseUrl));
        }
        signatureBaseBuilder.append("&");
        if (parameterString != null) {
            signatureBaseBuilder.append(urlEncode(parameterString));
        }

        String ret = signatureBaseBuilder.toString();
        
        LOG.log(Level.FINER, "{0}#createSignatureBaseString end {1}", 
                new Object[]{this.getClass().getSimpleName(), ret});
        return ret;
    }

    /**
     * Signin key (Consumer keyとSecretKeyでbase stringを暗号化)
     *
     * @return String
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    protected String createSigningKey() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#createSigningKey start", this.getClass().getSimpleName());

        StringBuilder sb = new StringBuilder();

        if (consumerSecret != null) {
            sb.append(urlEncode(consumerSecret));
        }
        sb.append("&");
        if (oAuthTokenSecret != null) {
            sb.append(urlEncode(oAuthTokenSecret));
        }

        String ret = sb.toString();
        
        LOG.log(Level.FINER, "{0}#createSigningKey end {1}", 
                new Object[]{this.getClass().getSimpleName(), ret});
        return ret;
    }

    /**
     * 暗号化してSigunatureを取得
     *
     * @param key key
     * @param value value
     * @return String
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    protected String calculateSignature(String key, String value) throws UnsupportedEncodingException  {
        LOG.log(Level.FINE, "{0}#calculateSignature start", this.getClass().getSimpleName());

        byte[] signatureBin = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(value);
        String ret = Base64.getEncoder().encodeToString(signatureBin);

        LOG.log(Level.FINER, "{0}#calculateSignature end {1}", 
                new Object[]{this.getClass().getSimpleName(), ret});
        return ret;
    }
    
    protected String urlEncode(String original) throws UnsupportedEncodingException{
        return URLEncoder.encode(original, "UTF-8")
                .replace("+", "%20");
    }
}

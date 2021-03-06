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

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.RushHourProperties;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class TwitterClient extends HttpClient {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TwitterClient.class.getName());

    protected static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    protected static final String OAUTH_NONCE = "oauth_nonce";
    protected static final String OAUTH_SIGNATURE = "oauth_signature";
    protected static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    protected static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    protected static final String OAUTH_VERSION = "oauth_version";
    protected static final String OAUTH_TOKEN = "oauth_token";
    protected static final String AUTHORIZATION = "Authorization";

    protected SortedMap<String, String> authorizationHeaders;

    protected String oAuthToken;
    protected String oAuthTokenSecret;

    @Inject
    protected TwitterSignatureBuilder sigBuilder;

    @PostConstruct
    public void init() {
        LOG.log(Level.FINE, "{0}#init", HttpClient.class.getSimpleName());

        // Twitter API 共通のパラメタ値を設定
        authorizationHeaders = new TreeMap<>();
        authorizationHeaders.put(OAUTH_CONSUMER_KEY, prop.get(RushHourProperties.TWITTER_CONSUMER_KEY));
        authorizationHeaders.put(OAUTH_NONCE, createNonce());
        authorizationHeaders.put(OAUTH_SIGNATURE_METHOD, "HMAC-SHA1");
        authorizationHeaders.put(OAUTH_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
        authorizationHeaders.put(OAUTH_VERSION, "1.0");
        requestHeaders.put(AUTHORIZATION, authorizationHeaders);

        //Sigunature Key作成のための初期設定
        sigBuilder.setConsumerSecret(prop.get(RushHourProperties.TWITTER_CONSUMER_SECRET));
    }

    public void setOAuthToken(@NotNull String oAuthToken) {
        this.oAuthToken = oAuthToken;
        requestHeaders.get(AUTHORIZATION).put(OAUTH_TOKEN, oAuthToken);
    }

    public void setOAuthTokenSecret(@NotNull String oAuthTokenSecret) {
        this.oAuthTokenSecret = oAuthTokenSecret;
        sigBuilder.setOAuthTokenSecret(oAuthTokenSecret);
    }

    /**
     * Authorization OAuth aaa="bbb", ccc="ddd"
     *
     * @return SortedMap
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    protected SortedMap<String, String> buildHeader() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#buildHeader start", HttpClient.class.getSimpleName());

        // Signatureの作成
        requestHeaders.get(AUTHORIZATION).put(OAUTH_SIGNATURE, buildSignature());

        StringBuilder sb = new StringBuilder();
        sb.append("OAuth ");

        for (Map.Entry<String, String> entry : requestHeaders.get(AUTHORIZATION).entrySet()) {
            if (!entry.getKey().equals(requestHeaders.get(AUTHORIZATION).firstKey())) {
                sb.append(", ");
            }
            sb.append(encodeURL(entry.getKey()));
            sb.append("=\"");
            sb.append(encodeURL(entry.getValue()));
            sb.append("\"");
        }
        LOG.log(Level.FINER, "{0}#buildHeader header = {1}",
                new Object[]{TwitterClient.class.getSimpleName(), sb.toString()});

        SortedMap<String, String> header = new TreeMap<>();
        header.put(AUTHORIZATION, sb.toString());
        return header;
    }

    protected String buildSignature() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#buildSignature start", HttpClient.class.getSimpleName());
        //GET
        sigBuilder.getParameters().putAll(getParameters);
        //POST
        sigBuilder.getParameters().putAll(postParameters);
        //header
        requestHeaders.forEach((key, value) -> {
            sigBuilder.getParameters().putAll(value);
        });
        return sigBuilder.build();
    }

    /**
     * word onlyのランダムな文字列を生成
     *
     * @return String
     */
    protected String createNonce() {
        byte[] base = new byte[32];
        new SecureRandom().nextBytes(base);

        return Base64.getEncoder().encodeToString(base)
                .replaceAll("\\+", "")
                .replaceAll("/", "")
                .replaceAll("=", "");
    }

}

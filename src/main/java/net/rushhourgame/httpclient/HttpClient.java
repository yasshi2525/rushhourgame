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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public abstract class HttpClient implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(HttpClient.class.getName());

    @Inject
    RushHourProperties prop;
    @Inject
    ErrorMessageBuilder errMsgBuilder;
    protected final SortedMap<String, String> getParameters = new TreeMap<>();
    protected final SortedMap<String, String> postParameters = new TreeMap<>();
    protected final SortedMap<String, SortedMap<String, String>> requestHeaders = new TreeMap<>();
    protected Map<String, String> responseHeaders;
    protected String resourceUrl;
    protected Response response;
    protected HttpMethod httpMethod;
    protected MediaType mediaType = MediaType.APPLICATION_FORM_URLENCODED_TYPE;
    protected boolean isExecuted;
    protected String responseHeader;

    public void execute() throws RushHourException {
        LOG.log(Level.FINE, "{0}#request execute", HttpClient.class.getSimpleName());
        // 前提
        // resourceUrl, requestHeaders, getParameters, postParameters に値がセットされている

        LOG.log(Level.FINE, "{0}#execute start url = {1}", new Object[]{HttpClient.class.getSimpleName(), resourceUrl});
        verifyResourceUrl();

        try {
            response = request(buildHeader(), getParameters, buildPOSTParams());
        } catch (UnsupportedEncodingException ex) {
            // ヘッダ文字列作成時にエンコード失敗
            LOG.log(Level.SEVERE, HttpClient.class + "#request", ex);
            throw new RushHourException(
                    errMsgBuilder.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_ENCODE,
                            ex.getMessage(), resourceUrl),
                    "fail to encode " + ex.getMessage() + " for " + resourceUrl);

        } catch (ProcessingException ex) {
            // 接続エラー
            LOG.log(Level.SEVERE, HttpClient.class + "#request", ex);
            throw new RushHourException(
                    errMsgBuilder.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_CONNECTION_ERR,
                            resourceUrl),
                    "connection error " + resourceUrl + " : " + ex.getMessage());

        }

        //レスポンスコードが200チェック
        verifyResponseCode();

        //レスポンスデータの解析
        responseHeaders = parseResponseData();

        isExecuted = true;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    /**
     * リクエストするURLを取得します
     *
     * @return String
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * リクエストするURLを指定します. 引数がnullのときは更新しません
     *
     * @param resourceUrl resourceUrl
     */
    public void setResourceUrl(@NotNull String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    protected SortedMap<String, String> getGetParameters() {
        return getParameters;
    }

    protected SortedMap<String, String> getPostParameters() {
        return postParameters;
    }

    protected Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    @NotNull
    protected abstract SortedMap<String, String> buildHeader() throws UnsupportedEncodingException;

    protected Response request(@NotNull SortedMap<String, String> headers,
            @NotNull Map<String, String> queryParams,
            Entity<Form> post) throws UnsupportedEncodingException, RushHourException {
        LOG.log(Level.FINE, "{0}#request start", HttpClient.class.getSimpleName());

        Invocation.Builder builder = createTarget().request(mediaType);

        //header
        headers.forEach((key, value) -> {
            builder.header(key, value);
        });

        if (httpMethod == null) {
            throw new RushHourException(errMsgBuilder.createSystemError(SIGNIN_FAIL, SIGNIN_FAIL_NO_HTTP_METHOD));
        }
        switch (httpMethod) {
            case GET:
                return builder.get();
            case POST:
                default:
                return builder.post(post);
        }
    }

    protected WebTarget createTarget() throws UnsupportedEncodingException {
        return ClientBuilder.newClient().target(resourceUrl + buildGETQuery());
    }

    /**
     * 不正な値の場合、Exceptionを発生させる
     *
     * @throws RushHourException RushHourException
     */
    protected void verifyResourceUrl() throws RushHourException {
        if (resourceUrl == null) {
            LOG.log(Level.SEVERE, "{0}#verifyResourceUrl resourceUrl is null", HttpClient.class.getSimpleName());
            throw new RushHourException(
                    errMsgBuilder.createSystemError(SIGNIN_FAIL, SIGNIN_FAIL_NO_RESOURCE),
                    "resourceUrl is null");
        }
    }

    protected void verifyResponseCode() throws RushHourException {
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            LOG.log(Level.SEVERE, "{0}#verifyResponseCode"
                    + " bad response status = {0} from {1}",
                    new String[]{String.valueOf(response.getStatus()), resourceUrl});
            throw new RushHourException(
                    errMsgBuilder.createReSignInError(
                            SIGNIN_FAIL, SIGNIN_FAIL_BAD_RES_STATUS,
                            String.valueOf(response.getStatus()), resourceUrl),
                    "bad response status = " + response.getStatus() + " from " + resourceUrl);
        }
    }

    protected void verifyResponseHeaderKey(String key) throws RushHourException {
        if (!responseHeaders.containsKey(key)) {
            LOG.log(Level.SEVERE, "{0}#verifyResponseHeaderKey key = {1} didn''t exist",
                    new Object[]{HttpClient.class.getSimpleName(), key});
            throw new RushHourException(errMsgBuilder.createReSignInError(
                    SIGNIN_FAIL, SIGNIN_FAIL_INVALID_RESPONSE),
                    "invalid response : key = " + key + " didn't exist"
            );
        }
    }

    protected String buildGETQuery() throws UnsupportedEncodingException {
        LOG.log(Level.FINE, "{0}#buildGETQuery start", this.getClass().getSimpleName());
        if (getParameters.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, String> entry : getParameters.entrySet()) {
            if (!getParameters.firstKey().equals(entry.getKey())) {
                sb.append("&");
            }
            sb.append(encodeURL(entry.getKey()));
            sb.append("=");
            sb.append(encodeURL(entry.getValue()));
        }
        return sb.toString();
    }

    protected Entity<Form> buildPOSTParams() {
        LOG.log(Level.FINE, "{0}#buildPOSTParams start", HttpClient.class.getSimpleName());
        return null;
    }

    protected SortedMap<String, String> parseResponseData() {
        LOG.log(Level.FINE, "{0}#parseResponseData start", HttpClient.class.getSimpleName());
        if (response != null) {
            responseHeader = response.readEntity(String.class);
            LOG.log(Level.FINER, "{0}#parseResponseData {1}",
                    new Object[]{HttpClient.class.getSimpleName(), responseHeader});
        }
        return parseQueryToMap(responseHeader);
    }

    protected SortedMap<String, String> parseQueryToMap(String query) {
        LOG.log(Level.FINE, "{0}#parseQueryToMap start", HttpClient.class.getSimpleName());
        SortedMap<String, String> map = new TreeMap<>();
        if (query == null) {
            return map;
        }
        String[] split = query.split("&");
        Pattern p = Pattern.compile("(.+)=(.+)");
        for (String token : split) {
            Matcher m = p.matcher(token);
            if (m.matches()) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

    protected String encodeURL(String original) throws UnsupportedEncodingException {
        return URLEncoder.encode(original, "UTF-8")
                .replace("+", "%20");
    }

    protected enum HttpMethod {
        GET, POST
    }
}

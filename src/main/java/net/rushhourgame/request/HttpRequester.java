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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL;
import static net.rushhourgame.RushHourResourceBundle.SIGNIN_FAIL_NO_RESOURCE;
import net.rushhourgame.exception.RushHourException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public abstract class HttpRequester implements Serializable {
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(HttpRequester.class.getName());

    @Inject
    transient RushHourProperties props;
    protected final SortedMap<String, String> parameters = new TreeMap<>();
    protected final URLCodec urlCodec = new URLCodec();
    protected String resourceUrl;
    protected Response response;

    public int getResponseStatus() {
        return response.getStatus();
    }

    public void request() throws RushHourException {
        LOG.log(Level.FINE, "{0}#request {1}", new Object[]{this.getClass().getSimpleName(), resourceUrl});

        if (resourceUrl == null) {
            LOG.log(Level.SEVERE, "{0}#request resourceUrl is null", this.getClass().getSimpleName());
            throw new RushHourException(
                    ErrorMessage.createSystemError(SIGNIN_FAIL, SIGNIN_FAIL_NO_RESOURCE),
                    "resourceUrl is null");
        }
    }

    /**
     * リクエストするURLを取得します
     *
     * @return
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * リクエストするURLを指定します. 引数がnullのときは更新しません
     *
     * @param resourceUrl
     */
    public void setResourceUrl(String resourceUrl) {
        if (resourceUrl != null) {
            this.resourceUrl = resourceUrl;
        }
    }

    /**
     * リクエストパラメタを取得します
     *
     * @return
     */
    public SortedMap<String, String> getParameters() {
        return parameters;
    }
    
    protected String buildQuery() throws EncoderException{
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for(Map.Entry entry : parameters.entrySet()){
            if(!parameters.firstKey().equals(entry.getKey())){
                sb.append("&");
            }
            sb.append(urlCodec.encode(entry.getKey()));
            sb.append("=");
            sb.append(urlCodec.encode(entry.getValue()));
        }
        return sb.toString();
    }
}

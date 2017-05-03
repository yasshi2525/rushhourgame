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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public abstract class OAuthRequester extends HttpRequester implements Serializable {
    private final int serialVersionUID = 1;
    protected Map<String, String> responseMap;

    protected String oauthTokenSecret = "";

    public void setOauthTokenSecret(String oauthTokenSecret) {
        if (oauthTokenSecret != null) {
            this.oauthTokenSecret = oauthTokenSecret;
        }
    }

    /**
     * レスポンスを取得します
     *
     * @return
     */
    public Map<String, String> getResponseMap() {
        return responseMap;
    }

    protected Map<String, String> convertQueryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) {
            return map;
        }
        String[] split = query.split("&");
        Pattern p = Pattern.compile("(.+)=(.+)");
        for (String token : split) {
            Matcher m = p.matcher(token);
            if (m.matches() && m.groupCount() == 2) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

}

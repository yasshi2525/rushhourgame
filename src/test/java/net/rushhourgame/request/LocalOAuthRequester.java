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

import net.rushhourgame.request.OAuthRequester;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class LocalOAuthRequester extends OAuthRequester {

    private static final Logger LOG = Logger.getLogger(LocalOAuthRequester.class.getName());
    boolean validResource = true;
    boolean successes;

    public void setValidResource(boolean validResource) {
        this.validResource = validResource;
    }

    public int getResponseStatus() {
        return successes && validResource ? 200 : 999;
    }

    public void request() throws RushHourException {
        if (validResource) {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                responseMap = new HashMap<String, String>() {
                    {
                        put("foo", "bar");
                    }
                };
                successes = true;
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }

        }
    }
}

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
package net.rushhourgame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractResourceBundle implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AbstractResourceBundle.class.getName());
    transient protected HashMap<Locale, ResourceBundle> cacheList = new HashMap<>();
    protected String baseName;

    public AbstractResourceBundle(String baseName) {
        this.baseName = baseName;
    }

    public String get(String key, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (!cacheList.containsKey(locale)) {
            fetch(locale);
        }

        return cacheList.get(locale).getString(key);
    }

    protected void fetch(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        cacheList.put(locale, ResourceBundle.getBundle(baseName, locale));
    }

    protected void clear() {
        cacheList.clear();
    }
}

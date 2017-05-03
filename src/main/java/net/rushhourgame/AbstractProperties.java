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

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class AbstractProperties {

    private static final Logger LOG = Logger.getLogger(AbstractProperties.class.getName());
    protected Properties cache;
    protected List<String> resourcePathList;

    public AbstractProperties(List<String> resourcePathList) {
        this.resourcePathList = resourcePathList;
    }

    public String get(String key) {
        if (cache == null) {
            fetch();
        }
        return cache.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        if (cache == null) {
            fetch();
        }
        return cache.getProperty(key, defaultValue);
    }

    protected void fetch() {
        if (cache == null) {
            cache = new Properties();
        }

        for (String resourcePath : resourcePathList) {
            try {
                cache.load(AbstractProperties.class
                        .getClassLoader()
                        .getResourceAsStream(resourcePath));

            } catch (IOException ex) {
                LOG.info(ex.getMessage());
                LOG.log(Level.SEVERE, "AbstractProperties#fetch fail to load {0}", resourcePath);
            }
        }
    }

    protected void clear() {
        cache = null;
    }
}

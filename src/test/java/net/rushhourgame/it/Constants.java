/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.it;

import java.util.Properties;
import org.openqa.selenium.Dimension;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class Constants {
    protected static final Properties CONFIG = new Properties();
    protected static final Boolean HEADLESS = Boolean.getBoolean("test.headless");
    protected static final String TARGET_URL = getProperty("test.targetUrl");
    protected static final String TWITTER_USER_NAME = getProperty("test.twitter.mailaddress");
    protected static final String TWITTER_PASSWORD = getProperty("test.twitter.password");
    protected static final long TIMEOUT = 10; // sec
    protected static final Dimension WINDOW_SIZE = new Dimension(1024, 1024);
    
    protected static final String ID_MENU_CREATE_RAIL = "clickmenu-create-rail";
    protected static final String ID_MENU_EXTEND_RAIL = "clickmenu-extend-rail";
    protected static final String ID_END_ACTION = "end-action";
    
    /**
     * linuxで maven 経由でシステムプロパティを設定すると末尾に /　が入る。謎。
     * @param name プロパティ名
     * @return トリミングした文字列
     */
    protected static String getProperty(String name) {
        return System.getProperty(name).replaceAll("/$", "");
    }
    
}

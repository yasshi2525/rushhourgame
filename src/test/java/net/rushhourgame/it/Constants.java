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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Dimension;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class Constants {
    protected static final Boolean HEADLESS = Boolean.getBoolean("test.headless");
    protected static String TARGET_URL;
    protected static String TWITTER_USER_NAME;
    protected static String TWITTER_PASSWORD;
    protected static final long TIMEOUT = 10; // sec
    protected static final long TIMEOUT_ANNOUNCEMENT = 10; // sec
    protected static final long TIMEOUT_POLLING = 1; // sec
    protected static final long SLEEP_POLLING = 5; //sec
    protected static final Dimension WINDOW_SIZE = new Dimension(1024, 1024);
    protected static final Dimension MOVABLE = new Dimension(500, 100);
    
    protected static final String ID_MENU_CREATE_RAIL = "clickmenu-create-rail";
    protected static final String ID_MENU_EXTEND_RAIL = "clickmenu-extend-rail";
    protected static final String ID_MENU_UNDEPLOY_TRAIN = "clickmenu-undeploy-train";
    protected static final String ID_MENU_REMOVE_STATION = "clickmenu-remove-station";
    protected static final String ID_END_ACTION = "end-action";
    protected static final String ID_ANNOUNCEMENT = "announcement_container";
    protected static final String ID_GUIDE = "guide";
    protected static final String ID_CONFIRM_OK = "confirmOK";
    protected static final String ID_ERROR_TITLE = "error-title";
    protected static final String ID_ERROR_DETAIL = "error-detail";
    protected static final String ID_ERROR_ACTION = "error-action";
    protected static final String ID_DEBUG_INFO = "debug-info";
    
    protected static final String PROP_PATH = "net/rushhourgame/conf/integration_test.properties";
    static {
        try(InputStream is = ClassLoader.getSystemResourceAsStream(PROP_PATH)) {
            Properties prop = new Properties();
            prop.load(is);
            
            TARGET_URL = prop.getProperty("test.targetUrl");
            TWITTER_USER_NAME = prop.getProperty("test.twitter.mailaddress");
            TWITTER_PASSWORD = prop.getProperty("test.twitter.password");
        } catch (IOException ex) {
            Logger.getLogger(Constants.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

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

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.it.Constants.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public abstract class AbstractIT {

    private static final Logger LOG = Logger.getLogger(AbstractIT.class.getName());
    protected static RushHourResourceBundle msg;
    protected WebDriver driver;
    protected CommonAction behave;

    @BeforeClass
    public static void setUpCommonClass() {
        msg = RushHourResourceBundle.getInstance();
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setUpCommon() {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(HEADLESS);
        options.addArguments("--lang=ja");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        driver.manage().window().setSize(WINDOW_SIZE);
        
        behave = new CommonAction(driver);
    }

    @After
    public void tearDownCommon() {
        if (driver != null) {
            try {
                if (driver.getCurrentUrl().endsWith("error.xhtml")) {
                    fail("end with error page.");
                }
                behave.unscrollMapAll();
            } finally {
                driver.quit();
            }
        }
    }
}

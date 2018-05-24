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
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static net.rushhourgame.it.Constants.*;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class LoginIT {

    protected WebDriver driver;

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLogin() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(HEADLESS);
        driver = new ChromeDriver(options);

        // ページを開く
        driver.get(TARGET_URL);
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.titleContains("RushHour"));

        // Twitterでサインイン要素まで移動
        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.TAB);
        body.sendKeys(Keys.TAB);
        WebElement signin = driver.switchTo().activeElement();

        // サインインボタンの押下
        signin.sendKeys(Keys.ENTER);

        // Twitterサインイン画面が表示されるまで待機
        new WebDriverWait(driver, TIMEOUT).until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("username_or_email")));

        // 名前とパスワードを入力
        WebElement name = driver.findElement(By.id("username_or_email"));
        name.sendKeys(TWITTER_USER_NAME);
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(TWITTER_PASSWORD);

        // ログインボタンを押下
        WebElement allow = driver.findElement(By.id("allow"));
        allow.click();

        // ゲーム画面に戻るまで待機
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.titleContains("RushHour"));

        driver.quit();
    }

}

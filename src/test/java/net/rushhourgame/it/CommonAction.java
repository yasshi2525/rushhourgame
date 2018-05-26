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

import java.util.Locale;
import java.util.Stack;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.it.Constants.*;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class CommonAction {

    protected Stack<Dimension> histories = new Stack<>();
    protected RushHourResourceBundle msg = RushHourResourceBundle.getInstance();
    protected WebDriver driver;
    protected WebDriverWait waitForAnnouncement;

    public CommonAction(WebDriver driver) {
        this.driver = driver;
        waitForAnnouncement = new WebDriverWait(driver, TIMEOUT_ANNOUNCEMENT);
    }

    public void login() {
        // ページを開く
        driver.get(TARGET_URL);

        // Twitterでサインイン要素まで移動
        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.TAB);
        body.sendKeys(Keys.TAB);
        WebElement signin = driver.switchTo().activeElement();

        // サインインボタンの押下
        signin.sendKeys(Keys.ENTER);

        // 名前とパスワードを入力
        WebElement name = driver.findElement(By.id("username_or_email"));
        name.sendKeys(TWITTER_USER_NAME);
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(TWITTER_PASSWORD);

        // ログインボタンを押下
        WebElement allow = driver.findElement(By.id("allow"));
        allow.click();
    }

    public void endAction() {
        driver.findElement(By.id(ID_END_ACTION)).click();
    }

    public void clickCanvas() {
        driver.findElement(By.tagName("canvas")).click();
    }

    public void selectClickMenu(String id) {
        moveClickMenu();
        driver.findElement(By.id(id)).click();
    }

    /**
     * 要素が登場するまで定期的にクリックメニューを開き続ける.、
     * @param id id
     * @param timeout timeout 
     */
    public void selectClickMenu(String id, long timeout) {
        new WebDriverWait(driver, timeout).until((d) -> {
            try {
                clickCanvas();
                moveClickMenu();
                return d.findElement(By.id(id));
            } finally {
                d.findElement(By.className("ui-icon-closethick")).click();
            }
        });
    }

    public void scrollMap(Dimension offset) {
        histories.push(offset);
        _scrollMap(offset.getWidth(), offset.getHeight());
    }

    public void unscrollMap() {
        Dimension offset = histories.pop();
        _scrollMap(-offset.getWidth(), -offset.getHeight());
    }

    public void unscrollMapAll() {
        while (!histories.empty()) {
            unscrollMap();
        }
    }

    public void assertAnnouncement(String msgId) {
        String expected = msg.get(msgId, Locale.JAPAN);

        String actual = findAnnouncement().getText();

        assertEquals(expected, actual);
        waitForFadeOutAnnouncement();
    }

    public void waitForFadeOutAnnouncement() {
        waitForAnnouncement.until(ExpectedConditions.invisibilityOf(findAnnouncement()));
    }

    protected void moveClickMenu() {
        WebElement iframe = driver.findElement(By.tagName("iframe"));
        driver.switchTo().frame(iframe);
    }

    protected void _scrollMap(int offsetX, int offsetY) {
        WebElement canvas = driver.findElement(By.tagName("canvas"));
        new Actions(driver).moveToElement(canvas)
                .clickAndHold()
                .moveByOffset(offsetX, offsetY)
                .release()
                .perform();
    }

    protected WebElement findAnnouncement() {
        return driver.findElement(By.id(ID_ANNOUNCEMENT))
                .findElement(By.className("ui-growl-title"));
    }
}

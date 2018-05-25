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

import static net.rushhourgame.it.Constants.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class CommonAction {
    
    public static void login(WebDriver driver) {
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
    
    public static void endAction(WebDriver driver) {
        driver.findElement(By.id(ID_END_ACTION)).click();
    }
    
    public static void clickCanvas(WebDriver driver) {
        driver.findElement(By.tagName("canvas")).click();
    }
    
    public static void selectClickMenu(WebDriver driver, String id) {
        // ダイアログに移動
        WebElement iframe = driver.findElement(By.tagName("iframe"));        
        driver.switchTo().frame(iframe);
        
        // クリックメニューから建築開始を選択
        driver.findElement(By.id(ID_MENU_CREATE_RAIL)).click();
    }
    
    public static void scrollMap(WebDriver driver, int offsetX, int offsetY) {
        WebElement canvas = driver.findElement(By.tagName("canvas"));
        new Actions(driver).moveToElement(canvas)
                .clickAndHold()
                .moveByOffset(offsetX, offsetY)
                .release()
                .perform();
    }
}

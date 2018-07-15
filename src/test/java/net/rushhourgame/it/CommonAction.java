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

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.rushhourgame.it.Constants.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class CommonAction {

    private static final Logger LOG = Logger.getLogger(CommonAction.class.getName());

    protected Stack<Dimension> histories = new Stack<>();
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WebDriverWait waitPolling;
    protected WebDriverWait waitForAnnouncement;

    public CommonAction(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, TIMEOUT, 200);
        waitPolling = new WebDriverWait(driver, TIMEOUT_POLLING, 200);
        waitPolling.withMessage("waitPolling causes timeout");
        waitForAnnouncement = new WebDriverWait(driver, TIMEOUT_ANNOUNCEMENT);
    }

    public void login() {
        LOG.log(Level.INFO, "logging in to " + TARGET_URL);
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

        LOG.log(Level.INFO, "logged in sucessfully");
    }

    /**
     * 線路の建築を開始する. 操作中の状態で終了する。
     *
     * @param point 開始座標
     * @return 操作完了メッセージ
     */
    public String startRailCreation(Dimension point) {
        LOG.log(Level.INFO, "starting rail creation to  " + point);

        scrollMap(new Dimension(-point.width, -point.height));
        clickCanvas();
        selectClickMenu(ID_MENU_CREATE_RAIL);
        WebElement announcement = findAnnouncement();
        String msg = announcement.getText();

        waitForAnnouncement.until(invisibilityOf(announcement));

        LOG.log(Level.INFO, "start rail creation sucessfully msg " + msg);
        return msg;
    }

    /**
     * 線路の延伸を開始する. 操作中の状態で終了する。
     *
     * @param point 開始座標
     * @return ガイドメッセージ
     */
    public String startRailExtension(Dimension point) {
        LOG.log(Level.INFO, "starting rail extension to  " + point);

        scrollMap(new Dimension(-point.width, -point.height));
        clickCanvas();
        selectClickMenu(ID_MENU_EXTEND_RAIL);

        String msg = findGuide().getText();
        LOG.log(Level.INFO, "start rail extension sucessfully msg " + msg);
        return msg;
    }

    /**
     * 線路を延伸する. このメソッド開始時点で、操作可能な状態である必要がある。 操作中の状態で終了する。
     *
     * @param point 延伸する座標
     * @return 設置完了メッセージ
     */
    public String extendRail(Dimension point) {
        LOG.log(Level.INFO, "extending rail to  " + point);
        scrollMap(new Dimension(-point.width, -point.height));
        clickCanvas();

        WebElement announcement = findAnnouncement();
        String msg = announcement.getText();
        waitForAnnouncement.until(invisibilityOf(announcement));

        LOG.log(Level.INFO, "extended rain sucessfully msg " + msg);
        return msg;
    }
    
    /**
     * 駅を削除する.
     * @param point 削除する座標
     */
    public String removeStation(Dimension point) {
        LOG.log(Level.INFO, "deleting station  " + point);

        scrollMap(new Dimension(-point.width, -point.height));
        clickCanvas();
        selectClickMenu(ID_MENU_REMOVE_STATION);
        
        driver.findElement(By.id(ID_CONFIRM_OK)).click();
        
        WebElement announcement = findAnnouncement();
        String msg = announcement.getText();

        waitForAnnouncement.until(invisibilityOf(announcement));

        LOG.log(Level.INFO, "deleted station sucessfully msg " + msg);
        return msg;
    }

    public void endAction() {
        LOG.log(Level.INFO, "ending action");

        WebElement end = driver.findElement(By.id(ID_END_ACTION));
        end.click();
        //wait.until(invisibilityOf(end));

        LOG.log(Level.INFO, "ended action sucessfully");
    }

    protected void clickCanvas() {
        LOG.log(Level.FINE, "clicking canvas");
        sleep(1);
        driver.findElement(By.tagName("canvas")).click();
    }

    protected void selectClickMenu(String id) {
        LOG.log(Level.FINE, "selecting click menu " + id);

        WebElement iframe = findClickMenu();
        driver.switchTo().frame(iframe);
        driver.findElement(By.id(id)).click();
        //driver.switchTo().parentFrame();
        //wait.until(invisibilityOf(iframe));

        LOG.log(Level.FINE, "selectted click menu sucessfully");
    }

    /**
     * 要素が登場するまで定期的にクリックメニューを開き続ける.、
     *
     * @param id id
     * @param timeout timeout
     */
    public void selectClickMenu(String id, long timeout) {
        LOG.log(Level.INFO, "selecting click menu " + id + " (with polling)");

        driver.manage().timeouts().implicitlyWait(TIMEOUT_POLLING, TimeUnit.SECONDS);

        new WebDriverWait(driver, timeout, SLEEP_POLLING * 1000).until((d) -> {
            WebElement iframe = null;
            try {
                clickCanvas();
                iframe = findClickMenu();
                d.switchTo().frame(iframe);
                LOG.log(Level.FINE, "shifeted iframe");
                try {
                    WebElement result = waitPolling.until(presenceOfElementLocated(By.id(id)));
                    LOG.log(Level.FINE, "result of polling " + result.getText());
                    return true;
                } catch (TimeoutException e) {
                    LOG.log(Level.FINE, "result of polling was failed");
                    return false;
                }
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "exception during polling", e);
                return false;
            } finally {
                try {
                    LOG.log(Level.FINE, "closing click menu");
                    d.switchTo().parentFrame();
                    LOG.log(Level.FINE, "switcherd parent frame");
                    WebElement dialog = d.findElement(By.id("openclickmenu_dlg"));
                    LOG.log(Level.FINE, "found dialog box");
                    new Actions(driver)
                            .sendKeys(Keys.TAB)
                            .sendKeys(Keys.ENTER)
                            .perform();
                    LOG.log(Level.FINE, "pushed enter key to close box");
                    wait.until(invisibilityOfElementLocated(By.tagName("iframe")));
                    LOG.log(Level.FINE, "closed click menu successfully");
                    sleep(2);
                } catch (RuntimeException e) {
                    LOG.log(Level.SEVERE, "exception during post polling process", e);
                }
            }
        });

        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
    }

    public void scrollMap(Dimension offset) {
        LOG.log(Level.FINE, "scrolling map " + offset);
        if (offset.width == 0 && offset.height == 0) {
            LOG.log(Level.FINE, "scrolled map successfully");
            return;
        }

        Dimension movable = new Dimension(
                offset.width > 0
                        ? Math.min(offset.width, MOVABLE.width)
                        : Math.max(offset.width, -MOVABLE.width),
                offset.height > 0
                        ? Math.min(offset.height, MOVABLE.height)
                        : Math.max(offset.height, -MOVABLE.height));

        histories.push(movable);
        _scrollMap(movable.getWidth(), movable.getHeight());

        LOG.log(Level.FINE, "scrolled map sucessfully " + movable);

        scrollMap(new Dimension(offset.width - movable.width, offset.height - movable.height));
    }

    protected void unscrollMap() {
        Dimension offset = histories.pop();
        _scrollMap(-offset.getWidth(), -offset.getHeight());
    }

    public void unscrollMapAll() {
        LOG.log(Level.FINE, "unscrolling map");
        while (!histories.empty()) {
            unscrollMap();
        }
        LOG.log(Level.FINE, "unscrolling map successfully");
    }

    public void createResidence(double x, double y) {
        driver.get(TARGET_URL + "/gm?op=src&x=" + x + "&y=" + y);
    }

    public void createCompany(double x, double y) {
        driver.get(TARGET_URL + "/gm?op=dst&x=" + x + "&y=" + y);
    }

    public void startGame() {
        driver.get(TARGET_URL + "/gm?op=start");
    }

    public void stopGame() {
        driver.get(TARGET_URL + "/gm?op=stop");
    }

    public void sleep(long sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommonAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected WebElement findClickMenu() {
        LOG.log(Level.FINE, "finding clieckmenu");
        wait.until(visibilityOfElementLocated(By.tagName("iframe")));
        WebElement iframe = driver.findElement(By.tagName("iframe"));
        LOG.log(Level.FINE, "found clieckmenu successfully");
        return iframe;
    }

    protected void _scrollMap(int offsetX, int offsetY) {
        WebElement canvas = driver.findElement(By.tagName("canvas"));
        new Actions(driver).moveToElement(canvas)
                .clickAndHold()
                .moveByOffset(offsetX, offsetY)
                .release()
                .perform();
    }

    protected WebElement findGuide() {
        LOG.log(Level.FINE, "finding guide");

        wait.until(visibilityOfElementLocated(By.id(ID_GUIDE)));
        WebElement component = driver.findElement(By.id(ID_GUIDE));

        LOG.log(Level.FINER, "found guide component successfully");

        wait.until(d -> !component.findElement(By.className("ui-messages-info-summary")).getText().isEmpty());
        WebElement guide = component.findElement(By.className("ui-messages-info-summary"));

        LOG.log(Level.FINE, "found guide successfully");
        return guide;
    }

    protected WebElement findAnnouncement() {
        LOG.log(Level.FINE, "finding announcement");

        wait.until(presenceOfElementLocated(By.id(ID_ANNOUNCEMENT)));
        WebElement component = driver.findElement(By.id(ID_ANNOUNCEMENT));

        LOG.log(Level.FINER, "found announcement component successfully");

        wait.until(d -> !component.findElement(By.className("ui-growl-title")).getText().isEmpty());
        WebElement announcement = component.findElement(By.className("ui-growl-title"));

        LOG.log(Level.FINE, "found announcement successfully");
        return announcement;
    }
}

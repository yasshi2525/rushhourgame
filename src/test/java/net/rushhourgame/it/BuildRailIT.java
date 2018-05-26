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

import static net.rushhourgame.RushHourResourceBundle.*;
import org.junit.Test;
import static net.rushhourgame.it.CommonAction.*;
import static net.rushhourgame.it.Constants.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class BuildRailIT extends AbstractIT{
    protected static final Dimension ONE = new Dimension(-100, -100);
    protected static final Dimension LINE1_1 = new Dimension(100, 100);
    protected static final Dimension LINE1_2 = new Dimension(150, 150);
    protected static final Dimension LINE1_3 = new Dimension(150, 0);
    protected static final Dimension LINE1_4 = new Dimension(0, 100);
    
    @Test
    public void testBuildOnePoint() {
        login(driver);
        scrollMap(driver, ONE);
        clickCanvas(driver);
        selectClickMenu(driver, Constants.ID_MENU_CREATE_RAIL);
        assertAnnouncement(driver, ANNOUNCEMENT_RAIL_CREATE);
        
        endAction(driver);
    }
    
    @Test
    public void testBuildLine() {
        login(driver);
        scrollMap(driver, LINE1_1);
        clickCanvas(driver);
        selectClickMenu(driver, ID_MENU_CREATE_RAIL);
        assertAnnouncement(driver, ANNOUNCEMENT_RAIL_CREATE);
        waitForInvisiblingAnnouncement(driver);
        
        scrollMap(driver, LINE1_2);
        clickCanvas(driver);
        assertAnnouncement(driver, ANNOUNCEMENT_RAIL_EXTEND);
        waitForInvisiblingAnnouncement(driver);
        
        scrollMap(driver, LINE1_3);
        clickCanvas(driver);
        assertAnnouncement(driver, ANNOUNCEMENT_RAIL_EXTEND);
        waitForInvisiblingAnnouncement(driver);
        endAction(driver);
        
        unscrollMap(driver);
        clickCanvas(driver);
        selectClickMenu(driver, ID_MENU_EXTEND_RAIL);
        
        scrollMap(driver, LINE1_4);
        clickCanvas(driver);
        assertAnnouncement(driver, ANNOUNCEMENT_RAIL_EXTEND);
        waitForInvisiblingAnnouncement(driver);
                
        endAction(driver);
    }
}

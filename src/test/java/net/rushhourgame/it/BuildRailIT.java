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

import org.junit.Test;
import static net.rushhourgame.it.CommonAction.*;
import static net.rushhourgame.it.Constants.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class BuildRailIT extends AbstractIT{
    protected static final int ONE_X = -100;
    protected static final int ONE_Y = -100;
    protected static final int LINE_X1 = 100;
    protected static final int LINE_Y1 = 100;
    protected static final int LINE_X2 = 400;
    protected static final int LINE_Y2 = 100;
    
    @Test
    public void testBuildOnePoint() {
        login(driver);
        scrollMap(driver, ONE_X, ONE_Y);
        clickCanvas(driver);
        selectClickMenu(driver, Constants.ID_MENU_CREATE_RAIL);
        endAction(driver);
        scrollMap(driver, -ONE_X, -ONE_Y);
    }
    
    @Test
    public void testBuildLine() {
        login(driver);
        scrollMap(driver, LINE_X1, LINE_Y1);
        clickCanvas(driver);
        selectClickMenu(driver, ID_MENU_CREATE_RAIL);
        
        scrollMap(driver, -LINE_X1, -LINE_Y1);
        scrollMap(driver, LINE_X2, LINE_Y2);
        clickCanvas(driver);
        endAction(driver);
        scrollMap(driver, -LINE_X2, -LINE_Y2);
    }
}

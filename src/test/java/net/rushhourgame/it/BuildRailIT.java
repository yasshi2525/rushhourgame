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
    protected static final int START_X1 = 100;
    protected static final int START_Y1 = 400;
    protected static final int START_X2 = 300;
    protected static final int START_Y2 = 300;
    
    @Test
    public void testBuildOnePoint() {
        login(driver);
        clickCanvas(driver, START_X1, START_Y1);
        selectClickMenu(driver, Constants.ID_MENU_CREATE_RAIL);
        endAction(driver);
    }
    
    @Test
    public void testBuildLine() {
        login(driver);
        clickCanvas(driver, START_X2, START_Y2);
        selectClickMenu(driver, ID_MENU_CREATE_RAIL);
        
        scrollMap(driver, 300, 0);
        
        clickCanvas(driver, START_X2, START_Y2);
        endAction(driver);
    }
}

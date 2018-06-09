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

import java.util.logging.Level;
import java.util.logging.Logger;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.it.Constants.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;

/**
 * 人を移動させながら操作しても問題ないことを確認する.
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RunningIT extends AbstractIT {

    private static final Logger LOG = Logger.getLogger(RunningIT.class.getName());

    // 1m = 4dot
    protected static final Dimension LINE1 = new Dimension(-500 * 8, -500 * 8);
    protected static final Dimension LINE1R = new Dimension(500 * 8, 500 * 8);
    protected static final Dimension LINE1_D1 = new Dimension(100 * 8, 0 * 8);
    protected static final Dimension LINE1_D2 = new Dimension(100 * 8, 0 * 8);
    protected static final Dimension LINE1_D3 = new Dimension(100 * 8, 0 * 8);
    protected static final Dimension LINE1_D4 = new Dimension(-100 * 8, 0 * 8);
    protected static final Dimension LINE1_D5 = new Dimension(0 * 8, 100 * 8);

    protected static final long RUNNING_INTERVAL = 10;
    protected static final long TRAIN_TIMEOUT = 60;

    @Test
    public void testRunning() {
        try {
            behave.startGame();
            setupInitialBuildings();
            
            behave.login();
            
            setupInitialLine();
            behave.sleep(RUNNING_INTERVAL);
            unsetupInitialLine();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "exception in testRunning", e);
            throw e;
        }
    }

    protected void setupInitialBuildings() {
        behave.createResidence(-550, -500);
        behave.createResidence(-300, -350);
        behave.createCompany(-300, -550);
        behave.createCompany(-150, -500);
    }

    protected void setupInitialLine() {
        behave.startRailCreation(LINE1);
        behave.extendRail(LINE1_D1);
        behave.extendRail(LINE1_D2);
        behave.extendRail(LINE1_D3);
        behave.endAction();

        behave.startRailExtension(LINE1_D4);
        behave.extendRail(LINE1_D5);
        behave.endAction();

        behave.unscrollMapAll();
    }

    protected void unsetupInitialLine() {
        behave.removeStation(LINE1);
        behave.removeStation(LINE1_D1);
        behave.removeStation(LINE1_D2);
        behave.removeStation(LINE1_D3);
        behave.scrollMap(reverse(LINE1_D4));
        behave.removeStation(LINE1_D5);
        
        behave.unscrollMapAll();
    }
}

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
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.rushhourgame.RushHourResourceBundle.*;
import org.junit.Test;
import static net.rushhourgame.it.Constants.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class BuildRailIT extends AbstractIT {

    private static final Logger LOG = Logger.getLogger(BuildRailIT.class.getName());

    protected static final Dimension ONE = new Dimension(-100, -100);
    protected static final Dimension LINE1 = new Dimension(100, 100);
    protected static final Dimension LINE1_D1 = new Dimension(150, 150);
    protected static final Dimension LINE1_D2 = new Dimension(150, 0);
    protected static final Dimension LINE1_D3 = new Dimension(-150, 0);
    protected static final Dimension LINE1_D4 = new Dimension(0, 100);
    protected static String MSG_RAIL_CREATED;
    protected static String MSG_RAIL_STARTING_EXTENTION;
    protected static String MSG_RAIL_EXTENDED;

    @BeforeClass
    public static void setUpClass() {
        MSG_RAIL_CREATED = msg.get(ANNOUNCEMENT_RAIL_CREATE, Locale.JAPAN);
        MSG_RAIL_STARTING_EXTENTION = msg.get(GUIDE_RAIL_EXTEND, Locale.JAPAN);
        MSG_RAIL_EXTENDED = msg.get(ANNOUNCEMENT_RAIL_EXTEND, Locale.JAPAN);
    }

    @Test
    public void testBuildOnePoint() {
        try {
            behave.login();

            assertEquals(MSG_RAIL_CREATED, behave.startRailCreation(ONE));
            behave.endAction();

            behave.unscrollMapAll();
            behave.removeStation(ONE);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "exception in testBuildOnePoint", e);
            LOG.log(Level.SEVERE, driver.getPageSource());
            throw e;
        }
    }

    @Test
    public void testBuildLine() {
        try {
            behave.login();

            assertEquals(MSG_RAIL_CREATED, behave.startRailCreation(LINE1));
            assertEquals(MSG_RAIL_EXTENDED, behave.extendRail(LINE1_D1));
            assertEquals(MSG_RAIL_EXTENDED, behave.extendRail(LINE1_D2));
            behave.endAction();

            assertEquals(MSG_RAIL_STARTING_EXTENTION, behave.startRailExtension(LINE1_D3));
            assertEquals(MSG_RAIL_EXTENDED, behave.extendRail(LINE1_D4));
            behave.endAction();

            behave.unscrollMapAll();
            behave.removeStation(LINE1);
            behave.removeStation(LINE1_D1);
            behave.removeStation(LINE1_D2);
            behave.scrollMap(reverse(LINE1_D3));
            behave.removeStation(LINE1_D4);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "exception in testBuildLine", e);
            LOG.log(Level.SEVERE, driver.getPageSource());
            throw e;
        }
    }
}

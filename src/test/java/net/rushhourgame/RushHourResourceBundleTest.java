/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
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
package net.rushhourgame;

import net.rushhourgame.RushHourResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RushHourResourceBundleTest {

    RushHourResourceBundle inst;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    protected static Locale locale = Locale.getDefault();

    @Before
    public void setUp() {
        inst = new RushHourResourceBundle();
    }

    @Test
    public void testFetch() {
        inst.fetch(locale);
        assertNotNull(inst.cacheList.get(locale));
        assertFalse(inst.cacheList.get(locale).keySet().isEmpty());
    }

    @Test
    public void testGet() {
        assertNotNull(inst.get("rushhour.test.message", locale));
        exception.expect(MissingResourceException.class);
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.test.message.unexistsparameter", locale));
        
        assertNotNull(inst.get("rushhour.test.message", null));
        exception.expect(MissingResourceException.class);
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.test.message.unexistsparameter", null));
    }
    
    @Test
    public void testEscape(){
        assertEquals("''This is sample.''", inst.get("rushhour.test.quote1", locale));
    }

    @Test
    public void testGetLocalized() {
        assertEquals("これはサンプルです。", inst.get("rushhour.test.message", Locale.JAPANESE));
        assertEquals("This is sample message.", inst.get("rushhour.test.message", Locale.ENGLISH));
    }

}

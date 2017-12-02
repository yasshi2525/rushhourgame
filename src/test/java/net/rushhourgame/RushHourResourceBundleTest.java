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

import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RushHourResourceBundleTest {
    @Spy
    protected RushHourResourceBundle inst;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    protected final static Locale LOCALE = Locale.getDefault();

    @Before
    public void setUp() {
        inst.prop = RushHourProperties.getInstance();
    }

    @Test
    public void testFetch() {
        inst.fetch(LOCALE);
        assertNotNull(inst.cacheList.get(LOCALE));
        assertFalse(inst.cacheList.get(LOCALE).keySet().isEmpty());
    }

    @Test
    public void testFetchNull() {
        inst.fetch(null);
        assertNotNull(inst.cacheList.get(LOCALE));
    }

    @Test
    public void testClear() {
        inst.fetch(null);
        inst.clear();
        assertTrue(inst.cacheList.isEmpty());
    }

    @Test
    public void testGet() {
        assertNotNull(inst.get("rushhour.test.message", LOCALE));
        exception.expect(MissingResourceException.class);
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.test.message.unexistsparameter", LOCALE));

        assertNotNull(inst.get("rushhour.test.message", null));
        exception.expect(MissingResourceException.class);
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.test.message.unexistsparameter", null));
    }

    @Test
    public void testEscape() {
        assertEquals("''This is sample.''", inst.get("rushhour.test.quote1", LOCALE));
    }

    @Test
    public void testGetLocalized() {
        assertEquals("これはサンプルです。", inst.get("rushhour.test.message", Locale.JAPANESE));
        assertEquals("This is sample message.", inst.get("rushhour.test.message", Locale.ENGLISH));
    }

    @Test
    public void testGetInstance() {
        assertNotNull(RushHourResourceBundle.getInstance());
        assertNotNull(RushHourResourceBundle.getInstance());
    }

    @Test
    public void testInitLangListNull() {
        inst.prop = mock(RushHourProperties.class);
        doReturn(null).when(inst.prop).get(RushHourProperties.LANG);
        inst.init();
        verify(inst, times(1)).fetch(Locale.getDefault());
    }
    
    @Test
    public void testInitLangListEmpty() {
        inst.prop = mock(RushHourProperties.class);
        doReturn("").when(inst.prop).get(RushHourProperties.LANG);
        inst.init();
        verify(inst, times(1)).fetch(Locale.getDefault());
    }
}

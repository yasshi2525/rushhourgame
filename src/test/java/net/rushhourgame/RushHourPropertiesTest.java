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

import net.rushhourgame.RushHourProperties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RushHourPropertiesTest {

    RushHourProperties inst;

    @Before
    public void setUp() {
        inst = RushHourProperties.getInstance();
    }

    @Test
    public void testFetch() {
        inst.fetch();
        assertNotNull(inst.cache);
        assertFalse(inst.cache.isEmpty());
    }
    
    /**
     * テスト用のプロパティファイルを読み込んでいるか
     */
    @Test
    public void testFetchTestProp(){
        inst.fetch();
        assertNotNull(inst.cache);
        assertNotNull(inst.get("rushhour.test"));
    }

    @Test
    public void testGet() {
        assertEquals("true", inst.get("rushhour.test"));
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.unexistsparameter"));
    }

    @Test
    public void testGetDefault() {
        assertEquals("default", inst.get("rushhour.unexistsparameter", "default"));
        //存在する値にはデフォルト値無効
        assertEquals("true", inst.get("rushhour.test", "default"));
    }

}

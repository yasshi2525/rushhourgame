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
package net.rushhourgame.entity;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class SimplePointTest {

    @Test
    public void testToString() {
        assertEquals("(0.0,0.0)", new SimplePoint().toString());
    }
    
    @Test
    public void testIsAreaIn() {
        SimplePoint inst = new SimplePoint();
        assertTrue(inst.isAreaIn(new SimplePoint(0, 0), -1));
        assertFalse(inst.isAreaIn(new SimplePoint(2, 0), -1));
        assertFalse(inst.isAreaIn(new SimplePoint(2, -1), -1));
        assertFalse(inst.isAreaIn(new SimplePoint(2, 1), -1));
    }
}

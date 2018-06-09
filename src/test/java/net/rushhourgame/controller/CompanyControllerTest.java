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
package net.rushhourgame.controller;

import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.SimplePoint;
import org.junit.Before;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class CompanyControllerTest extends AbstractControllerTest {

    protected CompanyController inst;
    private static final double TEST_X = 5.1;
    private static final double TEST_Y = 10.1;
    private static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    private static final double TEST_SCALE = 15.1;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = cCon;
    }

    @Test
    public void testCreate() throws RushHourException {
        Company created = inst.create(TEST_POS);
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertTrue(Double.parseDouble(PROP.get(GAME_DEF_CMP_SCALE)) == created.getScale());
        assertTrue(0 == created.distTo(created));

        assertEquals(1, inst.findAll().size());
    }

    @Test
    public void testCreate3arg() throws RushHourException {
        Company created = inst.create(TEST_POS, TEST_SCALE);
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertTrue(TEST_SCALE == created.getScale());

        assertEquals(1, inst.findAll().size());
    }

    @Test
    public void testCreateDuplication() throws RushHourException {
        inst.create(TEST_POS);
        try {
            inst.create(TEST_POS);
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }
}

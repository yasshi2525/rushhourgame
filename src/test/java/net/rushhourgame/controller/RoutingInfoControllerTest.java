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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.entity.Absorber;
import net.rushhourgame.entity.Owner;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RoutingInfoControllerTest extends AbstractControllerTest {
    protected RoutingInfoController inst;
    protected Owner admin;
    
    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createRoutingInfoController();
        try {
            admin = createAdmin();
        } catch (RushHourException ex) {
            fail();
        }
    }
    
    @Test
    @Ignore
    public void testSameClassInstance() throws RushHourException {
        /*inst.create(
                aCon.create(admin, 0, 0),
                aCon.create(admin, 0, 0), 
                aCon.create(admin, 0, 0));
        em.flush();*/
    }
    
    @Test
    @Ignore
    public void testDifferentClassInstance() throws RushHourException {
        /*inst.create(
                aCon.create(admin, 0, 0),
                aCon.create(admin, 0, 0), 
                aCon.create(admin, 0, 0));*/
    }
}

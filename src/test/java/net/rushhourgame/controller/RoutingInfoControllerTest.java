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

import javax.persistence.PersistenceException;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Node;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RoutingInfoControllerTest extends AbstractControllerTest {

    protected RoutingInfoController inst;

    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createRoutingInfoController();
    }

    @Test
    public void testCreateNull() {
        try {
            inst.create(null, null);
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_DATA_INCONSIST, ex.getErrMsg().getTitleId());
            assertNull(ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testCreateLoop() throws RushHourException {
        Node node = nCon.create(0, 0);
        try {
            inst.create(node, node);
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_DATA_INCONSIST, ex.getErrMsg().getTitleId());
            assertNull(ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testCreateSameRelation() throws RushHourException {
        Node node1 = nCon.create(0, 0);
        Node node2 = nCon.create(1, 1);
        em.flush();
        try {
            inst.create(node1, node2);
            em.flush();
            fail();
        } catch (PersistenceException ex) {
            em.getTransaction().rollback();
            em.getTransaction().begin();
        }
    }
}

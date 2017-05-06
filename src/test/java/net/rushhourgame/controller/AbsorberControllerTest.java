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

import net.rushhourgame.entity.LocalOwner;
import net.rushhourgame.entity.Owner;
import net.rushhourgame.entity.RoleType;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import org.junit.Before;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class AbsorberControllerTest extends AbstractControllerTest{
    protected static Owner admin = new LocalOwner(RoleType.ADMINISTRATOR);
    protected static Owner player = new LocalOwner(RoleType.PLAYER);
    protected AbsorberController inst;
    
    @Before
    public void setUp(){
        super.setUp();
        inst = ControllerFactory.createAbsorberController();
    }
    
    @Test
    public void testCreate() throws RushHourException{
        assertNotNull(inst.create(admin, 0.0, 0.0));
    }
    
    @Test
    public void testCreateByPlayer(){
        try {
            assertNotNull(inst.create(player, 0.0, 0.0));
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_NO_PRIVILEDGE_ONLY_ADMIN, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testCreateByNull(){
        try {
            assertNotNull(inst.create(null, 0.0, 0.0));
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_NO_OWNER, ex.getErrMsg().getDetailId());
        }
    }
}

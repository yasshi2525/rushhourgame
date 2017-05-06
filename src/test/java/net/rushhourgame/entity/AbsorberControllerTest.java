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
package net.rushhourgame.entity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class AbsorberControllerTest extends AbstractControllerTest{
    protected static Owner admin = new LocalOwner(RoleType.ADMINISTRATOR);
    protected static Owner player = new LocalOwner(RoleType.PLAYER);
    
    @Test
    public void testCreate() throws RushHourException{
        AbsorberController inst = createInstance();
        assertNotNull(inst.create(admin, 0.0, 0.0));
    }
    
    @Test
    public void testCreateByPlayer(){
        AbsorberController inst = createInstance();
        try {
            assertNotNull(inst.create(player, 0.0, 0.0));
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_NO_PRIVILEDGE_ONLY_ADMIN, ex.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testCreateByNull(){
        AbsorberController inst = createInstance();
        try {
            assertNotNull(inst.create(null, 0.0, 0.0));
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_NO_OWNER, ex.getErrMsg().getDetailId());
        }
    }
    
    protected static AbsorberController createInstance(){
        AbsorberController inst = new AbsorberController();
        inst.em = em;
        inst.prop = prop;
        return inst;
    }
}

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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class PointEntityTest extends AbstractEntityTest{

    /**
     * Test of distTo method, of class PointEntity.
     */
    @Test
    public void testDistTo() {
        PointEntity inst = new PointEntity();
        PointEntity target = new PointEntity();
        target.x = 3.0;
        target.y = 4.0;
        double result = inst.distTo(target);
        assertEquals(5.0, result, 0.0);
    }

    /**
     * Test of isPrivilegedBy method, of class PointEntity.
     */
    @Test
    public void testIsPrivilegedBy() {
        Owner admin = new Player();
        admin.getRoles().add(RoleType.ADMINISTRATOR);
        
        Owner other = new Player();
        admin.getRoles().add(RoleType.PLAYER);
        
        Owner self = new Player();
        PointEntity inst = new PointEntity();
        inst.owner = self;
        
        assertTrue(inst.isPrivilegedBy(self));
        assertTrue(inst.isPrivilegedBy(admin));
        assertFalse(inst.isPrivilegedBy(other));
        assertFalse(inst.isPrivilegedBy(null));
    }
    
}

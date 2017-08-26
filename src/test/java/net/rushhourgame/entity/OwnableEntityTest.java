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

import java.util.logging.Level;
import java.util.logging.Logger;
import static net.rushhourgame.entity.AbstractEntityTest.em;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.mockito.Mockito.spy;
import org.mockito.Spy;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class OwnableEntityTest extends AbstractEntityTest {

    private static final Logger LOG = Logger.getLogger(OwnableEntityTest.class.getName());
    
    protected Player player;
    protected Player other;
    protected Player admin;
    protected GameMaster gm;
    protected OwnableEntity inst;
    
    @Before
    public void setUp() {
        super.setUp();
        player = new Player();
        player.id="user1";
        player.getRoles().add(RoleType.PLAYER);
        other = new Player();
        other.getRoles().add(RoleType.PLAYER);
        admin = new Player();
        admin.getRoles().add(RoleType.ADMINISTRATOR);
        try {
            gm = gCon.create();
        } catch (RushHourException ex) {
            LOG.log(Level.SEVERE, null, ex);
            tCon.clean();
            fail();
        }
        inst = spy(OwnableEntity.class);
    }

    /**
     * Test of isPrivilegedBy method, of class Node.
     */
    @Test
    public void testIsPrivilegedBy() {
        inst.player = player;

        assertTrue(inst.isPrivilegedBy(player));
        assertTrue(inst.isPrivilegedBy(admin));
        assertFalse(inst.isPrivilegedBy(other));
        assertFalse(inst.isPrivilegedBy(null));
    }

    @Test
    public void testGetPlayerOwner() {
        inst.player = player;
        assertEquals(player, inst.getOwner());
        assertNotEquals(gm, inst.getOwner());
    }
    
    @Test
    public void testGetGMOwner() {
        inst.gameMaster = gm;
        assertEquals(gm, inst.getOwner());
        assertNotEquals(player, inst.getOwner());
    }
    
    @Test
    public void testSetPlayerOwner(){
        inst.setOwner(player);
        assertEquals(player, inst.getOwner());
        assertNotEquals(gm, inst.getOwner());
    }
    
    @Test
    public void testSetGMOwner(){
        inst.setOwner(gm);
        assertEquals(gm, inst.getOwner());
        assertNotEquals(player, inst.getOwner());
    }
    
    @Test
    public void testSetNullOwner(){
        ex.expect(NullPointerException.class);
        inst.setOwner(null);
    }
    
    @Test
    public void testChangeOwner(){
        inst.setOwner(player);
        assertEquals(player, inst.getOwner());
        assertNotEquals(gm, inst.getOwner());
        
        inst.setOwner(gm);
        assertEquals(gm, inst.getOwner());
        assertNotEquals(player, inst.getOwner());
        
        inst.setOwner(player);
        assertEquals(player, inst.getOwner());
        assertNotEquals(gm, inst.getOwner());
    }
}

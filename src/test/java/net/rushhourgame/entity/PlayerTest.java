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

import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class PlayerTest extends AbstractEntityTest{
    protected static final String JP_NAME = "日本語名";

    @Test
    public void testControlRole() {
        Player player = new Player();
        player.setId("hoge");
        EM.persist(player);
        assertEquals(0, player.getRoles().size());
        
        player.roles.add(RoleType.PLAYER);
        EM.merge(player);
        assertEquals(1, player.getRoles().size());
        
        player.roles.add(RoleType.ADMINISTRATOR);
        EM.merge(player);
        assertEquals(2, player.getRoles().size());
    }
    
    @Test
    public void testPersistNoRole() {
        Player player = new Player();
        player.setId("hoge");
        EM.persist(player);
        assertEquals(0, player.getRoles().size());
        
        player.roles.add(RoleType.PLAYER);
        EM.merge(player);
        assertEquals(1, player.getRoles().size());
        
        player.roles.add(RoleType.ADMINISTRATOR);
        EM.merge(player);
        assertEquals(2, player.getRoles().size());
    }
    
    @Test
    public void testLoadRole() {
        Player player = new Player();
        player.setId("hoge");
        player.roles.add(RoleType.PLAYER);
        EM.persist(player);
        assertEquals(1, player.getRoles().size());
        
        player.roles.add(RoleType.ADMINISTRATOR);
        EM.merge(player);
        assertEquals(2, player.getRoles().size());
    }
    
    @Test
    public void testSetJPName(){
        Player player = new Player();
        OwnerInfo info = new OwnerInfo();
        info.setColor("#000000");
        info.setIconUrl("#000000");
        info.setLocale(Locale.getDefault());
        info.setName(JP_NAME);
        info.setTextColor("#000000");
        player.setId("hoge");
        player.setInfo(info);
        EM.persist(player);
    }
}

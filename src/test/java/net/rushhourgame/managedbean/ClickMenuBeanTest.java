/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.managedbean;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ClickMenuBeanTest extends AbstractBeanTest {

    @Spy
    protected ClickMenuBean inst;

    protected Map<String, String> map;
    
    protected Pointable p;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        inst.session = session;
        inst.rCon = RAILCON;
        inst.pCon = PCON;
        
        try {
            Player player = createPlayer();
            doReturn(player.getToken()).when(session).getToken();
        } catch (RushHourException ex) {
            Logger.getLogger(ClickMenuBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }

        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        doReturn(requestContext).when(inst).getRequestContext();

        map = new HashMap<>();

        doReturn(map).when(externalContext).getRequestParameterMap();
        
        p = new SimplePoint(0, 0);
        inst.click = p;
    }

    @Test
    public void testCanCreateRail() {
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        inst.init();
        assertTrue(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailFarRight() throws RushHourException {
        RAILCON.create(createPlayer(), p);
        
        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "1.1");
        map.put("clickY", "0.0");
        inst.init();
        
        assertTrue(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailFarLeft() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "-1.1");
        map.put("clickY", "0.0");
        inst.init();

        assertTrue(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailFarTop() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "-1.1");
        inst.init();

        assertTrue(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailFarBottom() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "1.1");
        inst.init();

        assertTrue(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailNearRight() throws RushHourException {
        RAILCON.create(createPlayer(), p);
        System.out.println(TCON.findAll("RailNode", RailNode.class));

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.4");
        map.put("clickY", "0.0");
        inst.init();

        assertFalse(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailNearLeft() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "-0.4");
        map.put("clickY", "0.0");
        inst.init();

        assertFalse(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailNearTop() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "-0.4");
        inst.init();

        assertFalse(inst.canCreateRail());
    }

    @Test
    public void testCanCreateRailNearBottom() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "0.4");
        inst.init();

        assertFalse(inst.canCreateRail());
    }
    
    @Test
    public void testCreateRail() throws RushHourException {
        map.put("scale", "13.0");
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        inst.init();
        inst.createRail();
    }
    
    @Test
    public void testCanExtendRailFalse() {
        assertFalse(inst.canExtendRail());
    }
    
    @Test
    public void testCanExtendRail() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        map.put("scale", "3.0");
        map.put("clickX", "0.0");
        map.put("clickY", "0.4");
        inst.init();
        assertTrue(inst.canExtendRail());
    }
    
    @Test
    public void testExtendRail() throws RushHourException {
        RAILCON.create(createPlayer(), p);

        map.put("scale", "3.0");
        map.put("clickX", "0.0");
        map.put("clickY", "0.4");
        inst.init();
        inst.extendRail();
    }

    @Test
    public void testGetFacesContext() {
        assertNull(new ClickMenuBean().getFacesContext());
    }

    @Test
    public void testGetRequestMap() {
        assertNotNull(inst.getRequestMap());
    }
    
    @Test
    public void testRequestContext() {
        assertNull(new ClickMenuBean().getRequestContext());
    }
}

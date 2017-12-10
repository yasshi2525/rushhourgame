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
import net.rushhourgame.entity.RailNode;
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
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        
        inst.rCon = RAILCON;
        
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        
        Map<String, String> map = new HashMap<>();
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        
        doReturn(map).when(externalContext).getRequestParameterMap();
    }

    @Test
    public void testCanCreateRail() {
        assertTrue(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailFarRight() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(1.1).when(inst).getClickX();
        doReturn(0.0).when(inst).getClickY();
        
        assertTrue(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailFarLeft() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(-1.1).when(inst).getClickX();
        doReturn(0.0).when(inst).getClickY();
        
        assertTrue(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailFarTop() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(0.0).when(inst).getClickX();
        doReturn(-1.1).when(inst).getClickY();
        
        assertTrue(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailFarBottom() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(0.0).when(inst).getClickX();
        doReturn(1.1).when(inst).getClickY();
        
        assertTrue(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailNearRight() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        System.out.println(TCON.findAll("RailNode", RailNode.class));
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(0.4).when(inst).getClickX();
        doReturn(0.0).when(inst).getClickY();
        
        assertFalse(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailNearLeft() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(-0.4).when(inst).getClickX();
        doReturn(0.0).when(inst).getClickY();
        
        assertFalse(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailNearTop() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(0.0).when(inst).getClickX();
        doReturn(-0.4).when(inst).getClickY();
        
        assertFalse(inst.canCreateRail());
    }
    
    @Test
    public void testCanCreateRailNearBottom() throws RushHourException {
        RAILCON.create(createPlayer(), 0, 0);
        
        // scale 3 (幅8)のとき、±1を近いと判定
        doReturn(3.0).when(inst).getScale();
        
        doReturn(0.0).when(inst).getClickX();
        doReturn(0.4).when(inst).getClickY();
        
        assertFalse(inst.canCreateRail());
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
    public void testGetClickX() {
        assertTrue(11.0 == inst.getClickX());
    }

    @Test
    public void testGetClickY() {
        assertTrue(12.0 == inst.getClickY());
    }

    @Test
    public void testGetScale() {
        assertTrue(13.0 == inst.getScale());
    }
    
}

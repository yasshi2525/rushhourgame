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
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TrainDeployed;
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
    
    protected Player player;

    protected Map<String, String> map;
    
    protected Pointable p;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        inst.session = session;
        inst.rCon = railCon;
        inst.pCon = pCon;
        inst.tCon = tCon;
        inst.aCon = aCon;
        inst.stCon = stCon;
        inst.em = em;
        
        try {
            player = createPlayer();
            doReturn(player.getToken()).when(session).getToken();
        } catch (RushHourException ex) {
            Logger.getLogger(ClickMenuBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }

        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        doReturn(dialog).when(inst).getDialog();

        map = new HashMap<>();

        doReturn(map).when(externalContext).getRequestParameterMap();
        
        p = new SimplePoint(0, 0);
        inst.click = p;
    }
    
    @Test
    public void testInitWithEdges() throws RushHourException {
        RailNode n1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode n2 = railCon.extend(player, n1, new SimplePoint(100, 200));
        em.flush();
        em.refresh(n1);
        
        inst.player = player;
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        map.put("clickedEdge1", Long.toString(n1.getOutEdges().get(0).getId()));
        map.put("clickedEdge2", Long.toString(n1.getInEdges().get(0).getId()));
        
        inst.init();
        
        assertNotNull(inst.clickedEdges);
        assertTrue(inst.isDisplayRemoveRail());
        assertTrue(inst.isEnableRemoveRail());
    }
    
    @Test
    public void testInitWithInvalidEdge() throws RushHourException {
        RailNode n1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode n2 = railCon.extend(player, n1, new SimplePoint(100, 200));
        em.flush();
        em.refresh(n1);
        
        inst.player = player;
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        map.put("clickedEdge1", Long.toString(n1.getOutEdges().get(0).getId()));
        
        inst.init();
    }
    
    @Test
    public void testInitException() throws RushHourException {
        inst.player = player;
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        map.put("clickedEdge1", "1");
        map.put("clickedEdge2", "2");
        
        inst.init();
        
        assertNull(inst.clickedEdges);
        assertFalse(inst.isDisplayRemoveRail());
        assertFalse(inst.isEnableRemoveRail());
    }

    @Test
    public void testCanCreateRail() {
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        map.put("scale", "13.0");
        inst.init();
        assertTrue(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailFarRight() throws RushHourException {
        railCon.create(player, p);
        
        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "1.1");
        map.put("clickY", "0.0");
        inst.init();
        
        assertTrue(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailFarLeft() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "-1.1");
        map.put("clickY", "0.0");
        inst.init();

        assertTrue(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailFarTop() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "-1.1");
        inst.init();

        assertTrue(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailFarBottom() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "1.1");
        inst.init();

        assertTrue(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailNearRight() throws RushHourException {
        railCon.create(player, p);
        System.out.println(tableCon.findAll("RailNode", RailNode.class));

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.4");
        map.put("clickY", "0.0");
        inst.init();

        assertFalse(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailNearLeft() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "-0.4");
        map.put("clickY", "0.0");
        inst.init();

        assertFalse(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailNearTop() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "-0.4");
        inst.init();

        assertFalse(inst.isDisplayCreateRail());
    }

    @Test
    public void testCanCreateRailNearBottom() throws RushHourException {
        railCon.create(player, p);

        // scale 4 (幅16)のとき、±1を近いと判定
        map.put("scale", "4.0");
        map.put("clickX", "0.0");
        map.put("clickY", "0.4");
        inst.init();

        assertFalse(inst.isDisplayCreateRail());
    }
    
    @Test
    public void testCreateRail() throws RushHourException {
        map.put("scale", "13.0");
        map.put("clickX", "11.0");
        map.put("clickY", "12.0");
        inst.init();
        inst.createRail();
        assertEquals(1, tableCon.findAll("Station", RailNode.class).size());
        assertEquals(1, tableCon.findAll("Station", Station.class).size());
        assertEquals(1, tableCon.findAll("Line", Line.class).size());
        assertEquals(1, tableCon.findAll("LineStep", LineStep.class).size());
    }
    
    @Test
    public void testCanExtendRailFalse() {
        assertFalse(inst.isDisplayExtendRail());
    }
    
    @Test
    public void testCanExtendRail() throws RushHourException {
        railCon.create(player, p);

        map.put("scale", "3.0");
        map.put("clickX", "0.0");
        map.put("clickY", "0.4");
        inst.init();
        assertTrue(inst.isDisplayExtendRail());
    }
    
    @Test
    public void testExtendRail() throws RushHourException {
        map.put("scale", "6.0");
        map.put("clickX", "1.0");
        map.put("clickY", "1.0");
        
        railCon.create(player, new SimplePoint(1.0, 0.0));
        railCon.create(player, new SimplePoint(1.0, 2.0));

        inst.init();
        inst.extendRail();
    }
    
    @Test
    public void testExtendRail2() throws RushHourException {
        map.put("scale", "6.0");
        map.put("clickX", "1.0");
        map.put("clickY", "1.0");
        
        railCon.create(player, new SimplePoint(0.0, 2.5));
        railCon.create(player, new SimplePoint(0.0, 0.0));
        railCon.create(player, new SimplePoint(0.0, 0.5));

        inst.init();
        inst.extendRail();
    }
    
    @Test
    public void testRemoveRail() {
        inst.removeRail();
    }
        
    @Test
    public void testIsDisplayRemoveStation() {
        assertFalse(inst.isDisplayRemoveStation());
        
        inst.clickedStation = mock(Station.class);
        
        assertTrue(inst.isDisplayRemoveStation());
    }
    
    @Test
    public void testRemoveStation() {
        inst.removeStation();
    }
    
    @Test
    public void testIsDisplayUndeployTrain() {
        assertFalse(inst.isDisplayUndeployTrain());
        
        inst.clickedTrain = mock(TrainDeployed.class);
        
        assertTrue(inst.isDisplayUndeployTrain());
    }
    
    @Test
    public void testUndeployTrain() {
        inst.undeployTrain();
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
    public void testGetDialog() {
        assertNotNull(new ClickMenuBean().getDialog());
    }
}

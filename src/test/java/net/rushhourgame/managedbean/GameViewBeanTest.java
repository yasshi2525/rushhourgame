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
import javax.faces.context.ExternalContext;
import javax.faces.event.ActionEvent;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.SlideEndEvent;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GameViewBeanTest extends AbstractBeanTest{
    @Spy
    protected GameViewBean inst;
    
    protected Player player;
    
    protected static final int CLICK_X = 50;
    protected static final int CLICK_Y = 60;
    
    @Mock
    protected SelectEvent event;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        
        inst.msg = msg;
        inst.pCon = PCON;
        inst.cCon = CCON;
        inst.rCon = RCON;
        inst.railCon = RAILCON;
        inst.stCon = STCON;
        inst.lCon = LCON;
        inst.sCon = SCON;
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(GameViewBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        inst.session = session;
        doReturn(player.getToken()).when(session).getToken();
    }

    @Test
    public void testInit() {
        inst.init();
        assertEquals(player, inst.player);
    }
    
    @Test
    public void testRegisterClickPos() {
        Map<String, String> map = new HashMap<>();
        map.put("gamePos.x", Double.toString(99.9));
        map.put("gamePos.y", Double.toString(199.9));
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        doReturn(map).when(externalContext).getRequestParameterMap();
        
        inst.registerClickPos();
        assertTrue(99.9 == inst.getClickX());
        assertTrue(199.9 == inst.getClickY());
    }

    @Test
    public void testOpenClickMenu() throws RushHourException {
        inst.player = player;
        inst.setClickX(CLICK_X);
        inst.setClickY(CLICK_Y);
        doReturn(mock(RequestContext.class)).when(inst).getRequestContext();
        
        inst.openClickMenu();
        
    }
    
    @Test
    public void testGetFacesContext() {
        assertNull(new GameViewBean().getFacesContext());
    }
    
    @Test
    public void testIsOperatingDefault() {
        assertFalse(inst.isOperating());
    }
    
    @Test
    public void testIsOperating() {
        inst.setOperation(OperationType.CREATE_RAIL);
        assertEquals(OperationType.CREATE_RAIL, inst.getOperation());
        assertTrue(inst.isOperating());
    }
    
    @Test
    public void testGetCompanies() {
        inst.getCompanies();
    }
    
    @Test
    public void testGetRegidences() {
        inst.getResidences();
    }
    
    @Test
    public void testGetRailNodes() {
        inst.getRailNodes();
    }
    
    @Test
    public void testGetRailEdges() {
        inst.getRailEdges();
    }
    
    @Test
    public void testGetStation() {
        inst.getStations();
    }
    
    @Test
    public void testGetLines() {
        inst.getLines();
    }
    
    @Test
    public void testGetStepForHuman() {
        inst.getStepForHuman();
    }
    
    @Test
    public void testCenterPos() {
        inst.setCenterX(10);
        assertTrue(10 == inst.getCenterX());
        inst.setCenterY(20);
        assertTrue(20 == inst.getCenterY());
        inst.setScale(2);
        assertTrue(2 == inst.getScale());
        assertTrue(3 == inst.getLoadScale());
    }
    
    @Test
    public void testGetRequestContext() {
        assertNull(new GameViewBean().getRequestContext());
    }
    
    @Test
    public void testOnSlideEnd() {
        SlideEndEvent event = mock(SlideEndEvent.class);
        doReturn(100).when(event).getValue();
        
        inst.onSlideEnd(event);
        
        assertTrue(1 == inst.getScale());
    }
    
    @Test
    public void testInitTutorial() {
        doReturn(facesContext).when(inst).getFacesContext();
        
        inst.initGuide();
    }
    
    @Test
    public void testHandleReturnRailExtend() {
        doReturn(mock(RailNode.class)).when(event).getObject();
        doReturn(facesContext).when(inst).getFacesContext();
        inst.handleReturn(event);
        verify(event, times(2)).getObject();
    }
    
    @Test
    public void testHandleReturnNull() {
        doReturn(null).when(event).getObject();
        inst.handleReturn(event);
        verify(event, times(1)).getObject();
    }
}

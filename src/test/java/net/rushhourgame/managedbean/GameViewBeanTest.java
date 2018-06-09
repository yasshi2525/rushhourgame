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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.AssistanceController.Result;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.TrainDeployed;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.SlideEndEvent;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GameViewBeanTest extends AbstractBeanTest {

    @Spy
    protected GameViewBean inst;

    protected Player player;

    protected static final int CLICK_X = 50;
    protected static final int CLICK_Y = 60;
    protected static final Pointable CLICK_POS = new SimplePoint(CLICK_X, CLICK_Y);
    protected static final Pointable CLICK_POS2 = new SimplePoint(120, 150);

    @Mock
    protected SelectEvent event;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        inst.msg = msg;
        inst.pCon = pCon;
        inst.cCon = cCon;
        inst.rCon = rCon;
        inst.railCon = railCon;
        inst.stCon = stCon;
        inst.lCon = lCon;
        inst.sCon = sCon;
        inst.aCon = aCon;
        inst.tCon = tCon;
        inst.hCon = hCon;
        inst.em = em;
        inst.center = new SimplePoint();
        inst.click = new SimplePoint();
        inst.clickedRailEdge = new ArrayList<>();

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
        inst.clickedRailEdge.add(mock(RailEdge.class));

        inst.registerClickPos();
        assertTrue(99.9 == inst.getClickX());
        assertTrue(199.9 == inst.getClickY());
        assertTrue(inst.clickedRailEdge.isEmpty());
    }

    @Test
    public void testOpenClickMenu() throws RushHourException {
        inst.player = player;
        inst.setClickX(CLICK_X);
        inst.setClickY(CLICK_Y);
        doReturn(primeface).when(inst).getPrimeface();
        doReturn(dialog).when(primeface).dialog();
        
        inst.openClickMenu();
    }

    @Test
    public void testOpenClickMenuEdgeClicked() throws RushHourException {
        inst.player = player;
        inst.setClickX(CLICK_X);
        inst.setClickY(CLICK_Y);
        inst.clickedRailEdge.add(mock(RailEdge.class));
        inst.clickedRailEdge.add(mock(RailEdge.class));

        doReturn(primeface).when(inst).getPrimeface();
        doReturn(dialog).when(primeface).dialog();

        inst.openClickMenu();
    }

    @Test
    public void testGetFacesContext() {
        assertNull(new GameViewBean().getFacesContext());
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
    public void testGetTrains() {
        inst.getTrains();
    }

    @Test
    public void testGetMyRailNodes() {
        assertTrue(inst.getMyRailNodes().isEmpty());
    }

    @Test
    public void testGetMyLonelyRailNodes() throws RushHourException {
        inst.player = player;
        railCon.create(player, CLICK_POS);
        when(inst.getLoadScale()).thenReturn(16.0);
        em.flush();
        inst.getMyLonelyRailNodes();
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
    public void testGetHumans() {
        inst.getHumans();
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
    public void testGetPrimeface() {
        assertNotNull(new GameViewBean().getPrimeface());
    }

    @Test
    public void testOnSlideEnd() {
        SlideEndEvent event = mock(SlideEndEvent.class);
        doReturn(100).when(event).getValue();

        inst.onSlideEnd(event);

        assertTrue(1 == inst.getScale());
    }

    @Test
    public void testInitGuide() {
        doReturn(facesContext).when(inst).getFacesContext();

        inst.initGuide();
    }

    @Test
    public void testInitGuideLonely() throws RushHourException {
        inst.player = player;
        inst.center = new SimplePoint(50, 60);
        railCon.create(player, CLICK_POS);
        em.flush();
        doReturn(facesContext).when(inst).getFacesContext();

        inst.initGuide();

        verify(inst, times(1)).showLonelyRailTutorial();
    }
    
    @Test
    public void testInitGuideExtending() throws RushHourException {
        inst.player = player;
        inst.center = new SimplePoint(50, 60);
        inst.tailNode = railCon.create(player, CLICK_POS);
        em.flush();

        inst.initGuide();

        verify(inst, times(0)).showLonelyRailTutorial();
    }

    @Test
    public void testHandleReturnRailCreate() {
        doReturn(OperationBean.newRailCreate(new Result(mock(RailNode.class), mock(Station.class),mock(Line.class)))).when(event).getObject();
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();
        inst.handleReturn(event);
        assertTrue(inst.isUnderOperation());
    }

    @Test
    public void testHandleReturnRailExtend() {
        doReturn(OperationBean.newRailExtend(mock(RailNode.class))).when(event).getObject();
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();
        inst.handleReturn(event);
        assertTrue(inst.isUnderOperation());
    }

    @Test
    public void testHandleReturnRailRemove() {
        doReturn(OperationBean.newRailRemove()).when(event).getObject();
        doReturn(primeface).when(inst).getPrimeface();
        inst.handleReturn(event);
    }
    
    @Test
    public void testHandleReturnTrainRemove() {
        doReturn(OperationBean.newTrainUndeploy(mock(TrainDeployed.class))).when(event).getObject();
        doReturn(primeface).when(inst).getPrimeface();
        inst.handleReturn(event);
    }

    @Test
    public void testExtendRailLoop() throws RushHourException {
        inst.player = player;
        RailNode r1 = railCon.create(player, new SimplePoint(1, 1));
        RailNode r2 = railCon.extend(player, r1, new SimplePoint(10, 10));
        em.flush();
        inst.tailNode = r2;

        inst.click = new SimplePoint(0.99999999, 0.99999999);

        doReturn(facesContext).when(inst).getFacesContext();

        inst.extendRail();

        verify(inst, times(1)).showLoopRailWarning();
        verify(inst, times(1)).showExtendingRailGuide();
    }

    @Test
    public void testExtendRailConnected() throws RushHourException {
        inst.player = player;
        RailNode r1 = railCon.create(player, new SimplePoint(1, 1));
        RailNode r2 = railCon.create(player, new SimplePoint(10, 10));
        inst.tailNode = r1;
        inst.click = new SimplePoint(9.99999999, 9.99999999);

        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();

        inst.extendRail();

        verify(inst, times(1)).showConnectedRailAnnouncement();
        verify(inst, times(1)).getPrimeface();
        verify(inst, times(1)).showExtendingRailGuide();

        assertTrue(railCon.existsEdge(r1, r2));
        assertEquals(r2, inst.tailNode);
    }

    @Test
    public void testExtendRailZeroDistance() throws RushHourException {
        inst.player = player;
        inst.tailNode = railCon.create(player, new SimplePoint(10, 10));
        inst.click = new SimplePoint(10.01, 10.01);

        doReturn(facesContext).when(inst).getFacesContext();

        inst.extendRail();

        verify(inst, times(1)).showNeighborCreatingWarning();
        verify(inst, times(1)).showExtendingRailGuide();
    }

    @Test
    public void testExtendRailExtending() throws RushHourException {
        inst.player = player;
        RailNode r1 = aCon.startWithStation(player,
                new SimplePoint(10, 10), Locale.JAPANESE).node;
        inst.tailNode = r1;
        inst.click = new SimplePoint(20, 20);

        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();

        inst.extendRail();

        verify(inst, times(1)).showExtendedRailAnnouncement();
        verify(inst, times(1)).getPrimeface();
        verify(inst, times(1)).showExtendingRailGuide();
    }

    @Test
    public void testExtendRail() throws RushHourException {
        inst.player = player;
        inst.tailNode = aCon.startWithStation(player,
                new SimplePoint(10, 10), Locale.JAPANESE).node;
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();

        inst.extendRail();
    }

    @Test
    public void testExtendRailTrianDeployed() throws RushHourException {
        inst.player = player;
        Result result = aCon.startWithStation(player,
                new SimplePoint(10, 10), Locale.JAPANESE);
        inst.tailNode = result.node;
        Train t = tCon.create(player);
        em.flush();
        em.refresh(result.line);
        tCon.deploy(t, player, result.line.findTopDeparture());
        
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();

        inst.extendRail();
    }

    @Test
    public void testFinishedOperation() {
        inst.setUnderOperation(true);
        inst.finishesOperation();
        assertFalse(inst.isUnderOperation());
    }

    @Test
    public void testGetPlayer() {
        inst.player = player;
        assertEquals(inst.getPlayer(), player);
    }

    @Test
    public void testGetPlayerInfos() {
        assertEquals(1, inst.getPlayers().size());
    }

    @Test
    public void testIconPosNull() {
        assertFalse(inst.isIconIn(player));
        assertTrue(0 == inst.getIconPos(player).getX());
        assertTrue(0 == inst.getIconPos(player).getY());
    }

    @Test
    public void testGetIconPos() throws RushHourException {
        inst.player = player;
        inst.scale = 16;
        RailNode far1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode far2 = railCon.extend(player, far1, new SimplePoint(100, 200));

        RailNode near1 = railCon.create(player, new SimplePoint(10, 10));
        RailNode near2 = railCon.extend(player, near1, new SimplePoint(10, 20));

        RailNode tooFar = railCon.create(player, new SimplePoint(1000, 1000));
        RailNode tooFar2 = railCon.extend(player, far1, new SimplePoint(1000, 2000));

        assertTrue(inst.isIconIn(player));
        assertTrue(10 == inst.getIconPos(player).getX());
        assertTrue(15 == inst.getIconPos(player).getY());
    }

    @Test
    public void testGetReverseEdge() throws RushHourException {
        RailNode n1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode n2 = railCon.extend(player, n1, new SimplePoint(100, 200));
        em.flush();
        em.refresh(n1);

        assertEquals(n1.getOutEdges().get(0), inst.getReverseEdge(n1.getInEdges().get(0)));
    }

    @Test
    public void testRegisterEdgeId() throws RushHourException {
        inst.player = player;
        RailNode n1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode n2 = railCon.extend(player, n1, new SimplePoint(100, 200));
        em.flush();
        em.refresh(n1);

        Map<String, String> map = new HashMap<>();
        map.put("railEdge1.id", Long.toString(n1.getOutEdges().get(0).getId()));
        map.put("railEdge2.id", Long.toString(n1.getInEdges().get(0).getId()));
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(externalContext).when(facesContext).getExternalContext();
        doReturn(map).when(externalContext).getRequestParameterMap();

        inst.registerEdgeId();
    }

    @Test
    public void testRemoveRail() throws RushHourException {
        inst.player = player;
        RailNode n1 = railCon.create(player, new SimplePoint(100, 100));
        RailNode n2 = railCon.extend(player, n1, new SimplePoint(100, 200));
        em.flush();
        em.refresh(n1);

        inst.clickedRailEdge = new ArrayList<>();
        inst.clickedRailEdge.add(n1.getOutEdges().get(0));
        inst.clickedRailEdge.add(n1.getInEdges().get(0));
        inst.op = OperationBean.newRailRemove();

        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();
        
        inst.remove();
        
        verify(inst, times(1)).showAnnouncement(eq(ANNOUNCEMENT_RAIL_REMOVE));
    }
    
    @Test
    public void testRemoveStation() throws RushHourException {
        inst.player = player;
        Result res = aCon.startWithStation(player, CLICK_POS, Locale.JAPANESE);
        
        inst.op = OperationBean.newStationRemove(res.station);
        
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();
        
        inst.remove();
        
        verify(inst, times(1)).showAnnouncement(eq(ANNOUNCEMENT_STAION_REMOVE));
    }
    
    @Test
    public void testRemoveTrain() throws RushHourException {
        inst.player = player;
        Result start = aCon.startWithStation(player, CLICK_POS, Locale.JAPANESE);
        Result end = aCon.extendWithStation(player, start.node, CLICK_POS2, Locale.JAPANESE);
        TrainDeployed train = tCon.deploy(tCon.create(player), player, end.line.findTopDeparture());
        
        inst.op = OperationBean.newTrainUndeploy(train);
        
        doReturn(facesContext).when(inst).getFacesContext();
        doReturn(primeface).when(inst).getPrimeface();
        
        inst.remove();
        
        assertFalse(train.getTrain().isDeployed());
        verify(inst, times(1)).showAnnouncement(eq(ANNOUNCEMENT_TRAIN_REMOVE));
    }

    @Test
    public void testSortedLine() throws RushHourException {
        AssistanceController.Result start = aCon.startWithStation(player, CLICK_POS, Locale.JAPANESE);
        inst.getSortedLineSteps(start.line);

        aCon.extend(player, start.node, new SimplePoint());
        inst.getSortedLineSteps(start.line);
    }
}

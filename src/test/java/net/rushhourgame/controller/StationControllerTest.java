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

import java.util.List;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import org.junit.Before;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class StationControllerTest extends AbstractControllerTest {

    protected StationController inst;
    private static final String TEST_NAME = "test";
    private static final double TEST_X = 10;
    private static final double TEST_Y = 10;
    private static final double TEST_X2 = 20;
    private static final double TEST_Y2 = 10;
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_NUM = 3;

    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createStationController();
    }

    @Test
    public void testCreate() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        Station created = inst.create(player, node, TEST_NAME);

        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_GATE_NUM)), 
                created.getTicketGate().getGateNum());
        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_PLT_CAPACITY)), 
                created.getPlatform().getCapacity());

    }
    
    @Test
    public void testFull() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        Station created = inst.create(player, node, TEST_NAME, TEST_NUM, TEST_CAPACITY);
        
        //test station
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(player, created.getOwner());
        assertTrue(created.isOwnedBy(player));
        assertTrue(created.isPrivilegedBy(player));
        assertEquals(TEST_NAME, created.getName());
        
        //test gate
        TicketGate gate = created.getTicketGate();
        assertNotNull(gate);
        assertTrue(TEST_X == gate.getX());
        assertTrue(TEST_Y == gate.getY());
        assertEquals(player, gate.getOwner());
        assertEquals(created, gate.getStation());
        assertEquals(TEST_NUM, gate.getGateNum());
        assertTrue(gate.isOwnedBy(player));
        assertTrue(gate.isPrivilegedBy(player));
        
        //test platform
        Platform platform = created.getPlatform();
        assertNotNull(platform);
        assertTrue(TEST_X == platform.getX());
        assertTrue(TEST_Y == platform.getY());
        assertEquals(player, platform.getOwner());
        assertEquals(created, platform.getStation());
        assertEquals(TEST_CAPACITY, platform.getCapacity());
        assertTrue(platform.isOwnedBy(player));
        assertTrue(platform.isPrivilegedBy(player));
        
        EM.flush();
        EM.refresh(node);

        assertEquals(1, node.getPlatforms().size());
    }

    @Test
    public void testCreateNullOwner() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        try {
            inst.create(null, node, TEST_NAME);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testCreateNullNode() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        try {
            inst.create(player, null, TEST_NAME);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testCreateOtherOwner() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        try {
            inst.create(other, node, TEST_NAME);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testCreateNameNull() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        try {
            inst.create(other, node, null);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testEditName() throws RushHourException {
        Station created = createStation();
        
        inst.editStationName(created, created.getOwner(), "changed");
        assertEquals("changed", created.getName());
    }
    
    @Test
    public void testEditNameNullStation() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editStationName(null, created.getOwner(), "changed");
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testEditNameNullOwner() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editStationName(created, null, "changed");
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testEditNameOtherOwner() throws RushHourException {
        Station created = createStation();
        Player other = createOther();
        
        try {
            inst.editStationName(created, other, "changed");
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testEditNameNullName() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editStationName(created, created.getOwner(), null);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testEditPlatformCapacity() throws RushHourException {
        Station created = createStation();
        
        inst.editPlatformCapacity(created, created.getOwner(), 1000);
        assertEquals(1000, created.getPlatform().getCapacity());
    }
    
    @Test
    public void testEditPlatformCapacityNullStation() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editPlatformCapacity(null, created.getOwner(), 1000);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testEditPlatformCapacityNullOwner() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editPlatformCapacity(created, null, 1000);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testEditPlatformCapacityOtherOwner() throws RushHourException {
        Station created = createStation();
        Player other = createOther();
        
        try {
            inst.editPlatformCapacity(created, other, 1000);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testEditTicketGameNum() throws RushHourException {
        Station created = createStation();
        
        inst.editTicketGateNum(created, created.getOwner(), 100);
        assertEquals(100, created.getTicketGate().getGateNum());
    }
    
    @Test
    public void testEditTicketGameNumNullStation() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editTicketGateNum(null, created.getOwner(), 100);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }
    
    @Test
    public void testEditTicketGameNumNullOwner() throws RushHourException {
        Station created = createStation();
        
        try {
            inst.editTicketGateNum(created, null, 100);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testEditTicketGameNumOtherOwner() throws RushHourException {
        Station created = createStation();
        Player other = createOther();
        
        try {
            inst.editTicketGateNum(created, other, 100);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
}

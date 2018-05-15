/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to pass, copy, modify, merge, publish, distribute, sublicense, and/or sell
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

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
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
    private static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    private static final double TEST_X2 = 20;
    private static final double TEST_Y2 = 10;
    private static final Pointable TEST_POS2 = new SimplePoint(TEST_X2, TEST_Y2);
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_NUM = 3;
    private static final double TEST_MOBILITY = 0.002;
    private static final double TEST_PRODIST = 5.0;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createStationController();
    }
    
    @Test
    public void testFindNull() throws RushHourException {
        Station st = createStation();
        inst.entities = null;
        assertNull(inst.find(st.getPlatform()));
    }

    @Test
    public void testStep() throws RushHourException {
        Station st = createStation();
        for (int i = 0; i < st.getTicketGate().getGateNum(); i++) {
            st.getTicketGate().pass();
        }
        
        assertFalse(st.getTicketGate().canEnter());
        
        inst.step(1000);
        
        assertTrue(st.getTicketGate().canEnter());
    }

    @Test
    public void testCreate() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_POS);
        Station created = inst.create(player, node, TEST_NAME);

        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_GATE_NUM)),
                created.getTicketGate().getGateNum());
        assertTrue(Double.parseDouble(PROP.get(GAME_DEF_GATE_MOBILITY))
                == created.getTicketGate().getMobility());
        assertTrue(Double.parseDouble(PROP.get(GAME_DEF_GATE_PRODIST))
                == created.getTicketGate().getProdist());
        assertEquals(Integer.parseInt(PROP.get(GAME_DEF_PLT_CAPACITY)),
                created.getPlatform().getCapacity());
    }

    @Test
    public void testFull() throws RushHourException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_POS);
        Station created = inst.create(player, node,
                TEST_NAME, TEST_NUM, TEST_CAPACITY, TEST_MOBILITY, TEST_PRODIST);

        //test station
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(player, created.getOwner());
        assertFalse(created.isOwnedBy(null));
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
        assertTrue(TEST_MOBILITY == gate.getMobility());
        assertTrue(TEST_PRODIST == gate.getProdist());
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

        assertEquals(platform, node.getPlatform());
    }

    @Test
    public void testCreateOtherOwner() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode node = RAILCON.create(player, TEST_POS);
        try {
            inst.create(other, node, TEST_NAME);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testCreateDuplicateName() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_POS);
        RailNode node2 = RAILCON.extend(owner, node1, TEST_POS2);
        inst.create(owner, node1, "Station1");

        try {
            inst.create(owner, node2, "Station1");
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }

    /**
     * 他人の駅となら重複可
     *
     * @throws RushHourException
     */
    @Test
    public void testCreateOtherDuplicateName() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_POS);

        Player other = createOther();
        RailNode node2 = RAILCON.create(other, TEST_POS2);

        inst.create(owner, node1, "Station1");

        inst.create(other, node2, "Station1");
    }

    @Test
    public void testEditName() throws RushHourException {
        Station created = createStation();

        inst.editStationName(created, created.getOwner(), "changed");
        assertEquals("changed", created.getName());
    }

    @Test
    public void testEditSameName() throws RushHourException {
        Station created = createStation();

        inst.editStationName(created, created.getOwner(), created.getName());
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
    public void testEditNameDuplication() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_POS);
        RailNode node2 = RAILCON.extend(owner, node1, TEST_POS2);
        inst.create(owner, node1, "Station1");
        Station created = inst.create(owner, node2, "Station2");

        try {
            inst.editStationName(created, owner, "Station1");
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DUP, e.getErrMsg().getTitleId());
        }
    }

    /**
     * 他人の駅なら重複可能
     *
     * @throws RushHourException
     */
    @Test
    public void testEditNameDuplicationOther() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_POS);

        Player other = createOther();
        RailNode node2 = RAILCON.create(other, TEST_POS2);

        Station created = inst.create(owner, node1, "Station1");
        inst.create(other, node2, "Station2");

        inst.editStationName(created, owner, "Station2");
    }

    @Test
    public void testEditPlatformCapacity() throws RushHourException {
        Station created = createStation();

        inst.editPlatformCapacity(created, created.getOwner(), 1000);
        assertEquals(1000, created.getPlatform().getCapacity());
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

    @Test
    public void testFindIn() throws RushHourException {
        createStation();

        assertEquals(1, inst.findIn(new SimplePoint(0, 0), 1).size());
        assertEquals(0, inst.findIn(new SimplePoint(10, 10), 1).size());
    }

    @Test
    public void testInheritEntity() throws RushHourException {
        Station station = createStation();

        inst.synchronizeDatabase();
        inst.entities = null;
        inst.synchronizeDatabase();

        assertEquals(station.getPlatform(), inst.findAll().get(0).getPlatform());
        assertEquals(station.getTicketGate(), inst.findAll().get(0).getTicketGate());
    }
}

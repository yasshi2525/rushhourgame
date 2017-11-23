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

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.controller.AbstractControllerTest.validatorForExecutables;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
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
    private static final double TEST_X2 = 20;
    private static final double TEST_Y2 = 10;
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_NUM = 3;

    @Before
    @Override
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
    public void testCreateNullOwner() throws RushHourException, NoSuchMethodException {
        Player player = createPlayer();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        
        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("create", Player.class, RailNode.class, String.class),
                new Object[]{null, node, TEST_NAME});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testCreateNullNode() throws RushHourException, NoSuchMethodException {
        Player player = createPlayer();
        RAILCON.create(player, TEST_X, TEST_Y);
        
        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("create", Player.class, RailNode.class, String.class),
                new Object[]{player, null, TEST_NAME});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
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
    public void testCreateNameNull() throws RushHourException, NoSuchMethodException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode node = RAILCON.create(player, TEST_X, TEST_Y);
        
        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("create", Player.class, RailNode.class, String.class),
                new Object[]{player, node, null});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testCreateDuplicateName() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_X, TEST_Y);
        RailNode node2 = RAILCON.extend(owner, node1, TEST_X2, TEST_Y2);
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
        RailNode node1 = RAILCON.create(owner, TEST_X, TEST_Y);

        Player other = createOther();
        RailNode node2 = RAILCON.create(other, TEST_X2, TEST_Y2);

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
    public void testEditNameNullStation() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editStationName", Station.class, Player.class, String.class),
                new Object[]{null, created.getOwner(), "changed"});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testEditNameNullOwner() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editStationName", Station.class, Player.class, String.class),
                new Object[]{created, null, "changed"});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
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
    public void testEditNameNullName() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editStationName", Station.class, Player.class, String.class),
                new Object[]{created, created.getOwner(),  null});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testEditNameDuplication() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_X, TEST_Y);
        RailNode node2 = RAILCON.extend(owner, node1, TEST_X2, TEST_Y2);
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
     * @throws RushHourException 
     */
    @Test
    public void testEditNameDuplicationOther() throws RushHourException {
        Player owner = createPlayer();
        RailNode node1 = RAILCON.create(owner, TEST_X, TEST_Y);
        
        Player other = createOther();
        RailNode node2 = RAILCON.create(other, TEST_X2, TEST_Y2);
        
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
    public void testEditPlatformCapacityNullStation() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editPlatformCapacity", Station.class, Player.class, int.class),
                new Object[]{null, created.getOwner(), 1000});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testEditPlatformCapacityNullOwner() throws RushHourException, NoSuchMethodException {
        Station created = createStation();
        
        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editPlatformCapacity", Station.class, Player.class, int.class),
                new Object[]{created, null,  1000});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
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
    public void testEditInvalidPlatformCapacity() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editPlatformCapacity", Station.class, Player.class, int.class),
                new Object[]{created, created.getOwner(), 0});
        
        assertViolatedValueIs(0, violations);
        assertViolatedAnnotationTypeIs(Min.class, violations);
    }

    @Test
    public void testEditTicketGameNum() throws RushHourException {
        Station created = createStation();

        inst.editTicketGateNum(created, created.getOwner(), 100);
        assertEquals(100, created.getTicketGate().getGateNum());
    }

    @Test
    public void testEditTicketGameNumNullStation() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editTicketGateNum", Station.class, Player.class, int.class),
                new Object[]{null, created.getOwner(),  100});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }

    @Test
    public void testEditTicketGameNumNullOwner() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editTicketGateNum", Station.class, Player.class, int.class),
                new Object[]{created, null,  100});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
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
    public void testEditInvalidTicketGameNum() throws RushHourException, NoSuchMethodException {
        Station created = createStation();

        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                inst,
                StationController.class.getMethod("editTicketGateNum", Station.class, Player.class, int.class),
                new Object[]{created, created.getOwner(), 0});
        
        assertViolatedValueIs(0, violations);
        assertViolatedAnnotationTypeIs(Min.class, violations);
    }
}

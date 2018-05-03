/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import static net.rushhourgame.RushHourResourceBundle.GAME_DATA_INCONSIST;
import static net.rushhourgame.RushHourResourceBundle.GAME_NO_PRIVILEDGE_OTHER_OWNED;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Train;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TrainControllerTest extends AbstractControllerTest {

    protected TrainController inst = ControllerFactory.createTrainController();
    protected Player player;
    protected LineStep lineStep;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        try {
            player = createPlayer();
            AssistanceController.Result result = ACON.startWithStation(player, new SimplePoint(10.0, 15.0), Locale.JAPANESE);
            ACON.extend(player, result.node, new SimplePoint(20.0, 25.0));
            EM.flush();
            lineStep = result.line.findTop();
        } catch (RushHourException ex) {
            Logger.getLogger(TrainControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testCreate() throws RushHourException {
        Train created = inst.create(player);
        assertEquals(100, created.getMobility());
        assertTrue(0.2 == created.getSpeed());
        assertEquals(20, created.getCapacity());
        assertTrue(created.isPrivilegedBy(player));
        assertEquals(player, created.getOwner());

        try {
            created.getX();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Not deployed.", e.getMessage());
        }
        try {
            created.getY();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Not deployed.", e.getMessage());
        }
        try {
            created.distTo(new SimplePoint());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Not deployed.", e.getMessage());
        }
        try {
            created.step(new ArrayList<>(), 0);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Not deployed.", e.getMessage());
        }
    }

    @Test
    public void testDeploy() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        assertTrue(10.0 == train.getX());
        assertTrue(15.0 == train.getY());
        assertTrue(0.0 == train.distTo(new SimplePoint(10.0, 15.0)));
    }

    @Test
    public void testDeployOther() {
        Train train = inst.create(player);
        try {
            inst.deploy(train, createOther(), lineStep);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testDeployAlreadyDeployed() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        try {
            inst.deploy(train, player, lineStep);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testUndeploy() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        inst.undeploy(train, player);
    }
    
    @Test
    public void testUndeployOther() {
        Train train = inst.create(player);
        try {
            inst.undeploy(train, createOther());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testUndeployAlreadyUneployed() throws RushHourException {
        Train train = inst.create(player);
        try {
            inst.undeploy(train, player);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }

    @Test
    public void testFindAll_0args() {
        inst.findAll();
    }

    @Test
    public void testFindIn() {
        inst.findIn(new SimplePoint(), 0);
    }

    @Test
    public void testFindAll_Player() {
        inst.findAll(player);
    }

    @Test
    public void testStep() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        inst.step(train, 0, new ArrayList<>());
    }

    @Test
    public void testStepUndeploy() throws RushHourException {
        Train train = spy(inst.create(player));
        inst.step(train, 0, new ArrayList<>());
        verify(train, times(0)).step(anyList(), anyLong());
    }
}

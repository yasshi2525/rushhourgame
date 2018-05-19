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
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.TrainDeployed;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
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

    protected TrainController inst = spy(ControllerFactory.createTrainController());
    protected Player player;
    protected LineStep lineStep;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.writeLock = spy(inst.writeLock);
        inst.readLock = spy(inst.readLock);
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
        
        verify(inst.writeLock, times(2)).lock();
        verify(inst.writeLock, times(2)).unlock();
        
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
            created.isAreaIn(new SimplePoint(), 0);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Not deployed.", e.getMessage());
        }
    }

    @Test
    public void testDeploy() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        
        verify(inst.writeLock, times(3)).lock();
        verify(inst.writeLock, times(3)).unlock();
        
        assertTrue(10.0 == train.getX());
        assertTrue(15.0 == train.getY());
        assertTrue(0.0 == train.distTo(new SimplePoint(10.0, 15.0)));
        assertTrue(train.isAreaIn(new SimplePoint(10, 15), 0));
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
        inst.undeploy(train.getDeployed(), player);
        
        verify(inst.writeLock, times(4)).lock();
        verify(inst.writeLock, times(4)).unlock();
    }

    @Test
    public void testUndeployOther() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        try {
            inst.undeploy(train.getDeployed(), createOther());
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testUndeployWithPassenger() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        
        Residence src = RCON.create(new SimplePoint());
        Company dst = CCON.create(new SimplePoint());
        Human human = HCON.create(new SimplePoint(), src, dst);
        human.setOnTrain(train.getDeployed());
        
        try {
            inst.undeploy(train.getDeployed(), player);
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
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }

    @Test
    public void testFindAll_Player() {
        inst.findAll(player);
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }

    @Test
    public void testStep() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        inst.step(0);
        
        verify(inst.writeLock, times(4)).lock();
        verify(inst.writeLock, times(4)).unlock();
    }
    
    @Test
    public void testInheritEntity() throws RushHourException {
        Train train = inst.create(player);
        inst.deploy(train, player, lineStep);
        train.getDeployed().step(new ArrayList<>(), 1000);
        double x = train.getDeployed().getX();
        double y = train.getDeployed().getY();
        
        inst.synchronizeDatabase();
        inst.entities = null;
        inst.synchronizeDatabase();
        
        assertTrue(x == inst.findAll().get(0).getDeployed().getX());
        assertTrue(y == inst.findAll().get(0).getDeployed().getY());
    }
}

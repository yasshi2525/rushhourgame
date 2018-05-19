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
import java.util.List;
import javax.persistence.EntityManager;
import static net.rushhourgame.RushHourProperties.GAME_DEF_HUMAN_LIFESPAN;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.TrainDeployed;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HumanControllerTest extends AbstractControllerTest {

    protected HumanController inst;
    protected Pointable origin = new SimplePoint();
    protected static final double TEST_X = 10;
    protected static final double TEST_Y = 20;
    protected static final Pointable TEST = new SimplePoint(TEST_X, TEST_Y);

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = spy(ControllerFactory.createHumanController());
        inst.writeLock = spy(inst.writeLock);
        inst.readLock = spy(inst.readLock);
    }

    @After
    @Override
    public void tearDown() {
        super.tearDown();
    }
    
    @Test
    public void testCreate() throws RushHourException {
        inst.entities = new ArrayList<>();
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);

        Human h = inst.create(TEST, src, dst);
        
        verify(inst.writeLock, times(2)).lock();
        verify(inst.writeLock, times(2)).unlock();

        assertNotNull(h);
        assertTrue(TEST_X == h.getX());
        assertTrue(TEST_Y == h.getY());
        assertEquals(src, h.getSrc());
        assertEquals(dst, h.getDest());
        assertEquals(Long.parseLong(PROP.get(GAME_DEF_HUMAN_LIFESPAN)), h.getLifespan());
        assertEquals(Human.StandingOn.GROUND, h.getStandingOn());
        assertNull(h.getCurrent());
        assertFalse(h.isFinished());
        assertEquals(1, inst.entities.size());
        assertEquals(h, inst.entities.get(0));
    }

    @Test
    public void testCreateUnsynchronized() throws RushHourException {
        inst.entities = null;
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);

        Human h = inst.create(TEST, src, dst);

        assertNotNull(h);
        assertNull(inst.entities);
    }

    @Test
    public void testFindAll() {
        inst.entities = new ArrayList<>();
        Human human = mock(Human.class);
        inst.entities.add(human);

        List<Human> actual = inst.findAll();

        assertEquals(inst.entities, actual);
        assertEquals(1, actual.size());
        assertEquals(human, actual.get(0));
    }

    @Test
    public void testFindAllUnsynchronized() {
        inst.entities = null;

        List<Human> actual = inst.findAll();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindIn() {
        inst.entities = new ArrayList<>();

        Human humanIn = mock(Human.class);
        doReturn(true).when(humanIn).isAreaIn(any(Pointable.class), anyDouble());
        inst.entities.add(humanIn);

        Human humanOut = mock(Human.class);
        doReturn(false).when(humanOut).isAreaIn(any(Pointable.class), anyDouble());
        inst.entities.add(humanOut);

        List<Human> actual = inst.findIn(origin, 0);
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();

        assertEquals(1, actual.size());
        assertEquals(humanIn, actual.get(0));
    }

    @Test
    public void testFindInUnsynchronized() {
        inst.entities = null;

        List<Human> actual = inst.findIn(origin, 0);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testStepWithSearching() throws RushHourException {
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);
        inst.searcher.call();
        
        inst.create(origin, src, dst);
        Human h = spy(inst.findAll().get(0));
        inst.findAll().remove(0);
        inst.findAll().add(h);

        inst.step(1000000, 0.00001);
        
        verify(inst.writeLock, times(3)).lock();
        verify(inst.writeLock, times(3)).unlock();

        verify(h, times(1)).searchCurrent(eq(inst.searcher));
        verify(h, times(1)).step(eq(inst.em), eq(1000000L), eq(0.00001d));
    }

    @Test
    public void testStepWithoutSearching() throws RushHourException {
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);

        inst.create(origin, src, dst);
        Human h = spy(inst.findAll().get(0));
        inst.findAll().remove(0);
        inst.findAll().add(h);
        
        RouteNode node = mock(RouteNode.class);
        doReturn(mock(RouteEdge.class)).when(node).getViaEdge();
        h.setCurrent(node);
        doNothing().when(h).step(eq(inst.em), anyLong(), anyDouble());

        inst.step(1000000, 0.00001);

        verify(h, never()).searchCurrent(eq(inst.searcher));
        verify(h, times(1)).step(eq(inst.em), eq(1000000L), eq(0.00001d));
    }

    @Test
    public void testKillHuman() {
        inst.em = spy(EM);

        Human happy = spy(Human.class);
        Human tired = spy(Human.class); // lifespan = 0
        Human ended = spy(Human.class);

        happy.setLifespan(500);
        tired.setLifespan(0);
        ended.setLifespan(200);

        doReturn(false).when(happy).isFinished();
        doReturn(true).when(ended).isFinished();

        List<Human> humans = new ArrayList<>();
        humans.add(happy);
        humans.add(tired);
        humans.add(ended);
        inst.entities = humans;

        inst.killHuman();

        assertTrue(humans.contains(happy));
        assertFalse(humans.contains(tired));
        assertFalse(humans.contains(ended));
    }
    
    @Test
    public void testInheritEntity() throws RushHourException {
        Player player = createPlayer();
        Residence src = RCON.create(origin);
        Company dst = CCON.create(origin);
        RailNode node = RAILCON.create(player, origin);
        Station station = STCON.create(player, node, "hoge");
        
        Human human = inst.create(TEST, src, dst);
        human.enterIntoPlatform(station.getTicketGate(), station.getPlatform());
        
        assertEquals(station.getPlatform(), human.getOnPlatform());
        
        inst.synchronizeDatabase();
        inst.entities = null;
        inst.synchronizeDatabase();
        
        assertEquals(station.getPlatform(), inst.findAll().get(0).getOnPlatform());
    }
}

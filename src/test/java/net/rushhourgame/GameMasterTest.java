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
package net.rushhourgame;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.Train;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class GameMasterTest {
    protected ControllerFactory factory;

    protected GameMaster inst;

    @Mock
    protected Lock lock;

    protected static final Pointable ORIGIN = new SimplePoint();
    protected static final Pointable FAR = new SimplePoint(10, 20);
    
    @Mock
    protected Future<Boolean> future;

    @Before
    public void setUp() {
        factory = new ControllerFactory();

        inst = factory.getGameMaster();
        
        doNothing().when(inst.rCon).step(anyLong());
        // 人を生成しないようにする
        doReturn(future).when(inst.executorService).submit(any(RouteSearcher.class));
        
        factory.begin();
    }

    @After
    public void tearDown() {
        factory.rollback();
    }
    
    @Test
    public void testInit() {
        inst.init();
        
        assertNotNull(inst.writeLock);
    }

    @Test
    public void testPreDestroy() {
        inst.preDestroy();

        verify(inst, times(1)).stopGame();
    }

    @Test
    public void testConstructTempleateWorld() throws Exception {
        inst.constructTemplateWorld();

        verify(inst.debug).init();
    }

    @Test
    public void testStartGame() {
        inst.timerFuture = null;

        assertTrue(inst.startGame());

        verify(inst.timerService, times(1)).scheduleWithFixedDelay(any(GameMaster.class), anyLong(), anyLong(), any(TimeUnit.class));
        verify(inst, times(1)).synchronizeDatabase();
    }

    @Test
    public void testStartGameAlreadyRunning() {
        doReturn(false).when(inst.timerFuture).isDone();

        assertFalse(inst.startGame());
        verify(inst, never()).synchronizeDatabase();
        verify(inst.timerService, never()).scheduleWithFixedDelay(any(GameMaster.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testStartGameWhenCanceled() {
        doReturn(true).when(inst.timerFuture).isDone();

        assertTrue(inst.startGame());
        verify(inst.timerService, times(1)).scheduleWithFixedDelay(any(GameMaster.class), anyLong(), anyLong(), any(TimeUnit.class));
        verify(inst, times(1)).synchronizeDatabase();
    }

    @Test
    public void testStopGame() {
        doReturn(false).when(inst.timerFuture).isDone();
        doReturn(true).when(inst.timerFuture).cancel(eq(false));

        assertTrue(inst.stopGame());
        
        verify(inst.timerFuture, times(1)).cancel(eq(false));
        verify(inst, times(1)).synchronizeDatabase();
    }

    @Test
    public void testStopGameNull() {
        inst.timerFuture = null;

        assertFalse(inst.stopGame());
        
        verify(inst, never()).synchronizeDatabase();
    }

    @Test
    public void testStopGameAlreadyCanceled() {
        doReturn(true).when(inst.timerFuture).isDone();

        assertFalse(inst.stopGame());
        
        verify(inst.timerFuture, never()).cancel(anyBoolean());
        verify(inst, never()).synchronizeDatabase();
    }
    
    @Test
    public void testStopGameCancellingFailed() {
        doReturn(false).when(inst.timerFuture).isDone();
        doReturn(false).when(inst.timerFuture).cancel(eq(false));

        assertFalse(inst.stopGame());
        
        verify(inst.timerFuture, times(1)).cancel(eq(false));
        verify(inst, never()).synchronizeDatabase();
    }
    
    @Test
    public void synchronizeDatabase() {
        inst.synchronizeDatabase();
        
        verify(inst.stCon, times(1)).synchronizeDatabase();
        verify(inst.rCon, times(1)).synchronizeDatabase();
        verify(inst.tCon, times(1)).synchronizeDatabase();
        verify(inst.hCon, times(1)).synchronizeDatabase();
    }
    
    @Test
    public void testCreateResidence() throws RushHourException {
        inst.createResidence(new SimplePoint());
    }
    
    @Test
    public void testCreateCompany() throws RushHourException {
        inst.createCompany(new SimplePoint());
    }
    
    @Test
    public void testSearch() throws InterruptedException, ExecutionException {
        doReturn(true).when(future).get();
        
        assertTrue(inst.search());
    }
    
    @Test
    public void testSearchException() throws InterruptedException, ExecutionException {
        doThrow(ExecutionException.class).when(future).get();
        
        assertFalse(inst.search());
    }

    @Test
    public void testRunException() throws InterruptedException, ExecutionException {
        doThrow(ExecutionException.class).when(future).get();
        doReturn(false).when(inst.searcher).isAvailable();

        inst.run();
        
        verify(future, times(1)).get();
    }

    @Test
    public void testRunException2() {
        doReturn(true).when(inst.searcher).isAvailable();
        doThrow(NullPointerException.class).when(inst.searcher).lock();

        try {
            inst.run();
            fail();
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testRunWhenInterrupted() throws RushHourException {
        inst.synchronizeDatabase();
        WorldPack world = createSmallWorld();
        doReturn(false).when(inst.searcher).isAvailable();
        inst.run();
        verify(inst.executorService, times(1)).submit(any(RouteSearcher.class));
        verify(inst.stCon, never()).step(anyLong());
    }

    /**
     * 経路検索前にゲームスレッドを実行
     *
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testRun() throws RushHourException {
        inst.synchronizeDatabase();
        WorldPack world = createSmallWorld();
        doReturn(true).when(inst.searcher).isAvailable();

        inst.run();

        verify(inst.stCon, times(1)).step(anyLong());
        
        verify(inst.rCon, times(1)).step(anyLong());
        
        verify(inst.tCon, times(1)).step(anyLong());
        verify(inst.hCon, times(1)).step(anyLong(), anyDouble());

        assertNull(world.h.getCurrent());
    }

    /**
     * R St--St C
     *
     * @return WorldPack
     * @throws RushHourException
     */
    protected WorldPack createSmallWorld() throws RushHourException {
        return createSmallWorld(true);
    }

    protected WorldPack createSmallWorld(boolean createsHuman) throws RushHourException {
        WorldPack pack = new WorldPack();
        pack.rsd = inst.rCon.create(ORIGIN);
        pack.cmp = factory.getCompanyController().create(FAR);
        pack.owner = factory.getPlayerController()
                .upsertPlayer("admin", "admin", "admin", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());

        AssistanceController.Result start = factory.getAssistanceController()
                .startWithStation(pack.owner, ORIGIN, Locale.getDefault());
        pack.st1 = start.station;
        pack.l = start.line;

        AssistanceController.Result end = factory.getAssistanceController()
                .extendWithStation(pack.owner, start.node, FAR, Locale.getDefault());
        pack.st2 = end.station;

        if (createsHuman) {
            pack.h = inst.hCon.create(ORIGIN, pack.rsd, pack.cmp);
        }

        pack.t = factory.getTrainController().create(pack.owner);
        factory.getTrainController().deploy(pack.t, pack.owner, pack.l.findTopDeparture());

        return pack;
    }

    protected static class WorldPack {

        public Residence rsd;
        public Company cmp;
        public Player owner;
        public Station st1;
        public Station st2;
        public Human h;
        public Train t;
        public Line l;
    }
}

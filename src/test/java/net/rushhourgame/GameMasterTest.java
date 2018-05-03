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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.Platform;
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
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GameMasterTest {

    protected GameMaster inst;

    @Mock
    protected DebugInitializer debug;
    @Mock
    protected Lock lock;

    protected static final EntityManager EM = LocalEntityManager.createEntityManager();
    protected static final TrainController TCON = ControllerFactory.createTrainController();
    protected static final HumanController HCON = ControllerFactory.createHumanController();
    protected static final ResidenceController RCON = ControllerFactory.createResidenceController();
    protected static final CompanyController CCON = ControllerFactory.createCompanyController();
    protected static final AssistanceController ACON = ControllerFactory.createAssistanceController();
    protected static final PlayerController PCON = ControllerFactory.createPlayController();
    protected static final StationController STCON = ControllerFactory.createStationController();

    protected static final Pointable ORIGIN = new SimplePoint();
    protected static final Pointable FAR = new SimplePoint(10, 20);

    @Mock
    protected ManagedExecutorService executorService;

    @Mock
    protected ManagedScheduledExecutorService timerService;

    @Before
    public void setUp() {
        EM.getTransaction().begin();

        inst = new GameMaster();
        inst.debug = debug;
        inst.executorService = executorService;
        inst.timerService = timerService;
        inst.searcher = spy(ControllerFactory.createRouteSearcher(inst));
        inst.searcher.init();
        inst.prop = RushHourProperties.getInstance();
        inst.stCon = spy(STCON);
        inst.tCon = spy(TCON);
        inst.hCon = spy(HCON);
        inst.rCon = spy(RCON);
        inst.em = spy(EM);
        doNothing().when(inst.rCon).step(any(Residence.class), anyLong(), anyList());
        // 人を生成しないようにする
        doReturn(mock(Future.class)).when(executorService).submit(any(RouteSearcher.class));
    }

    @After
    public void tearDown() {
        EM.getTransaction().rollback();
    }

    @Test
    public void testInit() throws Exception {
        inst.init(null);

        verify(debug).init();
        verify(timerService).scheduleWithFixedDelay(any(GameMaster.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRunException() throws InterruptedException, ExecutionException {
        Future future = mock(Future.class);
        doReturn(future).when(executorService).submit(any(RouteSearcher.class));
        doThrow(ExecutionException.class).when(future).get();
        
        inst.run();
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
        WorldPack world = createSmallWorld();
        inst.humans = HCON.findAll();
        doReturn(false).when(inst.searcher).isAvailable();
        inst.run();
        verify(inst.stCon, never()).step(any(Station.class), anyLong());
    }

    /**
     * 経路検索前にゲームスレッドを実行
     *
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testRunBeforeRouting() throws RushHourException {
        WorldPack world = createSmallWorld();
        inst.humans = HCON.findAll();
        doReturn(true).when(inst.searcher).isAvailable();

        inst.run();

        verify(inst.stCon, times(2)).step(any(Station.class), anyLong());
        verify(inst.rCon, times(1)).step(any(Residence.class), anyLong(), anyList());
        verify(inst.tCon, times(1)).step(any(Train.class), anyLong(), anyList());
        verify(inst.hCon, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(inst.searcher, times(1)).isReachable(any(Residence.class), any(Company.class));
        verify(inst.searcher, never()).getStart(any(Residence.class), any(Company.class));
        verify(inst.searcher, never()).isReachable(any(Platform.class), any(Company.class));
        verify(inst.searcher, never()).getStart(any(Platform.class), any(Company.class));

        assertNull(world.h.getCurrent());
    }

    @Test
    public void testRunAfterRouting() throws RushHourException {
        WorldPack world = createSmallWorld(false);
        inst.humans = HCON.findAll();
        inst.searcher.call();
        doReturn(true).when(inst.searcher).isAvailable();
        
        Human human = HCON.create(ORIGIN, world.rsd, world.cmp);
        inst.humans = HCON.findAll();

        inst.run();

        verify(inst.rCon, times(1)).step(any(Residence.class), anyLong(), anyList());
        verify(inst.tCon, times(1)).step(any(Train.class), anyLong(), anyList());
        verify(inst.hCon, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(inst.searcher, times(1)).isReachable(any(Residence.class), any(Company.class));
        verify(inst.searcher, times(1)).getStart(any(Residence.class), any(Company.class));
        verify(inst.searcher, never()).isReachable(any(Platform.class), any(Company.class));
        verify(inst.searcher, never()).getStart(any(Platform.class), any(Company.class));

        assertNotNull(human.getCurrent());
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
        pack.rsd = RCON.create(ORIGIN);
        pack.cmp = CCON.create(FAR);
        pack.owner = PCON.upsertPlayer("admin", "admin", "admin", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());

        AssistanceController.Result start = ACON.startWithStation(pack.owner, ORIGIN, Locale.getDefault());
        pack.st1 = start.station;
        pack.l = start.line;

        AssistanceController.Result end = ACON.extendWithStation(pack.owner, start.node, FAR, Locale.getDefault());
        pack.st2 = end.station;

        if (createsHuman) {
            pack.h = HCON.create(ORIGIN, pack.rsd, pack.cmp);
        }

        pack.t = TCON.create(pack.owner);
        TCON.deploy(pack.t, pack.owner, pack.l.findTop());

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

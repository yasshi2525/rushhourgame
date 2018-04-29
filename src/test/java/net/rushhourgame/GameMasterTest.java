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
import java.util.concurrent.ExecutorService;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Train;
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
    protected Train train;
    @Mock
    protected Residence rsd;
    @Mock
    protected Human human;
    
    @Mock
    protected ManagedExecutorService executor;
    
    @Before
    public void setUp() {
        inst = new GameMaster();
        inst.debug = debug;
        inst.executorService = executor;
        inst.timerService = mock(ManagedScheduledExecutorService.class);
        inst.searcher = mock(RouteSearcher.class);
        inst.prop = RushHourProperties.getInstance();
        inst.tCon = spy(ControllerFactory.createTrainController());
        inst.hCon = spy(ControllerFactory.createHumanController());
        inst.rCon = spy(ControllerFactory.createResidenceController());
    }

    @Test
    public void testInit() throws Exception {
        inst.init(null);
        assertEquals(1000, inst.interval);
    }
    
    @Test
    public void testRunDoNothing() {
        inst.run();
    }

    @Test
    public void testRun() throws Exception {
        List<Train> trains = new ArrayList<>();
        trains.add(train);
        List<Residence> rsds = new ArrayList<>();
        rsds.add(rsd);
        List<Human> humans = new ArrayList<>();
        humans.add(human);
        doReturn(mock(Residence.class)).when(human).getSrc();
        doReturn(mock(Company.class)).when(human).getDest();
        
        doReturn(trains).when(inst.tCon).findAll();
        doReturn(humans).when(inst.hCon).findAll();
        doReturn(rsds).when(inst.rCon).findAll();
        
        inst.searcher.call();
        inst.run();
        
        verify(inst.searcher, times(1)).getStart(any(Residence.class), any(Company.class));
        verify(inst.hCon, times(1)).step(any(Human.class), anyLong(), anyDouble());
        verify(inst.tCon, times(1)).step(any(Train.class), anyLong());
        verify(inst.rCon, times(1)).step(any(Residence.class), anyLong());
    }
    
}

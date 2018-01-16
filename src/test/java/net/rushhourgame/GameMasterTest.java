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
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.entity.Train;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GameMasterTest {
    
    protected GameMaster inst;
    
    @Mock
    protected TrainController tCon;
    
    @Mock
    protected Train train;
    
    @Mock
    protected ManagedExecutorService executor;
    
    @Before
    public void setUp() {
        inst = new GameMaster();
        inst.executorService = executor;
        inst.timerService = mock(ManagedScheduledExecutorService.class);
        inst.prop = RushHourProperties.getInstance();
        inst.tCon = tCon;
    }

    @Test
    public void testInit() throws Exception {
        inst.init(null);
        assertEquals(1000, inst.interval);
    }

    @Test
    public void testStep() throws Exception {
        List<Train> trains = new ArrayList<>();
        trains.add(train);
        
        doReturn(trains).when(tCon).findAll();
        inst.run();
    }
    
}

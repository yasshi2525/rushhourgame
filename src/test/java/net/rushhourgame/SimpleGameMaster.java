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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.TrainController;
import static org.mockito.Mockito.mock;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class SimpleGameMaster extends GameMaster {
    
    private static final long serialVersionUID = 1L;
    
    public void init(
            EntityManager em,
            DebugInitializer debug,
            HumanController hCon,
            LineController lCon,
            RushHourProperties prop,
            ResidenceController rCon,
            RouteSearcher searcher,
            StationController stCon,
            TrainController tCon,
            CompanyController cCon
    ) {
        this.debug = debug;
        this.em = em;
        this.hCon = hCon;
        this.prop = prop;
        this.rCon = rCon;
        this.searcher = searcher;
        this.stCon = stCon;
        this.tCon = tCon;
        this.cCon = cCon;
        this.lCon = lCon;
        writeLock = mock(Lock.class);
        timerFuture = mock(ScheduledFuture.class);
        executorService = mock(ManagedExecutorService.class);
        timerService = mock(ManagedScheduledExecutorService.class);
    }
}

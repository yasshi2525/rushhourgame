/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class GameMaster implements Serializable, Runnable {

    private static final Logger LOG = Logger.getLogger(GameMaster.class.getName());

    private static final long serialVersionUID = 1L;

    @Resource
    protected ManagedScheduledExecutorService timerService;
    @Inject
    protected TrainController tCon;
    @Inject
    protected ResidenceController rCon;
    @Inject
    protected HumanController hCon;
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected RouteSearcher searcher;
    @Inject
    protected DebugInitializer debug;

    @Resource(lookup = "concurrent/RushHourGameRoute")
    protected ManagedExecutorService executorService;

    @Transactional
    public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext event) throws RushHourException {
        LOG.log(Level.INFO, "{0}#init start initialization : event = {1}", new Object[]{this.getClass().getSimpleName(), event});
        debug.init();
        timerService.scheduleWithFixedDelay(this, getInterval() * 5, getInterval(), TimeUnit.MILLISECONDS);
        LOG.log(Level.INFO, "{0}#init end initialization", GameMaster.class.getSimpleName());
    }

    @Transactional
    @Override
    public void run() {
        LOG.log(Level.FINE, "{0}#run", new Object[]{this.getClass().getSimpleName()});
        
        if (!searcher.isAvailable()) {
            executorService.submit(searcher);
            // RouteSearcherは別スレッドなのでトランザクション外
        }
        
        try {
            rCon.findAll().forEach(r -> {
                rCon.step(r, getInterval());
            });
            tCon.findAll().forEach(t -> {
                tCon.step(t, getInterval());
            });
            searcher.lock.lock();
            try {
                hCon.findAll().forEach(h -> {
                    if (h.getCurrent() == null && searcher.isReachable(h.getSrc(), h.getDest())) {
                        h.setCurrent(searcher.getStart(h.getSrc(), h.getDest()));  // HumanControllerから RouteSearcherを呼ぶと循環してしまう
                    }
                    hCon.step(h, getInterval(), getHumanSpeed());
                });
            } finally {
                searcher.lock.unlock();
            }
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, "GameMaster#run exception in run()", e);
            throw e;
        }
    }

    public void research() {
        executorService.submit(searcher);
    }

    protected long getInterval() {
        return Long.parseLong(prop.get(GAME_INTERVAL));
    }

    protected double getHumanSpeed() {
        return Double.parseDouble(prop.get(GAME_DEF_HUMAN_SPEED));
    }
}

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
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.entity.Human;
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
    protected ScheduledFuture<?> timerFuture;
    
    @Inject
    protected TrainController tCon;
    @Inject
    protected ResidenceController rCon;
    @Inject
    protected StationController stCon;
    @Inject
    protected HumanController hCon;
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected RouteSearcher searcher;
    @Inject
    protected DebugInitializer debug;
    @PersistenceContext
    protected EntityManager em;

    @Resource(lookup = "concurrent/RushHourGameRoute")
    protected ManagedExecutorService executorService;

    /**
     * 永続化すると経路情報が消えてしまう
     */
    protected List<Human> humans;
    
    @PreDestroy
    public void preDestroy() {
        LOG.log(Level.INFO, "{0}#preDestroy stop game", new Object[]{GameMaster.class});
        stopGame();
    }
    
    @Transactional
    public void constructTemplateWorld() throws RushHourException {
        LOG.log(Level.INFO, "{0}#constructTemplateWorld start", new Object[]{GameMaster.class});
        debug.init();
        executorService.submit(searcher);
        LOG.log(Level.INFO, "{0}#constructTemplateWorld end", new Object[]{GameMaster.class});
    }
    
    @Transactional
    public boolean startGame() {
        if (timerFuture != null && !timerFuture.isDone()) {
            LOG.log(Level.WARNING, "{0}#startGame failed to start game because game is already running.", new Object[]{GameMaster.class});
            return false;
        }
        
        humans = hCon.findAll();
        timerFuture = timerService.scheduleWithFixedDelay(this, 0L, getInterval(), TimeUnit.MILLISECONDS);
        return true;
    }
    
    public boolean stopGame() {
        if (timerFuture == null) {
            LOG.log(Level.WARNING, "{0}#stopGame failed to stop game because game was not started.", new Object[]{GameMaster.class});
            return false;
        }
        if (timerFuture.isDone()) {
            LOG.log(Level.WARNING, "{0}#stopGame failed to stop game because game was already stopped.", new Object[]{GameMaster.class});
            return false;
        }
        return timerFuture.cancel(false);
    }

    @Transactional
    @Override
    public void run() {
        LOG.log(Level.FINE, "{0}#run", new Object[]{GameMaster.class});

        if (!searcher.isAvailable()) {
            try {
                executorService.submit(searcher).get();
                return;
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(GameMaster.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }

        try {
            searcher.lock();
            try {
                // 更新された場合、なにもしない
                if (!searcher.isAvailable()) {
                    return;
                }

                stCon.findAll().forEach(st -> {
                    stCon.step(st, getInterval());
                });
                rCon.findAll().forEach(r -> {
                    rCon.step(r, getInterval(), humans);
                });

                mergeHumans();
                tCon.findAll().forEach(t -> {
                    tCon.step(t, getInterval(), humans);
                });
                humans.forEach(h -> {
                    if (h.getCurrent() == null) {
                        h.searchCurrent(searcher);
                    }
                    hCon.step(h, getInterval(), getHumanSpeed());
                });
                hCon.killHuman(humans);
            } finally {
                searcher.unlock();
            }
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, "{0}#run exception in run()", GameMaster.class);
            throw e;
        }
    }

    protected long getInterval() {
        return Long.parseLong(prop.get(GAME_INTERVAL));
    }

    protected double getHumanSpeed() {
        return Double.parseDouble(prop.get(GAME_DEF_HUMAN_SPEED));
    }

    /**
     * Read Only
     * @return humans
     */
    public List<Human> getHumans() {
        mergeHumans();
        return humans;
    }

    protected void mergeHumans() {
        humans = humans.stream().map(h -> h.merge(em)).collect(Collectors.toList());
    }
}

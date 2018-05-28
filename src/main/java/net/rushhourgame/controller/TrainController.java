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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.TrainDeployed;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class TrainController extends CachedController<Train> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TrainController.class.getName());

    @Inject
    protected HumanController hCon;
    @Inject
    protected LineController lCon;
    @Inject
    protected RouteSearcher searcher;

    @Override
    public void synchronizeDatabase() {
        LOG.log(Level.INFO, "{0}#synchronizeDatabase start", new Object[]{TrainController.class});
        writeLock.lock();
        try {
            synchronizeDatabase("Train.findAll", Train.class);
            findAll().stream()
                    .filter(t -> t.isDeployed())
                    .map(t -> t.getDeployed())
                    .forEach(t -> t.mergeCurrent(lCon.find(t.getCurrent())));
        } finally {
            writeLock.unlock();
        }
        LOG.log(Level.INFO, "{0}#synchronizeDatabase end", new Object[]{TrainController.class});
    }
    
    public TrainDeployed find(TrainDeployed old) {
        readLock.lock();
        try {
            if (old == null) {
                return null;
            }
            if (entities == null) {
                LOG.log(Level.WARNING, "{0}#find controller never synchronize database", new Object[]{TrainController.class});
                return null;
            }
            return entities.stream().filter(e -> e.getDeployed().equalsId(old)).findFirst().get().getDeployed();
        } finally {
            readLock.unlock();
        }
    }

    public Train create(@NotNull Player p) {
        return create(p, Long.parseLong(prop.get(GAME_DEF_TRAIN_MOBILITY)),
                Double.parseDouble(prop.get(GAME_DEF_TRAIN_SPEED)),
                Integer.parseInt(prop.get(GAME_DEF_TRAIN_CAPACITY)));
    }

    public Train create(@NotNull Player p, long mobility, double speed, int capacity) {
        writeLock.lock();
        try {
            Train train = new Train();
            train.setOwner(p);
            train.setMobility(mobility);
            train.setSpeed(speed);
            train.setCapacity(capacity);
            persistEntity(train);
            em.flush();
            LOG.log(Level.INFO, "{0}#create created {1}", new Object[]{TrainController.class, train});
            return train;
        } finally {
            writeLock.unlock();
        }
    }

    public TrainDeployed deploy(@NotNull Train train, @NotNull Player p, @NotNull LineStep lineStep) throws RushHourException {
        writeLock.lock();
        try {
            train = find(train);
            if (!train.isOwnedBy(p)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }
            if (train.isDeployed()) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            TrainDeployed info = new TrainDeployed();
            info.setTrain(train);
            info.setCurrent(lineStep);

            train.setDeployed(info);
            em.persist(info);

            em.flush();
            LOG.log(Level.INFO, "{0}#deploy created {1}", new Object[]{TrainController.class, info});

            train.setDeployed(info);
            
            return info;
        } finally {
            writeLock.unlock();
        }
    }

    public void undeploy(@NotNull TrainDeployed train, @NotNull Player p) throws RushHourException {
        writeLock.lock();
        try {
            train = find(train.getTrain()).getDeployed();
            if (!train.getTrain().isOwnedBy(p)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }
            getOffPassenger(train);
            train.getTrain().setDeployed(null);
            em.createNamedQuery("TrainDeployed.deleteBy", TrainDeployed.class)
                    .setParameter("obj", train).executeUpdate();

            LOG.log(Level.INFO, "{0}#undeploy removed {1}", new Object[]{TrainController.class, train});
        } finally {
            writeLock.unlock();
        }
    }

    public List<Train> findBy(@NotNull Line line) {
        readLock.lock();
        try {
            return findAll().stream()
                    .filter(t -> t.isDeployed())
                    .filter(t -> t.getDeployed().getCurrent().getParent().equalsId(line))
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    public void step(long time) {
        writeLock.lock();
        try {
            findAll().stream().filter(t -> t.isDeployed())
                    .forEach(t -> t.getDeployed().step(hCon.findAll(), time));
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 電車を撤去する際、乗客を強制的にその場に降ろす。
     * 乗客は経路が変わるため、再計算する
     * @param train 撤去対象
     */
    protected void getOffPassenger(TrainDeployed train) {
        hCon.getWriteLock().lock();
        try {
            hCon.findAll().stream()
                    .filter(h -> train.equalsId(h.getOnTrain()))
                    .forEach(h -> h.getOffTrainForce());
            searcher.notifyUpdate();
        } finally {
            hCon.getWriteLock().unlock();
        } 
    }
}

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
@Dependent
public class TrainController extends PointEntityController {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TrainController.class.getName());
    
    @Inject
    HumanController hCon;
    
    public Train create(@NotNull Player p) {
        return create(p, Long.parseLong(prop.get(GAME_DEF_TRAIN_MOBILITY)), 
                Double.parseDouble(prop.get(GAME_DEF_TRAIN_SPEED)),
                Integer.parseInt(prop.get(GAME_DEF_TRAIN_CAPACITY)));
    }

    public Train create(@NotNull Player p, long mobility, double speed, int capacity) {
        Train train = new Train();
        train.setOwner(p);
        train.setMobility(mobility);
        train.setSpeed(speed);
        train.setCapacity(capacity);
        em.persist(train);
        em.flush();
        LOG.log(Level.INFO, "{0}#create created {1}", new Object[] {TrainController.class, train});
        return train;
    }

    public void deploy(@NotNull Train train, @NotNull Player p, @NotNull LineStep lineStep) throws RushHourException {
        if (!train.isOwnedBy(p)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (train.isDeployed()) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        TrainDeployed info = new TrainDeployed();
        info.setTrain(train);
        info.setCurrent(lineStep);
        em.persist(info);
        em.flush();
        LOG.log(Level.INFO, "{0}#deploy created {1}", new Object[] {TrainController.class, info});

        train.setDeployed(info);
    }

    public void undeploy(@NotNull Train train, @NotNull Player p) throws RushHourException {
        if (!train.isOwnedBy(p)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (!train.isDeployed()) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        em.remove(train.getDeployed());
        LOG.log(Level.INFO, "{0}#undeploy removed {1}", new Object[] {TrainController.class, train.getDeployed()});
    }

    public List<Train> findAll() {
        return em.createNamedQuery("Train.findAll", Train.class).getResultList();
    }

    public List<Train> findIn(@NotNull Pointable center, double scale) {
        return findIn(em.createNamedQuery("Train.findIn", Train.class), center, scale);
    }

    public List<Train> findAll(Player p) {
        return em.createNamedQuery("Train.findMyAll", Train.class)
                .setParameter("owner", p).getResultList();
    }
    
    public List<Train> findBy(@NotNull Line line) {
        return em.createNamedQuery("Train.findByLine", Train.class)
                .setParameter("line", line).getResultList();
    }

    public void step(Train t, long time) {
        if (t.isDeployed()) {
            t.step(hCon.findAll(), time);
        }
    }
}

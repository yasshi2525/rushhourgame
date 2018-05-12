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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.Train;
import net.rushhourgame.entity.TrainDeployed;
import net.rushhourgame.exception.RushHourException;

/**
 * 
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class HumanController extends CachedController<Human> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(HumanController.class.getName());

    @Inject
    RouteSearcher searcher;
    
    @Override
    public void synchronizeDatabase() {
        LOG.log(Level.INFO, "{0}#synchronizeDatabase start", new Object[]{HumanController.class});
        synchronizeDatabase("Human.findAll", Human.class);
        LOG.log(Level.INFO, "{0}#synchronizeDatabase end", new Object[]{HumanController.class});
    }

    public Human create(@NotNull Pointable point, @NotNull Residence src, @NotNull Company dst) {
        Human human = new Human();
        human.setX(point.getX());
        human.setY(point.getY());
        human.setSrc(src);
        human.setDest(dst);
        human.setLifespan(Long.parseLong(prop.get(GAME_DEF_HUMAN_LIFESPAN)));
        human.setStandingOn(Human.StandingOn.GROUND);
        persistEntity(human);
        
        em.flush();
        LOG.log(Level.FINE, "{0}#create created {1}", new Object[]{HumanController.class, human.toString()});
        return human;
    }

    

    public void step(Human h, long interval, double speed) {
        if (h.getCurrent() == null) {
            h.searchCurrent(searcher);
        }
        h.step(em, interval, speed);
    }

    public void merge(Residence obj) {
        entities.forEach(h -> h.merge(obj));
    }

    public void merge(Company obj) {
        entities.forEach(h -> h.merge(obj));
    }

    public void merge(Station obj) {
        entities.forEach(h -> h.merge(obj.getPlatform()));
    }

    public void merge(Train obj) {
        if (obj.isDeployed()) {
            entities.forEach(h -> h.merge(obj.getDeployed()));
        }
    }

    public void killHuman() {
        entities.removeIf(h -> {
            boolean res = h.shouldDie();
            if (res) {
                em.createNamedQuery("Human.deleteBy", Human.class).setParameter("h", h).executeUpdate();
                if (h.getLifespan() <= 0) {
                    LOG.log(Level.FINE, "{0}#killFinishedHuman() deleted lifespan 0 {1} : {2})",
                            new Object[]{HumanController.class, h.toString(), h.getCurrent()});
                }
            }
            return res;
        });
    }
}

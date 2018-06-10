/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class ResidenceController extends CachedController<Residence> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ResidenceController.class.getName());

    @Inject
    protected HumanController hCon;

    @Inject
    protected CompanyController cCon;

    @Inject
    protected StepForHumanController sCon;

    @Inject
    protected RouteSearcher searcher;

    @Override
    public void synchronizeDatabase() {
        LOG.log(Level.INFO, "{0}#synchronizeDatabase start", new Object[]{ResidenceController.class});
        writeLock.lock();
        try {
            synchronizeDatabase("Residence.findAll", Residence.class);
        } finally {
            writeLock.unlock();
        }
        LOG.log(Level.INFO, "{0}#synchronizeDatabase end", new Object[]{ResidenceController.class});
    }

    public Residence create(@NotNull Pointable p) throws RushHourException {
        return create(p,
                Integer.parseInt(prop.get(GAME_DEF_RSD_CAPACITY)),
                Long.parseLong(prop.get(GAME_DEF_RSD_INTERVAL)),
                Double.parseDouble(prop.get(GAME_DEF_RSD_PRODIST)));
    }

    public Residence create(@NotNull Pointable p, @Min(1) int capacity, @Min(1) long interval, @Min(0) double prodist) throws RushHourException {
        writeLock.lock();
        try {
            if (exists(p)) {
                throw new RushHourException(errMsgBuilder.createResidenceDuplication(p));
            }
            Residence inst = new Residence();
            inst.setCapacity(capacity);
            inst.setInterval(interval);
            inst.setCount((long) (interval * Math.random()));
            inst.setX(p.getX());
            inst.setY(p.getY());
            inst.setProdist(prodist);
            persistEntity(inst);
            em.flush();
            LOG.log(Level.INFO, "{0}#create created {1}", new Object[]{ResidenceController.class, inst});
            sCon.addResidence(inst);
            return inst;
        } finally {
            writeLock.unlock();
        }
    }

    public void step(long interval) {
        writeLock.lock();
        try {
            findAll().forEach(r -> {
                r.step(interval);
                while (r.expires()) {
                    List<Company> companies = cCon.findAll();
                    if (companies.isEmpty()) {
                        LOG.log(Level.WARNING, "{0}#step() skip create human because there is no company.", ResidenceController.class);
                        return;
                    }
                    Collections.shuffle(companies);
                    double cost = searcher.getCost(r, companies.get(0));

                    if (cost <= Double.parseDouble(prop.get(GAME_DEF_HUMAN_MAXCOST))) {
                        for (int i = 0; i < r.getCapacity(); i++) {
                            hCon.create(r.makeNearPoint(r.getProdist()), r, companies.get(0));
                        }
                    } else {
                        LOG.log(Level.FINE, "{0}#step() skip create human because of too cost {1} ({2} -> {3})",
                                new Object[]{ResidenceController.class, cost, r, companies.get(0)});
                    }

                    r.consume();
                }
            });
        } finally {
            writeLock.unlock();
        }
    }

}

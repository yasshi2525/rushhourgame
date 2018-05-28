/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to pass, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@ApplicationScoped
public class StationController extends CachedController<Station> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(StationController.class.getName());

    @Inject
    protected StepForHumanController sCon;

    @Override
    public void synchronizeDatabase() {
        LOG.log(Level.INFO, "{0}#synchronizeDatabase start", new Object[]{StationController.class});
        writeLock.lock();
        try {
            synchronizeDatabase("Station.findAll", Station.class);
        } finally {
            writeLock.unlock();
        }
        LOG.log(Level.INFO, "{0}#synchronizeDatabase end", new Object[]{StationController.class});
    }

    public Platform find(Platform old) {
        readLock.lock();
        try {
            if (old == null) {
                return null;
            }
            if (entities == null) {
                LOG.log(Level.WARNING, "{0}#find controller never synchronize database", new Object[]{StationController.class});
                return null;
            }
            return entities.stream().filter(e -> e.getPlatform().equalsId(old)).findFirst().get().getPlatform();
        } finally {
            readLock.unlock();
        }
    }
    
    public TicketGate find(TicketGate old) {
        readLock.lock();
        try {
            if (old == null) {
                return null;
            }
            if (entities == null) {
                LOG.log(Level.WARNING, "{0}#find controller never synchronize database", new Object[]{StationController.class});
                return null;
            }
            return entities.stream().filter(e -> e.getTicketGate().equalsId(old)).findFirst().get().getTicketGate();
        } finally {
            readLock.unlock();
        }
    }

    public void step(long interval) {
        writeLock.lock();
        try {
            findAll().forEach(st -> {
                // 専有されていた改札機を開放する
                st.getTicketGate().step(interval);
            });
        } finally {
            writeLock.unlock();
        }
    }

    public Station create(@NotNull Player owner, @NotNull RailNode node, @NotNull String name) throws RushHourException {
        return create(owner, node, name,
                Integer.parseInt(prop.get(GAME_DEF_GATE_NUM)),
                Integer.parseInt(prop.get(GAME_DEF_PLT_CAPACITY)),
                Double.parseDouble(prop.get(GAME_DEF_GATE_MOBILITY)),
                Double.parseDouble(prop.get(GAME_DEF_GATE_PRODIST)));
    }

    public Station create(@NotNull Player owner, @NotNull RailNode node, @NotNull String name,
            @Min(1) int gatenum, @Min(1) int platformCapacity, @Min(0) double gateMobility, @Min(0) double prodist) throws RushHourException {
        writeLock.lock();
        try {
            if (!node.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }
            if (existsName(owner, name)) {
                throw new RushHourException(errMsgBuilder.createStationNameDuplication(name));
            }

            Station s = new Station();
            s.setOwner(owner);
            s.setName(name);

            s.setTicketGate(createTicketGate(s, gatenum, gateMobility, prodist));
            s.setPlatform(createPlatform(s, node, platformCapacity));

            persistEntity(s);
            em.flush();
            LOG.log(Level.INFO, "{0}#create created {1}", new Object[]{StationController.class, s});
            sCon.addStation(s);

            return s;
        } finally {
            writeLock.unlock();
        }
    }

    public void editStationName(@NotNull Station station, @NotNull Player owner, @NotNull String name) throws RushHourException {
        writeLock.lock();
        try {
            if (!station.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }
            // 変更なしのときは何もしない
            if (station.getName().equals(name)) {
                return;
            }
            if (existsName(owner, name)) {
                throw new RushHourException(errMsgBuilder.createStationNameDuplication(name));
            }
            station.setName(name);
            LOG.log(Level.INFO, "{0}#editStationName edited {1}", new Object[]{StationController.class, station});
        } finally {
            writeLock.unlock();
        }
    }

    public void editPlatformCapacity(@NotNull Station station, @NotNull Player owner, @Min(1) int capacity) throws RushHourException {
        writeLock.lock();
        try {
            if (!station.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }

            station.getPlatform().setCapacity(capacity);
            LOG.log(Level.INFO, "{0}#editPlatformCapacity edited {1}", new Object[]{StationController.class, station});
        } finally {
            writeLock.unlock();
        }
    }

    public void editTicketGateNum(@NotNull Station station, @NotNull Player owner, @Min(1) int num) throws RushHourException {
        writeLock.lock();
        try {
            if (!station.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }

            station.getTicketGate().setGateNum(num);
            LOG.log(Level.INFO, "{0}#editTicketGateNum edited {1}", new Object[]{StationController.class, station});
        } finally {
            writeLock.unlock();
        }
    }

    public List<Platform> findPlatformAll() {
        readLock.lock();
        try {
            return findAll().stream().map(st -> st.getPlatform()).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    public List<TicketGate> findTicketGateAll() {
        readLock.lock();
        try {
            return findAll().stream().map(st -> st.getTicketGate()).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    protected Platform createPlatform(Station s, RailNode node, int capacity) {
        Platform p = new Platform();
        p.setStation(s);
        p.setCapacity(capacity);
        p.setRailNode(node);

        return p;
    }

    protected TicketGate createTicketGate(Station s, int num, double mobility, double prodist) {
        TicketGate gate = new TicketGate();
        gate.setStation(s);
        gate.setGateNum(num);
        gate.setMobility(mobility);
        gate.setProdist(prodist);

        return gate;
    }

    protected boolean existsName(Player owner, String name) {
        return findAll().stream().filter(st -> st.isOwnedBy(owner)).anyMatch(st -> st.getName().equals(name));
    }
}

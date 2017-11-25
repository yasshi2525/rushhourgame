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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class StationController extends PointEntityController {

    private static final long serialVersionUID = 1L;

    @Inject
    protected StepForHumanController sCon;

    public Station create(@NotNull Player owner, @NotNull RailNode node, @NotNull String name) throws RushHourException {
        return create(owner, node, name,
                Integer.parseInt(prop.get(GAME_DEF_GATE_NUM)),
                Integer.parseInt(prop.get(GAME_DEF_PLT_CAPACITY)));
    }

    public Station create(@NotNull Player owner, @NotNull RailNode node, @NotNull String name,
            @Min(1) int gatenum, @Min(1) int platformCapacity) throws RushHourException {
        if (!node.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (exists("Station.existsName", owner, "name", name)) {
            throw new RushHourException(errMsgBuilder.createStationNameDuplication(name));
        }

        Station s = new Station();
        s.setOwner(owner);
        s.setName(name);

        s.setTicketGate(createTicketGate(s, gatenum));
        s.setPlatform(createPlatform(s, node, platformCapacity));

        em.persist(s);
        sCon.addStation(s);

        return s;
    }

    public void editStationName(@NotNull Station station, @NotNull Player owner, @NotNull String name) throws RushHourException {
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        // 変更なしのときは何もしない
        if (station.getName().equals(name)) {
            return;
        }
        if (exists("Station.existsName", owner, "name", name)) {
            throw new RushHourException(errMsgBuilder.createStationNameDuplication(name));
        }
        station.setName(name);
    }

    public void editPlatformCapacity(@NotNull Station station, @NotNull Player owner, @Min(1) int capacity) throws RushHourException {
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        station.getPlatform().setCapacity(capacity);
    }

    public void editTicketGateNum(@NotNull Station station, @NotNull Player owner, @Min(1) int num) throws RushHourException {
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        station.getTicketGate().setGateNum(num);
    }
    
    public List<Station> findIn(double centerX, double centerY, double scale){
        return super.findIn(em.createNamedQuery("Station.findIn", Station.class), 
                centerX, centerY, scale);
    }

    public List<Platform> findPlatformAll() {
        return em.createNamedQuery("Platform.findAll", Platform.class).getResultList();
    }

    public List<TicketGate> findTicketGate() {
        return em.createNamedQuery("TicketGate.findAll", TicketGate.class).getResultList();
    }

    protected Platform createPlatform(Station s, RailNode node, int capacity) {
        Platform p = new Platform();
        p.setStation(s);
        p.setCapacity(capacity);
        p.setRailNode(node);

        return p;
    }

    protected TicketGate createTicketGate(Station s, int num) {
        TicketGate gate = new TicketGate();
        gate.setStation(s);
        gate.setGateNum(num);

        return gate;
    }
}

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
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.entity.Company;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
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

    public Station create(Player owner, RailNode node, String name) throws RushHourException {
        return create(owner, node, name,
                Integer.parseInt(prop.get(GAME_DEF_GATE_NUM)),
                Integer.parseInt(prop.get(GAME_DEF_PLT_CAPACITY)));
    }

    public Station create(Player owner, RailNode node, String name,
            int gatenum, int platformCapacity) throws RushHourException {
        if (owner == null) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_OWNER));
        }
        if (node == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (name == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (!node.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
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

    public void editStationName(Station station, Player owner, String name) throws RushHourException {
        if (station == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (owner == null) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_OWNER));
        }
        if (name == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        station.setName(name);
    }

    public void editPlatformCapacity(Station station, Player owner, int capacity) throws RushHourException {
        if (station == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (owner == null) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_OWNER));
        }
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        station.getPlatform().setCapacity(capacity);
    }

    public void editTicketGateNum(Station station, Player owner, int num) throws RushHourException {
        if (station == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        if (owner == null) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_OWNER));
        }
        if (!station.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        station.getTicketGate().setGateNum(num);
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
        p.setRailPoint(node);

        return p;
    }

    protected TicketGate createTicketGate(Station s, int num) {
        TicketGate gate = new TicketGate();
        gate.setStation(s);
        gate.setGateNum(num);

        return gate;
    }
}

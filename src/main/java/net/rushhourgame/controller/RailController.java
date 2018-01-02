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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class RailController extends PointEntityController {

    private static final long serialVersionUID = 1L;

    @Inject
    PlayerController pCon;

    public RailNode create(@NotNull Player owner, @NotNull Pointable p) throws RushHourException {
        if (exists("RailNode.exists", owner, p)) {
            throw new RushHourException(errMsgBuilder.createRailNodeDuplication(p));
        }
        RailNode n = new RailNode();
        n.setOwner(owner);
        n.setX(p.getX());
        n.setY(p.getY());
        em.persist(n);
        return n;
    }

    public RailNode extend(@NotNull Player owner, @NotNull RailNode from, @NotNull Pointable p) throws RushHourException {
        if (!from.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (exists("RailNode.exists", owner, p)) {
            throw new RushHourException(errMsgBuilder.createRailNodeDuplication(p));
        }
        RailNode to = new RailNode();
        to.setOwner(owner);
        to.setX(p.getX());
        to.setY(p.getY());
        em.persist(to);

        createEdge(owner, from, to);

        return to;
    }

    public void connect(@NotNull Player owner, @NotNull RailNode from, @NotNull RailNode to) throws RushHourException {
        if (!from.isOwnedBy(owner) || !to.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (from.equals(to)) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        createEdge(owner, from, to);
    }

    public List<RailNode> findNodeIn(@NotNull Pointable center, double scale) {
        return findIn(em.createNamedQuery("RailNode.findIn", RailNode.class), center, scale);
    }

    public List<RailNode> findNodeIn(Player owner, @NotNull Pointable center, double scale) {
        return findIn(em.createNamedQuery("RailNode.findMyIn", RailNode.class), owner, center, scale);
    }

    public List<RailNode> findLonelyIn(Player owner, @NotNull Pointable center, double scale) {
        return findIn(em.createNamedQuery("RailNode.findMyLonelyIn", RailNode.class), owner, center, scale);
    }

    public List<RailEdge> findEdgeIn(@NotNull Pointable center, double scale) {
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);

        return em.createNamedQuery("RailEdge.findIn", RailEdge.class)
                .setParameter("x1", center.getX() - width / 2.0)
                .setParameter("x2", center.getX() + width / 2.0)
                .setParameter("y1", center.getY() - height / 2.0)
                .setParameter("y2", center.getY() + height / 2.0)
                .getResultList();
    }

    public List<RailEdge> findEdgeIn(Player owner, @NotNull Pointable center, double scale) {
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);

        return em.createNamedQuery("RailEdge.findMyIn", RailEdge.class)
                .setParameter("owner", owner)
                .setParameter("x1", center.getX() - width / 2.0)
                .setParameter("x2", center.getX() + width / 2.0)
                .setParameter("y1", center.getY() - height / 2.0)
                .setParameter("y2", center.getY() + height / 2.0)
                .getResultList();
    }

    public boolean hasRailNode(Player owner) {
        return exists("RailNode.has", owner);
    }

    protected void createEdge(Player owner, RailNode from, RailNode to) {
        RailEdge e1 = new RailEdge();
        e1.setOwner(owner);
        e1.setFrom(from);
        e1.setTo(to);
        em.persist(e1);

        RailEdge e2 = new RailEdge();
        e2.setOwner(owner);
        e2.setFrom(to);
        e2.setTo(from);
        em.persist(e2);
    }

    public boolean existsEdge(@NotNull RailNode from, @NotNull RailNode to) {
        return !em.createNamedQuery("RailEdge.find", RailEdge.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList().isEmpty();
    }

    public RailEdge findNearestEdge(Player p, @NotNull Pointable center, double scale) {
        List<RailEdge> list = findEdgeIn(p, center, scale);
        
        if (list.isEmpty()) {
            return null;
        }

        return list.stream().min((e1, e2)
                -> {
            if (e1.distTo(center) > e2.distTo(center)) {
                return 1;
            } 
            if (e1.distTo(center) < e2.distTo(center)) {
                return -1;
            } 
            return 0;
        }).get();
    }
}

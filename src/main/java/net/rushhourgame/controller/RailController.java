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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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
    
    @Inject
    protected StationController stCon;

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RailController.class.getName());

    public RailNode create(@NotNull Player owner, @NotNull Pointable p) throws RushHourException {
        if (exists("RailNode.exists", owner, p)) {
            throw new RushHourException(errMsgBuilder.createRailNodeDuplication(p));
        }
        RailNode n = new RailNode();
        n.setOwner(owner);
        n.setX(p.getX());
        n.setY(p.getY());
        em.persist(n);
        em.flush();
        LOG.log(Level.INFO, "{0}#create created {1}", new Object[] {RailController.class, n});
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

        em.flush();
        LOG.log(Level.INFO, "{0}#extend created {1}", new Object[] {RailController.class, to});
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

    public void remove(@NotNull Player owner, @NotNull List<RailEdge> edges) throws RushHourException {
        if (edges.size() != 2 || !edges.get(0).isReverse(edges.get(1))) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        RailNode n1 = edges.get(0).getFrom();
        RailNode n2 = edges.get(0).getTo();

        for (RailEdge e : edges) {
            if (!e.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }
            if (isInSteps(e)) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }
            em.remove(e);
            LOG.log(Level.INFO, "{0}#remove removed {1}", new Object[] {RailController.class, e});
        }

        em.flush();
        removeIfIsolatedRailNode(n1);
        removeIfIsolatedRailNode(n2);
    }

    public boolean canRemove(Player player, List<RailEdge> edges) throws RushHourException {
        if (edges.size() != 2 || !edges.get(0).isReverse(edges.get(1))) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        for (RailEdge e : edges) {
            if (!e.isOwnedBy(player)) {
                return false;
            }
            if (isInSteps(e)) {
                return false;
            }
        }
        return true;
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
        em.flush();
        LOG.log(Level.INFO, "{0}#createEdge created {1}", new Object[] {RailController.class, e1});

        RailEdge e2 = new RailEdge();
        e2.setOwner(owner);
        e2.setFrom(to);
        e2.setTo(from);
        em.persist(e2);
        em.flush();
        LOG.log(Level.INFO, "{0}#createEdge created {1}", new Object[] {RailController.class, e2});
    }

    public boolean existsEdge(@NotNull RailNode from, @NotNull RailNode to) {
        return findEdge(from, to) != null;
    }

    public RailEdge findEdge(@NotNull RailNode from, @NotNull RailNode to) {
        try {
            return em.createNamedQuery("RailEdge.find", RailEdge.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
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

    public RailEdge findReverseEdge(@NotNull RailEdge e) {
        return em.createNamedQuery("RailEdge.find", RailEdge.class)
                .setParameter("from", e.getTo())
                .setParameter("to", e.getFrom())
                .getSingleResult();
    }

    protected boolean isInSteps(RailEdge e) {
        em.refresh(e);
        return !e.getMovingSteps().isEmpty() || !e.getPassingSteps().isEmpty() || !e.getStoppingSteps().isEmpty();
    }

    public void removeIfIsolatedRailNode(RailNode node) {
        em.refresh(node);
        if (node.getInEdges().isEmpty() && node.getOutEdges().isEmpty() && stCon.findOn(node) == null) {
            em.remove(node);
            LOG.log(Level.INFO, "{0}#remove removed {1}", new Object[] {RailController.class, node});
        }
    }

    public List<RailEdge> findEdge(Player p, long id1, long id2) throws RushHourException {
        List<RailEdge> edges = new ArrayList<>();
        RailEdge e1 = em.find(RailEdge.class, id1);
        RailEdge e2 = em.find(RailEdge.class, id2);

        if (e1 == null || e2 == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        if (!e1.isOwnedBy(p) || !e2.isOwnedBy(p)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        if (!e1.isReverse(e2)) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        edges.add(e1);
        edges.add(e2);
        return edges;
    }
}

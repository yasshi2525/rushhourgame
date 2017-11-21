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
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class RailController extends PointEntityController{
    private static final long serialVersionUID = 1L;
    
    public RailNode create(@NotNull Player owner, double x, double y) throws RushHourException{
        if (exists("RailNode.exists", owner, x, y)) {
            throw new RushHourException(errMsgBuilder.createRailNodeDuplication(x, y));
        }
        RailNode n = new RailNode();
        n.setOwner(owner);
        n.setX(x);
        n.setY(y);
        em.persist(n);
        return n;
    }
    
    public RailNode extend(@NotNull Player owner, @NotNull RailNode from, double x, double y) throws RushHourException{
        if (!from.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (exists("RailNode.exists", owner, x, y)) {
            throw new RushHourException(errMsgBuilder.createRailNodeDuplication(x, y));
        }
        RailNode to = new RailNode();
        to.setOwner(owner);
        to.setX(x);
        to.setY(y);
        em.persist(to);
        
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
        
        return to;
    }
    
    
    public List<RailNode> findNodeIn(double centerX, double centerY, double scale){
        return findIn(em.createNamedQuery("RailNode.findIn", RailNode.class), centerX, centerY, scale);
    }
    
    public List<RailEdge> findEdgeIn(double centerX, double centerY, double scale){
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return em.createNamedQuery("RailEdge.findIn", RailEdge.class)
                .setParameter("x1", centerX - width / 2.0)
                .setParameter("x2", centerX + width / 2.0)
                .setParameter("y1", centerY - height / 2.0)
                .setParameter("y2", centerY + height / 2.0)
                .getResultList();
    }
}

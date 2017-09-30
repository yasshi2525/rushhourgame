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
import net.rushhourgame.ErrorMessage;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Rail;
import net.rushhourgame.entity.RailPoint;
import net.rushhourgame.entity.Owner;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class RailController extends PointEntityController{
    private static final long serialVersionUID = 1L;
    
    public RailPoint create(Owner owner, double x, double y) throws RushHourException{
        if(owner == null){
            throw new RushHourException(ErrorMessage.createNoPrivileged(GAME_NO_OWNER));
        }
        RailPoint n = new RailPoint();
        n.setX(x);
        n.setY(y);
        em.persist(n);
        return n;
    }
    
    public RailPoint extend(Owner owner, RailPoint from, double x, double y) throws RushHourException{
        if(owner == null){
            throw new RushHourException(ErrorMessage.createNoPrivileged(GAME_NO_OWNER));
        }
        if(!from.isOwnedBy(owner)){
            throw new RushHourException(ErrorMessage.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        RailPoint to = new RailPoint();
        to.setX(x);
        to.setY(y);
        em.persist(to);
        
        Rail e1 = new Rail();
        e1.setFrom(from);
        e1.setTo(to);
        em.persist(e1);
        
        Rail e2 = new Rail();
        e2.setFrom(to);
        e2.setTo(from);
        em.persist(e2);
        
        return to;
    }
    
    
    public List<RailPoint> findNodeIn(double centerX, double centerY, double scale){
        throw new UnsupportedOperationException();
        //return findIn("Node", centerX, centerY, scale);
    }
    
    public List<Rail> findEdgeIn(double centerX, double centerY, double scale){
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return em.createQuery("SELECT e FROM Edge e WHERE"
                        + "    (:x1 < e.fromNode.x AND e.fromNode.x < :x2 AND :y1 < e.fromNode.y AND e.fromNode.y < :y2)"
                        + " OR (:x1 < e.toNode.x   AND e.toNode.x   < :x2 AND :y1 < e.toNode.y   AND e.toNode.y   < :y2)", 
                Rail.class)
                .setParameter("x1", centerX - width / 2.0)
                .setParameter("x2", centerX + width / 2.0)
                .setParameter("y1", centerY - height / 2.0)
                .setParameter("y2", centerY + height / 2.0)
                .getResultList();
    }
}

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
import javax.persistence.TypedQuery;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.SimplePoint;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class PointEntityController extends AbstractController{
    private static final long serialVersionUID = 1L;
    
    protected boolean exists(String query, Pointable p) {
        return (em.createNamedQuery(query, Number.class)
                .setParameter("x", p.getX())
                .setParameter("y", p.getY())
                .getSingleResult()).longValue() == 1L;
    }
    
    protected boolean exists(String query, Player owner, Pointable p) {
        return (em.createNamedQuery(query, Number.class)
                .setParameter("owner", owner)
                .setParameter("x", p.getX())
                .setParameter("y", p.getY())
                .getSingleResult()).longValue() == 1L;
    }
    
    protected <T> List<T> findIn(TypedQuery<T> query, Pointable center, double scale){
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return query
                .setParameter("x1", center.getX() - width / 2.0)
                .setParameter("x2", center.getX() + width / 2.0)
                .setParameter("y1", center.getY() - height / 2.0)
                .setParameter("y2", center.getY() + height / 2.0)
                .getResultList();
    }
    
    protected <T> List<T> findIn(TypedQuery<T> query, Player owner, Pointable center, double scale){
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return query
                .setParameter("owner", owner)
                .setParameter("x1", center.getX() - width / 2.0)
                .setParameter("x2", center.getX() + width / 2.0)
                .setParameter("y1", center.getY() - height / 2.0)
                .setParameter("y2", center.getY() + height / 2.0)
                .getResultList();
    }
}

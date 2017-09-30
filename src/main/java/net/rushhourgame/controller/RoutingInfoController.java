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
import net.rushhourgame.entity.Link;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class RoutingInfoController extends AbstractController {
    private static final long serialVersionUID = 1L;
    
    public RoutingInfo create(Node src, Node dest) throws RushHourException {
        return create(src, null, dest);
    }
    
    public RoutingInfo create(Node src, Link next, Node dest) throws RushHourException {
        if(src == null || dest == null){
            throw new RushHourException(ErrorMessage.createDataInconsitency(null));
        }
        
        if(src.equals(dest)){
            throw new RushHourException(ErrorMessage.createDataInconsitency(null));
        }
        
        RoutingInfo inst = new RoutingInfo();
        
        inst.setStart(src);
        inst.setNext(next);
        inst.setGoal(dest);
        
        em.persist(inst);
        return inst;
    }
    
    public void insertIntoNetwork(Node inst) throws RushHourException {
        List<Node> network = em.createNamedQuery("Node.findAll", Node.class).getResultList();
        for(Node other : network){
            em.persist(create(inst, other));
            em.persist(create(other, inst));
        }
    }
    
    public List<RoutingInfo> findNetwork(Node goal) {
        return 
                em.createNamedQuery("RoutingInfo.findByGoal", RoutingInfo.class)
                .setParameter("goal", goal)
                .getResultList();
    }
}

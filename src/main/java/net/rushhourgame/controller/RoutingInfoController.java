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
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.entity.RelayPointForHuman;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class RoutingInfoController extends AbstractController {
    private static final long serialVersionUID = 1L;
    
    public RoutingInfo create(RelayPointForHuman src, RelayPointForHuman dest) throws RushHourException {
        return create(src, null, dest);
    }
    
    public RoutingInfo create(RelayPointForHuman src, StepForHuman next, RelayPointForHuman dest) throws RushHourException {
        if(src == null || dest == null){
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        
        if(src.equals(dest)){
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        
        RoutingInfo inst = new RoutingInfo();
        
        inst.setStart(src);
        inst.setNext(next);
        inst.setGoal(dest);
        
        em.persist(inst);
        return inst;
    }
    
    public void insertIntoNetwork(RelayPointForHuman inst) throws RushHourException {
        List<RelayPointForHuman> network = em.createNamedQuery("Node.findAll", RelayPointForHuman.class).getResultList();
        for(RelayPointForHuman other : network){
            em.persist(create(inst, other));
            em.persist(create(other, inst));
        }
    }
    
    public List<RoutingInfo> findNetwork(RelayPointForHuman goal) {
        return 
                em.createNamedQuery("RoutingInfo.findByGoal", RoutingInfo.class)
                .setParameter("goal", goal)
                .getResultList();
    }
}

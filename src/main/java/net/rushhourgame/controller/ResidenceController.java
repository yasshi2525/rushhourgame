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
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.exception.RushHourException;

/**
 * 
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class ResidenceController extends PointEntityController {
    private static final long serialVersionUID = 1L;
    
    @Inject
    protected StepForHumanController sCon;
    
    public Residence create(@NotNull Pointable p) throws RushHourException{
        return create(p, 
                Integer.parseInt(prop.get(GAME_DEF_RSD_CAPACITY)),
                Integer.parseInt(prop.get(GAME_DEF_RSD_INTERVAL)));
    }
    
    public Residence create(@NotNull Pointable p, @Min(1) int capacity, @Min(1) int interval) throws RushHourException{
        if (exists("Residence.exists", p)) {
            throw new RushHourException(errMsgBuilder.createResidenceDuplication(p));
        }
        Residence inst = new Residence();
        inst.setCapacity(capacity);
        inst.setInterval(interval);
        inst.setX(p.getX());
        inst.setY(p.getY());
        em.persist(inst);
        sCon.addResidence(inst);
        return inst;
    }
    
    public List<Residence> findIn(@NotNull Pointable center, double scale){
        return super.findIn(em.createNamedQuery("Residence.findIn", Residence.class), 
                center, scale);
    }
    
    public List<Residence> findAll() {
        return em.createNamedQuery("Residence.findAll", Residence.class).getResultList();
    }
}

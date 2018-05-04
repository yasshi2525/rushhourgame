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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.Company;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.exception.RushHourException;

/**
 * 
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class CompanyController extends PointEntityController {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CompanyController.class.getName());
    
    @Inject
    protected StepForHumanController sCon;
    
    public Company create(@NotNull Pointable p) throws RushHourException{
        return create(p, Double.parseDouble(prop.get(GAME_DEF_CMP_SCALE)));
    }

    public Company create(@NotNull Pointable p, @DecimalMin(value = "0.0", inclusive = false) double scale) throws RushHourException{
        if (exists("Company.exists", p)) {
            throw new RushHourException(errMsgBuilder.createCompanyDuplication(p));
        }
        Company inst = new Company();
        inst.setScale(scale);
        inst.setX(p.getX());
        inst.setY(p.getY());
        em.persist(inst);
        LOG.log(Level.INFO, "{0}#create created {1}", new Object[] {CompanyController.class, inst});
        sCon.addCompany(inst);
        return inst;
    }
    
    public List<Company> findIn(@NotNull Pointable center, double scale){
        return super.findIn(em.createNamedQuery("Company.findIn", Company.class), 
                center, scale);
    }
    
    public List<Company> findAll() {
        return em.createNamedQuery("Company.findAll", Company.class).getResultList();
    }
}

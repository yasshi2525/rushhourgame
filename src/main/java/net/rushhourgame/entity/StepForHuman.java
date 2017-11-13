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
package net.rushhourgame.entity;

import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanMethod;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * 人用移動ステップ
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class StepForHuman extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanResidenceToStation fromResidence;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanIntoStation intoStation;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanThroughTrain throughTrain;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanOutOfStation outOfStation;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanStationToCompany toCompany;
    
    @OneToOne(mappedBy = "parent")
    protected StepForHumanDirectly directly;

    public double getCost() {
        return seekMethodInstance().getCost();
    }

    @NotNull
    public RelayPointForHuman getFrom() {
        return seekMethodInstance().getFrom();
    }

    @NotNull
    public RelayPointForHuman getTo() {
        return seekMethodInstance().getTo();
    }
    
    @NotNull
    protected StepForHumanMethod seekMethodInstance() {
        if (fromResidence != null) {
            return fromResidence;
        } else if (intoStation != null) {
            return intoStation;
        } else if (throughTrain != null) {
            return throughTrain;
        } else if (outOfStation != null) {
            return outOfStation;
        } else if (toCompany != null) {
            return toCompany;
        } else {
            return directly;
        }
    }
}

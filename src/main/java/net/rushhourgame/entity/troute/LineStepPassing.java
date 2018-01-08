/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.entity.troute;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.AbstractEntity;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RailEdge;

/**
 * 路線ステップ通過
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "LineStep.findByPassingGoalRailNode",
            query = "SELECT obj.parent FROM LineStepPassing obj WHERE obj.running._to = :node"
    )
})
public class LineStepPassing extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    @OneToOne
    protected LineStep parent;
    
    @NotNull
    @ManyToOne
    protected RailEdge running;
    
    @NotNull
    @ManyToOne
    protected Platform goal;

    public LineStep getParent() {
        return parent;
    }

    public void setParent(LineStep parent) {
        this.parent = parent;
    }

    public RailEdge getRunning() {
        return running;
    }

    public void setRunning(RailEdge running) {
        this.running = running;
    }

    public Platform getGoal() {
        return goal;
    }

    public void setGoal(Platform goal) {
        this.goal = goal;
    }
}

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

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepMoving;
import net.rushhourgame.entity.troute.LineStepStopping;

/**
 * 路線ステップ
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class LineStep extends AbstractEntity implements Ownable {
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @ManyToOne
    protected Line parent;
    
    @NotNull
    @OneToOne
    protected LineStep next;
    
    protected TargetType target;
    
    @ManyToOne
    protected RailEdge onRail;
    
    @ManyToOne
    protected Station onStation;
    
    @OneToMany
    protected List<Train> trains;
    
    @OneToOne(mappedBy = "parent")
    protected LineStepMoving moving;
    
    @OneToOne(mappedBy = "parent")
    protected LineStepStopping stopping;
    
    @OneToOne(mappedBy = "parent")
    protected LineStepDeparture departure;

    public Line getParent() {
        return parent;
    }

    public void setParent(Line parent) {
        this.parent = parent;
    }

    public LineStep getNext() {
        return next;
    }

    public void setNext(LineStep next) {
        this.next = next;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget(TargetType target) {
        this.target = target;
    }

    public RailEdge getOnRail() {
        return onRail;
    }

    public void setOnRail(RailEdge onRail) {
        this.onRail = onRail;
    }

    public Station getOnStation() {
        return onStation;
    }

    public void setOnStation(Station onStation) {
        this.onStation = onStation;
    }

    public List<Train> getTrains() {
        return trains;
    }

    @Override
    public void setOwner(Player owner) {
        parent.setOwner(owner);
    }

    @Override
    public Player getOwner() {
        return parent.getOwner();
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return parent.isPrivilegedBy(owner);
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return parent.isOwnedBy(owner);
    }
    
    public enum TargetType{
        RAIL_LINE, STATION
    }
    
    public enum ActionType{
        STOP, PASS
    }
    
}

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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepMoving;
import net.rushhourgame.entity.troute.LineStepPassing;
import net.rushhourgame.entity.troute.LineStepStopping;

/**
 * 路線ステップ
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "LineStep.findTop",
            query = "SELECT obj FROM LineStep obj WHERE"
            + " obj.parent = :line"
            + " AND NOT EXISTS (SELECT x FROM LineStep x WHERE obj = x.next)"
    )})
public class LineStep extends AbstractEntity implements Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Line parent;

    @OneToOne
    protected LineStep next;

    protected TargetType target;

    @ManyToOne
    protected RailEdge onRail;

    @ManyToOne
    protected Station onStation;

    @OneToMany
    protected List<Train> trains;

    @OneToOne(mappedBy = "parent", cascade = CascadeType.PERSIST)
    protected LineStepMoving moving;

    @OneToOne(mappedBy = "parent", cascade = CascadeType.PERSIST)
    protected LineStepStopping stopping;

    @OneToOne(mappedBy = "parent", cascade = CascadeType.PERSIST)
    protected LineStepDeparture departure;

    @OneToOne(mappedBy = "parent", cascade = CascadeType.PERSIST)
    protected LineStepPassing passing;

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

    public LineStepMoving getMoving() {
        return moving;
    }

    public LineStepStopping getStopping() {
        return stopping;
    }

    public LineStepDeparture getDeparture() {
        return departure;
    }

    public LineStepPassing getPassing() {
        return passing;
    }

    public enum TargetType {
        RAIL_LINE, STATION
    }

    public enum ActionType {
        STOP, PASS
    }
    
    public RailNode getStartRailNode() {
        if (departure != null) {
            return departure.getStaying().getRailNode();
        } else if (moving != null) {
            return moving.getRunning().getFrom();
        } else if (passing != null) {
            return passing.getRunning().getFrom();
        } else if (stopping != null) {
            return stopping.getRunning().getFrom();
        } else {
            throw new IllegalStateException("line step doesn't have any children.");
        }
    }

    public RailNode getGoalRailNode() {
        if (departure != null) {
            return departure.getStaying().getRailNode();
        } else if (moving != null) {
            return moving.getRunning().getTo();
        } else if (passing != null) {
            return passing.getGoal().getRailNode();
        } else if (stopping != null) {
            return stopping.getGoal().getRailNode();
        } else {
            throw new IllegalStateException("line step doesn't have any children.");
        }
    }

    public RailEdge getOnRailEdge() {
        if (departure != null) {
            return null;
        } else if (moving != null) {
            return moving.getRunning();
        } else if (passing != null) {
            return passing.getRunning();
        } else if (stopping != null) {
            return stopping.getRunning();
        } else {
            throw new IllegalStateException("line step doesn't have any children.");
        }
    }

    public void registerDeparture(@NotNull Platform platform) {
        verifyUnregistered();

        LineStepDeparture child = new LineStepDeparture();
        child.setParent(this);
        child.setStaying(platform);

        departure = child;
    }

    public void registerMoving(@NotNull RailEdge e) {
        verifyUnregistered();

        LineStepMoving child = new LineStepMoving();
        child.setParent(this);
        child.setRunning(e);

        moving = child;
    }

    public void registerStopping(@NotNull RailEdge e, @NotNull Platform platform) {
        verifyUnregistered();

        LineStepStopping child = new LineStepStopping();
        child.setParent(this);
        child.setRunning(e);
        child.setGoal(platform);

        stopping = child;
    }

    public void registerPassing(@NotNull RailEdge e, @NotNull Platform platform) {
        verifyUnregistered();

        LineStepPassing child = new LineStepPassing();
        child.setParent(this);
        child.setRunning(e);
        child.setGoal(platform);

        passing = child;
    }

    protected void verifyUnregistered() {
        if (departure != null) {
            throw new IllegalStateException("departure was already registered.");
        } else if (moving != null) {
            throw new IllegalStateException("moving was already registered.");
        } else if (passing != null) {
            throw new IllegalStateException("passing was already registered.");
        } else if (stopping != null) {
            throw new IllegalStateException("stopping was already registered.");
        }
    }

    public boolean canConnect(@NotNull LineStep target) {
        if (next != null) {
            throw new IllegalStateException("line step was already connected.");
        }

        if (stopping != null) {
            // 停車の次は発車でなければならない
            return target.departure != null
                    && stopping.getGoal().equals(target.departure.getStaying());

        } else if (departure != null || moving != null || passing != null) {
            // 移動の次は発車以外でなければならない
            return (target.moving != null
                    || target.stopping != null
                    || target.passing != null)
                    && getGoalRailNode().equals(target.getStartRailNode());
        } else {
            throw new IllegalStateException("line step doesn't have any children.");
        }
    }
    
    public double getDist() {
        if (departure != null) {
            return 0.0;
        } else if (moving != null) {
            return moving.getRunning().getDist();
        } else if (passing != null) {
            return passing.getRunning().getDist();
        } else if (stopping != null) {
            return stopping.getRunning().getDist();
        } else {
            throw new IllegalStateException("line step doesn't have any children.");
        }
    }
}

/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to pass, copy, modify, merge, publish, distribute, sublicense, and/or sell
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;
import net.rushhourgame.controller.route.TemporaryHumanRouteEdge;
import net.rushhourgame.controller.route.TemporaryHumanStep;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;

/**
 * 人
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQuery(
        name = "Human.findAll",
        query = "SELECT obj FROM Human obj"
)
@NamedQuery(
        name = "Human.deleteBy",
        query = "DELETE FROM Human obj WHERE obj = :obj"
)
public class Human extends GeoEntity implements Pointable {

    private static final Logger LOG = Logger.getLogger(Human.class.getName());

    private static final long serialVersionUID = 1L;

    protected long lifespan;

    @NotNull
    @ManyToOne
    protected Residence src;

    @NotNull
    @ManyToOne
    protected Company dest;

    @NotNull
    protected StandingOn stand;

    @ManyToOne
    protected Platform onPlatform;

    @ManyToOne
    protected TrainDeployed onTrain;

    protected transient RouteEdge current;

    protected boolean isFinished;

    public void step(long interval, double speed) {
        if (isFinished || current == null) {
            return;
        }

        while (interval > 0) {
            StepForHuman currentStep = current.getOriginal();
            interval -= currentStep.step(this, interval, speed);
            if (currentStep.isFinished(this)) {
                // 目的地に到着した
                if (current.getTo().isEnd()) {
                    isFinished = true;
                    return;
                }
                shiftEdge();
            }
        }
    }

    public long walkTo(long interval, double speed, Pointable goal) {
        return walkTo(this, interval, speed, goal);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void getInTrain(TrainDeployed t) {
        onPlatform.exit();
        LOG.log(Level.FINER, "{0}#getInTraiin {1} exitted from {2} ({3})", 
                new Object[]{Human.class, this, onPlatform, onPlatform.occupied});
        onPlatform = null;
        onTrain = t;
        stand = StandingOn.TRAIN;
    }
    
    /**
     * 電車自体が撤去されるため、強制的に退去
     */
    public void getOffTrainForce() {
        setXY(makeNearPoint(onTrain.getTrain().getProdist()));
        onTrain = null;
        stand = StandingOn.GROUND;
        current = null;
    }

    public void getOffTrain(Platform platform) {
        onTrain = null;
        onPlatform = platform;
        onPlatform.enter(true);
        LOG.log(Level.FINER, "{0}#getOffTrain {1} entered to {2} ({3})", 
                new Object[]{Human.class, this, onPlatform, onPlatform.occupied});
        shiftEdge(); // 乗車タスクの完了
        stand = StandingOn.PLATFORM;
    }

    public void setStandingOn(StandingOn stand) {
        this.stand = stand;
    }

    public StandingOn getStandingOn() {
        return stand;
    }

    public Platform getOnPlatform() {
        return onPlatform;
    }

    public void setOnPlatform(Platform onPlatform) {
        this.onPlatform = onPlatform;
    }

    public TrainDeployed getOnTrain() {
        return onTrain;
    }

    public void enterIntoPlatform(TicketGate from, Platform to) {
        from.pass();
        to.enter();
        this.onPlatform = to;
        LOG.log(Level.FINER, "{0}#enterIntoPlatform {1} entered to {2} ({3})", 
                new Object[]{Human.class, this, onPlatform, onPlatform.occupied});
        stand = StandingOn.PLATFORM;
    }

    public void exitFromPlatform(Platform from, TicketGate to) {
        from.exit();
        to.pass();
        LOG.log(Level.FINER, "{0}#exitFromPlatform {1} exitted from {2} ({3})", 
                new Object[]{Human.class, this, from, from.occupied});
        this.onPlatform = null;

        setXY(makeNearPoint(to.getProdist()));
        stand = StandingOn.GROUND;
    }
    
    /**
     * 駅自体が撤去されるため、強制的に退去
     */
    public void exitFromPlatformForce() {
        onPlatform.exit();
        
        setXY(makeNearPoint(onPlatform.getStation().getTicketGate().getProdist()));
        stand = StandingOn.GROUND;
        
        onPlatform = null;
        current = null;
    }

    public void setOnTrain(TrainDeployed onTrain) {
        this.onTrain = onTrain;
        stand = StandingOn.TRAIN;
    }

    protected void shiftEdge() {
        current = current.getTo().getViaEdge();
    }

    public void flushCurrent() {
        current = null;
    }

    public long getLifespan() {
        return lifespan;
    }

    public void consumeLifespan(long interval) {
        lifespan -= interval;
    }

    public void setLifespan(long lifespan) {
        this.lifespan = lifespan;
    }

    public boolean shouldDie() {
        return lifespan <= 0 || isFinished();
    }

    public Residence getSrc() {
        return src;
    }

    public void setSrc(Residence src) {
        this.src = src;
    }

    public Company getDest() {
        return dest;
    }

    public void setDest(Company dest) {
        this.dest = dest;
    }

    public RouteEdge getCurrent() {
        return current;
    }

    public void setCurrent(RouteNode current) {
        this.current = current.getViaEdge();
    }

    public boolean shouldRide(Platform platform, TrainDeployed train) {
        if (platform.equalsId(onPlatform) && current != null
                && current.getOriginal() instanceof StepForHumanThroughTrain) {
            // TODO : 反対方面の電車に乗ってしまう
            return train.getCurrent().getParent().equalsId(
                    ((StepForHumanThroughTrain) current.getOriginal()).getLine());
        }
        return false;
    }

    public boolean shouldGetOff(TrainDeployed train, Platform platform) {
        if (this.onTrain == null || !train.equalsId(onTrain)) {
            return false;
        }
        // 目的なし乗車はおりる
        return current == null || platform.equalsId(current.getOriginal().getTo());
    }

    public void moveTo(Pointable to, double dist) {
        double theta = Math.atan2(to.getY() - y, to.getX() - x);

        x += dist * Math.cos(theta);
        y += dist * Math.sin(theta);
    }

    public void merge(Company cmp) {
        if (dest.equalsId(cmp) && !dest.equals(cmp)) {
            dest = cmp;
            LOG.log(Level.FINE, "{0}#merge merged reference {1} of {2}", new Object[]{Human.class, cmp, this});
        }
    }

    /**
     * HumanControllerから RouteSearcherを呼ぶと循環してしまう
     *
     * @param searcher RouteSearcher
     */
    public void searchCurrent(RouteSearcher searcher) {
        // 乗車中に経路再計算による目的なし乗車のため降ろされた
        if (onPlatform != null && searcher.isReachable(onPlatform, dest)) {
            setCurrent(searcher.getStart(onPlatform, dest));
            return;
        }

        if (searcher.isReachable(src, dest)) {
            setCurrent(searcher.getStart(src, dest));
        }
    }

    @Override
    public String toString() {
        return "h(" + id + ")";
    }

    public enum StandingOn {
        GROUND,
        PLATFORM,
        TRAIN
    }
}

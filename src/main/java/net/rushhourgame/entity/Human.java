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
@NamedQueries({
    @NamedQuery(
            name = "Human.findAll",
            query = "SELECT obj FROM Human obj"
    )
    ,
    @NamedQuery(
            name = "Human.findIn",
            query = "SELECT obj FROM Human obj WHERE obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2"
    )
})
public class Human extends AbstractEntity implements Pointable {

    private static final long serialVersionUID = 1L;

    protected double x;
    protected double y;
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

    public void step(EntityManager em, long interval, double speed) {
        if (isFinished || current == null) {
            return;
        }

        while (interval > 0) {
            StepForHuman currentStep = getMergedCurrent(em);
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

    protected StepForHuman getMergedCurrent(EntityManager em) {
        if (current instanceof TemporaryHumanRouteEdge) {
            return current.getOriginal();
        }

        // currentはメモリ上にキャッシュされるため、Entityの情報が古い
        return em.merge(current.getOriginal());
    }

    public long walkTo(long interval, double speed, Pointable goal) {
        return walkTo(this, interval, speed, goal);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void getInTrain(TrainDeployed t) {
        onPlatform.exit();
        onPlatform = null;
        onTrain = t;
        stand = StandingOn.TRAIN;
    }

    public void getOffTrain(Platform platform) {
        onTrain = null;
        onPlatform = platform;
        onPlatform.enter(true);
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

    public TrainDeployed getOnTrain() {
        return onTrain;
    }

    public void enterIntoPlatform(TicketGate from, Platform to) {
        from.pass();
        to.enter();
        this.onPlatform = to;
        stand = StandingOn.PLATFORM;
    }

    public void exitFromPlatform(Platform from, TicketGate to) {
        from.exit();
        to.pass();
        this.onPlatform = null;

        Pointable newPoint = new SimplePoint(x, y).makeNearPoint(to.getProdist());
        x = newPoint.getX();
        y = newPoint.getY();
        stand = StandingOn.GROUND;
    }

    public void setOnTrain(TrainDeployed onTrain) {
        this.onTrain = onTrain;
        stand = StandingOn.TRAIN;
    }

    protected void shiftEdge() {
        current.unreffer(this);
        current = current.getTo().getViaEdge();
        current.reffer(this);
    }

    public void flushCurrent() {
        current = null;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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
        return lifespan < 0 || isFinished();
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
        if (this.current != null) {
            this.current.unreffer(this);
        }
        this.current = current.getViaEdge();
        this.current.reffer(this);
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

    @Override
    public double distTo(Pointable p) {
        return calcDist(x, y, p);
    }

    public void moveTo(Pointable to, double dist) {
        double theta = Math.atan2(to.getY() - y, to.getX() - x);

        x += dist * Math.cos(theta);
        y += dist * Math.sin(theta);
    }

    /**
     * EntityManager#merge()はtransient属性をコピーしない
     *
     * @param em EntityManager
     * @return attachしたオブジェクト
     */
    public Human merge(EntityManager em) {
        Human newHuman = em.merge(this);

        if (current != null) {
            current.unreffer(this);
            newHuman.current = current;
            current.reffer(newHuman);
        }

        return newHuman;
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

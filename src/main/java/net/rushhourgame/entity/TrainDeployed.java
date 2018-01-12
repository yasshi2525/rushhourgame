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
package net.rushhourgame.entity;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 電車配置
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class TrainDeployed extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToOne
    protected Train train;

    @NotNull
    @ManyToOne
    protected LineStep current;

    @Min(0)
    @Max(1)
    protected double progress;

    protected double x;
    protected double y;

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
    
    public LineStep getCurrent() {
        return current;
    }

    public void setCurrent(LineStep current) {
        this.current = current;
        progress = 0.0;
        registerPoint();
    }

    public void consumeTime(@Min(0) long remainTime) {
        while (remainTime > 0) {
            if (shouldRun()) {
                remainTime -= consumeTimeByRunning(remainTime);
            }
            if (shouldShiftStep()) {
                shiftStep();
            }
            if (shouldStay()) {
                remainTime -= consumeTimeByStaying(remainTime);
            }
            if (shouldShiftStep()) {
                shiftStep();
            }
        }
    }

    protected boolean shouldRun() {
        return current.getMoving() != null || current.getStopping() != null || current.getPassing() != null;
    }

    protected boolean shouldStay() {
        return current.getDeparture() != null;
    }

    protected long consumeTimeByRunning(@Min(0) long time) {
        return run(Math.min(calcMovableDist(time), calcRemainDist()));
    }

    protected double calcRemainDist() {
        return current.getDist() * (1.0 - progress);
    }

    protected double calcMovableDist(@Min(0) long time) {
        return train.getSpeed() * time;
    }

    /**
     * 指定された距離走行する
     *
     * @param dist 走行する距離
     * @return 消費した時間
     */
    protected long run(@Min(0) double dist) {
        progress += dist / current.getDist();
        registerPoint();
        return (long) Math.ceil(dist / train.getSpeed());
    }

    protected long consumeTimeByStaying(@Min(0) long time) {
        return stay(Math.min(time, calcStayableTime()));
    }

    protected long calcStayableTime() {
        return (long) Math.ceil(train.getMobility() * (1.0 - progress));
    }

    /**
     * 指定された時間停車する。
     *
     * @param time 停車する時間
     * @return 消費した時間
     */
    protected long stay(@Min(0) long time) {
        progress += time / (double) train.getMobility();
        return time;
    }

    protected void registerPoint() {
        x = current.getStartRailNode().getX() * (1.0 - progress)
                + current.getGoalRailNode().getX() * progress;
        y = current.getStartRailNode().getY() * (1.0 - progress)
                + current.getGoalRailNode().getY() * progress;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void registerPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected boolean shouldShiftStep() {
        return progress >= 1.0;
    }
    
    protected void shiftStep() {
        current = current.getNext();
        progress = 0.0;
    }

    public void freeHuman(List<Human> hs) {
        for (Human h : hs) {
            h.getOffTrain(current.getOnStation());
        }
    }

    public void collectHuman(List<Human> hs) {
        for (Human h : hs) {
            h.getInTrain(train);
        }
    }
}

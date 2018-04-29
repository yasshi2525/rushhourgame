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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import net.rushhourgame.controller.route.RouteEdge;
import net.rushhourgame.controller.route.RouteNode;

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

    protected transient RouteEdge current;
    
    protected boolean isFinished;

    public void step(long interval, double speed) {
        if (isFinished || current == null) {
            return;
        }
        
        while (interval > 0) {
            interval -= current.getOriginal().step(this, interval, speed);
            if (current.getOriginal().isFinished(this)) {
                // 目的地に到着した
                if (current.getTo().isEnd()) {
                    isFinished = true;
                    return;
                }
                current = current.getTo().getViaEdge();
            }
        }
    }

    public void idle() {

    }

    public void walk() {
        throw new UnsupportedOperationException();
    }

    public boolean finishes() {
        return isFinished;
    }

    public void enterStation() {
        throw new UnsupportedOperationException();
    }

    public void exitStation() {
        throw new UnsupportedOperationException();
    }

    public void getInTrain(Train t) {
        /*if (true) {
            x = t.getX();
            y = t.getY();
            shiftTask();
        }*/
    }

    public void getOffTrain(Station st) {
        /*if (true) {
            x = st.getX();
            y = st.getY();
            shiftTask();
        }*/
    }

    protected void shiftTask() {
        throw new UnsupportedOperationException();
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
        return lifespan < 0 || finishes();
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

    @Override
    public double distTo(Pointable p) {
        return calcDist(x, y, p);
    }
    
    public void moveTo(Pointable to, double dist) {
        double theta = Math.atan2(to.getY() - y, to.getX() - x);
        
        x += dist * Math.cos(theta);
        y += dist * Math.sin(theta);
    }
}

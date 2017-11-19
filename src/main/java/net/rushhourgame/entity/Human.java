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
import javax.validation.constraints.NotNull;

/**
 * 人
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class Human extends AbstractEntity implements Pointable {

    private static final long serialVersionUID = 1L;

    protected double x;
    protected double y;
    protected int liveCount;

    @NotNull
    @ManyToOne
    protected Residence src;

    @NotNull
    @ManyToOne
    protected Company dest;

    public void idle() {

    }

    public void walk() {
        throw new UnsupportedOperationException();
    }

    public boolean finishes() {
        throw new UnsupportedOperationException();
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

    public int getLiveCount() {
        return liveCount;
    }

    public void incrementLiveCount() {
        liveCount++;
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

    @Override
    public double distTo(Pointable p) {
        return calcDist(x, y, p);
    }
}

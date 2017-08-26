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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Entity
public class Human extends AbstractEntity implements Pointable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    protected double x;
    protected double y;

    @ManyToOne
    protected Link current;

    protected int live;

    @ManyToOne
    Distributer src;

    @ManyToOne
    Absorber dest;

    public void idle() {

    }

    public void walk() {
        throw new UnsupportedOperationException();
    }

    public boolean finishes() {
        throw new UnsupportedOperationException();
    }

    public void enterStation() {
        if (current.getType() == Link.Type.ENTER_STATION) {
            shiftTask();
        }
    }

    public void exitStation() {
        if (current.getType() == Link.Type.EXIT_STATION) {
            shiftTask();
        }
    }

    public void getInTrain(Train t) {
        if (current.getWay().getId() == t.getCurrent().getId()) {
            x = t.getX();
            y = t.getY();
            shiftTask();
        }
    }

    public void getOffTrain(Station st) {
        if (current.getTo().getId() == st.getPlatform().getId()) {
            x = st.getX();
            y = st.getY();
            shiftTask();
        }
    }

    protected void shiftTask() {
        throw new UnsupportedOperationException();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public Distributer getSrc() {
        return src;
    }

    public void setSrc(Distributer src) {
        this.src = src;
    }

    public Absorber getDest() {
        return dest;
    }

    public void setDest(Absorber dest) {
        this.dest = dest;
    }

    public Link getCurrent() {
        return current;
    }

    public void setCurrent(Link current) {
        this.current = current;
    }

    @Override
    public double distTo(Pointable other) {
        return Math.sqrt((other.getX() - x) * (other.getX() - x)
                + (other.getY() - y) * (other.getY() - y));
    }
}

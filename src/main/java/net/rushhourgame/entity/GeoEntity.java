/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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

import javax.persistence.MappedSuperclass;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@MappedSuperclass
public class GeoEntity extends AbstractEntity implements Pointable {

    private static final long serialVersionUID = 1L;

    protected double x;
    protected double y;

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

    @Override
    public double distTo(Pointable p) {
        return calcDist(getX(), getY(), p);
    }

    protected double calcDist(double x, double y, Pointable other) {
        return Math.sqrt((other.getX() - x) * (other.getX() - x)
                + (other.getY() - y) * (other.getY() - y));
    }

    @Override
    public boolean isAreaIn(Pointable center, double scale) {
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);

        return getX() > center.getX() - width / 2.0
                && getX() < center.getX() + width / 2.0
                && getY() > center.getY() - height / 2.0
                && getY() < center.getY() + height / 2.0;
    }

    protected long walkTo(Human h, long interval, double speed, Pointable dst) {
        if (interval * speed < h.distTo(dst)) {
            h.moveTo(dst, interval * speed);
            return interval;
        } else {
            long consumed = (long) (h.distTo(dst) / speed);
            h.setX(dst.getX());
            h.setY(dst.getY());
            return consumed;
        }
    }
}

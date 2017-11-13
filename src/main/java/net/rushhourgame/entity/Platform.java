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

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * プラットフォーム
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class Platform extends AbstractEntity implements Pointable, RelayPointForHuman, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToOne
    protected Station station;

    @NotNull
    @ManyToOne
    protected RailNode railPoint;

    protected int capacity;

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public RailNode getRailPoint() {
        return railPoint;
    }

    public void setRailPoint(RailNode railPoint) {
        this.railPoint = railPoint;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void setX(double x) {
        station.setX(x);
    }

    @Override
    public void setY(double y) {
        station.setY(y);
    }

    @Override
    public double getX() {
        return station.getX();
    }

    @Override
    public double getY() {
        return station.getY();
    }

    @Override
    public double distTo(Pointable p) {
        return station.distTo(p);
    }

    @Override
    public void setOwner(Player owner) {
        station.setOwner(owner);
    }

    @Override
    public Player getOwner() {
        return station.getOwner();
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return station.isPrivilegedBy(owner);
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return station.isOwnedBy(owner);
    }

    @Override
    public List<StepForHuman> getOutEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<StepForHuman> getInEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCost(double cost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RelayPointForHuman getVia() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVia(RelayPointForHuman via) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(RelayPointForHuman o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

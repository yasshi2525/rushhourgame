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
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * 電車
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQuery(
        name = "Train.findAll",
        query = "SELECT obj FROM Train obj JOIN FETCH obj.deployed"
)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
public class Train extends GeoEntity implements Pointable, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Player owner;

    protected String name;
    protected int capacity;
    protected double speed;
    protected long mobility;
    protected double prodist;

    @OneToOne(mappedBy = "train")
    protected TrainDeployed deployed;

    public boolean isDeployed() {
        return deployed != null;
    }

    public TrainDeployed getDeployed() {
        return deployed;
    }

    public void setDeployed(TrainDeployed deployed) {
        this.deployed = deployed;
    }

    @Override
    public double getX() {
        if (deployed == null) {
            throw new IllegalStateException("Not deployed.");
        }
        return deployed.getX();
    }

    @Override
    public double getY() {
        if (deployed == null) {
            throw new IllegalStateException("Not deployed.");
        }
        return deployed.getY();
    }

    @Override
    public double distTo(Pointable p) {
        if (deployed == null) {
            throw new IllegalStateException("Not deployed.");
        }
        return deployed.distTo(p);
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return hasPrivilege(this.owner, owner);
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return isOwn(this.owner, owner);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getMobility() {
        return mobility;
    }

    public void setMobility(long mobility) {
        this.mobility = mobility;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getProdist() {
        return prodist;
    }

    public void setProdist(double prodist) {
        this.prodist = prodist;
    }

    @Override
    public String toString() {
        return "t(" + id + "){" + deployed + "}";
    }

    @Override
    public boolean isAreaIn(Pointable center, double scale) {
        if (deployed == null) {
            return false;
        }
        return deployed.isAreaIn(center, scale);
    }
}

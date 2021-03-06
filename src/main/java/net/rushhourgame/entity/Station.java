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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * 駅
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQuery(
        name = "Station.findAll",
        query = "SELECT obj FROM Station obj"
)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
public class Station extends GeoEntity implements Pointable, Ownable, Nameable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Player owner;

    @NotNull
    protected String name;

    @OneToOne(mappedBy = "station", cascade = CascadeType.PERSIST, orphanRemoval = true)
    protected Platform platform;

    @OneToOne(mappedBy = "station", cascade = CascadeType.PERSIST, orphanRemoval = true)
    protected TicketGate ticketGate;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public TicketGate getTicketGate() {
        return ticketGate;
    }

    public void setTicketGate(TicketGate ticketGate) {
        this.ticketGate = ticketGate;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getX() {
        return platform.getX();
    }

    @Override
    public double getY() {
        return platform.getY();
    }

    @Override
    public double distTo(Pointable p) {
        return platform.distTo(p);
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

    @Override
    public String toString() {
        return "s(" + id + "){" + ticketGate + ",_" + platform + "}";
    }
}

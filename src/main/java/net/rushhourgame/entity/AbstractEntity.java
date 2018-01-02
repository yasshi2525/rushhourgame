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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created == null ? null : new Date(created.getTime());
    }

    public Date getUpdated() {
        return updated == null ? null : new Date(updated.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Date(created.getTime());
    }

    public void setUpdated(Date updated) {
        this.updated = new Date(updated.getTime());
    }

    protected double calcDist(double x, double y, Pointable other) {
        return Math.sqrt((other.getX() - x) * (other.getX() - x)
                + (other.getY() - y) * (other.getY() - y));
    }

    protected boolean hasPrivilege(@NotNull Player owner, Player other) {
        return isOwn(owner, other);
    }

    protected boolean isOwn(@NotNull Player owner, Player other) {
        if (other == null) {
            return false;
        }
        return owner.getId() == other.getId();
    }

    protected boolean isAreaIn(Pointable p, Pointable center, double scale) {
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return p.getX() > center.getX() - width / 2.0 
                && p.getX() < center.getX() + width / 2.0
                && p.getY() > center.getY() - height / 2.0
                && p.getY() < center.getY() + height / 2.0;
    }
}

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

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepMoving;

/**
 * 線路ノード
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "RailNode.findIn",
            query = "SELECT obj FROM RailNode obj WHERE obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2"
    ),
    @NamedQuery(
            name = "RailNode.findMyIn",
            query = "SELECT obj FROM RailNode obj WHERE obj.owner = :owner AND obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2"
    ),
    @NamedQuery(
            name = "RailNode.findMyLonelyIn",
            query = "SELECT obj FROM RailNode obj WHERE NOT EXISTS (SELECT e FROM RailEdge e WHERE obj = e._from OR obj = e._to)"
                    + " AND NOT EXISTS (SELECT pf FROM Platform pf WHERE obj = pf.railNode)"
                    + " AND obj.owner = :owner AND obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2 "
    ),
    @NamedQuery(
            name = "RailNode.has",
            query = "SELECT CASE WHEN count(obj.id) > 0 THEN true ELSE false END"
                    + " FROM RailNode obj WHERE obj.owner = :owner"
    ),
    @NamedQuery(
            name = "RailNode.exists",
            query = "SELECT CASE WHEN count(obj.id) > 0 THEN true ELSE false END"
                    + " FROM RailNode obj WHERE obj.owner = :owner AND obj.x = :x AND obj.y = :y"
    )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "x", "y"}))
public class RailNode extends GeoEntity implements Pointable, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Player owner;

    @OneToMany(mappedBy = "_from")
    protected List<RailEdge> outEdges;

    @OneToMany(mappedBy = "_to")
    protected List<RailEdge> inEdges;
    
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

    public List<RailEdge> getOutEdges() {
        return outEdges;
    }

    public List<RailEdge> getInEdges() {
        return inEdges;
    }

    @Override
    public String toString() {
        return "rn(" + id + ")";
    }
}

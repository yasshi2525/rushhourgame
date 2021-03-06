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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.entity.troute.LineStepMoving;
import net.rushhourgame.entity.troute.LineStepPassing;
import net.rushhourgame.entity.troute.LineStepStopping;

/**
 * 線路エッジ
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "RailEdge.find",
            query = "SELECT obj FROM RailEdge obj WHERE obj._from = :from AND obj._to = :to"
    ),
    @NamedQuery(
            name = "RailEdge.findIn",
            query = "SELECT obj FROM RailEdge obj"
                    + " WHERE (obj._from.x > :x1 AND obj._from.x < :x2 AND obj._from.y > :y1 AND obj._from.y < :y2)"
                    + " OR (obj._to.x > :x1 AND obj._to.x < :x2 AND obj._to.y > :y1 AND obj._to.y < :y2)"
    ),
    @NamedQuery(
            name = "RailEdge.findMyIn",
            query = "SELECT obj FROM RailEdge obj"
                    + " WHERE obj.owner = :owner AND ((obj._from.x > :x1 AND obj._from.x < :x2 AND obj._from.y > :y1 AND obj._from.y < :y2)"
                    + " OR (obj._to.x > :x1 AND obj._to.x < :x2 AND obj._to.y > :y1 AND obj._to.y < :y2))"
    )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"_from_id", "_to_id"}))
public class RailEdge extends GeoEntity implements Ownable, Pointable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Player owner;

    @NotNull
    @ManyToOne
    protected RailNode _from;

    @NotNull
    @ManyToOne
    protected RailNode _to;
    
    @OneToMany(mappedBy = "running")
    protected List<LineStepMoving> movingSteps;
    
    @OneToMany(mappedBy = "running")
    protected List<LineStepPassing> passingSteps;
    
    @OneToMany(mappedBy = "running")
    protected List<LineStepStopping> stoppingSteps;

    public RailNode getFrom() {
        return _from;
    }

    public void setFrom(RailNode from) {
        _from = from;
    }

    public RailNode getTo() {
        return _to;
    }

    public void setTo(RailNode to) {
        _to = to;
    }

    public double getDist() {
        return _from.distTo(_to);
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
    public double getX() {
        return (_from.getX() + _to.getX()) / 2;
    }

    @Override
    public double getY() {
        return (_from.getY() + _to.getY()) / 2;
    }
    
    public List<LineStepMoving> getMovingSteps() {
        return movingSteps;
    }

    public List<LineStepPassing> getPassingSteps() {
        return passingSteps;
    }

    public List<LineStepStopping> getStoppingSteps() {
        return stoppingSteps;
    }
    
    /**
     * これを使用する {@link RailController#findEdge(net.rushhourgame.entity.Player, long, long) }
     * で em.find すると、違うインスタンスになってしまうため、IDを比較している.
     * @param e 線路エッジ
     * @return 対称関係にあるか
     */
    public boolean isReverse(RailEdge e) {
        return _from.equalsId(e.getTo()) && _to.equalsId(e.getFrom());
    }
    
    @Override
    public String toString() {
        return "re(" + id + "){"+ _from + "_=>_" + _to + "}";
    }
}

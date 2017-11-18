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
import javax.validation.constraints.NotNull;

/**
 * 線路ノード
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "RailNode.findIn",
            query = "SELECT obj FROM RailNode obj WHERE obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2"
    )
})
public class RailNode extends AbstractEntity implements Pointable, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne
    protected Player owner;
    
    protected double x;
    protected double y;

    @OneToMany(mappedBy = "_from")
    protected List<RailEdge> outEdges;

    @OneToMany(mappedBy = "_to")
    protected List<RailEdge> inEdges;

    @OneToMany(mappedBy = "railPoint")
    protected List<Platform> platforms;

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

    public double distTo(Pointable other) {
        return calcDist(x, y, other);
    }

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

    public List<Platform> getPlatforms() {
        return platforms;
    }
}

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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 * 論理的な位置情報を持つエンティティ
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Node.findAll",
            query = "SELECT x FROM Node x"),
    @NamedQuery(
            name="Node.findIn",
            query = "SELECT obj FROM Node obj WHERE obj.point.x > :x1 AND obj.point.x < :x2 AND obj.point.y > :y1 AND obj.point.y < :y2"
    )
})
public class Node extends AbstractEntity implements Pointable, Comparable<Node> {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    
    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    protected Point point;
    
    @OneToMany(mappedBy = "_from", orphanRemoval = true)
    protected List<Link> outEdges;
    
    @OneToMany(mappedBy = "_to", orphanRemoval = true)
    protected List<Link> inEdges;
    
    @OneToMany
    protected List<Human> humans;
    
    protected transient double cost;
    
    protected transient Node via;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public void setX(double x) {
        point.setX(x);
    }
    
    @Override
    public void setY(double y) {
        point.setY(y);
    }
    
    @Override
    public double getX() {
        return point.getX();
    }
    
    @Override
    public double getY() {
        return point.getY();
    }
    
    @Override
    public double distTo(Pointable p) {
        return point.distTo(p);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Human> getHumans() {
        return humans;
    }

    public List<Link> getOutEdges() {
        return outEdges;
    }

    public List<Link> getInEdges() {
        return inEdges;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Node getVia() {
        return via;
    }

    public void setVia(Node via) {
        this.via = via;
    }
    
    @Override
    public int compareTo(Node o) {
        return this.cost > o.cost ? 1
                    : this.cost < o.cost ? -1 : 0;
    }
}

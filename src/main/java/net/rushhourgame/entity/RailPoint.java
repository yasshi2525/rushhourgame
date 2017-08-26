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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Entity
public class RailPoint extends OwnableEntity implements Pointable{
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @ManyToOne
    protected Point point;
    
    @OneToMany(mappedBy = "_from")
    protected List<Rail> outEdges;
    
    @OneToMany(mappedBy = "_to")
    protected List<Rail> inEdges;
    
    @OneToMany(mappedBy = "railPoint")
    protected List<Station> stations;
    
    public double getX() {
        return point.getX();
    }

    public void setX(double x) {
        point.setX(x);
    }

    public double getY() {
        return point.getY();
    }

    public void setY(double y) {
        point.setY(y);
    }
    
    @Override
    public double distTo(Pointable other) {
        return point.distTo(other);
    }

    public List<Rail> getOutEdges() {
        return outEdges;
    }

    public List<Rail> getInEdges() {
        return inEdges;
    }

    public List<Station> getStations() {
        return stations;
    }
}

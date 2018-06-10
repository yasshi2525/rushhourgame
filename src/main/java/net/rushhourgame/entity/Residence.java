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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;

/**
 * 住宅(人を生成するオブジェクト)
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQuery(
        name = "Residence.findAll",
        query = "SELECT x FROM Residence x"
)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"x", "y"}))
public class Residence extends GeoEntity implements Pointable, RelayPointForHuman {

    private static final long serialVersionUID = 1L;

    @Min(1)
    protected int capacity;
    @Min(1)
    protected long _interval;
    protected long count;
    @Min(0)
    protected double prodist;

    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanDirectly> directlyList;

    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanResidenceToStation> stList;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getInterval() {
        return _interval;
    }

    public void setInterval(long _interval) {
        this._interval = _interval;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void step(long interval) {
        count += interval;
    }

    public void consume() {
        count -= _interval;
    }

    public boolean expires() {
        return count >= _interval;
    }

    public double getProdist() {
        return prodist;
    }

    public void setProdist(double prodist) {
        this.prodist = prodist;
    }

    @Override
    public List<StepForHuman> getInEdges() {
        return Collections.<StepForHuman>emptyList();
    }

    @Override
    public List<StepForHuman> getOutEdges() {
        return Stream.concat(stList.stream(), directlyList.stream()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "r(" + id + ")";
    }
}

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
import javax.validation.constraints.DecimalMin;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;

/**
 * 会社(人の行き先。人を吸収するオブジェクト)
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Company.findAll",
            query = "SELECT x FROM Company x"
    )
    ,
    @NamedQuery(
            name = "Company.findIn",
            query = "SELECT obj FROM Company obj WHERE obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2"
    ),
    @NamedQuery(
            name = "Company.exists",
            query = "SELECT CASE WHEN count(obj.id) > 0 THEN true ELSE false END"
                    + " FROM Company obj WHERE obj.x = :x AND obj.y = :y"
    )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"x", "y"}))
public class Company extends AbstractEntity implements Pointable, RelayPointForHuman {

    private static final long serialVersionUID = 1L;
    
    @DecimalMin(value = "0.0", inclusive = false)
    protected double scale;

    protected double x;
    protected double y;

    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanDirectly> directlyList;

    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanStationToCompany> stList;

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double distTo(Pointable p) {
        return calcDist(x, y, p);
    }

    @Override
    public List<StepForHuman> getOutEdges() {
        return Collections.<StepForHuman>emptyList();
    }

    @Override
    public List<StepForHuman> getInEdges() {
        return Stream.concat(stList.stream(), directlyList.stream()).collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "c(" + id + ")";
    }
}

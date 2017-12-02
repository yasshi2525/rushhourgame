/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;

/**
 * プラットフォーム
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Platform.findAll",
            query = "SELECT x FROM Platform x"
    ),
    @NamedQuery(
            name = "Platform.findInLine",
            query = "SELECT obj FROM Platform obj WHERE EXISTS"
                    + " (SELECT step FROM LineStep step"
                    + " JOIN LineStepDeparture dpt ON step.departure = dpt"
                    + " WHERE step.parent = :line AND dpt.staying = obj)"
    )
})
public class Platform extends AbstractEntity implements Pointable, RelayPointForHuman, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToOne
    protected Station station;

    @NotNull
    @OneToOne
    protected RailNode railNode;

    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanThroughTrain> tFromList;

    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanThroughTrain> tToList;

    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanIntoStation> stToList;

    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanOutOfStation> stFromList;

    @Min(1)
    protected int capacity;

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public RailNode getRailNode() {
        return railNode;
    }

    public void setRailNode(RailNode railNode) {
        this.railNode = railNode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public double getX() {
        return railNode.getX();
    }

    @Override
    public double getY() {
        return railNode.getY();
    }

    @Override
    public double distTo(Pointable p) {
        return railNode.distTo(p);
    }

    @Override
    public Player getOwner() {
        return station.getOwner();
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return station.isPrivilegedBy(owner);
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return station.isOwnedBy(owner);
    }

    @Override
    public List<StepForHuman> getOutEdges() {
        return Stream.concat(tFromList.stream(), stFromList.stream()).collect(Collectors.toList());
    }

    @Override
    public List<StepForHuman> getInEdges() {
        return Stream.concat(tToList.stream(), stToList.stream()).collect(Collectors.toList());
    }
}

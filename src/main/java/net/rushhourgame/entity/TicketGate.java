/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to pass, copy, modify, merge, publish, distribute, sublicense, and/or sell
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.entity.hroute.StepForHumanTransfer;

/**
 * 改札口
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "TicketGate.findAll",
            query = "SELECT x FROM TicketGate x"
    )
})
public class TicketGate extends AbstractEntity implements Pointable, RelayPointForHuman, Ownable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToOne
    protected Station station;

    @Min(0)
    protected double mobility;
    
    @Min(1)
    protected int gateNum;
    
    @Min(0)
    protected double occupied;
    
    @Min(0)
    protected double prodist;
    
    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanIntoStation> stFromList;
    
    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanOutOfStation> stToList;
    
    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanResidenceToStation> rsdList;
    
    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanStationToCompany> cmpList;
    
    @OneToMany(mappedBy = "_from")
    protected List<StepForHumanTransfer> tgFromList;
    
    @OneToMany(mappedBy = "_to")
    protected List<StepForHumanTransfer> tgToList;
    
    public void step(long interval) {
        occupied -= mobility * interval;
        if (occupied < 0) {
            occupied = 0;
        }
    }
    
    public boolean canEnter() {
        return isAvailable() && station.getPlatform().canEnter();
    }
    
    public boolean canExit() {
        return isAvailable();
    }
    
    public void pass() {
        if (!canEnter()) {
            throw new IllegalStateException("Tried to enter full station.");
        }
        occupied++;
    }
    
    protected boolean isAvailable() {
        return gateNum - occupied >= 1.0;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getGateNum() {
        return gateNum;
    }

    public void setGateNum(int gateNum) {
        this.gateNum = gateNum;
    }

    public double getMobility() {
        return mobility;
    }

    public void setMobility(double mobility) {
        this.mobility = mobility;
    }

    public double getProdist() {
        return prodist;
    }

    public void setProdist(double prodist) {
        this.prodist = prodist;
    }

    @Override
    public double getX() {
        return station.getX();
    }

    @Override
    public double getY() {
        return station.getY();
    }

    @Override
    public double distTo(Pointable p) {
        return station.distTo(p);
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
        List<StepForHuman> outList = new ArrayList<>();
        outList.addAll(stFromList);
        outList.addAll(tgFromList);
        outList.addAll(cmpList);
        
        return outList;
    }

    @Override
    public List<StepForHuman> getInEdges() {
        List<StepForHuman> inList = new ArrayList<>();
        inList.addAll(stToList);
        inList.addAll(tgToList);
        inList.addAll(rsdList);
        
        return inList;
    }
}

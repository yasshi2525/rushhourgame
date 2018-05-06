/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.controller.route;

import java.util.ArrayList;
import java.util.List;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.TicketGate;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public abstract class AbstractRouteNode implements RouteNode {

    protected final RelayPointForHuman original;
    protected double cost;
    protected RouteNode via;
    protected List<RouteEdge> inEdges = new ArrayList<>();
    protected List<RouteEdge> outEdges = new ArrayList<>();
    protected boolean isFixed;
    protected RouteEdge viaEdge;

    public AbstractRouteNode(RelayPointForHuman original) {
        this.original = original;
    }

    @Override
    public RelayPointForHuman getOriginal() {
        return original;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public RouteNode getVia() {
        return via;
    }

    @Override
    public void setVia(RouteNode via) {
        this.via = via;
    }

    @Override
    public List<RouteEdge> getInEdges() {
        if (isFixed) {
            throw new IllegalStateException("no more avilable route info because this object is already fixed.");
        }
        return inEdges;
    }

    @Override
    public List<RouteEdge> getOutEdges() {
        if (isFixed) {
            throw new IllegalStateException("no more avilable route info because this object is already fixed.");
        }
        return outEdges;
    }

    @Override
    public boolean isEnd() {
        return via == null;
    }
    
    @Override
    public void fix() {
        viaEdge = outEdges.stream().filter(e -> e.getTo().equals(via)).findFirst().orElse(null);
        inEdges = null;
        outEdges = null;
        isFixed = true;
    }

    @Override
    public RouteEdge getViaEdge() {
        return viaEdge;
    }

    @Override
    public int compareTo(RouteNode o) {
        return this.cost > o.getCost() ? 1
                : this.cost < o.getCost() ? -1 : 0;
    }

    @Override
    public String toStringAsRoute() {
        StringBuilder sb = new StringBuilder(this.toString());
        RouteNode node = via;
        while (node != null) {
            sb.append("_->_");
            sb.append(node.toString());
            node = node.getVia();
        }
        return sb.toString();
    }
}

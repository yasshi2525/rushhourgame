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
package net.rushhourgame.controller.route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.rushhourgame.entity.RelayPointForHuman;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RouteNode implements Comparable<RouteNode> {

    protected final RelayPointForHuman original;
    protected double cost;
    protected RouteNode via;
    protected List<RouteEdge> inEdges = new ArrayList<>();
    protected List<RouteEdge> outEdges = new ArrayList<>();

    public RouteNode(RelayPointForHuman original) {
        this.original = original;
    }

    public RelayPointForHuman getOriginal() {
        return original;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public RouteNode getVia() {
        return via;
    }

    public void setVia(RouteNode via) {
        this.via = via;
    }

    public List<RouteEdge> getInEdges() {
        return inEdges;
    }

    public List<RouteEdge> getOutEdges() {
        return outEdges;
    }

    @Override
    public int compareTo(RouteNode o) {
        return this.cost > o.cost ? 1
                : this.cost < o.cost ? -1 : 0;
    }

}

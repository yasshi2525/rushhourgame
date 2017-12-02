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
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.RelayPointForHuman;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class LineRouteNode implements Comparable<LineRouteNode> {

    protected final Platform original;
    protected double cost;
    protected LineRouteNode via;
    protected List<LineRouteEdge> inEdges = new ArrayList<>();
    protected List<LineRouteEdge> outEdges = new ArrayList<>();

    public LineRouteNode(@NotNull Platform original) {
        this.original = original;
    }

    public Platform getOriginal() {
        return original;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setVia(LineRouteNode via) {
        this.via = via;
    }

    public List<LineRouteEdge> getInEdges() {
        return inEdges;
    }

    public List<LineRouteEdge> getOutEdges() {
        return outEdges;
    }

    @Override
    public int compareTo(LineRouteNode o) {
        return this.cost > o.cost ? 1
                : this.cost < o.cost ? -1 : 0;
    }

}

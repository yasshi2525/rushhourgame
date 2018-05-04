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

import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class LineRouteEdge {
    final protected double cost;
    final protected LineRouteNode from;
    final protected LineRouteNode to;

    public LineRouteEdge(
            double cost,
            @NotNull LineRouteNode from, 
            @NotNull LineRouteNode to) {
        this.cost = cost;
        this.from = from;
        this.to = to;
    }
    
    public double getCost() {
        return cost;
    }

    public LineRouteNode getFrom() {
        return from;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("le{");
        sb.append(from);
        sb.append("_=>_");
        sb.append(to);
        sb.append("}");
        return sb.toString();
    }
}

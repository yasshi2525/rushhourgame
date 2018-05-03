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

import java.util.HashSet;
import java.util.Set;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.StepForHuman;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public abstract class AbstractRouteEdge {

    protected Set<Human> referredHumans = new HashSet<>();

    final protected StepForHuman original;
    final protected RouteNode from;
    final protected RouteNode to;

    public AbstractRouteEdge(StepForHuman original, RouteNode from, RouteNode to) {
        this.original = original;
        this.from = from;
        this.to = to;
    }

    public double getCost() {
        return original.getCost();
    }

    public StepForHuman getOriginal() {
        return original;
    }

    public RouteNode getFrom() {
        return from;
    }

    public RouteNode getTo() {
        return to;
    }

    public void reffer(Human h) {
        referredHumans.add(h);
    }

    public void unreffer(Human h) {
        referredHumans.remove(h);
    }
}

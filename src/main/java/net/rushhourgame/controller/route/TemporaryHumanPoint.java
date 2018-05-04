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

import java.util.Collections;
import java.util.List;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Identifiable;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.StepForHuman;

/**
 * 人の移動中に経路がリセットされたとき、その人の現在地を開始点として再経路探索するためのクラス.
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class TemporaryHumanPoint implements RelayPointForHuman {
    protected Human human;
    protected Pointable point;
    protected RouteNode node;
    
    public TemporaryHumanPoint(Human h) {
        this.point = new SimplePoint(h);
        this.human = h;
    }

    public Human getHuman() {
        return human;
    }

    public RouteNode getNode() {
        return node;
    }

    public void setNode(RouteNode node) {
        this.node = node;
    }

    @Override
    public long getId() {
        return human.getId();
    }

    @Override
    public boolean equalsId(Identifiable other) {
        if (other == null) {
            return false;
        }
        return this.getClass().equals(other.getClass()) && this.getId() == other.getId();
    }

    @Override
    public List<StepForHuman> getOutEdges() {
        return Collections.<StepForHuman>emptyList();
    }

    @Override
    public List<StepForHuman> getInEdges() {
        return Collections.<StepForHuman>emptyList();
    }

    @Override
    public double getX() {
        return point.getX();
    }

    @Override
    public double getY() {
        return point.getY();
    }

    @Override
    public double distTo(Pointable p) {
        return point.distTo(p);
    }
    
    @Override
    public String toString() {
        return "tp{" + human + "}";
    }
}

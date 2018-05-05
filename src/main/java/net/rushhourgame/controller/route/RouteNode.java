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
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.TicketGate;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public interface RouteNode extends Comparable<RouteNode> {

    public RelayPointForHuman getOriginal();

    public double getCost();

    public void setCost(double cost);

    public RouteNode getVia();

    public void setVia(RouteNode via);

    public List<RouteEdge> getInEdges();

    public List<RouteEdge> getOutEdges();

    public boolean isEnd();

    /**
     * 経路探索が終了したら必ず呼び出す. inEdges, outEdgesの中身が残っているといつまでもGCされず、メモリ不足になる現象が生じたため
     */
    public void fix();

    public RouteEdge getViaEdge();

    public String toStringAsRoute();
}

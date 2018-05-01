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

import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.StepForHuman;

/**
 * 人の移動中に経路がリセットされたとき、その人の現在地を開始点として再経路探索するためのクラス.
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class TemporaryHumanStep implements StepForHuman {
    protected TemporaryHumanPoint from;
    protected RelayPointForHuman to;

    public TemporaryHumanStep(TemporaryHumanPoint from, RelayPointForHuman to) {
        this.from = from;
        this.to = to;
    }
    
    @Override
    public String getUid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RelayPointForHuman getFrom() {
        return from;
    }

    @Override
    public RelayPointForHuman getTo() {
        return to;
    }

    @Override
    public double getCost() {
        return from.distTo(to);
    }

    @Override
    public boolean isAreaIn(Pointable center, double scale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long step(Human h, long interval, double speed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFinished(Human h) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

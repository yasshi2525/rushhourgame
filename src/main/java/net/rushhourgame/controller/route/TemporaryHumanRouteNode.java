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
import net.rushhourgame.entity.RelayPointForHuman;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class TemporaryHumanRouteNode extends AbstractRouteNode implements RouteNode {
    final protected Human human;
    final protected TemporaryHumanPoint originalCopy;
    
    public TemporaryHumanRouteNode(TemporaryHumanPoint original) {
        super(original);
        this.human = original.getHuman();
        this.originalCopy = original;
    }

    @Override
    public TemporaryHumanPoint getOriginal() {
        return originalCopy;
    }
    
    public void registerCurrentTaskToHuman() {
        human.setCurrent(this);
    }

    public Human getHuman() {
        return human;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("tRouteNode_");
        sb.append("h(");
        sb.append(human.getId());
        sb.append(")");
        if (via != null) {
            sb.append("_=>_");
            sb.append(via.toString());
        }
        return sb.toString();
    }
}

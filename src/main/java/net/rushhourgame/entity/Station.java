/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
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
package net.rushhourgame.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
public class Station extends Building {
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @ManyToOne
    protected RailPoint railPoint;
    
    /**
     * 駅の前. nodeは駅前を表し、他のBuildingから徒歩で移動できる。
     * platformは駅の中を表し、nodeからのみ徒歩移動できる。
     * node - 徒歩 - platform - 電車移動 - platform - node
     */
    @NotNull
    @ManyToOne
    protected Node platform;
    
    protected String name;
    
    protected int capacity;
    
    public void collectHuman(){
        throw new UnsupportedOperationException();
    }
    
    public void freeHuman(){
        throw new UnsupportedOperationException();
    }

    public Node getPlatform() {
        return platform;
    }
    
    public void setPlatform(Node platform) {
        this.platform = platform;
    }

    public RailPoint getRailPoint() {
        return railPoint;
    }

    public void setRailPoint(RailPoint railPoint) {
        this.railPoint = railPoint;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}

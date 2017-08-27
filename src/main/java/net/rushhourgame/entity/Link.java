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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 論理的な接続情報
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Entity
public class Link extends AbstractEntity{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    
    @ManyToOne
    protected Node _from;
    
    @ManyToOne
    protected Node _to;
    
    @ManyToOne
    protected LineStep way;
    
    protected double cost;
    
    protected Type type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Node getFrom() {
        return _from;
    }

    public void setFrom(Node from) {
        this._from = from;
    }

    public Node getTo() {
        return _to;
    }

    public void setTo(Node to) {
        this._to = to;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LineStep getWay() {
        return way;
    }

    public void setWay(LineStep way) {
        this.way = way;
    }
    
    public enum Type{
        WALK,
        ENTER_STATION,
        TRAIN,
        EXIT_STATION
    }
}
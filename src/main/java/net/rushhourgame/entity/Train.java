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
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Entity
public class Train extends OwnableEntity implements Pointable{
    
    protected String name;
    protected int capacity;
    protected double speed;
    protected double mobility;
    protected double process;
    
    @NotNull
    @ManyToOne
    protected Line on;
    
    @NotNull
    @ManyToOne
    protected LineStep current;

    @Override
    public void setX(double x) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setY(double y) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public double distTo(Pointable other) {
        return Math.sqrt((other.getX() - getX()) * (other.getX() - getX())
                + (other.getY() - getY()) * (other.getY() - getY()));
    }
    
    protected void incrementProcess(double val) {
        process += val;
        if(process >= 1.0){
            process = 1.0;
        }
    }
    
    
    public void nextStep(){
        current = current.getNext();
        process = 0.0;
    }
    
    public void run(){
        if(current == null || current.getTarget() != LineStep.TargetType.RAIL_LINE){
            return;
        }
        incrementProcess(speed / current.getOnRail().getDist());
        if(process >= 1.0){
            nextStep();
        }
    }
    
    public void idle(){
        if(current == null || current.getTarget() != LineStep.TargetType.STATION){
            return;
        }
        incrementProcess(mobility);
        if(process >= 1.0){
            nextStep();
        }
    }
    
    public double getX() {
        switch(current.getTarget()){
            case STATION:
                return current.getOnStation().getX();
            case RAIL_LINE:
                return current.getOnRail().getFrom().getX()
                        + (current.getOnRail().getTo().getX() - current.getOnRail().getFrom().getX())
                        * process;
        }
        return Double.NaN;
    }

    public double getY() {
        switch(current.getTarget()){
            case STATION:
                return current.getOnStation().getY();
            case RAIL_LINE:
                return current.getOnRail().getFrom().getY()
                        + (current.getOnRail().getTo().getY() - current.getOnRail().getFrom().getY())
                        * process;
        }
        return Double.NaN;
    }
}

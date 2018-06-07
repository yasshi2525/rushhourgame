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
package net.rushhourgame.managedbean;

import java.io.Serializable;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TrainDeployed;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class OperationBean implements Serializable{

    private static final long serialVersionUID = 1L;
    protected Type type;
    protected RailNode tailNode;
    protected Line line;
    protected TrainDeployed train;
    protected Station station;
    
    private OperationBean(Type type) {
        this.type = type;
    }
    
    private OperationBean(Type type, RailNode tailNode) {
        this.type = type;
        this.tailNode = tailNode;
    }
    
    private OperationBean(Type type, AssistanceController.Result result) {
        this.type = type;
        this.tailNode = result.node;
        this.line = result.line;
    }
    
    private OperationBean(Type type, Station station) {
        this.type = type;
        this.station = station;
    }
    
    private OperationBean(Type type, TrainDeployed train) {
        this.type = type;
        this.train = train;
    }
    
    public static OperationBean newRailCreate(AssistanceController.Result result) {
        return new OperationBean(Type.RAIL_CREATE, result);
    }
    
    public static OperationBean newRailExtend(RailNode tailNode) {
        return new OperationBean(Type.RAIL_EXTEND, tailNode);
    }
    
    public static OperationBean newRailRemove() {
        return new OperationBean(Type.RAIL_REMOVE);
    }
    
    public static OperationBean newStationRemove(Station station) {
        return new OperationBean(Type.STATION_REMOVE, station);
    }
    
    public static OperationBean newTrainUndeploy(TrainDeployed train) {
        return new OperationBean(Type.TRAIN_UNDEPLOY, train);
    }

    public Type getType() {
        return type;
    }

    public RailNode getTailNode() {
        return tailNode;
    }

    public TrainDeployed getTrain() {
        return train;
    }

    public Station getStation() {
        return station;
    }
    
    public enum Type {
        RAIL_CREATE,
        RAIL_EXTEND,
        RAIL_REMOVE,
        STATION_REMOVE,
        TRAIN_UNDEPLOY
    }
}

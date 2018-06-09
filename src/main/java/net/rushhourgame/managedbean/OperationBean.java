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
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public abstract class OperationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    abstract public void select(GameViewBean view);

    abstract public void confirmOK(GameViewBean view) throws RushHourException;

    public static class RailCreation extends OperationBean {

        protected final RailNode tailNode;
        protected final Line line;

        public RailCreation(AssistanceController.Result result) {
            this.tailNode = result.node;
            this.line = result.line;
        }

        @Override
        public void select(GameViewBean view) {
            view.setTailNode(tailNode);
            view.showCreatedRailAnnouncement();
            view.showExtendingRailGuide();
            view.startExtendingMode();
        }

        @Override
        public void confirmOK(GameViewBean view) {
            // do-nothing
        }
    }

    public static class RailExtension extends OperationBean {

        protected RailNode tailNode;

        public RailExtension(RailNode tailNode) {
            this.tailNode = tailNode;
        }

        @Override
        public void select(GameViewBean view) {
            view.setTailNode(tailNode);
            view.showExtendingRailGuide();
            view.startExtendingMode();
        }

        @Override
        public void confirmOK(GameViewBean view) {
            // do-nothing
        }
    }

    public static class RailDeletion extends OperationBean {

        @Override
        public void select(GameViewBean view) {
            view.showConfirmDialog();
        }

        @Override
        public void confirmOK(GameViewBean view) throws RushHourException {
            view.removeRail();
        }
    }

    public static class StationDeletion extends OperationBean {

        protected final Station station;

        public StationDeletion(Station station) {
            this.station = station;
        }

        @Override
        public void select(GameViewBean view) {
            view.showConfirmDialog();
        }

        @Override
        public void confirmOK(GameViewBean view) throws RushHourException {
            view.removeStation(station);
        }
    }

    public static class TrainUndeploy extends OperationBean {

        protected final TrainDeployed train;

        public TrainUndeploy(TrainDeployed train) {
            this.train = train;
        }
        
        @Override
        public void select(GameViewBean view) {
            view.showConfirmDialog();
        }

        @Override
        public void confirmOK(GameViewBean view) throws RushHourException {
            view.undeployTrain(train);
        }
    }
}

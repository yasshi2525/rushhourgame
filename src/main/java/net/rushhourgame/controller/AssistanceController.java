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
package net.rushhourgame.controller;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.PersistenceUnit;
import javax.validation.constraints.NotNull;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Nameable;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.exception.RushHourException;

/**
 * 線路の設置と路線の設定を一緒に行うコントローラ
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class AssistanceController extends AbstractController{
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AssistanceController.class.getName());
    
    @Inject
    RailController rCon;
    @Inject
    LineController lCon;
    @Inject
    StationController stCon;
    @Inject
    RushHourResourceBundle msg;
    
    public Result startWithStation(@NotNull Player player, @NotNull Pointable p, @NotNull Locale locale) throws RushHourException {
        RailNode startNode = rCon.create(player, p);
        Station station = stCon.create(player, startNode, getDefaultStationName(player, locale));
        Line line = lCon.create(player, getDefaultLineName(player, locale));
        lCon.start(line, player, station);
        Result result = new Result(startNode, station, line);
        LOG.log(Level.INFO, "{0}#startWithStation created {1}", new Object[] {AssistanceController.class, result});
        return result;
    } 
    
    public Result extend(@NotNull Player player, @NotNull RailNode tailNode, @NotNull Pointable p) throws RushHourException {
        return _extend(player, tailNode, p, null, false);
    }
    
    public Result extendWithStation(@NotNull Player player, @NotNull RailNode tailNode, @NotNull Pointable p, @NotNull Locale locale) throws RushHourException {
        return _extend(player, tailNode, p, locale, true);
    }
    
    protected Result _extend(Player player, RailNode tailNode, Pointable p, Locale locale, boolean isWithStation) throws RushHourException {
        RailNode extended = rCon.extend(player, tailNode, p);
        Station station = null;
        if (isWithStation) {
            station = stCon.create(player, extended, getDefaultStationName(player, locale));
        }
        LineStep inserted = lCon.insert(tailNode, extended, player);
        if (inserted.getNext() == null) {
            if (lCon.canEnd(inserted, player)) {
                lCon.end(inserted, player);
            }
        }
        Result result = new Result(extended, station, inserted.getParent());
        LOG.log(Level.INFO, "{0}#_extend created {1}", new Object[] {AssistanceController.class, result});
        return result;
    }
    
    protected String getDefaultStationName(Player player, Locale locale) {
        return getDefaultName(stCon.findAll(player), msg.get(LABEL_STATION, locale));
    }
    
    protected String getDefaultLineName(Player player, Locale locale) {
        return getDefaultName(lCon.findAll(player), msg.get(LABEL_LINE, locale));
    }
    
    protected String getDefaultName(List<? extends Nameable> list, String prefix) {
        for (long suffix = 1; true; suffix++) {
            final String name = prefix + suffix;
            if(!list.stream().filter(obj -> obj.getName().equals(name))
                    .findAny().isPresent()) {
                return name;
            }
        }
    }
    
    public static class Result {
        public RailNode node;
        public Station station;
        public Line line;

        public Result(RailNode node, Station station, Line line) {
            this.node = node;
            this.station = station;
            this.line = line;
        }
        
        @Override
        public String toString() {
            return "{" + node + ",_" + station + ",_" + line.toStringAsRoute() + "}";
        }
    }
}

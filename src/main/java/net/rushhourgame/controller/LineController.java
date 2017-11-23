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
package net.rushhourgame.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourResourceBundle.GAME_NO_PRIVILEDGE_OTHER_OWNED;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.troute.LineStepDeparture;
import net.rushhourgame.entity.troute.LineStepStopping;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class LineController extends AbstractController {

    private static final long serialVersionUID = 1L;

    public Line create(@NotNull Player owner, @NotNull String name) throws RushHourException {
        if (exists("Line.existsName", owner, "name", name)) {
            throw new RushHourException(errMsgBuilder.createLineNameDuplication(name));
        }

        Line inst = new Line();
        inst.setName(name);
        inst.setOwner(owner);
        em.persist(inst);

        return inst;
    }

    public LineStep start(@NotNull Line line, @NotNull Player owner, @NotNull Station start) throws RushHourException {
        if (!line.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        // どのedgeからくるのかわからないため、stoppingは生成しない
        LineStep parent = new LineStep();
        parent.setParent(line);
        parent.registerDeparture(start);

        em.persist(parent);

        return parent;
    }

    public List<RailEdge> findNext(@NotNull LineStep current, @NotNull Player owner) throws RushHourException {
        if (!current.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        RailNode startNode;
        try {
            startNode = current.getGoalRailNode();
        } catch (IllegalStateException e) {
            // currentに紐づくchildがいないとき
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        // 線路ノードに隣接する線路エッジをロード
        em.refresh(startNode);

        // 路線に属する全ステップをロード
        Line line = current.getParent();
        em.refresh(line);

        try {
            // 隣接線路エッジの中から、未到達のものだけフィルタリング
            return startNode.getOutEdges().stream()
                    .filter(e -> !line.hasVisited(e))
                    .collect(Collectors.toList());
        } catch (IllegalStateException e) {
            // hasVisited の中の 各stepに紐づくchildがいないとき
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.RushHourResourceBundle.GAME_NO_PRIVILEDGE_OTHER_OWNED;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
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

    @Inject
    protected StepForHumanController sCon;
    @Inject
    protected RailController rCon;

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
        parent.registerDeparture(start.getPlatform());

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

    public LineStep extend(@NotNull LineStep current, @NotNull Player owner, @NotNull RailEdge edge) throws RushHourException {
        return extend(current, owner, edge, false);
    }

    public LineStep extend(@NotNull LineStep base, @NotNull Player owner, @NotNull RailEdge extend, boolean passing) throws RushHourException {
        if (!base.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (!extend.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        if (base.getNext() != null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        // startとnextがつながっていない
        if (!base.getGoalRailNode().equals(extend.getFrom())) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        // 駅にとまっている場合、まず駅から発車する
        if (base.getStopping() != null) {
            base = createDeparture(base.getStopping());
        }

        RailNode to = extend.getTo();
        em.refresh(to);

        if (to.getPlatform() != null) {
            // 駅につく
            if (!passing) {
                base = createStopping(base, extend, to.getPlatform());
            } else {
                base = createPassing(base, extend, to.getPlatform());
            }
        } else {
            base = createMoving(base, extend);
        }

        return base;
    }

    /**
     * start -&gt; goal を start -&gt; insertedStart -&gt; insertedGoal -&gt; goal にする
     * @param from 元々あった線路ノード
     * @param to 路線に加えたい線路ノード
     * @param owner Player
     * @return to から from に戻る路線エッジ
     * @throws RushHourException 例外
     */
    public LineStep insert(@NotNull RailNode from, @NotNull RailNode to, @NotNull Player owner) throws RushHourException {
        
        if (!from.isOwnedBy(owner) || !to.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }
        
        if (!rCon.existsEdge(from, to)) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        
        List<LineStep> bases = findByGoalRailNode(from);
        if (bases.isEmpty()) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        
        LineStep start = bases.get(0);
        LineStep goal = start.getNext();
        
        start.setNext(null);
                
        RailEdge forward = rCon.findEdge(from, to);
        RailEdge back = rCon.findEdge(to, from);
        
        LineStep insertedStart = extend(start, owner, forward);
        LineStep insertedGoal = extend(insertedStart, owner, back);
        
        // 新規に停車をする場合、発車ステップを入れて後続との整合性をとる
        if (insertedGoal.getStopping() != null && goal != null) {
            insertedGoal = createDeparture(insertedGoal.getStopping());
        }
        
        insertedGoal.setNext(goal);
        
        return insertedGoal;
    }

    public boolean canEnd(LineStep tail, Player owner) throws RushHourException {
        if (!tail.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        if (tail.getNext() != null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        LineStep top = findTop(tail.getParent());

        return tail.canConnect(top);
    }

    public void end(LineStep tail, Player owner) throws RushHourException {
        if (!tail.isOwnedBy(owner)) {
            throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
        }

        if (tail.getNext() != null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

        LineStep top = findTop(tail.getParent());

        if (tail.canConnect(top)) {
            tail.setNext(top);
        } else {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }
        sCon.addCompletedLine(tail.getParent());
    }

    public boolean isCompleted(@NotNull Line line) {
        return em.createNamedQuery("Line.isImcompleted", Number.class)
                .setParameter("line", line)
                .getSingleResult().longValue() != 1L;
    }

    protected LineStep createDeparture(LineStepStopping base) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent().getParent());
        newStep.registerDeparture(base.getGoal());
        em.persist(newStep);

        base.getParent().setNext(newStep);
        return newStep;
    }

    protected LineStep createStopping(LineStep base, RailEdge extend, Platform goal) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerStopping(extend, goal);
        em.persist(newStep);

        base.setNext(newStep);
        return newStep;
    }

    protected LineStep createMoving(LineStep base, RailEdge extend) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerMoving(extend);
        em.persist(newStep);

        base.setNext(newStep);
        return newStep;
    }

    protected LineStep createPassing(LineStep base, RailEdge extend, Platform goal) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerPassing(extend, goal);
        em.persist(newStep);

        base.setNext(newStep);
        return newStep;
    }

    public List<Line> findAll() {
        return em.createNamedQuery("Line.findAll", Line.class).getResultList();
    }

    public List<Line> findAll(Player p) {
        return em.createNamedQuery("Line.findMyAll", Line.class)
                .setParameter("owner", p).getResultList();
    }

    public List<Line> findIn(@NotNull Pointable center, double scale) {
        return em.createNamedQuery("Line.findAll", Line.class).getResultList()
                .stream().filter(l -> {
                    em.refresh(l);
                    return l.isAreaIn(center, scale);
                })
                .collect(Collectors.toList());
    }

    public List<LineStep> findByGoalRailNode(RailNode node) {
        String[] queries = {
            "LineStep.findByDepartingRailNode",
            "LineStep.findByMovingGoalRailNode",
            "LineStep.findByStoppingGoalRailNode",
            "LineStep.findByPassingGoalRailNode"
        };

        List<LineStep> result = new ArrayList<>();

        for (String query : queries) {
            result.addAll(em.createNamedQuery(query, LineStep.class)
                    .setParameter("node", node)
                    .getResultList());
        }
        return result;
    }
    
    protected LineStep findTop(@NotNull Line line) {
        return em.createNamedQuery("LineStep.findTop", LineStep.class)
                .setParameter("line", line)
                .getSingleResult();
    }

    public Line autocreate(Player player, Station start, String name) throws RushHourException {
        Line line = create(player, name);
        em.flush();

        LineStep tail = start(line, player, start);

        List<RailEdge> candinates;

        while (!(candinates = findNext(tail, player)).isEmpty()) {
            tail = extend(tail, player, candinates.get(0));
            em.flush();
        }

        if (canEnd(tail, player)) {
            end(tail, player);
        }
        return line;
    }
}

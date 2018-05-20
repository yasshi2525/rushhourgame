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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class LineController extends CachedController<Line> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LineController.class.getName());

    @Inject
    protected StepForHumanController sCon;
    @Inject
    protected RailController rCon;
    @Inject
    protected StationController stCon;

    @Override
    public void synchronizeDatabase() {
        LOG.log(Level.INFO, "{0}#synchronizeDatabase start", new Object[]{LineController.class});
        writeLock.lock();
        try {
            synchronizeDatabase("Line.findAll", Line.class);
        } finally {
            writeLock.unlock();
        }
        LOG.log(Level.INFO, "{0}#synchronizeDatabase end", new Object[]{LineController.class});
    }

    @Override
    protected Line mergeEntity(Line entity) {
        entity.setSteps(entity.getSteps().stream().map(step -> em.merge(step)).collect(Collectors.toList()));
        return em.merge(entity);
    }

    protected Line mergeEntitySingle(Line entity) {
        entity.setSteps(entity.getSteps().stream().map(step -> em.merge(step)).collect(Collectors.toList()));
        // LineStepのparentも更新されるためLine自体も更新
        entities.remove(entity);
        Line newEntity = em.merge(entity);
        entities.add(newEntity);
        return newEntity;
    }

    public Line create(@NotNull Player owner, @NotNull String name) throws RushHourException {
        writeLock.lock();
        try {
            if (existsName(owner, name)) {
                throw new RushHourException(errMsgBuilder.createLineNameDuplication(name));
            }

            Line inst = new Line();
            inst.setName(name);
            inst.setOwner(owner);
            persistEntity(inst);

            em.flush();
            LOG.log(Level.INFO, "{0}#create created {1}", new Object[]{LineController.class, inst.toStringAsRoute()});
            return inst;
        } finally {
            writeLock.unlock();
        }
    }

    public LineStep start(@NotNull Line line, @NotNull Player owner, @NotNull Station start) throws RushHourException {
        writeLock.lock();
        try {
            if (!line.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }

            if (!line.getSteps().isEmpty()) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            // どのedgeからくるのかわからないため、stoppingは生成しない
            LineStep parent = new LineStep();
            parent.setParent(line);
            parent.registerDeparture(start.getPlatform());
            line.getSteps().add(parent);

            em.persist(parent);

            em.flush();
            LOG.log(Level.INFO, "{0}#start created {1}", new Object[]{LineController.class, parent});
            return parent;
        } finally {
            writeLock.unlock();
        }
    }

    public List<RailEdge> findNext(@NotNull LineStep current, @NotNull Player owner) throws RushHourException {
        readLock.lock();
        try {
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

            Line line = current.getParent();

            try {
                // 隣接線路エッジの中から、未到達のものだけフィルタリング
                return startNode.getOutEdges().stream()
                        .filter(e -> !line.hasVisited(e))
                        .collect(Collectors.toList());
            } catch (IllegalStateException e) {
                // hasVisited の中の 各stepに紐づくchildがいないとき
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }
        } finally {
            readLock.unlock();
        }
    }

    public LineStep extend(@NotNull LineStep current, @NotNull Player owner, @NotNull RailEdge edge) throws RushHourException {
        return extend(current, owner, edge, false);
    }

    public LineStep extend(@NotNull LineStep base, @NotNull Player owner, @NotNull RailEdge extend, boolean passing) throws RushHourException {
        writeLock.lock();
        try {
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
            if (!base.getGoalRailNode().equalsId(extend.getFrom())) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            // 駅にとまっている場合、まず駅から発車する
            if (base.getStopping() != null) {
                base = createDeparture(base.getStopping());
            }

            RailNode to = extend.getTo();
            em.refresh(to);
            Platform toPlatform = stCon.find(to.getPlatform());

            if (toPlatform != null) {
                // 駅につく
                if (!passing) {
                    base = createStopping(base, extend, toPlatform);
                } else {
                    base = createPassing(base, extend, toPlatform);
                }
            } else {
                base = createMoving(base, extend);
            }

            em.flush();
            LOG.log(Level.INFO, "{0}#extend created {1}", new Object[]{LineController.class, base});
            return base;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * start -&gt; goal を start -&gt; insertedStart -&gt; insertedGoal -&gt;
     * goal にする
     *
     * @param from 元々あった線路ノード
     * @param to 路線に加えたい線路ノード
     * @param owner Player
     * @return to から from に戻る路線エッジ
     * @throws RushHourException 例外
     */
    public LineStep insert(@NotNull RailNode from, @NotNull RailNode to, @NotNull Player owner) throws RushHourException {
        writeLock.lock();
        try {
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

            em.flush();
            LOG.log(Level.INFO, "{0}#insert created {1}", new Object[]{LineController.class, insertedGoal});
            return insertedGoal;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean canEnd(LineStep tail, Player owner) throws RushHourException {
        readLock.lock();
        try {
            if (!tail.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }

            if (tail.getNext() != null) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            LineStep top = findTop(tail.getParent());

            return tail.canConnect(top);
        } finally {
            readLock.unlock();
        }
    }

    public void end(LineStep tail, Player owner) throws RushHourException {
        writeLock.lock();
        try {
            if (!tail.isOwnedBy(owner)) {
                throw new RushHourException(errMsgBuilder.createNoPrivileged(GAME_NO_PRIVILEDGE_OTHER_OWNED));
            }

            if (tail.getNext() != null) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            LineStep top = findTop(tail.getParent());

            if (tail.canConnect(top)) {
                tail.setNext(top);
                // 更新であるが、保存しないと電車が走れなくなるため保存
                mergeEntitySingle(tail.getParent());
            } else {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }
            sCon.addCompletedLine(tail.getParent());
        } finally {
            writeLock.unlock();
        }
    }

    protected LineStep createDeparture(LineStepStopping base) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent().getParent());
        newStep.registerDeparture(base.getGoal());
        em.persist(newStep);

        base.getParent().setNext(newStep);
        newStep.getParent().getSteps().add(newStep);
        return newStep;
    }

    protected LineStep createStopping(LineStep base, RailEdge extend, Platform goal) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerStopping(extend, goal);
        em.persist(newStep);

        base.setNext(newStep);
        newStep.getParent().getSteps().add(newStep);
        return newStep;
    }

    protected LineStep createMoving(LineStep base, RailEdge extend) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerMoving(extend);
        em.persist(newStep);

        base.setNext(newStep);
        newStep.getParent().getSteps().add(newStep);
        return newStep;
    }

    protected LineStep createPassing(LineStep base, RailEdge extend, Platform goal) {
        LineStep newStep = new LineStep();
        newStep.setParent(base.getParent());
        newStep.registerPassing(extend, goal);
        em.persist(newStep);

        base.setNext(newStep);
        newStep.getParent().getSteps().add(newStep);
        return newStep;
    }

    protected List<LineStep> findByGoalRailNode(RailNode node) {
        List<LineStep> result = new ArrayList<>();

        findAll().stream()
                .filter(l -> l.isOwnedBy(node.getOwner()))
                .forEach(l -> result.addAll(
                l.getSteps().stream()
                        .filter(step -> step.getGoalRailNode().equalsId(node))
                        .collect(Collectors.toList())));
        return result;
    }

    /**
     * 誰からも参照されていない LineStep を探す
     *
     * @param line line
     * @return LineStep
     * @throws net.rushhourgame.exception.RushHourException 例外
     */
    protected LineStep findTop(@NotNull Line line) throws RushHourException {
        Map<LineStep, Boolean> referred = new HashMap<>();
        line.getSteps().forEach(step -> referred.put(step, Boolean.FALSE));
        line.getSteps().stream()
                .filter(step -> step.getNext() != null)
                .forEach(step -> referred.put(step.getNext(), Boolean.TRUE));
        for (LineStep step : referred.keySet()) {
            if (!referred.get(step)) {
                return step;
            }
        }
        throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
    }

    public Line autocreate(Player player, Station start, String name) throws RushHourException {
        writeLock.lock();
        try {
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
            LOG.log(Level.INFO, "{0}#autocreate created {1}", new Object[]{LineController.class, line.toStringAsRoute()});

            em.flush();
            return line;
        } finally {
            writeLock.unlock();
        }
    }

    protected boolean existsName(Player owner, String name) {
        return findAll().stream().filter(l -> l.isOwnedBy(owner)).anyMatch(l -> l.getName().equals(name));
    }

    public LineStep find(LineStep old) {
        readLock.lock();
        try {
            if (old == null) {
                return null;
            }
            return findAll().stream()
                    .filter(l -> l.equalsId(old.getParent()))
                    .findFirst().get().getSteps().stream()
                    .filter(step -> step.equalsId(old))
                    .findFirst().get();
        } finally {
            readLock.unlock();
        }
    }
}

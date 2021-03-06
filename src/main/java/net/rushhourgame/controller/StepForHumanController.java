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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import net.rushhourgame.entity.hroute.StepForHumanTransfer;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class StepForHumanController extends AbstractController {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(StepForHumanController.class.getName());

    @Inject
    protected LineRouteSearcher lSearcher;

    @Inject
    protected RouteSearcher searcher;

    @Inject
    protected StationController stCon;

    @Inject
    protected ResidenceController rCon;

    @Resource(lookup = "concurrent/RushHourGameRoute")
    protected ManagedExecutorService executorService;

    public List<StepForHuman> findAll() {
        return _findAll().stream().collect(Collectors.toList());
    }

    public List<StepForHuman> findIn(@NotNull Pointable center, double scale) {
        return _findAll().stream().filter(s -> s.isAreaIn(center, scale))
                .collect(Collectors.toList());
    }

    protected List<StepForHuman> _findAll() {
        // 人用移動ステップをすべて取得する。
        List<StepForHuman> list = new ArrayList<>();
        list.addAll(findDirectlyAll());
        list.addAll(findFromResidenceAll());
        list.addAll(findIntoStationAll());
        list.addAll(findThroughTrainAll());
        list.addAll(findOutOfStationAll());
        list.addAll(findTransfer());
        list.addAll(findToCompanyAll());
        return list;
    }

    public List<StepForHumanDirectly> findDirectlyAll() {
        return em.createNamedQuery("StepForHumanDirectly.findAll", StepForHumanDirectly.class).getResultList();
    }

    public List<StepForHumanResidenceToStation> findFromResidenceAll() {
        return em.createNamedQuery("StepForHumanResidenceToStation.findAll", StepForHumanResidenceToStation.class).getResultList();
    }

    public List<StepForHumanIntoStation> findIntoStationAll() {
        return em.createNamedQuery("StepForHumanIntoStation.findAll", StepForHumanIntoStation.class).getResultList();
    }

    public List<StepForHumanThroughTrain> findThroughTrainAll() {
        return em.createNamedQuery("StepForHumanThroughTrain.findAll", StepForHumanThroughTrain.class).getResultList();
    }

    public List<StepForHumanOutOfStation> findOutOfStationAll() {
        return em.createNamedQuery("StepForHumanOutOfStation.findAll", StepForHumanOutOfStation.class).getResultList();
    }

    public List<StepForHumanTransfer> findTransfer() {
        return em.createNamedQuery("StepForHumanTransfer.findAll", StepForHumanTransfer.class).getResultList();
    }

    public List<StepForHumanStationToCompany> findToCompanyAll() {
        return em.createNamedQuery("StepForHumanStationToCompany.findAll", StepForHumanStationToCompany.class).getResultList();
    }

    /**
     * 会社に到達する人用移動ステップを作成する
     *
     * @param newInst Company
     * @throws RushHourException newInstがnull
     */
    public void addCompany(@NotNull Company newInst) throws RushHourException {
        searcher.lock();
        try {

            List<Residence> residences
                    = em.createNamedQuery("Residence.findAll", Residence.class).getResultList();

            for (Residence r : residences) {
                persistStepForHuman(createDirectly(r, newInst));
            }

            List<TicketGate> gates = stCon.findTicketGateAll();

            for (TicketGate t : gates) {
                persistStepForHuman(createStationToCompany(t, newInst));
            }

            LOG.log(Level.INFO, "{0}#addCompany created for {1}", new Object[]{StepForHumanController.class, newInst});
            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void addResidence(@NotNull Residence newInst) throws RushHourException {
        searcher.lock();
        try {
            List<Company> companies
                    = em.createNamedQuery("Company.findAll", Company.class).getResultList();

            for (Company c : companies) {
                persistStepForHuman(createDirectly(newInst, c));
            }

            List<TicketGate> gates = stCon.findTicketGateAll();

            for (TicketGate t : gates) {
                persistStepForHuman(createResidenceToStation(newInst, t));
            }

            LOG.log(Level.INFO, "{0}#addResidence created for {1}", new Object[]{StepForHumanController.class, newInst});
            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void addStation(@NotNull Station newInst) throws RushHourException {
        searcher.lock();
        try {
            // 家 -> 改札口
            List<Residence> residences
                    = em.createNamedQuery("Residence.findAll", Residence.class).getResultList();

            for (Residence r : residences) {
                persistStepForHuman(createResidenceToStation(r, newInst.getTicketGate()));
            }

            // 改札口 -> 会社
            List<Company> companies
                    = em.createNamedQuery("Company.findAll", Company.class).getResultList();

            for (Company c : companies) {
                persistStepForHuman(createStationToCompany(newInst.getTicketGate(), c));
            }

            // 改札口 -> 改札口
            List<TicketGate> ticketGates = stCon.findTicketGateAll();

            for (TicketGate gate : ticketGates) {
                if (!newInst.getTicketGate().equals(gate)) {
                    persistStepForHuman(createTransfer(newInst.getTicketGate(), gate));
                    persistStepForHuman(createTransfer(gate, newInst.getTicketGate()));
                }
            }

            // 改札口 <-> プラットフォーム
            persistStepForHuman(createIntoStation(newInst.getTicketGate(), newInst.getPlatform()));
            persistStepForHuman(createOutOfStation(newInst.getPlatform(), newInst.getTicketGate()));

            LOG.log(Level.INFO, "{0}#addStation created for {1}", new Object[]{StepForHumanController.class, newInst});
            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void removeStation(Station st) {
        searcher.lock();
        try {
            // 家     -> 改札口
            findFromResidenceAll().stream()
                    .filter(step -> step.getTo().equalsId(st.getTicketGate()))
                    .forEach(step -> em.remove(step));
            // 改札口 -> 会社
            findToCompanyAll().stream()
                    .filter(step -> step.getFrom().equalsId(st.getTicketGate()))
                    .forEach(step -> em.remove(step));
            // 改札口 <-> 改札口
            findTransfer().stream()
                    .filter(step -> step.getFrom().equalsId(st.getTicketGate()) || step.getTo().equalsId(st.getTicketGate()))
                    .forEach(step -> em.remove(step));
            // 改札口 <-> プラットフォーム
            findIntoStationAll().stream()
                    .filter(step -> step.getTo().equalsId(st.getPlatform()))
                    .forEach(step -> em.remove(step));
            findOutOfStationAll().stream()
                    .filter(step -> step.getFrom().equalsId(st.getPlatform()))
                    .forEach(step -> em.remove(step));
            // 電車
            findThroughTrainAll().stream()
                    .filter(step -> step.getFrom().equalsId(st.getPlatform()) || step.getTo().equalsId(st.getPlatform()))
                    .forEach(step -> em.remove(step));

            LOG.log(Level.INFO, "{0}#removeStation remvoed for {1}", new Object[]{StepForHumanController.class, st});

            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void addCompletedLine(@NotNull Line line) throws RushHourException {
        searcher.lock();
        try {
            if (!line.isCompleted()) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            lSearcher.persist(line);

            LOG.log(Level.INFO, "{0}#addCompletedLine created for {1}", new Object[]{StepForHumanController.class, line});
            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void modifyCompletedLine(@NotNull Line line) throws RushHourException {
        searcher.lock();
        try {
            if (!line.isCompleted()) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            int num = removeAllStepForHuman(line);
            LOG.log(Level.FINE, "{0}#modifyCOmpletedLine removed {1} steps", new Object[]{StepForHumanController.class, num});

            lSearcher.persist(line);

            LOG.log(Level.INFO, "{0}#modifyCOmpletedLine updated for {1}", new Object[]{StepForHumanController.class, line});
            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    protected int removeAllStepForHuman(Line line) {
        return em.createNamedQuery("StepForHumanThroughTrain.removeByLine")
                .setParameter("line", line)
                .executeUpdate();
    }

    protected StepForHumanDirectly createDirectly(Residence from, Company to) {
        StepForHumanDirectly inst = new StepForHumanDirectly();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createDirectly created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected StepForHumanResidenceToStation createResidenceToStation(Residence from, TicketGate to) {
        StepForHumanResidenceToStation inst = new StepForHumanResidenceToStation();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createResidenceToStation created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected StepForHumanStationToCompany createStationToCompany(TicketGate from, Company to) {
        StepForHumanStationToCompany inst = new StepForHumanStationToCompany();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createStationToCompany created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected StepForHumanIntoStation createIntoStation(TicketGate from, Platform to) {
        StepForHumanIntoStation inst = new StepForHumanIntoStation();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createIntoStation created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected StepForHumanOutOfStation createOutOfStation(Platform from, TicketGate to) {
        StepForHumanOutOfStation inst = new StepForHumanOutOfStation();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createOutOfStation created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected StepForHumanTransfer createTransfer(TicketGate from, TicketGate to) {
        StepForHumanTransfer inst = new StepForHumanTransfer();
        inst.setFrom(from);
        inst.setTo(to);
        LOG.log(Level.FINE, "{0}#createTransfer created {1}", new Object[]{StepForHumanController.class, inst});
        return inst;
    }

    protected void persistStepForHuman(StepForHuman child) {
        em.persist(child);
    }

}

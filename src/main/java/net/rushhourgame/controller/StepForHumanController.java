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
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Line;
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

    @Inject
    protected LineRouteSearcher lSearcher;

    @Inject
    protected RouteSearcher searcher;

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

            List<TicketGate> gates
                    = em.createNamedQuery("TicketGate.findAll", TicketGate.class).getResultList();

            for (TicketGate t : gates) {
                persistStepForHuman(createStationToCompany(t, newInst));
            }

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

            List<TicketGate> gates
                    = em.createNamedQuery("TicketGate.findAll", TicketGate.class).getResultList();

            for (TicketGate t : gates) {
                persistStepForHuman(createResidenceToStation(newInst, t));
            }

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
            List<TicketGate> ticketGates
                    = em.createNamedQuery("TicketGate.findAll", TicketGate.class).getResultList();

            for (TicketGate gate : ticketGates) {
                if (!newInst.getTicketGate().equals(gate)) {
                    persistStepForHuman(createTransfer(newInst.getTicketGate(), gate));
                    persistStepForHuman(createTransfer(gate, newInst.getTicketGate()));
                }
            }

            // 改札口 <-> プラットフォーム
            persistStepForHuman(createIntoStation(newInst.getTicketGate(), newInst.getPlatform()));
            persistStepForHuman(createOutOfStation(newInst.getPlatform(), newInst.getTicketGate()));

            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    public void addCompletedLine(@NotNull Line line) throws RushHourException {
        searcher.lock();
        try {
            if (em.createNamedQuery("Line.isImcompleted", Number.class)
                    .setParameter("line", line)
                    .getSingleResult().longValue() == 1L) {
                throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
            }

            lSearcher.persist(line);

            // ここで submit すると EntityManager が close して JSF でエラーになる
            searcher.notifyUpdate();
        } finally {
            searcher.unlock();
        }
    }

    protected StepForHumanDirectly createDirectly(Residence from, Company to) {
        StepForHumanDirectly inst = new StepForHumanDirectly();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected StepForHumanResidenceToStation createResidenceToStation(Residence from, TicketGate to) {
        StepForHumanResidenceToStation inst = new StepForHumanResidenceToStation();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected StepForHumanStationToCompany createStationToCompany(TicketGate from, Company to) {
        StepForHumanStationToCompany inst = new StepForHumanStationToCompany();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected StepForHumanIntoStation createIntoStation(TicketGate from, Platform to) {
        StepForHumanIntoStation inst = new StepForHumanIntoStation();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected StepForHumanOutOfStation createOutOfStation(Platform from, TicketGate to) {
        StepForHumanOutOfStation inst = new StepForHumanOutOfStation();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected StepForHumanTransfer createTransfer(TicketGate from, TicketGate to) {
        StepForHumanTransfer inst = new StepForHumanTransfer();
        inst.setFrom(from);
        inst.setTo(to);
        return inst;
    }

    protected void persistStepForHuman(StepForHuman child) {
        em.persist(child);
    }

}

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
import javax.enterprise.context.Dependent;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.TicketGate;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.exception.RushHourRuntimeException;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class StepForHumanController extends AbstractController {

    private static final long serialVersionUID = 1L;

    public List<StepForHuman> findAll() {
        // 人用移動ステップをすべて取得する。
        // concat にしたのは List.addAllより早そうと思ったから
        return Stream.concat(
                findDirectlyAll().stream(),
                Stream.concat(
                        findFromResidenceAll().stream(),
                        Stream.concat(
                                findIntoStationAll().stream(),
                                Stream.concat(
                                        findThroughTrainAll().stream(),
                                        Stream.concat(
                                                findOutOfStationAll().stream(),
                                                findToCompanyAll().stream()
                                        )
                                )
                        )
                )
        ).collect(Collectors.toList());
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

    public List<StepForHumanStationToCompany> findToCompanyAll() {
        return em.createNamedQuery("StepForHumanStationToCompany.findAll", StepForHumanStationToCompany.class).getResultList();
    }

    /**
     * 会社に到達する人用移動ステップを作成する
     *
     * @param newInst Company
     * @throws RushHourException newInstがnull
     */
    public void addCompany(Company newInst) throws RushHourException {
        if (newInst == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

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
    }

    public void addResidence(Residence newInst) throws RushHourException {
        if (newInst == null) {
            throw new RushHourException(errMsgBuilder.createDataInconsitency(null));
        }

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

    protected void persistStepForHuman(StepForHuman child) {
        em.persist(child);
    }

}
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
package net.rushhourgame.controller;

import net.rushhourgame.SimpleGameMaster;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import net.rushhourgame.DebugInitializer;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.GameMaster;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.SimpleDebugInitializer;
import net.rushhourgame.entity.EncryptConverter;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ControllerFactory {

    protected AssistanceController aCon;
    protected CompanyController cCon;
    protected DigestCalculator calc;
    protected EncryptConverter converter;
    protected HumanController hCon;
    protected LineController lCon;
    protected LineRouteSearcher lSearcher;
    protected OAuthController oCon;
    protected PlayerController pCon;
    protected RailController railCon;
    protected ResidenceController rCon;
    protected RouteSearcher searcher;
    protected StationController stCon;
    protected StepForHumanController sCon;
    protected TrainController tCon;

    protected SimpleGameMaster gm;
    protected SimpleDebugInitializer debug;

    protected LocalTableController tableCon;
    protected ManagedExecutorService service;

    protected final EntityManager em;
    protected final RushHourProperties prop;
    protected final RushHourResourceBundle msg;
    protected final ErrorMessageBuilder builder;

    public ControllerFactory() {
        this.em = Persistence.createEntityManagerFactory("test_rushhour_RushHour1.0_war_1.0-SNAPSHOTPU").createEntityManager();
        this.prop = spy(RushHourProperties.getInstance());
        this.msg = spy(RushHourResourceBundle.getInstance());
        this.builder = spy(ErrorMessageBuilder.getInstance());

        instantiate();

        initLocalTableController();

        initAssistanceController();
        initCompanyController();
        initDigestCalculator();
        initEncryptConverter();
        initHumanController();
        initLineController();
        initLineRouteSearcher();
        initOAuthController();
        initPlayController();
        initRailController();
        initResidenceController();
        initRouteSearcher();
        initStationController();
        initStepForHumanController();
        initTrainController();
        
        initGameMaster();
        initDebugInitializer();
    }
    
    public void begin() {
        em.getTransaction().begin();
    }
    
    public void rollback() {
        em.getTransaction().rollback();
    }
    
    public EntityManager getEntityManager() {
        return em;
    }

    public AssistanceController getAssistanceController() {
        return aCon;
    }

    public CompanyController getCompanyController() {
        return cCon;
    }

    public DigestCalculator getDigestCalculator() {
        return calc;
    }

    public EncryptConverter getEncryptConverter() {
        return converter;
    }

    public HumanController getHumanController() {
        return hCon;
    }

    public LineController getLineController() {
        return lCon;
    }

    public LineRouteSearcher getLineRouteSearcher() {
        return lSearcher;
    }

    public OAuthController getOAuthController() {
        return oCon;
    }

    public PlayerController getPlayerController() {
        return pCon;
    }

    public RailController getRailController() {
        return railCon;
    }

    public ResidenceController getResidenceController() {
        return rCon;
    }

    public RouteSearcher getRouteSearcher() {
        return searcher;
    }

    public StationController getStationController() {
        return stCon;
    }

    public StepForHumanController getStepForHumanController() {
        return sCon;
    }

    public TrainController getTrainController() {
        return tCon;
    }

    public GameMaster getGameMaster() {
        return gm;
    }

    public DebugInitializer getDebugInitializer() {
        return debug;
    }

    public LocalTableController getLocalTableController() {
        return tableCon;
    }

    private void instantiate() {
        tableCon = new LocalTableController(em);

        aCon = spy(new AssistanceController());
        cCon = spy(new CompanyController());
        calc = spy(new DigestCalculator());
        converter = spy(new EncryptConverter());
        hCon = spy(new HumanController());
        lCon = spy(new LineController());
        lSearcher = spy(new LineRouteSearcher());
        oCon = spy(new OAuthController());
        pCon = spy(new PlayerController());
        rCon = spy(new ResidenceController());
        railCon = spy(new RailController());
        sCon = spy(new StepForHumanController());
        searcher = spy(new RouteSearcher());
        service = mock(ManagedExecutorService.class);
        stCon = spy(new StationController());
        tCon = spy(new TrainController());

        gm = spy(new SimpleGameMaster());
        debug = spy(new SimpleDebugInitializer());
    }

    private void initAssistanceController() {
        aCon.lCon = lCon;
        aCon.msg = msg;
        aCon.rCon = railCon;
        aCon.stCon = stCon;
        aCon.sCon = sCon;
        aCon.init();
        init(aCon);
    }

    private void initCompanyController() {
        cCon.sCon = sCon;
        init(cCon);
    }

    private void initDigestCalculator() {
        calc.prop = prop;
    }

    private void initEncryptConverter() {
    }

    private void initHumanController() {
        hCon.rCon = rCon;
        hCon.searcher = searcher;
        hCon.stCon = stCon;
        hCon.tCon = tCon;
        init(hCon);
        initLock(hCon);
    }

    private void initLineController() {
        lCon.rCon = railCon;
        lCon.sCon = sCon;
        lCon.stCon = stCon;
        lCon.tCon = tCon;
        init(lCon);
        initLock(lCon);
    }

    private void initLineRouteSearcher() {
        lSearcher.stCon = stCon;
        init(lSearcher);
    }

    private void initOAuthController() {
        oCon.calculator = calc;
        init(oCon);
    }

    private void initPlayController() {
        pCon.calculator = calc;
        pCon.oCon = oCon;
        init(pCon);
    }

    private void initRailController() {
        railCon.stCon = stCon;
        init(railCon);
    }

    private void initResidenceController() {
        rCon.cCon = cCon;
        rCon.hCon = hCon;
        rCon.sCon = sCon;
        rCon.searcher = searcher;
        init(rCon);
        initLock(rCon);
    }

    private void initRouteSearcher() {
        searcher.cCon = cCon;
        searcher.hCon = hCon;
        searcher.rCon = rCon;
        searcher.stCon = stCon;
        searcher.sCon = sCon;
        searcher.lock = new ReentrantReadWriteLock().writeLock();
        searcher.init();
        init(searcher);
    }

    private void initStationController() {
        stCon.sCon = sCon;
        stCon.rCon = railCon;
        stCon.lCon = lCon;
        stCon.hCon = hCon;
        stCon.init();
        init(stCon);
        initLock(stCon);
    }

    private void initStepForHumanController() {
        sCon.executorService = service;
        sCon.lSearcher = lSearcher;
        sCon.searcher = searcher;
        sCon.stCon = stCon;
        sCon.rCon = rCon;
        init(sCon);
    }

    private void initTrainController() {
        tCon.hCon = hCon;
        tCon.lCon = lCon;
        tCon.searcher = searcher;
        tCon.stCon = stCon;
        init(tCon);
        initLock(tCon);
    }

    private void initGameMaster() {
        gm.init(em, debug, hCon, lCon, prop, rCon, searcher, stCon, tCon, cCon);
    }

    private void initDebugInitializer() {
        debug.initialize(aCon, cCon, em, hCon, lCon, oCon, pCon, rCon, railCon, stCon, tCon);
    }

    private void initLocalTableController() {
        tableCon.em = em;
    }

    private void init(AbstractController inst) {
        inst.em = em;
        inst.prop = prop;
        inst.errMsgBuilder = builder;
    }
    
    private void initLock(CachedController inst) {
        inst.init();
        inst.writeLock = spy(inst.writeLock);
        inst.readLock = spy(inst.readLock);
    }
}

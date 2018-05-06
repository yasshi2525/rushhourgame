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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.enterprise.concurrent.ManagedExecutorService;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.GameMaster;
import net.rushhourgame.LocalEntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.entity.EncryptConverter;
import static org.mockito.Mockito.mock;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class ControllerFactory {

    public static ResidenceController createResidenceController(RouteSearcher searcher) {
        ResidenceController inst = new ResidenceController();
        inst.sCon = createStepForHumanController();
        inst.cCon = createCompanyController();
        inst.hCon = createHumanController();
        inst.searcher = searcher;
        init(inst);
        return inst;
    }
    
    public static CompanyController createCompanyController() {
        CompanyController inst = new CompanyController();
        inst.sCon = createStepForHumanController();
        init(inst);
        return inst;
    }
    
    public static RailController createRailController() {
        RailController inst = new RailController();
        init(inst);
        return inst;
    }
    
    public static StationController createStationController() {
        StationController inst = new StationController();
        inst.sCon = createStepForHumanController();
        init(inst);
        return inst;
    }
    
    public static LineController createLineController() {
        LineController inst = new LineController();
        inst.rCon = createRailController();
        inst.sCon = createStepForHumanController();
        init(inst);
        return inst;
    }

    public static OAuthController createOAuthController() {
        OAuthController inst = new OAuthController();
        inst.calculator = createDigestCalculator();
        init(inst);
        return inst;
    }

    public static PlayerController createPlayController() {
        PlayerController inst = new PlayerController();
        inst.calculator = createDigestCalculator();
        init(inst);
        inst.oCon = createOAuthController();
        return inst;
    }

    public static LocalTableController createLocalTableController() {
        LocalTableController inst = new LocalTableController();
        inst.em = LocalEntityManager.createEntityManager();
        return inst;
    }

    public static DigestCalculator createDigestCalculator() {
        DigestCalculator inst = new DigestCalculator();
        inst.prop = RushHourProperties.getInstance();
        return inst;
    }
    
    public static EncryptConverter createEncryptConverter() {
        EncryptConverter inst = new EncryptConverter();
        return inst;
    }
    
    public static StepForHumanController createStepForHumanController() {
        StepForHumanController inst = new StepForHumanController();
        inst.lSearcher = createLineRouteSearcher();
        RouteSearcher searcher = new RouteSearcher(); // 循環防止
        searcher.lock = new ReentrantReadWriteLock().writeLock();
        inst.searcher = searcher;
        init(inst);
        return inst;
    } 
    
    public static RouteSearcher createRouteSearcher(GameMaster gm) {
        RouteSearcher inst = new RouteSearcher();
        inst.gm = gm;
        inst.cCon = createCompanyController();
        inst.rCon = createResidenceController(inst);
        inst.stCon = createStationController();
        inst.sCon = createStepForHumanController();
        inst.lock = new ReentrantReadWriteLock().writeLock();
        init(inst);
        return inst;
    }
    
    public static LineRouteSearcher createLineRouteSearcher() {
        LineRouteSearcher inst = new LineRouteSearcher();
        init(inst);
        return inst;
    }
    
    public static AssistanceController createAssistanceController() {
        AssistanceController inst = new AssistanceController();
        inst.lCon = createLineController();
        inst.msg = RushHourResourceBundle.getInstance();
        inst.rCon = createRailController();
        inst.stCon = createStationController();
        inst.sCon = createStepForHumanController();
        init(inst);
        
        return inst;
    }
    
    public static TrainController createTrainController() {
        TrainController inst = new TrainController();
        init(inst);
        return inst;
    }
    
    public static HumanController createHumanController() {
        HumanController inst = new HumanController();
        init(inst);
        return inst;
    }

    protected static void init(AbstractController inst) {
        inst.em = LocalEntityManager.createEntityManager();
        inst.prop = RushHourProperties.getInstance();
        inst.errMsgBuilder = ErrorMessageBuilder.getInstance();
    }
}

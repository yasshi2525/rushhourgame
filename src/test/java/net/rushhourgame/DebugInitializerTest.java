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
package net.rushhourgame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedExecutors;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.DigestCalculator;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.LocalTableController;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StepForHumanController;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class DebugInitializerTest {

    protected final static EntityManager EM = LocalEntityManager.createEntityManager();
    protected final static LocalTableController TCON = ControllerFactory.createLocalTableController();
    protected final static DigestCalculator CALCULATOR = ControllerFactory.createDigestCalculator();
    protected final static RushHourProperties PROP = RushHourProperties.getInstance();
    protected final static PlayerController PCON = ControllerFactory.createPlayController();
    protected final static OAuthController OCON = ControllerFactory.createOAuthController();
    protected final static CompanyController CCON = ControllerFactory.createCompanyController();
    protected final static ResidenceController RCON = ControllerFactory.createResidenceController();
    protected final static RailController RAILCON = ControllerFactory.createRailController();
    protected final static StationController STCON = ControllerFactory.createStationController();
    protected final static LineController LCON = ControllerFactory.createLineController();
    protected final static StepForHumanController SCON = ControllerFactory.createStepForHumanController();
    protected final static RouteSearcher SEARCHER = ControllerFactory.createRouteSearcher();
    
    protected static ValidatorFactory validatorFactory;
    protected static ExecutableValidator validatorForExecutables;
    
    protected DebugInitializer inst;
    protected ExecutorService executor;
    
    @BeforeClass
    public static void setUpClass() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validatorForExecutables = validatorFactory.getValidator().forExecutables();
    }
    
    @Before
    public void setUp() {
        executor = Executors.newSingleThreadExecutor();
        inst = new DebugInitializer();
        
        inst.executorService = mock(ManagedExecutorService.class);
        //inst.searcher = SEARCHER;
        inst.em = EM;
        
        inst.rCon = RCON;
        inst.cCon = CCON;
        inst.oCon = OCON;
        inst.pCon = PCON;
        inst.railCon = RAILCON;
        inst.stCon = STCON;
        inst.lCon = LCON;
        
        EM.getTransaction().begin();
    }

    @After
    public void tearDown() {
        EM.getTransaction().rollback();
        
        executor.shutdown();
    }
    
    @AfterClass
    public static void tearDownClass() {
        validatorFactory.close();
    }
    

    @Test
    public void testInit() throws RushHourException {
        inst.init();
    }
}

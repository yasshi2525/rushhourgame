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

import java.util.Locale;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ExecutableValidator;
import net.rushhourgame.DebugInitializer;
import net.rushhourgame.GameMaster;
import net.rushhourgame.LocalEntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.SimpleGameMaster;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractControllerTest {

    protected final static SimpleGameMaster GM = new SimpleGameMaster();
    protected final static EntityManager EM = LocalEntityManager.createEntityManager();
    protected final static LocalTableController TCON = ControllerFactory.createLocalTableController();
    protected final static DigestCalculator CALCULATOR = ControllerFactory.createDigestCalculator();
    protected final static RushHourProperties PROP = RushHourProperties.getInstance();
    protected final static PlayerController PCON = ControllerFactory.createPlayController();
    protected final static OAuthController OCON = ControllerFactory.createOAuthController();
    protected final static CompanyController CCON = ControllerFactory.createCompanyController();
    protected final static RailController RAILCON = ControllerFactory.createRailController();
    protected final static StationController STCON = ControllerFactory.createStationController();
    protected final static LineController LCON = ControllerFactory.createLineController();
    protected final static StepForHumanController SCON = ControllerFactory.createStepForHumanController();
    protected final static AssistanceController ACON = ControllerFactory.createAssistanceController();
    protected final static HumanController HCON = ControllerFactory.createHumanController();
    protected final static TrainController TRAINCON = ControllerFactory.createTrainController();
    protected static RouteSearcher SEARCHER;
    protected static ResidenceController RCON;
    
    protected static ValidatorFactory validatorFactory;
    protected static ExecutableValidator validatorForExecutables;
    
    @BeforeClass
    public static void setUpClass() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validatorForExecutables = validatorFactory.getValidator().forExecutables();
        SEARCHER = ControllerFactory.createRouteSearcher(GM);
        RCON = ControllerFactory.createResidenceController(SEARCHER);
        GM.init(EM, mock(DebugInitializer.class), HCON, PROP, RCON, SEARCHER, STCON, TRAINCON);
    }
    
    @Before
    public void setUp() {
        EM.getTransaction().begin();
    }

    @After
    public void tearDown() {
        EM.getTransaction().rollback();
    }
    
    @AfterClass
    public static void tearDownClass() {
        validatorFactory.close();
    }
    
    protected static Player createPlayer() throws RushHourException{
        return PCON.upsertPlayer("_player", "_player", "_player", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
    }
    
    protected static Player createOther() throws RushHourException{
        return PCON.upsertPlayer("_other", "_other", "_other", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
    }
    
    protected static Station createStation() throws RushHourException {
        Player owner = createPlayer();
        RailNode ndoe = RAILCON.create(owner, new SimplePoint(0, 0));
        return STCON.create(owner, ndoe, "_test");
    }
    
    protected static <U, T> void assertViolatedAnnotationTypeIs(Class<U> annotation, Set<ConstraintViolation<T>> violations) {
        assertEquals(1, violations.size() );
        assertEquals(annotation, 
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }
    
    protected static <T> void assertViolatedValueIs(Object violatedValue, Set<ConstraintViolation<T>> violations) {
        assertEquals(1, violations.size() );
        assertEquals(violatedValue, violations.iterator().next().getInvalidValue());
    }
    
}

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
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.entity.EncryptConverter;
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
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractControllerTest {
    protected ControllerFactory factory;

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

    protected LocalTableController tableCon;

    protected EntityManager em;
    protected static RushHourProperties PROP = RushHourProperties.getInstance();
    protected static RushHourResourceBundle MSG = RushHourResourceBundle.getInstance();
    protected static ErrorMessageBuilder BUILDER = ErrorMessageBuilder.getInstance();

    protected static ValidatorFactory validatorFactory;
    protected static ExecutableValidator validatorForExecutables;

    @BeforeClass
    public static void setUpClass() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validatorForExecutables = validatorFactory.getValidator().forExecutables();
    }

    @Before
    public void setUp() {
        factory = new ControllerFactory();
        instantiate();
        
        factory.begin();
        
        lCon.synchronizeDatabase();
        rCon.synchronizeDatabase();
        stCon.synchronizeDatabase();
        hCon.synchronizeDatabase();
        tCon.synchronizeDatabase();
    }

    @After
    public void tearDown() {
        factory.rollback();
    }

    @AfterClass
    public static void tearDownClass() {
        validatorFactory.close();
    }

    protected Player createPlayer() throws RushHourException {
        return pCon.upsertPlayer("_player", "_player", "_player", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
    }

    protected Player createOther() throws RushHourException {
        return pCon.upsertPlayer("_other", "_other", "_other", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
    }

    protected Station createStation() throws RushHourException {
        Player owner = createPlayer();
        RailNode ndoe = railCon.create(owner, new SimplePoint(0, 0));
        return stCon.create(owner, ndoe, "_test");
    }

    protected static <U, T> void assertViolatedAnnotationTypeIs(Class<U> annotation, Set<ConstraintViolation<T>> violations) {
        assertEquals(1, violations.size());
        assertEquals(annotation,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    protected static <T> void assertViolatedValueIs(Object violatedValue, Set<ConstraintViolation<T>> violations) {
        assertEquals(1, violations.size());
        assertEquals(violatedValue, violations.iterator().next().getInvalidValue());
    }

    protected void instantiate() {
        aCon = factory.getAssistanceController();
        cCon = factory.getCompanyController();
        calc = factory.getDigestCalculator();
        converter = factory.getEncryptConverter();
        hCon = factory.getHumanController();
        lCon = factory.getLineController();
        lSearcher = factory.getLineRouteSearcher();
        oCon = factory.getOAuthController();
        pCon = factory.getPlayerController();
        rCon = factory.getResidenceController();
        railCon = factory.getRailController();
        sCon = factory.getStepForHumanController();
        searcher = factory.getRouteSearcher();
        stCon = factory.getStationController();
        tCon = factory.getTrainController();
        tableCon = factory.getLocalTableController();
        em = factory.getEntityManager();
    }
}

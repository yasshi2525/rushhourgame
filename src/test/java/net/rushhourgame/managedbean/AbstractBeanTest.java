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
package net.rushhourgame.managedbean;

import java.util.Locale;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import net.rushhourgame.DebugInitializer;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.GameMaster;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.DigestCalculator;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.LineRouteSearcher;
import net.rushhourgame.controller.LocalTableController;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StepForHumanController;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.entity.EncryptConverter;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.primefaces.PrimeFaces;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractBeanTest {

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
    
    protected GameMaster gm;
    protected DebugInitializer debug;
    
    protected EntityManager em;
    protected static RushHourProperties PROP = RushHourProperties.getInstance();
    protected static RushHourResourceBundle MSG = RushHourResourceBundle.getInstance();
    protected static ErrorMessageBuilder BUILDER = ErrorMessageBuilder.getInstance();

    @Mock
    protected FacesContext facesContext;

    @Mock
    protected ExternalContext externalContext;

    @Mock
    protected PrimeFaces primeface;

    @Mock
    protected PrimeFaces.Dialog dialog;

    @Mock
    protected RushHourSession session;

    @Mock
    protected RushHourResourceBundle msg;

    @Before
    public void setUp() {
        factory = new ControllerFactory();
        instantiate();

        factory.begin();
        lCon.synchronizeDatabase();
        rCon.synchronizeDatabase();
        stCon.synchronizeDatabase();
        tCon.synchronizeDatabase();
        hCon.synchronizeDatabase();
    }

    @After
    public void tearDown() {
        factory.rollback();
    }

    protected Player createPlayer() throws RushHourException {
        return pCon.upsertPlayer("_player", "_player", "_player", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
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
        gm = factory.getGameMaster();
        debug = factory.getDebugInitializer();
    }
}

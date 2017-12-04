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
import net.rushhourgame.LocalEntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.DigestCalculator;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.LocalTableController;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StepForHumanController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractBeanTest {

    protected final static EntityManager EM = LocalEntityManager.createEntityManager();
    protected final static LocalTableController TCON = ControllerFactory.createLocalTableController();
    protected final static PlayerController PCON = ControllerFactory.createPlayController();
    protected final static OAuthController OCON = ControllerFactory.createOAuthController();
    protected final static RailController RAILCON = ControllerFactory.createRailController();
    protected final static DigestCalculator CALCULATOR = ControllerFactory.createDigestCalculator();
    protected final static CompanyController CCON = ControllerFactory.createCompanyController();
    protected final static ResidenceController RCON = ControllerFactory.createResidenceController();
    protected final static StationController STCON = ControllerFactory.createStationController();
    protected final static LineController LCON = ControllerFactory.createLineController();
    protected final static StepForHumanController SCON = ControllerFactory.createStepForHumanController();
    protected final static RushHourProperties PROP = RushHourProperties.getInstance();
    
    @Mock
    protected FacesContext facesContext;
    
    @Mock
    protected ExternalContext externalContext;
    
    @Mock
    protected RushHourSession session;
    
    @Mock
    protected RushHourResourceBundle msg;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
        EM.getTransaction().begin();
    }
    
    @After
    public void tearDown() {
        EM.getTransaction().rollback();
    }
    
    protected static Player createPlayer() throws RushHourException{
        return PCON.upsertPlayer("_player", "_player", "_player", SignInType.LOCAL, new SimpleUserData(), Locale.getDefault());
    }
}

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

import javax.persistence.EntityManager;
import net.rushhourgame.LocalEntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Station;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class AbstractControllerTest {

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
    protected final static StepForHumanController SCON = ControllerFactory.createStepForHumanController();
    protected final static RouteSearcher SEARCHER = ControllerFactory.createRouteSearcher();

    @Before
    public void setUp() {
        EM.getTransaction().begin();
    }

    @After
    public void tearDown() {
        EM.getTransaction().commit();
        TCON.clean();
    }
    
    protected static Player createPlayer() throws RushHourException{
        OCON.createOAuthBean("_player", "_player_sec");
        return PCON.createPlayer("_player", "_player", "_player", new SimpleUserData());
    }
    
    protected static Player createOther() throws RushHourException{
        OCON.createOAuthBean("_other", "_other_sec");
        return PCON.createPlayer("_other", "_other", "_other", new SimpleUserData());
    }
    
    protected static Station createStation() throws RushHourException {
        Player owner = createPlayer();
        RailNode ndoe = RAILCON.create(owner, 0, 0);
        return STCON.create(owner, ndoe, "_test");
    }
}

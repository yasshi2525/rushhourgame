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
import net.rushhourgame.entity.RoleType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class AbstractControllerTest {

    protected static EntityManager em = LocalEntityManager.createEntityManager();
    protected static LocalTableController tCon = ControllerFactory.createLocalTableController();
    protected static DigestCalculator calculator = ControllerFactory.createDigestCalculator();
    protected static RushHourProperties prop = RushHourProperties.getInstance();
    protected static PlayerController pCon = ControllerFactory.createPlayController();
    protected static OAuthController oCon = ControllerFactory.createOAuthController();
    protected static AbsorberController aCon = ControllerFactory.createAbsorberController();
    protected static GameMasterController gCon = ControllerFactory.createGameMasterController();
    protected static NodeController nCon = ControllerFactory.createNodeController();
    protected static RoutingInfoController rCon = ControllerFactory.createRoutingInfoController();
    protected static LinkController lCon = ControllerFactory.createLinkController();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        em.getTransaction().begin();
    }

    @After
    public void tearDown() {
        em.getTransaction().commit();
        tCon.clean();
    }
    
    protected static Player createPlayer() throws RushHourException{
        oCon.createOAuthBean("_player", "_player_sec");
        return pCon.createPlayer("_player", "_player", "_player", new SimpleUserData());
    }
    
    protected static Player createAdmin() throws RushHourException{
        oCon.createOAuthBean("_admin", "_admin_sec");
        Player admin = pCon.createPlayer("_admin", "_admin", "_admin", new SimpleUserData());
        admin.getRoles().add(RoleType.ADMINISTRATOR);
        
        return admin;
    }
}

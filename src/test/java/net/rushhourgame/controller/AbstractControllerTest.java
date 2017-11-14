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

import java.util.List;
import javax.persistence.EntityManager;
import net.rushhourgame.LocalEntityManager;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
    protected final static StationController STCON = ControllerFactory.createStationController();
    protected final static StepForHumanController SCON = ControllerFactory.createStepForHumanController();
    protected final static RouteSearcher SEARCHER = ControllerFactory.createRouteSearcher();
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

    /**
     * JPAの挙動をたしかめるためのテスト.
     * persistしたインスタンスと、JPQLで取得したインスタンスは同一
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrence() throws RushHourException {
        Company original = CCON.create(1, 1);
        EM.flush();
        Company fromJPQL = TCON.findAll("Company", Company.class).get(0);
        
        assertEquals(original, fromJPQL);
    }
    
    /**
     * JPAの挙動をたしかめるためのテスト.
     * JPQLを複数回実行しても、同じインスタンスが返ってくる
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrenceJPQL() throws RushHourException {
        Company original = CCON.create(1, 1);
        EM.flush();
        Company fromJPQL1 = TCON.findAll("Company", Company.class).get(0);
        Company fromJPQL2 = TCON.findAll("Company", Company.class).get(0);
        
        assertEquals(fromJPQL1, fromJPQL2);
    }
    
    /**
     * JPAの挙動をたしかめるためのテスト.
     * 関連で取得したインスタンスが他のJPQLで取得したインスタンスと同じものである
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrenceJPQLrelation() throws RushHourException {
        Company c = CCON.create(1, 1);
        Residence r = RCON.create(2, 2);
        EM.flush();
        Company fromCompanyJPQL = TCON.findAll("Company", Company.class).get(0);
        
        RelayPointForHuman fromEdgeJPQL = TCON.findAll("StepForHumanDirectly", StepForHumanDirectly.class).get(0).getTo();
        assertEquals(fromCompanyJPQL, fromEdgeJPQL);
    }
}

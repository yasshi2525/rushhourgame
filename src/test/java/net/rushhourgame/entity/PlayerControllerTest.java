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
package net.rushhourgame.entity;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.entity.OAuthController;
import net.rushhourgame.entity.PlayerController;
import javax.persistence.EntityManager;
import net.rushhourgame.RushHourProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class PlayerControllerTest {

    protected static EntityManager em;
    protected static LocalTableController controller;
    protected static DigestCalculator calculator;
    protected PlayerController inst;
    protected static OAuthController oAuthController;
    protected static final String TEST_USER_ID = "test_user_id_001";
    protected static final String TEST_USER_PLAIN_ACCESS_TOKEN = "access_token_001";
    protected static final String TEST_USER_DISPLAY_NAME = "test_user1";
    protected static final String TEST_USER2_ID = "test_user_id_002";
    protected static final String TEST_USER2_PLAIN_ACCESS_TOKEN = "access_token_002";
    protected static final String TEST_USER2_DISPLAY_NAME = "test_user2";
    protected static final String UNEXIST_USER_ID = "unexist_user_id_999";
    protected static final String UNEXIST_ACCESS_TOKEN = "unexist_access_token_999";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        em = LocalTableController.lookupEntityManager();
        controller = new LocalTableController(em);
        controller.clean();
        calculator = new DigestCalculator();
        calculator.prop = RushHourProperties.getInstance();
        oAuthController = new OAuthController();
        oAuthController.em = em;
        oAuthController.digestCalculater = calculator;
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        inst = new PlayerController();
        inst.em = em;
        inst.oCon = oAuthController;
        inst.calculator = calculator;
        em.getTransaction().begin();
    }

    @After
    public void tearDown() {
        em.getTransaction().commit();
        controller.clean();
    }

    @Test
    public void testCreatePlayer() throws RushHourException {
        OAuth oAuth = oAuthController.createOAuthBean("foo", "foosec");
        System.out.println("requestToken(digest )=" + oAuth.getId());
        System.out.println("requestToken(encrypt)=" + oAuth.getRequestToken());
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);

        assertNotNull(created.getToken());
        assertNotEquals(TEST_USER_PLAIN_ACCESS_TOKEN, created.getToken());
        assertEquals(TEST_USER_DISPLAY_NAME, created.getDisplayName());
        assertEquals(null, created.getIcon());
        assertNotNull(created.getId());
        assertNotNull(created.getOauth());
        assertEquals(1, controller.findPlayers().size());
    }

    @Test
    public void testCreateNoOAuthPlayer() throws RushHourException {
        exception.expect(NullPointerException.class);
        inst.createPlayer(null, TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
    }

    @Test
    public void testCreateSameRequestToken() {
        try {
            OAuth oAuth = oAuthController.createOAuthBean("foo", "foosec");
            Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
            Player p2 = inst.createPlayer("foo", TEST_USER2_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, TEST_USER2_DISPLAY_NAME);
        } catch (RushHourException ex) {
            fail();
        }

    }

    @Test
    public void testCreateSameUserID() {
        Player createPlayer = null;
        try {
            oAuthController.createOAuthBean("foo", "foosec");
            createPlayer = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);

            oAuthController.createOAuthBean("bar", "barsec");
            inst.createPlayer("bar", TEST_USER_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, TEST_USER2_DISPLAY_NAME);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_USER_ID, ex.getErrMsg().getDetailId());
            assertTrue(ex.getMessage().startsWith("User Id is already registered : "));
        }
    }

    @Test
    public void testCreateSameAccessToken() {
        Player createPlayer = null;
        try {
            oAuthController.createOAuthBean("foo", "foosec");
            createPlayer = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
            oAuthController.createOAuthBean("bar", "barsec");
            inst.createPlayer("bar", TEST_USER2_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER2_DISPLAY_NAME);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_ACCESS_TOKEN,
                    "User accessToken is already registered : " + createPlayer.getToken(), ex.getMessage());
        }
    }

    @Test
    public void testReLogin() {
        try {
            OAuth oldOAuth = oAuthController.createOAuthBean("foo", "foosec");
            Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
            assertEquals(oldOAuth, p1.getOauth());
            OAuth newOAuth = oAuthController.createOAuthBean("bar", "barsec");
            inst.updateToken(p1, "bar", "new access token");
            assertNotEquals(oldOAuth, p1.getOauth());
            assertEquals(newOAuth, p1.getOauth());
        } catch (RushHourException ex) {
            fail();
        }
    }

    /**
     * 違うユーザが違うユーザのtokenにしようしたとき
     */
    @Test
    public void testUpdateSameTokenByDifferentPlayer() {
        Player p1 = null;
        try {
            oAuthController.createOAuthBean("foo", "foosec");
            p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
            oAuthController.createOAuthBean("bar", "barsec");
            Player p2 = inst.createPlayer("bar", TEST_USER2_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, TEST_USER2_DISPLAY_NAME);
            inst.updateToken(p2, "bar", TEST_USER_PLAIN_ACCESS_TOKEN);
            fail();
        } catch (RushHourException ex) {
            assertEquals(ACCOUNT_FAIL_UPDATE_ACCESS_TOKEN, ex.getErrMsg().getDetailId());
            assertEquals("accessToken is already existed : " + p1.getToken(), ex.getMessage());
        }
    }

    @Test
    public void testUpdateSameTokenBySampePlayer() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
        oAuthController.createOAuthBean("bar", "barsec");
        inst.updateToken(p1, "bar", TEST_USER_PLAIN_ACCESS_TOKEN);
    }

    @Test
    public void testExistsUserId() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
        assertTrue(inst.existsUserId(created.getUserId()));
        assertFalse(inst.existsUserId(UNEXIST_USER_ID));
    }

    @Test
    public void testIsValidAccessToken() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
        assertTrue(inst.isValidToken(created.getToken()));
        assertFalse(inst.isValidToken(UNEXIST_ACCESS_TOKEN));
    }

    @Test
    public void testFindByUserId() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);

        assertEquals(created.getId(), inst.findByUserId(created.getUserId()).getId());
        assertNull(inst.findByUserId(UNEXIST_USER_ID));
    }

    @Test
    public void testFindByAccessToken() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);

        assertEquals(created.getId(), inst.findByToken(created.getToken()).getId());
        assertNull(inst.findByToken(UNEXIST_ACCESS_TOKEN));
    }

    @Test
    public void testClearAccessToken() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        String accessToken = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN,
                TEST_USER_DISPLAY_NAME).getToken();
        assertTrue(inst.isValidToken(accessToken));
        inst.clearToken(accessToken);
        assertFalse(inst.isValidToken(accessToken));

        inst.clearToken(UNEXIST_ACCESS_TOKEN);
    }

    @Test
    public void testDelete() throws RushHourException {
        oAuthController.createOAuthBean("foo", "foosec");
        Player player = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, TEST_USER_DISPLAY_NAME);
        //oAuth.setPlayer(player);
        //em.remove(oAuth);
        em.remove(player);
        //assertEquals(0, controller.findOAuths().size());
        assertEquals(0, controller.findPlayers().size());
    }
}

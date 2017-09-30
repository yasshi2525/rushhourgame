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

import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RoleType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.UserData;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class PlayerControllerTest extends AbstractControllerTest {

    protected PlayerController inst;
    protected final static UserData USERDATA1 = new SimpleUserData();
    protected final static UserData USERDATA2 = new SimpleUserData();
    protected final static UserData USERDATA999 = new SimpleUserData();

    protected static final String TEST_USER_ID = "test_user_id_001";
    protected static final String TEST_USER_PLAIN_ACCESS_TOKEN = "access_token_001";
    protected static final String TEST_USER2_ID = "test_user_id_002";
    protected static final String TEST_USER2_PLAIN_ACCESS_TOKEN = "access_token_002";
    protected static final String UNEXIST_USER_ID = "unexist_user_id_999";
    protected static final String UNEXIST_ACCESS_TOKEN = "unexist_access_token_999";

    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createPlayController();
    }

    @Test
    public void testCreatePlayer() throws RushHourException {
        OAuth oAuth = OCON.createOAuthBean("foo", "foosec");
        System.out.println("requestToken(digest )=" + oAuth.getId());
        System.out.println("requestToken(encrypt)=" + oAuth.getRequestToken());
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);

        assertNotNull(created.getToken());
        assertNotEquals(TEST_USER_PLAIN_ACCESS_TOKEN, created.getToken());
        assertNotNull(created.getInfo());
        assertNotNull(created.getId());
        assertNotNull(created.getOauth());
        assertEquals(1, TCON.findPlayers().size());
    }

    @Test
    public void testCreateNoOAuthPlayer() {
        try {
            inst.createPlayer(null, TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_REQ_TOKEN, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testCreateSameRequestToken() {
        try {
            OCON.createOAuthBean("foo", "foosec");
            inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
            inst.createPlayer("foo", TEST_USER2_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, USERDATA2);
        } catch (RushHourException ex) {
            fail();
        }

    }

    @Test
    public void testCreateSameUserID() {
        try {
            OCON.createOAuthBean("foo", "foosec");
            inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);

            OCON.createOAuthBean("bar", "barsec");
            inst.createPlayer("bar", TEST_USER_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, USERDATA2);
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
            OCON.createOAuthBean("foo", "foosec");
            createPlayer = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
            OCON.createOAuthBean("bar", "barsec");
            inst.createPlayer("bar", TEST_USER2_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA2);
            fail();
        } catch (RushHourException ex) {
            if (createPlayer != null) {
                assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_DUPLICATE_ACCESS_TOKEN,
                        "User accessToken is already registered : " + createPlayer.getToken(), ex.getMessage());
            } else {
                fail();
            }
        }
    }

    @Test
    public void testReLogin() {
        try {
            OAuth oldOAuth = OCON.createOAuthBean("foo", "foosec");
            Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
            assertEquals(oldOAuth, p1.getOauth());
            OAuth newOAuth = OCON.createOAuthBean("bar", "barsec");
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
            OCON.createOAuthBean("foo", "foosec");
            p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
            OCON.createOAuthBean("bar", "barsec");
            Player p2 = inst.createPlayer("bar", TEST_USER2_ID, TEST_USER2_PLAIN_ACCESS_TOKEN, USERDATA2);
            inst.updateToken(p2, "bar", TEST_USER_PLAIN_ACCESS_TOKEN);
            fail();
        } catch (RushHourException ex) {
            assertEquals(ACCOUNT_FAIL_UPDATE_ACCESS_TOKEN, ex.getErrMsg().getDetailId());
            if (p1 != null) {
                assertEquals("token is already existed : " + p1.getToken(), ex.getMessage());
            } else {
                fail();
            }
        }
    }

    @Test
    public void testUpdateSameTokenBySampePlayer() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        OCON.createOAuthBean("bar", "barsec");
        inst.updateToken(p1, "bar", TEST_USER_PLAIN_ACCESS_TOKEN);
    }

    @Test
    public void testUpdateNullAccessToken() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player p1 = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        OCON.createOAuthBean("bar", "barsec");
        try {
            inst.updateToken(p1, "bar", null);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_ACCESS_TOKEN_INVALID_ACCESS_TOKEN, ex.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExistsUserId() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        assertTrue(inst.existsUserId(created.getUserId()));
        assertFalse(inst.existsUserId(UNEXIST_USER_ID));
    }

    @Test
    public void testIsValidAccessToken() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        assertTrue(inst.isValidToken(created.getToken()));
        assertFalse(inst.isValidToken(UNEXIST_ACCESS_TOKEN));
    }

    @Test
    public void testFindByUserId() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);

        assertEquals(created.getId(), inst.findByUserId(created.getUserId()).getId());
        assertNull(inst.findByUserId(UNEXIST_USER_ID));
    }

    @Test
    public void testFindByAccessToken() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player created = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);

        assertEquals(created.getId(), inst.findByToken(created.getToken()).getId());
        assertNull(inst.findByToken(UNEXIST_ACCESS_TOKEN));
    }

    @Test
    public void testClearAccessToken() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        String accessToken = inst
                .createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1)
                .getToken();
        assertTrue(inst.isValidToken(accessToken));
        inst.clearToken(accessToken);
        assertFalse(inst.isValidToken(accessToken));

        inst.clearToken(UNEXIST_ACCESS_TOKEN);
    }

    @Test
    public void testClearAccessTokenMultiPlayer() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player user1 = inst.createPlayer("foo", "user1", "user1_at", USERDATA1);
        OCON.createOAuthBean("bar", "barsec");
        Player user2 = inst.createPlayer("bar", "user2", "user2_at", USERDATA2);

        inst.clearToken(user1.getToken());
        inst.clearToken(user2.getToken());
    }

    @Test
    public void testDelete() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player player = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        EM.remove(player);
        assertEquals(0, TCON.findPlayers().size());
    }

    @Test
    public void testRole() throws RushHourException {
        OCON.createOAuthBean("foo", "foosec");
        Player player = inst.createPlayer("foo", TEST_USER_ID, TEST_USER_PLAIN_ACCESS_TOKEN, USERDATA1);
        assertTrue(player.getRoles().contains(RoleType.PLAYER));
    }
}

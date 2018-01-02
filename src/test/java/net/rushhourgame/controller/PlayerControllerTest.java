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

import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.entity.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.controller.ControllerFactory.createDigestCalculator;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.UserData;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PlayerControllerTest extends AbstractControllerTest {
    @Spy
    protected PlayerController inst;
    protected final static UserData USERDATA1 = new SimpleUserData();
    protected final static UserData USERDATA2 = new SimpleUserData();
    protected final static UserData USERDATA999 = new SimpleUserData();

    protected static final String TEST_USER_ID = "test_user_id_001";
    protected static final String TEST_USER_PLAIN_ACCESS_TOKEN = "access_token_001";
    protected static final String TEST_USER_PLAIN_ACCESS_TOKEN_SECRET = "access_token_secret_001";
    protected static final String TEST_USER2_ID = "test_user_id_002";
    protected static final String TEST_USER2_PLAIN_ACCESS_TOKEN = "access_token_002";
    protected static final String TEST_USER2_PLAIN_ACCESS_TOKEN_SECRET = "access_token_secret_002";
    protected static final String UNEXIST_USER_ID = "unexist_user_id_999";
    protected static final String UNEXIST_ACCESS_TOKEN = "unexist_access_token_999";

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.calculator = createDigestCalculator();
        inst.em = EM;
        inst.errMsgBuilder = ErrorMessageBuilder.getInstance();
        inst.oCon = OCON;
        inst.prop = PROP;
    }

    @Test
    public void testUpsertPlayerNew() throws RushHourException {
        Player created = inst.upsertPlayer(
                TEST_USER_PLAIN_ACCESS_TOKEN,
                TEST_USER_PLAIN_ACCESS_TOKEN_SECRET,
                TEST_USER_ID,
                SignInType.LOCAL,
                USERDATA1,
                Locale.CANADA);

        assertEquals(TEST_USER_PLAIN_ACCESS_TOKEN, created.getAccessToken());
        assertEquals(TEST_USER_PLAIN_ACCESS_TOKEN_SECRET, created.getAccessTokenSecret());
        assertEquals(SignInType.LOCAL, created.getSignIn());
        assertEquals(TEST_USER_ID, created.getUserId());
        assertNotNull(created.getToken());
        assertNotEquals(TEST_USER_PLAIN_ACCESS_TOKEN, created.getToken());
        assertNotNull(created.getInfo());
        assertNotNull(created.getInfo().getPlayer());
        assertEquals(USERDATA1.getColor(), created.getInfo().getColor());
        assertEquals(USERDATA1.getIconUrl(), created.getInfo().getIconUrl());
        assertEquals(Locale.CANADA, created.getInfo().getLocale());
        assertEquals(USERDATA1.getName(), created.getInfo().getName());
        assertEquals(USERDATA1.getTextColor(), created.getInfo().getTextColor());
        assertEquals(1, TCON.findAll("Player", Player.class).size());
    }

    @Test
    public void testUpsertPlayerUpdate() throws RushHourException {
        Player created = inst.upsertPlayer(
                TEST_USER_PLAIN_ACCESS_TOKEN,
                TEST_USER_PLAIN_ACCESS_TOKEN_SECRET,
                TEST_USER_ID,
                SignInType.LOCAL,
                USERDATA1,
                Locale.CANADA);

        Player updated = inst.upsertPlayer(
                TEST_USER2_PLAIN_ACCESS_TOKEN,
                TEST_USER2_PLAIN_ACCESS_TOKEN_SECRET,
                TEST_USER_ID,
                SignInType.LOCAL,
                USERDATA2,
                Locale.CHINA);

        assertEquals(created.getId(), updated.getId());
        assertEquals(created.getUserId(), updated.getUserId());
        assertEquals(created.getSignIn(), updated.getSignIn());
        assertEquals(TEST_USER2_PLAIN_ACCESS_TOKEN, updated.getAccessToken());
        assertEquals(TEST_USER2_PLAIN_ACCESS_TOKEN_SECRET, updated.getAccessTokenSecret());
        assertEquals(SignInType.LOCAL, updated.getSignIn());
        assertEquals(TEST_USER_ID, updated.getUserId());
        assertNotNull(updated.getInfo());
        assertEquals(USERDATA2.getColor(), updated.getInfo().getColor());
        assertEquals(USERDATA2.getIconUrl(), updated.getInfo().getIconUrl());
        assertEquals(Locale.CHINA, updated.getInfo().getLocale());
        assertEquals(USERDATA2.getName(), updated.getInfo().getName());
        assertEquals(USERDATA2.getTextColor(), updated.getInfo().getTextColor());
        assertEquals(1, TCON.findAll("Player", Player.class).size());
    }

    @Test
    public void testUpsertPlayerException() throws NoSuchAlgorithmException, RushHourException {
        DigestCalculator mock = mock(DigestCalculator.class);
        doThrow(NoSuchAlgorithmException.class).when(mock).calcDigest(anyString());
        doReturn(null).when(inst).findByUserId(anyString(), any(SignInType.class));
        inst.calculator = mock;
        try {
            inst.upsertPlayer(
                    TEST_USER_PLAIN_ACCESS_TOKEN,
                    TEST_USER_PLAIN_ACCESS_TOKEN_SECRET,
                    TEST_USER_ID,
                    SignInType.LOCAL,
                    USERDATA1,
                    Locale.CANADA);
            fail();
        } catch (RushHourException e) {
            assertEquals(SIGNIN_FAIL, e.getErrMsg().getTitleId());
            assertEquals(UNKNOWN_DETAIL, e.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, e.getErrMsg().getActionId());
        }
    }

    @Test
    public void testExistsUserId() throws RushHourException {
        Player created = createPlayer();
        assertTrue(inst.existsUserId(created.getUserId(), SignInType.LOCAL));
        assertFalse(inst.existsUserId(created.getUserId(), SignInType.TWITTER));
        assertFalse(inst.existsUserId(null, SignInType.LOCAL));
        assertFalse(inst.existsUserId(UNEXIST_USER_ID, SignInType.LOCAL));
    }

    @Test
    public void testIsValidToken() throws RushHourException {
        Player created = createPlayer();
        assertFalse(inst.isValidToken(null));
        assertTrue(inst.isValidToken(created.getToken()));
        assertFalse(inst.isValidToken(UNEXIST_ACCESS_TOKEN));
    }

    @Test
    public void testFindByUserId() throws RushHourException {
        Player created = createPlayer();
        Player found = inst.findByUserId(created.getUserId(), SignInType.LOCAL);

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());

        assertNull(inst.findByUserId(null, SignInType.LOCAL));
        assertNull(inst.findByUserId(created.getUserId(), SignInType.TWITTER));
        assertNull(inst.findByUserId(UNEXIST_USER_ID, SignInType.LOCAL));
    }
    
    @Test
    public void testFindByUserIdException() throws RushHourException, NoSuchAlgorithmException {
        inst.calculator = mock(DigestCalculator.class);
        doThrow(NoSuchAlgorithmException.class).when(inst.calculator).calcDigest(anyString());

        try {
            inst.findByUserId(TEST_USER_ID, SignInType.LOCAL);
            fail();
        } catch (RushHourException e) {
            assertEquals(SIGNIN_FAIL, e.getErrMsg().getTitleId());
            assertEquals(UNKNOWN_DETAIL, e.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, e.getErrMsg().getActionId());
        }
    }

    @Test
    public void testFindByToken() throws RushHourException {
        Player created = createPlayer();
        Player found = inst.findByToken(created.getToken());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());

        assertNull(inst.findByToken(null));
        assertNull(inst.findByToken("hoge"));
    }

    @Test
    public void testClearToken() throws RushHourException {
        String vaildedToken = createPlayer().getToken();
        assertTrue(inst.isValidToken(vaildedToken));
        inst.clearToken(vaildedToken);
        assertFalse(inst.isValidToken(vaildedToken));
    }
    
    @Test
    public void testClearTokenUnexisted() throws RushHourException {
        String vaildToken = createPlayer().getToken();
        assertTrue(inst.isValidToken(vaildToken));
        inst.clearToken("hoge");
        assertTrue(inst.isValidToken(vaildToken));
    }


    @Test
    public void testClearTokenMultiPlayer() throws RushHourException {
        String remained = createPlayer().getToken();
        String removed = createOther().getToken();

        inst.clearToken(removed);
        assertTrue(inst.isValidToken(remained));
    }
}

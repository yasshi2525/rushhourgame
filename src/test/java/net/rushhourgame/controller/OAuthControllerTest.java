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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.controller.ControllerFactory.createDigestCalculator;
import net.rushhourgame.entity.EncryptConverter;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.SignInType;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class OAuthControllerTest extends AbstractControllerTest{

    protected static final String TEST_REQ_TOKEN = "testAAA";
    protected static final String TEST_REQ_TOKEN_SECRET = "test_secretBBB";
    protected static final String NOT_EXIST_REQ_TOKEN = "unexistrequesttoken";
    
    @Spy
    protected OAuthController inst;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        ControllerFactory.init(inst);
        inst.calculator = createDigestCalculator();
    }

    @Test
    public void testUpsertRequestToken() throws RushHourException {
        OAuth created = inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        assertNotNull(created.getId());
        assertEquals(TEST_REQ_TOKEN, created.getRequestToken());
        assertEquals(TEST_REQ_TOKEN_SECRET, created.getRequestTokenSecret());
        assertEquals(1, TCON.findAll("OAuth", OAuth.class).size());
    }

    /**
     * すでに存在している場合updateされる
     * @throws RushHourException 例外
     */
    @Test
    public void testUpsertRequestTokenAlreadyExist() throws RushHourException, InterruptedException {
        inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        OAuth updated = inst.upsertRequestToken(TEST_REQ_TOKEN, "updated", SignInType.LOCAL);
        assertEquals(TEST_REQ_TOKEN, updated.getRequestToken());
        assertEquals("updated", updated.getRequestTokenSecret());
        assertEquals(SignInType.LOCAL, updated.getSignIn());
        assertEquals(1, TCON.findAll("OAuth", OAuth.class).size());
    }
    
    @Test
    public void testUpsertRequestTokenException() throws NoSuchAlgorithmException, RushHourException {
        inst.calculator = mock(DigestCalculator.class);
        doThrow(NoSuchAlgorithmException.class).when(inst.calculator).calcDigest(anyString());
        doReturn(null).when(inst).findByRequestToken(anyString(), any(SignInType.class));
        try {
            inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
            fail();
        } catch (RushHourException e) {
            assertEquals(SIGNIN_FAIL, e.getErrMsg().getTitleId());
            assertEquals(UNKNOWN_DETAIL, e.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, e.getErrMsg().getActionId());
        }
    }
    
    @Test
    public void testUpsertRequestTokenOtherSignIn() throws RushHourException {
        OAuth target = inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        OAuth other = inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.TWITTER);
        EM.flush();
        assertNotEquals(target.getId(), other.getId());
        assertNotEquals(target.getSignIn(), other.getSignIn());
        assertEquals(2, TCON.findAll("OAuth", OAuth.class).size());
    }

    @Test
    public void testIsRegisteredRequestToken() throws RushHourException, NoSuchAlgorithmException {
        inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        assertTrue(inst.isRegisteredRequestToken(TEST_REQ_TOKEN, SignInType.LOCAL));
        assertFalse(inst.isRegisteredRequestToken(TEST_REQ_TOKEN, SignInType.TWITTER));
        assertFalse(inst.isRegisteredRequestToken(NOT_EXIST_REQ_TOKEN, SignInType.LOCAL));
    }

    @Test
    public void testFindByRequestToken() throws RushHourException, NoSuchAlgorithmException {
        inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        
        assertEquals(TEST_REQ_TOKEN, inst.findByRequestToken(TEST_REQ_TOKEN, SignInType.LOCAL).getRequestToken());
        assertNull(inst.findByRequestToken(TEST_REQ_TOKEN, SignInType.TWITTER));
        assertNull(inst.findByRequestToken(NOT_EXIST_REQ_TOKEN, SignInType.LOCAL));
    }
    
    @Test
    public void testFindByRequestTokenException() throws Exception{
        inst.calculator = mock(DigestCalculator.class);
        doThrow(NoSuchAlgorithmException.class).when(inst.calculator).calcDigest(anyString());
        try {
            inst.findByRequestToken(TEST_REQ_TOKEN, SignInType.LOCAL);
            fail();
        } catch (RushHourException e) {
            assertEquals(SIGNIN_FAIL, e.getErrMsg().getTitleId());
            assertEquals(UNKNOWN_DETAIL, e.getErrMsg().getDetailId());
            assertEquals(SYSTEM_ERR_ACTION, e.getErrMsg().getActionId());
        }
    }
     
    @Test
    public void testPurgeOld() throws RushHourException{
        OAuth target = inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        assertEquals(1, TCON.findAll("OAuth", OAuth.class).size());
        inst.purgeOld(0);
        assertEquals(0, TCON.findAll("OAuth", OAuth.class).size());
    }
    
    @Test
    public void testPurgeOldRemain() throws RushHourException{
        inst.upsertRequestToken(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET, SignInType.LOCAL);
        assertEquals(1, TCON.findAll("OAuth", OAuth.class).size());
        inst.purgeOld(1);
        assertEquals(1, TCON.findAll("OAuth", OAuth.class).size());
    }
}

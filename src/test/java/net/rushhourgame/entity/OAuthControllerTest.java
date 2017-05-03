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
import java.sql.SQLIntegrityConstraintViolationException;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import net.rushhourgame.RushHourProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class OAuthControllerTest {

    protected static final String TEST_REQ_TOKEN = "testAAA";
    protected static final String TEST_REQ_TOKEN_SECRET = "test_secretBBB";
    protected static final String NOT_EXIST_REQ_TOKEN = "unexistrequesttoken";
    protected static LocalTableController tableController;
    protected static PlayerController playerController;
    protected OAuthController inst;
    protected static EntityManager em;
    protected static DigestCalculator calculater;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        em = LocalTableController.lookupEntityManager();
        calculater = new DigestCalculator();
        calculater.prop = RushHourProperties.getInstance();
        tableController = new LocalTableController(em);
        tableController.clean();
        playerController = new PlayerController();
        playerController.em = em;
        playerController.calculator = calculater;
    }

    @Before
    public void setUp() {
        inst = new OAuthController();
        inst.em = em;
        inst.digestCalculater = calculater;
        em.getTransaction().begin();
    }

    @After
    public void tearDown() {
        em.getTransaction().commit();
        tableController.clean();
    }

    @Test
    public void testCreateOAuthBean() throws RushHourException, NoSuchAlgorithmException {
        OAuth created = inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);
        assertNotNull(created.getId());
        assertEquals(null, created.getAccessToken());
        assertEquals(null, created.getAccessTokenSecret());
        assertEquals(TEST_REQ_TOKEN, created.getRequestToken());
        assertEquals(TEST_REQ_TOKEN_SECRET, created.getRequestTokenSecret());
        assertEquals(null, created.getOauthVerifier());
        assertEquals(1, tableController.findOAuths().size());
    }

    /**
     * すでに存在している場合createできない
     * @throws java.security.NoSuchAlgorithmException
     */
    @Test
    public void testCreateOAuthBeanAlreadyExist() throws NoSuchAlgorithmException {
        try {
            inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);
            inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);
            fail();
        } catch (RushHourException ex) {
            assertEquals(SIGNIN_FAIL_GET_REQ_TOKEN_DUPLICATE, ex.getErrMsg().getDetailId());
            assertTrue(ex.getMessage().startsWith("already exists : " + TEST_REQ_TOKEN));
        }
    }

    @Test
    public void testIsRegisteredRequestToken() throws RushHourException, NoSuchAlgorithmException {
        inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);

        assertTrue(inst.isRegisteredRequestToken(TEST_REQ_TOKEN));
        assertFalse(inst.isRegisteredRequestToken(NOT_EXIST_REQ_TOKEN));
    }

    @Test
    public void testFindByRequestToken() throws RushHourException, NoSuchAlgorithmException {
        inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);
        
        assertEquals(TEST_REQ_TOKEN, inst.findByRequestToken(TEST_REQ_TOKEN).getRequestToken());
        assertNull(inst.findByRequestToken(NOT_EXIST_REQ_TOKEN));
    }
    
    @Test
    public void testDeleteOAuth() throws RushHourException, NoSuchAlgorithmException{
        OAuth created = inst.createOAuthBean(TEST_REQ_TOKEN, TEST_REQ_TOKEN_SECRET);
        em.remove(created);
        assertEquals(0, tableController.findOAuths().size());
    }
}

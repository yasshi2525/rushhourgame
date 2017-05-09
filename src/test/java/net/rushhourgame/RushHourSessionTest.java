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
package net.rushhourgame;

import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static net.rushhourgame.RushHourSession.SESSION_NAME;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RushHourSessionTest {

    protected static RushHourSessionBean bean;

    public RushHourSessionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        bean = new RushHourSessionBean();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetLocale_Locale() {
        System.out.println("setLocale");
        Locale locale = null;
        RushHourSession instance = new RushHourSession();
        instance.setLocale(locale);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetToken_String() {
        System.out.println("setToken");
        String accessToken = "";
        RushHourSession instance = new RushHourSession();
        instance.setToken(accessToken);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetLocale() {
        System.out.println("getLocale");
        RushHourSession instance = new RushHourSession();
        Locale expResult = null;
        Locale result = instance.getLocale();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetToken() {
        System.out.println("getToken");
        RushHourSession instance = new RushHourSession();
        String expResult = "";
        String result = instance.getToken();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testHasValidBean() {
        RushHourSession inst = new RushHourSession();

        HttpSession mockedSession = mock(HttpSession.class);
        when(mockedSession.getAttribute(SESSION_NAME)).thenReturn(bean);

        inst.injectedSession = mockedSession;
        assertTrue(inst.hasValidBean());

        verify(mockedSession, times(2)).getAttribute(SESSION_NAME);
    }

    @Test
    public void testHasValidBeanWhenNull() {
        RushHourSession inst = new RushHourSession();

        HttpSession mockedSession = mock(HttpSession.class);
        when(mockedSession.getAttribute(RushHourSession.SESSION_NAME)).thenReturn(null);

        inst.injectedSession = mockedSession;
        assertFalse(inst.hasValidBean());
        
        verify(mockedSession).getAttribute(RushHourSession.SESSION_NAME);
    }

    @Test
    public void testHasValidBeanWhenOtherObject() {
        RushHourSession inst = new RushHourSession();
        
        HttpSession mockedSession = mock(HttpSession.class);
        when(mockedSession.getAttribute(RushHourSession.SESSION_NAME)).thenReturn(new Object());

        inst.injectedSession = mockedSession;
        assertFalse(inst.hasValidBean());
        
        verify(mockedSession, times(2)).getAttribute(RushHourSession.SESSION_NAME);
    }

    @Test
    public void testSetLocale_HttpSession_Locale() {
        System.out.println("setLocale");
        HttpSession session = null;
        Locale locale = null;
        RushHourSession.setLocale(session, locale);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetToken_HttpSession_String() {
        System.out.println("setToken");
        HttpSession session = null;
        String accessToken = "";
        RushHourSession.setToken(session, accessToken);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetLocale_HttpSession() {
        System.out.println("getLocale");
        HttpSession session = null;
        Locale expResult = null;
        Locale result = RushHourSession.getLocale(session);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetToken_HttpSession() {
        System.out.println("getToken");
        HttpSession session = null;
        String expResult = "";
        String result = RushHourSession.getToken(session);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testHasValidBean_HttpSession() {
        System.out.println("hasValidBean");
        HttpSession session = null;
        boolean expResult = false;
        boolean result = RushHourSession.hasValidBean(session);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testFindOrCreateBeanWhenValidBean() {
        RushHourSession inst = new RushHourSession();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(bean);
        inst.injectedSession = session;

        assertEquals(bean, inst.findOrCreateBean());
        
        verify(session, times(3)).getAttribute(SESSION_NAME);
    }

    @Test
    public void testFindOrCreateBean_HttpSession() {
        System.out.println("findOrCreateBean");
        HttpSession session = null;
        RushHourSessionBean expResult = null;
        RushHourSessionBean result = RushHourSession.findOrCreateBean(session);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}

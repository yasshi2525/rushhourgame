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
import net.rushhourgame.entity.RailNode;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RushHourSessionTest {

    private static RushHourSessionBean bean;
    private static RushHourProperties prop;
    
    @BeforeClass
    public static void setUpClass() {
        bean = new RushHourSessionBean(0);
        prop = RushHourProperties.getInstance();
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
    public void testIsValidBean_HttpSession() {
        assertFalse(RushHourSession.isValidBean(null));
        
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(null);
        
        assertFalse(RushHourSession.isValidBean(session));
        
        when(session.getAttribute(SESSION_NAME)).thenReturn(new Object());
        assertFalse(RushHourSession.isValidBean(session));
        
        when(session.getAttribute(SESSION_NAME)).thenReturn(new RushHourSessionBean(0));
        assertTrue(RushHourSession.isValidBean(session));
    }

    @Test
    public void testFindBeanWhenValidBean() {
        RushHourSession inst = new RushHourSession();
        RushHourSession spy = spy(inst);
        doReturn(true).when(spy).hasValidBean();
        
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(bean);
        spy.injectedSession = session;

        assertEquals(bean, spy.findOrCreateBean());
        
        verify(session, times(1)).getAttribute(SESSION_NAME);
    }
    
    @Test
    public void testCreateBean(){
        RushHourSession inst = new RushHourSession();
        inst.prop = prop;
        RushHourSession spy = spy(inst);
        doReturn(false).when(spy).hasValidBean();
        
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(null);
        spy.injectedSession = session;
        
        RushHourSessionBean actual = spy.findOrCreateBean();
        assertNotNull(actual);
        assertNotEquals(bean, actual);
        
        verify(spy, times(1)).hasValidBean();
        verify(session, times(1)).getAttribute(SESSION_NAME);
    }
    
    @Test
    public void testReplaceBean(){
        RushHourSession inst = new RushHourSession();
        inst.prop = prop;
        
        HttpSession session = mock(HttpSession.class);
        
        Object before = new Object();
        when(session.getAttribute(SESSION_NAME)).thenReturn(before);
        inst.injectedSession = session;
        
        RushHourSessionBean actual = inst.findOrCreateBean();
        assertNotNull(actual);
        assertNotEquals(before, actual);
        
        verify(session, times(5)).getAttribute(SESSION_NAME);
    }

    @Test
    public void testFindBeanWhenValidBeanHttpSession() {
       
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(bean);
        

        assertEquals(bean, RushHourSession.findOrCreateBean(session));
        
        verify(session, times(3)).getAttribute(SESSION_NAME);
    }
    
    @Test
    public void testCreateBeanHttpSession(){
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(SESSION_NAME)).thenReturn(null);
        
        RushHourSessionBean actual = RushHourSession.findOrCreateBean(session);
        assertNotNull(actual);
        assertNotEquals(bean, actual);
        
        verify(session, times(2)).getAttribute(SESSION_NAME);
    }
    
    @Test
    public void testReplaceBeanHttpSession(){
        HttpSession session = mock(HttpSession.class);
        
        Object before = new Object();
        when(session.getAttribute(SESSION_NAME)).thenReturn(before);
        
        RushHourSessionBean actual = RushHourSession.findOrCreateBean(session);
        assertNotNull(actual);
        assertNotEquals(before, actual);
        
        verify(session, times(5)).getAttribute(SESSION_NAME);
    }

    @Test
    public void testBean() {
        RushHourSession obj = spy(RushHourSession.class);
        doReturn(new RushHourSessionBean(0)).when(obj).findOrCreateBean();
        
        obj.setToken("hoge");
        obj.setCenterX(10);
        obj.setCenterY(10);
        obj.setLocale(Locale.ITALY);
        obj.setScale(2);
        obj.setTailNode(mock(RailNode.class));
        
        assertEquals("hoge", obj.getToken());
        assertTrue(10 == obj.getCenterX());
        assertTrue(10 == obj.getCenterY());
        assertTrue(2 == obj.getScale());
        assertEquals(Locale.ITALY, obj.getLocale());
        assertNotNull(obj.getTailNode());
    }
    
    @Test
    public void testStaticMethod() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(RushHourSession.SESSION_NAME)).thenReturn(new RushHourSessionBean(0));

        RushHourSession.setLocale(session, Locale.ITALY);
        assertEquals(Locale.ITALY, RushHourSession.getLocale(session));
    }
    
    @Test
    public void testSimpleInstance() {
        assertNotNull(RushHourSession.getSimpleSession().findOrCreateBean());
    }
}

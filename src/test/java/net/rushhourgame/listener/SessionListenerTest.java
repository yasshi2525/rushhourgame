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
package net.rushhourgame.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import net.rushhourgame.RushHourSessionBean;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.controller.PlayerController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SessionListenerTest {
    @Mock
    protected HttpSessionEvent se;
    @Mock
    protected HttpSession session;
    @Mock
    protected RushHourSessionBean bean;
    protected final static PlayerController PCON = ControllerFactory.createPlayController();
    protected SessionListener instance;
    
    public SessionListenerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        when(se.getSession()).thenReturn(session);
        
        instance = new SessionListener();
        instance.controller = spy(PCON);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSessionCreated() {
        instance.sessionCreated(se);
    }

    @Test
    public void testSessionDestroyedNull() {
        when(se.getSession().getAttribute("rushhour")).thenReturn(null);
        instance.sessionDestroyed(se);
        
        verify(instance.controller, times(0)).clearToken(any());
    }
    
    @Test
    public void testSessionDestroyedOther() {
        when(se.getSession().getAttribute("rushhour")).thenReturn(new Object());
        instance.sessionDestroyed(se);
        
        verify(instance.controller, times(0)).clearToken(any());
    }
    
    @Test
    public void testSessionDestroyedValidNullToken() {
        when(se.getSession().getAttribute("rushhour")).thenReturn(bean);
        when(bean.getToken()).thenReturn(null);
        instance.sessionDestroyed(se);
        
        verify(instance.controller, times(1)).clearToken(any());
    }
}

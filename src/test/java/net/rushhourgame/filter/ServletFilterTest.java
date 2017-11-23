/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.filter;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.RushHourSessionBean;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class ServletFilterTest {

    @Spy
    protected ServletFilter inst;

    @Mock
    protected HttpServletRequest request;
    @Mock
    protected ServletResponse response;
    @Mock
    protected FilterChain chain;
    @Mock
    protected HttpSession session;
    @Mock
    protected RushHourSessionBean bean;
    
    protected static Locale OTHER_LOCALE = Locale.ITALY;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("rushhour")).thenReturn(bean);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        doNothing().when(inst).handleLocale(request);
        doNothing().when(chain).doFilter(request, response);
        
        inst.doFilter(request, response, chain);
    }
    
    @Test
    public void testDoFilterExcepetion() throws IOException, ServletException {
        doNothing().when(inst).handleLocale(request);
        doThrow(IOException.class).when(chain).doFilter(request, response);
        
        inst.doFilter(request, response, chain);
    }
    
    @Test
    public void testHandleLocaleGetParamJp() {
        when(bean.getLocale()).thenReturn(null);
        when(request.getParameter("lang")).thenReturn("jp");
        inst.handleLocale(request);
        verify(bean, times(1)).setLocale(Locale.JAPANESE);
    }
    
    @Test
    public void testHandleLocaleGetParamEn() {
        when(bean.getLocale()).thenReturn(null);
        when(request.getParameter("lang")).thenReturn("en");
        inst.handleLocale(request);
        verify(bean, times(1)).setLocale(Locale.ENGLISH);
    }
    
    @Test
    public void testHandleLocaleGetParamOther() {
        when(bean.getLocale()).thenReturn(null);
        when(request.getParameter("lang")).thenReturn("unknown");
        inst.handleLocale(request);
        verify(request, times(3)).getParameter("lang");
        verify(bean, times(1)).setLocale(Locale.ENGLISH);
    }

    @Test
    public void testHandleLocaleAlreadyRegistered() {
        when(bean.getLocale()).thenReturn(OTHER_LOCALE);
        inst.handleLocale(request);
        verify(request, times(0)).getLocale();
    }
    
    @Test
    public void testHandleLocaleGetParamNull() {
        when(bean.getLocale()).thenReturn(null);
        when(request.getParameter("lang")).thenReturn(null);
        inst.handleLocale(request);
        verify(bean, times(1)).setLocale(null);
    }

    @Test
    public void testInit() throws ServletException {
        inst.init(null);
    }

    @Test
    public void testDestroy() {
        inst.destroy();
    }

}

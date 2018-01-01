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
package net.rushhourgame;

import java.util.Iterator;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import net.rushhourgame.exception.RushHourException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RushHourExceptionHandlerTest {
    
    @Mock
    protected ExceptionHandler wrapped;
    
    @Spy
    protected RushHourExceptionHandler inst = new RushHourExceptionHandler(wrapped);
        
    @Mock
    protected Iterable<ExceptionQueuedEvent> events;
    
    @Mock
    protected Iterator i;
    
    @Mock
    protected ExceptionQueuedEvent event;
    
    @Mock
    protected ExceptionQueuedEventContext context;
    
    @Mock
    protected Throwable t;
    
    @Mock
    protected RushHourException rhEx;
    
    @Mock
    protected FacesContext fc;
    
    @Mock
    protected ExternalContext exContext;
    
    @Mock
    protected Map<String, Object> map;
    
    @Mock
    protected Application app;
    
    @Mock
    protected NavigationHandler navHandler;
    
    @Before
    public void setUp() {
        doReturn(events).when(inst).getUnhandledExceptionQueuedEvents();
        doReturn(i).when(events).iterator();
        when(i.hasNext()).thenReturn(true).thenReturn(false);
        when(i.next()).thenReturn(event);
        doReturn(context).when(event).getSource();
        doReturn(t).when(context).getException();
        doReturn(fc).when(inst).getFacesInstance();
        doReturn(exContext).when(fc).getExternalContext();
        doReturn(map).when(exContext).getRequestMap();
        doReturn(app).when(fc).getApplication();
        doReturn(navHandler).when(app).getNavigationHandler();
    }
    
    @Test
    public void testFactory() {
        ExceptionHandlerFactory parent = mock(ExceptionHandlerFactory.class);
        RushHourExceptionHandlerFactory factory = new RushHourExceptionHandlerFactory(parent);
        
        doReturn(wrapped).when(parent).getExceptionHandler();
        
        assertNotNull(factory.getExceptionHandler());
    }

    @Test
    public void testHandle() {
        doReturn(rhEx).when(inst).extractRushHourException(any(Throwable.class));
        doReturn(wrapped).when(inst).getWrapped();
        inst.handle();
        verify(map, times(1)).put(eq("errorMsg"), eq(null));
    }
    
    @Test
    public void testHandleNull() {
        doReturn(null).when(inst).extractRushHourException(any(Throwable.class));
        doReturn(wrapped).when(inst).getWrapped();
        inst.handle();
        verify(map, times(1)).put(eq("errorMsg"), any(ErrorMessage.class));
    }

    @Test
    public void testExtractRushHourException() {
        RushHourException e = mock(RushHourException.class);
        Exception parent = new Exception(e);
        assertEquals(e, inst.extractRushHourException(parent));
    }
    
    @Test
    public void testExtractRushHourExceptionOther() {
        Exception e = new Exception(new Exception());
        assertNull(inst.extractRushHourException(e));
    }
    
    @Test
    public void testExtractRushHourExceptionDirect() {
        RushHourException e = mock(RushHourException.class);
        
        assertEquals(e, inst.extractRushHourException(e));
    }
    
    @Test
    public void testExtractRushHourExceptionNull() {
        assertNull(inst.extractRushHourException(null));
    }

    @Test
    public void testGetWrapped() {
        assertEquals(wrapped,
                new RushHourExceptionHandler(wrapped)
                        .getWrapped());
    }
    
    @Test
    public void testGetFacesInstance() {
        assertNull(new RushHourExceptionHandler(wrapped).getFacesInstance());
    }
}

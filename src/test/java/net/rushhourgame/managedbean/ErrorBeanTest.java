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
package net.rushhourgame.managedbean;

import java.util.Locale;
import java.util.Map;
import javax.faces.context.ExternalContext;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.RushHourSession;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ErrorBeanTest extends AbstractBeanTest {

    @Spy
    protected ErrorBean inst;
    
    @Mock
    protected ExternalContext context;
    
    @Mock
    protected Map<String, Object> requestMap;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.msgProps = msg;
        inst.rushHourSession = session;
        doReturn(context).when(inst).getExternalContext();
        doReturn(requestMap).when(context).getRequestMap();
    }

    @Test
    public void testInit() {
        ErrorMessage msg = new ErrorMessage();
        doReturn(msg).when(requestMap).get("error");
        inst.init();
        assertEquals(msg, inst.contents);
    }

    @Test
    public void testInitNull() {
        doReturn(null).when(requestMap).get("error");
        inst.init();
        assertNotNull(inst.contents);
    }

    @Test(expected = NullPointerException.class)
    public void testGetErrorMessage() {
        // JSF上でないので NPE が発生
        new ErrorBean().getExternalContext();
    }

    @Test
    public void testGetTitle() {
        inst.contents = new ErrorMessage();
        assertEquals(ErrorMessage.NO_CONTENTS, inst.getTitle());
    }

    @Test
    public void testGetDetail() {
        inst.contents = new ErrorMessage();
        assertEquals(ErrorMessage.NO_CONTENTS, inst.getDetail());
    }

    @Test
    public void testGetAction() {
        inst.contents = new ErrorMessage();
        assertEquals(ErrorMessage.NO_CONTENTS, inst.getAction());
    }

    @Test
    public void testGetTitleNull() {
        assertEquals("No Contents", inst.getTitle());
    }

    @Test
    public void testGetDetailNull() {
        assertEquals("No Contents", inst.getDetail());
    }

    @Test
    public void testGetActionNull() {
        assertEquals("No Contents", inst.getAction());
    }
}
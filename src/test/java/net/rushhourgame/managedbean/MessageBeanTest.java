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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MessageBeanTest extends AbstractBeanTest{
    
    protected MessageBean inst;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = new MessageBean();
        inst.session = session;
        inst.res = msg;
        doReturn(Locale.JAPANESE).when(session).getLocale();
        doReturn("_test").when(msg).get(anyString(), any(Locale.class));
    }

    @Test
    public void test() {
        assertNotNull(inst.getRushHour());
        assertNotNull(inst.getRushHourVer());
        assertNotNull(inst.getError());
        assertNotNull(inst.getDetail());
        assertNotNull(inst.getAction());
        assertNotNull(inst.getTopTitle());
        assertNotNull(inst.getTwitterSignIn());
        assertNotNull(inst.getFetchAccessToken());
        assertNotNull(inst.getWelcome());
        assertNotNull(inst.getLang());
        assertNotNull(inst.getLangEn());
        assertNotNull(inst.getLangJp());
        assertNotNull(inst.getLogOut());
        assertNotNull(inst.getRail());
        assertNotNull(inst.getStation());
        assertNotNull(inst.getRoute());
        assertNotNull(inst.getTrain());
        assertNotNull(inst.getCreate());
        assertNotNull(inst.getEdit());
        assertNotNull(inst.getRemove());
        assertNotNull(inst.getGoBack());
        assertNotNull(inst.getCancel());
        assertNotNull(inst.getCreateRail());
        assertNotNull(inst.getExtendRail());
        assertNotNull(inst.getRemoveRail());
        assertNotNull(inst.getRemoveStation());
        assertNotNull(inst.getRemoveTrain());
        assertNotNull(inst.getConfirmation());
        assertNotNull(inst.getConfirmationMessage());
    }
}

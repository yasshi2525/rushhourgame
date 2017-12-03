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
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class LanguageBeanTest extends AbstractBeanTest {
    
    @Spy
    protected LanguageBean inst;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst.rushHourSession = session;
        inst.pCon = PCON;
    }

    @Test
    public void testInit() {
        inst.init();
        assertNull(inst.player);
    }
    
    @Test
    public void testChange() throws RushHourException {
        inst.player = createPlayer();
        
        inst.change("jp");
        assertEquals(Locale.JAPANESE, inst.player.getInfo().getLocale());
        inst.change("en");
        assertEquals(Locale.ENGLISH, inst.player.getInfo().getLocale());
    }
    
    
    @Test
    public void testChangePlayerNullJp() {
        inst.change("jp");
        verify(session, times(0)).setLocale(Locale.ENGLISH);
        verify(session, times(1)).setLocale(Locale.JAPANESE);
    }
    
    @Test
    public void testChangePlayerNullEn() {
        inst.change("en");
        verify(session, times(1)).setLocale(Locale.ENGLISH);
        verify(session, times(0)).setLocale(Locale.JAPANESE);
    }

    @Test
    public void testChangeNull() {
        inst.change(null);
        verify(session, times(0)).setLocale(any(Locale.class));
    }
    
}

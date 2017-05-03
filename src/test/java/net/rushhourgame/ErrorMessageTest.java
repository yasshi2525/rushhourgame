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

import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.ErrorMessage;
import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class ErrorMessageTest {
    static protected RushHourResourceBundle prop;
    static protected String TEST1 = "rushhour.test.text1";
    static protected String TEST2 = "rushhour.test.text2";
    static protected String QUOTE2 = "rushhour.test.quote2";
    static protected String UNEXIST_ID = "rushhour.test.unexistid";
    static protected Locale locale = Locale.getDefault();
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @BeforeClass
    public static void setUpClass() {
        prop = new RushHourResourceBundle();
    }
    
    /**
     * Test of buildTitle method, of class ErrorMessage.
     */
    @Test
    public void testBuildMessage() {
        ErrorMessage instance = new ErrorMessage(TEST1, TEST1, TEST1);
        assertEquals("message", instance.buildTitle(prop, locale));
        assertEquals("message", instance.buildDetail(prop, locale));
        assertEquals("message", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testQuoteParam(){
        ErrorMessage instance = new ErrorMessage(QUOTE2, QUOTE2, QUOTE2);
        instance.getTitleParams().add("hoge");
        instance.getDetailParams().add("hoge");
        instance.getActionParams().add("hoge");
        assertEquals("'hoge'", instance.buildTitle(prop, locale));
        assertEquals("'hoge'", instance.buildDetail(prop, locale));
        assertEquals("'hoge'", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testIgnoreParam(){
        ErrorMessage instance = new ErrorMessage(TEST1, TEST1, TEST1);
        
        instance.getTitleParams().add("hoge");
        instance.getDetailParams().add("hoge");
        instance.getActionParams().add("hoge");
        
        assertEquals("message", instance.buildTitle(prop, locale));
        assertEquals("message", instance.buildDetail(prop, locale));
        assertEquals("message", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testNullId(){
        ErrorMessage instance = new ErrorMessage();
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildTitle(prop, locale));
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildDetail(prop, locale));
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildAction(prop, locale));
    }
    
    @Test
    public void testInvalidId(){
        ErrorMessage instance = new ErrorMessage(UNEXIST_ID, UNEXIST_ID, UNEXIST_ID);
        exception.expect(MissingResourceException.class);
        instance.buildTitle(prop, locale);
        instance.buildDetail(prop, locale);
        instance.buildAction(prop, locale);
    }
    
    @Test
    public void testNullProp(){
        ErrorMessage instance = new ErrorMessage(TEST1, TEST1, TEST1);
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildTitle(null, locale));
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildDetail(null, locale));
        assertEquals(ErrorMessage.NO_CONTENTS, instance.buildAction(null, locale));
    }
    
    @Test
    public void testNullLocale(){
        ErrorMessage instance = new ErrorMessage(TEST1, TEST1, TEST1);
        assertEquals("message", instance.buildTitle(prop, null));
        assertEquals("message", instance.buildDetail(prop, null));
        assertEquals("message", instance.buildAction(prop, null));
    }
    
    @Test
    public void testFormat(){
        ErrorMessage instance = new ErrorMessage(TEST2, TEST2, TEST2);
        instance.getTitleParams().add("foo");
        instance.getTitleParams().add("bar");
        instance.getDetailParams().add("foo");
        instance.getDetailParams().add("bar");
        instance.getActionParams().add("foo");
        instance.getActionParams().add("bar");
        
        assertEquals("foo bar", instance.buildTitle(prop, locale));
        assertEquals("foo bar", instance.buildDetail(prop, locale));
        assertEquals("foo bar", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testFormatEmpty(){
        ErrorMessage instance = new ErrorMessage(TEST2, TEST2, TEST2);
        assertEquals("{0} {1}", instance.buildTitle(prop, locale));
        assertEquals("{0} {1}", instance.buildDetail(prop, locale));
        assertEquals("{0} {1}", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testFormatShortage(){
        ErrorMessage instance = new ErrorMessage(TEST2, TEST2, TEST2);
        instance.getTitleParams().add("foo");
        instance.getDetailParams().add("foo");
        instance.getActionParams().add("foo");
        assertEquals("foo {1}", instance.buildTitle(prop, locale));
        assertEquals("foo {1}", instance.buildDetail(prop, locale));
        assertEquals("foo {1}", instance.buildAction(prop, locale));
    }
    
    @Test
    public void testFormatExtra(){
        ErrorMessage instance = new ErrorMessage(TEST2, TEST2, TEST2);
        instance.getTitleParams().add("foo");
        instance.getDetailParams().add("foo");
        instance.getActionParams().add("foo");
        instance.getTitleParams().add("bar");
        instance.getDetailParams().add("bar");
        instance.getActionParams().add("bar");
        instance.getTitleParams().add("three");
        instance.getDetailParams().add("three");
        instance.getActionParams().add("three");
        assertEquals("foo bar", instance.buildTitle(prop, locale));
        assertEquals("foo bar", instance.buildDetail(prop, locale));
        assertEquals("foo bar", instance.buildAction(prop, locale));
    }
}

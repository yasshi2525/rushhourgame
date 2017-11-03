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

import static net.rushhourgame.RushHourProperties.ADMINISTRATOR;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class ErrorMessageBuilderTest {
    
    protected ErrorMessageBuilder inst;
    protected RushHourProperties prop = RushHourProperties.getInstance();
    
    @Before
    public void setUp() {
        inst = new ErrorMessageBuilder();
        inst.prop = prop;
    }

    @Test
    public void testCreateUnkownError() {
        ErrorMessage unknown = inst.createUnkownError();
        assertEquals(UNKNOWN, unknown.getTitleId());
        assertEquals(UNKNOWN_DETAIL, unknown.getDetailId());
        assertEquals(UNKNOWN_ACTION, unknown.getActionId());
    }

    @Test
    public void testCreateUnkownError_String() {
        ErrorMessage unknown = inst.createUnkownError("hoge");
        assertEquals("hoge", unknown.getTitleId());
        assertEquals(UNKNOWN_DETAIL, unknown.getDetailId());
        assertEquals(UNKNOWN_ACTION, unknown.getActionId());
    }

    @Test
    public void testCreateSystemError_String_String() {
        
        ErrorMessage system = inst.createSystemError("title", "detail");
        assertEquals("title", system.getTitleId());
        assertEquals("detail", system.getDetailId());
        assertEquals(SYSTEM_ERR_ACTION, system.getActionId());
        assertEquals(1, system.getActionParams().size());
        assertEquals(prop.get(ADMINISTRATOR), system.getActionParams().get(0));
        
    }

    @Test
    public void testCreateSystemError_3args() {
        ErrorMessage system = inst.createSystemError("title", "detail", "d1");
        assertEquals("title", system.getTitleId());
        assertEquals("detail", system.getDetailId());
        assertEquals(SYSTEM_ERR_ACTION, system.getActionId());
        assertEquals(1, system.getDetailParams().size());
        assertEquals("d1", system.getDetailParams().get(0));
        assertEquals(1, system.getActionParams().size());
        assertEquals(prop.get(ADMINISTRATOR), system.getActionParams().get(0));
        
    }

    @Test
    public void testCreateReSignInError_String_String() {
        ErrorMessage resign = inst.createReSignInError("title", "detail");
        assertEquals("title", resign.getTitleId());
        assertEquals("detail", resign.getDetailId());
        assertEquals(SIGNIN_FAIL_ACTION, resign.getActionId());
    }

    @Test
    public void testCreateReSignInError_3args() {
        ErrorMessage resign = inst.createReSignInError("title", "detail", "d1");
        assertEquals("title", resign.getTitleId());
        assertEquals("detail", resign.getDetailId());
        assertEquals(SIGNIN_FAIL_ACTION, resign.getActionId());
        
        assertEquals(1, resign.getDetailParams().size());
        assertEquals("d1", resign.getDetailParams().get(0));
    }

    @Test
    public void testCreateRetryError_String_String() {
        ErrorMessage retry = inst.createRetryError("title", "detail");
        assertEquals("title", retry.getTitleId());
        assertEquals("detail", retry.getDetailId());
        assertEquals(SERVER_ERR_ACTION, retry.getActionId());
    }

    @Test
    public void testCreateRetryError_3args() {
        ErrorMessage retry = inst.createRetryError("title", "detail", "d1");
        assertEquals("title", retry.getTitleId());
        assertEquals("detail", retry.getDetailId());
        assertEquals(SERVER_ERR_ACTION, retry.getActionId());
        
        assertEquals(1, retry.getDetailParams().size());
        assertEquals("d1", retry.getDetailParams().get(0));
    }

    @Test
    public void testCreateInvalidToken() {
        ErrorMessage token = inst.createInvalidToken();
        assertEquals(REQUEST_FAIL, token.getTitleId());
        assertEquals(REQUEST_FAIL_INVALID_TOKEN, token.getDetailId());
        assertEquals(SIGNIN_FAIL_ACTION, token.getActionId());
    }

    @Test
    public void testCreateNoPrivileged() {
        ErrorMessage privilege = inst.createNoPrivileged("hoge");
        assertEquals(GAME_NO_PRIVILEDGE, privilege.getTitleId());
        assertEquals("hoge", privilege.getDetailId());
        assertEquals(GAME_NO_PRIVILEDGE_ACTION, privilege.getActionId());
    }

    @Test
    public void testCreateDataInconsitency() {
        ErrorMessage data = inst.createDataInconsitency("hoge");
        assertEquals(GAME_DATA_INCONSIST, data.getTitleId());
        assertEquals("hoge", data.getDetailId());
        assertEquals(GAME_DATA_INCONSIST_ACTION, data.getActionId());
    }

    @Test
    public void testGetInstance() {
        ErrorMessageBuilder result = ErrorMessageBuilder.getInstance();
        assertNotNull(result);
        assertNotNull(result.prop);
    }
    
}

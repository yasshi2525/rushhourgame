/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rushhourgame.GameMaster;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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
public class ControlGameMasterServletTest {

    @Mock
    protected GameMaster gm;
    @Spy
    protected ControlGameMasterServlet inst;
    @Mock
    protected HttpServletRequest req;
    @Mock
    protected HttpServletResponse res;
    @Mock
    protected PrintWriter out;

    @Before
    public void setUp() {
        inst.gm = gm;
        try {
            doReturn(out).when(res).getWriter();
        } catch (IOException ex) {
            Logger.getLogger(ControlGameMasterServletTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testProcessNoOp() throws ServletException, IOException, RushHourException {
        doReturn(null).when(req).getParameter(anyString());

        inst.doGet(req, res);

        verify(out, times(1)).println(eq("no GET parameter \"op\" was specified."));
        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
    }

    @Test
    public void testProcessConstruct() throws ServletException, IOException, RushHourException {
        doReturn("construct").when(req).getParameter(eq("op"));

        inst.doGet(req, res);

        verify(gm, times(1)).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }

    @Test
    public void testProcessConstructException() throws ServletException, IOException, RushHourException {
        doReturn("construct").when(req).getParameter(eq("op"));
        doThrow(RushHourException.class).when(gm).constructTemplateWorld();

        inst.doGet(req, res);

        verify(gm, times(1)).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("failed. show log in detail."));
    }

    @Test
    public void testProcessStart() throws ServletException, IOException, RushHourException {
        doReturn("start").when(req).getParameter(eq("op"));
        doReturn(true).when(gm).startGame();

        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, times(1)).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }

    @Test
    public void testProcessStop() throws ServletException, IOException, RushHourException {
        doReturn("stop").when(req).getParameter(eq("op"));
        doReturn(true).when(gm).stopGame();

        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, times(1)).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }
    
    @Test
    public void testProcessSearch() throws ServletException, IOException, RushHourException {
        doReturn("search").when(req).getParameter(eq("op"));
        doReturn(true).when(gm).search();

        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, times(1)).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }
    
    @Test
    public void testProcessCreateResidence() throws ServletException, IOException, RushHourException {
        doReturn("src").when(req).getParameter(eq("op"));
        
        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, times(1)).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }
    
    @Test
    public void testProcessCreateResidenceException() throws ServletException, IOException, RushHourException {
        doReturn("src").when(req).getParameter(eq("op"));
        doThrow(RushHourException.class).when(gm).createResidence(any(Pointable.class));
        
        inst.doGet(req, res);verify(out, times(1)).println(eq("failed. show log in detail."));
    }
    
    @Test
    public void testProcessCreateCompany() throws ServletException, IOException, RushHourException {
        doReturn("dst").when(req).getParameter(eq("op"));
        
        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, times(1)).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("success"));
    }
    
    @Test
    public void testProcessCreateCompanyException() throws ServletException, IOException, RushHourException {
        doReturn("dst").when(req).getParameter(eq("op"));
        doThrow(RushHourException.class).when(gm).createCompany(any(Pointable.class));
        
        inst.doGet(req, res);verify(out, times(1)).println(eq("failed. show log in detail."));
    }
    
    @Test
    public void testProcessInvalid() throws ServletException, IOException, RushHourException {
        doReturn("invalid").when(req).getParameter(eq("op"));

        inst.doGet(req, res);

        verify(gm, never()).constructTemplateWorld();
        verify(gm, never()).startGame();
        verify(gm, never()).stopGame();
        verify(gm, never()).search();
        verify(gm, never()).createResidence(any(Pointable.class));
        verify(gm, never()).createCompany(any(Pointable.class));
        verify(out, times(1)).println(eq("invalid GET parameter \"op\" : invalid"));
        verify(out, times(1)).println(eq("failed. show log in detail."));
    }
    
    @Test
    public void testGetPointNoValue() {
        doReturn(null).when(req).getParameter(eq("x"));
        doReturn(null).when(req).getParameter(eq("y"));
        
        Pointable p = inst.getPoint(req, 200);
        
        assertTrue(p.getX() < 200);
        assertTrue(p.getY() < 200);
        assertTrue(p.getX() > -200);
        assertTrue(p.getY() > -200);
    }
    
    @Test
    public void testGetPointInvalidPosition() throws ServletException, IOException, RushHourException {
        doReturn("foo").when(req).getParameter("x");
        
        inst.getPoint(req, 200);
        
        verify(req, times(1)).getParameter(eq("x"));
    }
    
    @Test
    public void testGetPoint() throws ServletException, IOException, RushHourException {
        doReturn("100").when(req).getParameter(eq("x"));
        doReturn("200").when(req).getParameter(eq("y"));
        
        Pointable point = inst.getPoint(req, 0);
        
        assertTrue(point.getX() == 100.0);
        assertTrue(point.getY() == 200.0);
    }
}

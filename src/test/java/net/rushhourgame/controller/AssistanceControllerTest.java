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
package net.rushhourgame.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.entity.Nameable;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AssistanceControllerTest extends AbstractControllerTest{
    
    protected AssistanceController inst;
    protected Player player;
    protected static final Pointable ORGIN = new SimplePoint(10, 10);
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createAssistanceController();
        try {
            player = createPlayer();
        } catch (RushHourException ex) {
            Logger.getLogger(AssistanceControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    @Test
    public void testStartWithStation() throws Exception {
        RailNode node = inst.startWithStation(player, ORGIN,  Locale.JAPANESE);
        EM.flush();
        EM.refresh(node);
        
        assertTrue(node.distTo(ORGIN) == 0);
        assertEquals("駅1", node.getPlatform().getStation().getName());
        assertTrue(LCON.findAll(player).get(0).getName().equals("路線1"));
    }

    @Test
    public void testGetDefaultNameEmpty() {
        String name = inst.getDefaultName(new ArrayList<>(), "hoge");
        assertEquals("hoge1", name);
    }
    
    @Test
    public void testGetDefaultNameUnique() throws RushHourException {
        List<Nameable> list = new ArrayList<>();
        Nameable obj = mock(Nameable.class);
        list.add(obj);
        doReturn("unique").when(obj).getName();
        
        String name = inst.getDefaultName(list, "hoge");
        assertEquals("hoge1", name);
    }
    
    @Test
    public void testGetDefaultStationNameDuplicated() throws RushHourException {
        List<Nameable> list = new ArrayList<>();
        Nameable obj = mock(Nameable.class);
        list.add(obj);
        doReturn("hoge1").when(obj).getName();
        
        String name = inst.getDefaultName(list, "hoge");
        assertEquals("hoge2", name);
    }
}

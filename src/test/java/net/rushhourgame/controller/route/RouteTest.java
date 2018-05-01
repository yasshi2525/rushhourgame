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
package net.rushhourgame.controller.route;

import java.util.SortedSet;
import java.util.TreeSet;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RelayPointForHuman;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RouteTest {
    
    @Test
    public void testCompareRouteNodeEqual() {
        SortedSet<PermanentRouteNode> set = new TreeSet<>();
        PermanentRouteNode r1 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r1.setCost(0);
        set.add(r1);
        
        PermanentRouteNode r2 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r2.setCost(0);
        set.add(r2);
    }
    
    @Test
    public void testCompareRouteNodeGreater() {
        SortedSet<PermanentRouteNode> set = new TreeSet<>();
        PermanentRouteNode r1 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r1.setCost(1);
        set.add(r1);
        
        PermanentRouteNode r2 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r2.setCost(0);
        set.add(r2);
        
        assertEquals(r2, set.first());
    }
    
    @Test
    public void testCompareRouteNodeLess() {
        SortedSet<PermanentRouteNode> set = new TreeSet<>();
        PermanentRouteNode r1 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r1.setCost(0);
        set.add(r1);
        
        PermanentRouteNode r2 = new PermanentRouteNode(mock(RelayPointForHuman.class));
        r2.setCost(1);
        set.add(r2);
        
        assertEquals(r1, set.first());
    }
    
    @Test
    public void testCompareLineRouteNodeEqual() {
        SortedSet<LineRouteNode> set = new TreeSet<>();
        LineRouteNode r1 = new LineRouteNode(mock(Platform.class));
        r1.setCost(0);
        set.add(r1);
        
        LineRouteNode r2 = new LineRouteNode(mock(Platform.class));
        r2.setCost(0);
        set.add(r2);
    }
    
    @Test
    public void testCompareLineRouteNodeGreater() {
        SortedSet<LineRouteNode> set = new TreeSet<>();
        LineRouteNode r1 = new LineRouteNode(mock(Platform.class));
        r1.setCost(1);
        set.add(r1);
        
        LineRouteNode r2 = new LineRouteNode(mock(Platform.class));
        r2.setCost(0);
        set.add(r2);
        
        assertEquals(r2, set.first());
    }
    
    @Test
    public void testCompareLineRouteNodeLess() {
        SortedSet<LineRouteNode> set = new TreeSet<>();
        LineRouteNode r1 = new LineRouteNode(mock(Platform.class));
        r1.setCost(0);
        set.add(r1);
        
        LineRouteNode r2 = new LineRouteNode(mock(Platform.class));
        r2.setCost(1);
        set.add(r2);
        
        assertEquals(r1, set.first());
    }
}

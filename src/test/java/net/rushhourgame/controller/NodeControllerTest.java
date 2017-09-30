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
package net.rushhourgame.controller;

import java.util.List;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;
import net.rushhourgame.exception.RushHourException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class NodeControllerTest extends AbstractControllerTest {
    protected NodeController inst;
    
    @Before
    public void setUp(){
        super.setUp();
        inst = ControllerFactory.createNodeController();
    }
    
    @Test
    public void testCreate() throws RushHourException{
        Node n1 = inst.create(0, 0);
        EM.flush();
        assertEquals(0, TCON.findRoutingInfos().size());
        
        Node n2 = inst.create(1, 1);
        EM.flush();
        List<RoutingInfo> network = TCON.findRoutingInfos();
        assertEquals(2, network.size());
        assertEquals(n2.getId(), network.get(0).getStart().getId());
        assertEquals(n1.getId(), network.get(0).getGoal().getId());
        assertEquals(n1.getId(), network.get(1).getStart().getId());
        assertEquals(n2.getId(), network.get(1).getGoal().getId());
        
        inst.create(2, 2);
        EM.flush();
        assertEquals(6, TCON.findRoutingInfos().size());
        
        inst.create(3, 3);
        EM.flush();
        assertEquals(12, TCON.findRoutingInfos().size());
    }
}

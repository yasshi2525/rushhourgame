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

import java.util.ArrayList;
import java.util.List;
import net.rushhourgame.entity.Link;
import net.rushhourgame.entity.Node;
import net.rushhourgame.entity.RoutingInfo;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RouteSearcherTest extends AbstractControllerTest {

    @Test
    public void testSearch1() throws RushHourException {
        Node node1 = nCon.create(0, 0);
        em.flush();
        
        List<Node> nodes = nCon.findIn(0, 0, 1);
        List<RoutingInfo> network = rCon.findNetwork(node1);
        List<Link> links = lCon.findIn(0, 0, 1);
        
        RouteSearcher instance = new RouteSearcher();
        instance.search(network, links, nodes, node1);
        
        assertTrue(node1.getCost() == 0.0);
    }

    
    @Test
    public void testSearch2() {
        
    }
}

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

import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.SimplePoint;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TemporaryHumanPointTest {
    
    @Spy
    protected Human human;
    
    @Mock
    protected RouteNode node;

    @Test
    public void testBean() {
        human.setX(10);
        human.setY(20);
        TemporaryHumanPoint inst = new TemporaryHumanPoint(human);
        assertEquals(human, inst.getHuman());
        
        inst.setNode(node);
        assertEquals(node, inst.getNode());
        
        assertEquals(human.getId(), inst.getId());
        assertFalse(inst.equalsId(null));
        assertFalse(inst.equalsId(human));
        assertTrue(inst.equalsId(inst));
        assertTrue(inst.getOutEdges().isEmpty());
        assertTrue(inst.getInEdges().isEmpty());
        assertTrue(human.getX() == inst.getX());
        assertTrue(human.getY() == inst.getY());
        assertTrue(inst.isAreaIn(new SimplePoint(10, 20), 0));
    }
    
}

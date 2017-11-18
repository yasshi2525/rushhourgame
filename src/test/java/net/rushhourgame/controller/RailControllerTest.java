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
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import org.junit.Before;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RailControllerTest extends AbstractControllerTest {

    protected RailController inst;
    private static final double TEST_X = 10;
    private static final double TEST_Y = 10;
    private static final double TEST_X2 = 20;
    private static final double TEST_Y2 = 10;
    private static final int TEST_CAPACITY = 2;
    private static final int TEST_INTERVAL = 3;

    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createRailController();

    }

    @Test
    public void testCreate() throws RushHourException {
        Player player = createPlayer();
        RailNode created = inst.create(player, TEST_X, TEST_Y);
        assertNotNull(created);
        assertTrue(TEST_X == created.getX());
        assertTrue(TEST_Y == created.getY());
        assertEquals(player, created.getOwner());
        assertTrue(created.isOwnedBy(player));
        assertTrue(created.isPrivilegedBy(player));

        EM.flush();

        assertEquals(0, created.getPlatforms().size());
        assertEquals(0, created.getInEdges().size());
        assertEquals(0, created.getOutEdges().size());
    }

    @Test
    public void testCreateNull() {
        try {
            inst.create(null, TEST_X, TEST_Y);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }

    @Test
    public void testExtend() throws RushHourException {
        Player player = createPlayer();
        RailNode created1 = inst.create(player, TEST_X, TEST_Y);
        RailNode created2 = inst.extend(player, created1, TEST_X2, TEST_Y2);

        EM.flush();
        EM.refresh(created1);
        EM.refresh(created2);
        
        // 1 --> 2
        RailEdge edge1 = created1.getOutEdges().get(0);
        // 1 <-- 2
        RailEdge edge2 = created1.getInEdges().get(0);
                
        assertTrue(created1.distTo(created2) == 10);
        assertTrue(created2.distTo(created1) == 10);
        assertTrue(edge1.getDist() == 10);
        assertTrue(edge2.getDist() == 10);
        
        assertNotNull(created2);
        assertEquals(player, edge1.getOwner());
        assertEquals(player, edge2.getOwner());
        assertTrue(edge1.isOwnedBy(player));
        assertTrue(edge2.isOwnedBy(player));
        assertTrue(edge1.isPrivilegedBy(player));
        assertTrue(edge2.isPrivilegedBy(player));
        
        // check 1 --> (2)
        assertEquals(1, created2.getInEdges().size());
        assertEquals(created1, created2.getInEdges().get(0).getFrom());
        assertEquals(created2, created2.getInEdges().get(0).getTo());
        
        // check 1 <-- (2) 
        assertEquals(1, created2.getOutEdges().size());
        assertEquals(created2, created2.getOutEdges().get(0).getFrom());
        assertEquals(created1, created2.getOutEdges().get(0).getTo());

        // check (1) <-- 2
        assertEquals(1, created1.getInEdges().size());
        assertEquals(created2, created1.getInEdges().get(0).getFrom());
        assertEquals(created1, created1.getInEdges().get(0).getTo());
        
        // check (1) --> 2 
        assertEquals(1, created1.getOutEdges().size());
        assertEquals(created1, created1.getOutEdges().get(0).getFrom());
        assertEquals(created2, created1.getOutEdges().get(0).getTo());
    }
    
    @Test
    public void testExtendOwnerNull() throws RushHourException {
        Player player = createPlayer();
        RailNode create = inst.create(player, TEST_X, TEST_Y);
        try {
            inst.extend(null, create, TEST_X, TEST_Y);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_OWNER, e.getErrMsg().getDetailId());
        }
    }
    
    @Test
    public void testExtendFromNull() throws RushHourException {
        Player player = createPlayer();
        RailNode create = inst.create(player, TEST_X, TEST_Y);
        try {
            inst.extend(player, null, TEST_X, TEST_Y);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
    }


    /**
     * 違う人が線路を伸ばそうとした
     *
     * @throws RushHourException
     */
    @Test
    public void testExtendOther() throws RushHourException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode create = inst.create(player, TEST_X, TEST_Y);
        try {
            inst.extend(other, create, TEST_X, TEST_Y);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_NO_PRIVILEDGE_OTHER_OWNED, e.getErrMsg().getDetailId());
        }
    }
}
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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.controller.AbstractControllerTest.EM;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.entity.hroute.StepForHumanIntoStation;
import net.rushhourgame.entity.hroute.StepForHumanOutOfStation;
import net.rushhourgame.entity.hroute.StepForHumanResidenceToStation;
import net.rushhourgame.entity.hroute.StepForHumanStationToCompany;
import net.rushhourgame.entity.hroute.StepForHumanThroughTrain;
import org.junit.Before;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class StepForHumanControllerTest extends AbstractControllerTest {

    protected StepForHumanController inst;

    @Before
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createStepForHumanController();
    }
    
    @Test
    public void testAddCompanyToEmptyWorld() throws RushHourException {
        // このなかで addCompanyが呼ばれる
        CCON.create(0, 0);
        
        // StepForHumanができていない
        assertParentNumEquals(0);
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    @Test
    public void testAddNullCompany() {
        try {
            inst.addCompany(null);
            fail();
        } catch (RushHourException ex) {
            assertEquals(GAME_DATA_INCONSIST, ex.getErrMsg().getTitleId());
        }
    }
    
    protected void assertParentNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHuman", new StepForHuman()).size());
    }
    
    protected void assertDirectlyNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanDirectly", new StepForHumanDirectly()).size());
    }
    
    protected void assertIntoStationNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanIntoStation", new StepForHumanIntoStation()).size());
    }
    
    protected void assertOutOfStationNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanOutOfStation", new StepForHumanOutOfStation()).size());
    }
    
    protected void assertFromResidenceNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanResidenceToStation", new StepForHumanResidenceToStation()).size());
    }
    
    protected void assertToCompanyNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanStationToCompany", new StepForHumanStationToCompany()).size());
    }
    
    protected void assertThroughTrainNumEquals(int expected) {
        assertEquals(expected, TCON.findAll("StepForHumanThroughTrain", new StepForHumanThroughTrain()).size());
    }
    
    
}

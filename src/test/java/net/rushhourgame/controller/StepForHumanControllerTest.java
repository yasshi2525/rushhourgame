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
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import net.rushhourgame.exception.RushHourException;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
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
    protected double TEST_X = 10.0;
    protected double TEST_Y = 20.0;
    protected double TEST_X2 = 30.0;
    protected double TEST_Y2 = 40.0;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createStepForHumanController();
    }
    
    @Test
    public void testAddCompanyToEmptyWorld() throws RushHourException {
        // このなかで addCompanyが呼ばれる
        CCON.create(TEST_X, TEST_Y);
        
        // StepForHumanができていない
        assertEquals(0, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    @Test
    public void testAddResidenceToEmptyWorld() throws RushHourException {
        // このなかで addResidenceが呼ばれる
        RCON.create(TEST_X, TEST_Y);
        
        // StepForHumanができていない
        assertEquals(0, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    @Test
    public void testAddStationToEmptyWorld() throws RushHourException {
        // このなかで、addStationが呼ばれる。
        createStation();
        
        // TicketGate <-> Platform
        assertEquals(2, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    /**
     * 住宅1 に 会社1 を追加すると Directly が追加される
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testAddCompanyToOneResidenceWorld() throws RushHourException {
        RCON.create(10, 10);
        CCON.create(20, 10);
        
        assertEquals(1, inst.findAll().size());
        assertDirectlyNumEquals(1);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
        
        StepForHumanDirectly directly = findDirectly().get(0);
        assertTrue(directly.getCost() == 10.0);
    }
    
    /**
     * 会社1 に 住宅1 を追加すると Directly が追加される
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testAddResidenceToOneCompanyWorld() throws RushHourException {
        CCON.create(20, 10);
        RCON.create(10, 10);
        
        assertEquals(1, inst.findAll().size());
        assertDirectlyNumEquals(1);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
        
        StepForHumanDirectly directly = findDirectly().get(0);
        assertTrue(directly.getCost() == 10.0);
    }
       
    @Test
    public void testAddCompanyToStationWorld() throws RushHourException {
        createStation();
        CCON.create(20, 10);
        
        assertEquals(3, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(1);
        assertThroughTrainNumEquals(0);
    }
    
    @Test
    public void testAddResidenceToStationWorld() throws RushHourException {
        createStation();
        RCON.create(20, 10);
        
        assertEquals(3, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(1);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    /**
     * 住宅2 * 会社2 で計4の Directly が作成される
     * @throws RushHourException 
     */
    @Test
    public void testTwoResidenceTwoCompany() throws RushHourException {
        RCON.create(TEST_X, TEST_Y);
        RCON.create(TEST_X2, TEST_Y2);
        CCON.create(TEST_X, TEST_Y);
        CCON.create(TEST_X2, TEST_Y2);
        
        assertEquals(4, inst.findAll().size());
        assertDirectlyNumEquals(4);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
    }
    
    @Test
    public void testAddStationToOneRsdCmpWorld() throws RushHourException {
        RCON.create(TEST_X, TEST_Y);
        CCON.create(TEST_X, TEST_Y);
        createStation();
        
        // R -> Gate <-> Platform
        //      Gate  -> C
        // R     ->      C
        
        assertEquals(5, inst.findAll().size());
        assertDirectlyNumEquals(1);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(1);
        assertToCompanyNumEquals(1);
        assertThroughTrainNumEquals(0);
    }
    
    protected List<StepForHumanDirectly> findDirectly() {
        return TCON.findAll("StepForHumanDirectly", StepForHumanDirectly.class);
    }
    
    protected List<StepForHumanIntoStation> findIntoStation() {
        return TCON.findAll("StepForHumanIntoStation", StepForHumanIntoStation.class);
    }
    
    protected List<StepForHumanOutOfStation> findOutOfStation() {
        return TCON.findAll("StepForHumanOutOfStation", StepForHumanOutOfStation.class);
    }
    
    protected List<StepForHumanResidenceToStation> findFromResidence() {
        return TCON.findAll("StepForHumanResidenceToStation", StepForHumanResidenceToStation.class);
    }
    
    protected List<StepForHumanStationToCompany> findToCompany() {
        return TCON.findAll("StepForHumanStationToCompany", StepForHumanStationToCompany.class);
    }
    
    protected List<StepForHumanThroughTrain> findThroughTrain() {
        return TCON.findAll("StepForHumanThroughTrain", StepForHumanThroughTrain.class);
    }
    
    protected void assertDirectlyNumEquals(int expected) {
        assertEquals(expected, findDirectly().size());
    }
    
    protected void assertIntoStationNumEquals(int expected) {
        assertEquals(expected, findIntoStation().size());
    }
    
    protected void assertOutOfStationNumEquals(int expected) {
        assertEquals(expected, findOutOfStation().size());
    }
    
    protected void assertFromResidenceNumEquals(int expected) {
        assertEquals(expected, findFromResidence().size());
    }
    
    protected void assertToCompanyNumEquals(int expected) {
        assertEquals(expected, findToCompany().size());
    }
    
    protected void assertThroughTrainNumEquals(int expected) {
        assertEquals(expected, findThroughTrain().size());
    }
    
    
}

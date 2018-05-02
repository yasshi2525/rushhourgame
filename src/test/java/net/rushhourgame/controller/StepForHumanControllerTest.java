/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to pass, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
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
    protected static final double TEST_X = 10.0;
    protected static final double TEST_Y = 20.0;
    protected static final Pointable TEST_POS = new SimplePoint(TEST_X, TEST_Y);
    protected static final double TEST_X2 = 30.0;
    protected static final double TEST_Y2 = 40.0;
    protected static final Pointable TEST_POS2 = new SimplePoint(TEST_X2, TEST_Y2);

    @Before
    @Override
    public void setUp() {
        super.setUp();
        inst = ControllerFactory.createStepForHumanController();
    }
    
    @Test
    public void testFindIn() throws RushHourException {
        // 家 駅(0,0)-駅(1,0) 会社
        
        RCON.create(new SimplePoint(-1, 0));
        CCON.create(new SimplePoint(2, 0));
        
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode r2 = RAILCON.extend(owner, st.getPlatform().getRailNode(), new SimplePoint(1, 1));
        STCON.create(owner, r2, "_test2");
        EM.flush();
        EM.refresh(r2);
        RailEdge go = r2.getInEdges().get(0);
        RailEdge back = r2.getOutEdges().get(0);
        
        Line line = LCON.create(owner, "_test");
        LineStep start = LCON.start(line, owner, st);
        LineStep extend = LCON.extend(start, owner, go);
        LineStep tail = LCON.extend(extend, owner, back);
        LCON.end(tail, owner);
        
        assertEquals(11, inst.findIn(new SimplePoint(0, 0), 2).size());
        inst.findIn(new SimplePoint(0, 0), -1);
        inst.findIn(new SimplePoint(2, 0), -1);
        inst.findIn(new SimplePoint(2, -1), -1);
        inst.findIn(new SimplePoint(2, 1), -1);
        assertEquals(0, inst.findIn(new SimplePoint(10, 10), 2).size());
    }

    @Test
    public void testAddCompanyToEmptyWorld() throws RushHourException {
        // このなかで addCompanyが呼ばれる
        CCON.create(TEST_POS);

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
        RCON.create(TEST_POS);

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
        Station st = createStation();

        // TicketGate <-> Platform
        assertEquals(2, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
        
        StepForHumanIntoStation inStep = findIntoStation().get(0);
        assertEquals(st.getTicketGate(), inStep.getFrom());
        assertEquals(st.getPlatform(), inStep.getTo());
        assertTrue(0 == inStep.getCost());
        assertTrue(inStep.getUid().startsWith("into"));
        
        
        StepForHumanOutOfStation outStep = findOutOfStation().get(0);
        assertEquals(st.getPlatform(), outStep.getFrom());
        assertEquals(st.getTicketGate(), outStep.getTo());
        assertTrue(0 == outStep.getCost());
        assertTrue(outStep.getUid().startsWith("outof"));
    }

    /**
     * 住宅1 に 会社1 を追加すると Directly が追加される
     *
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testAddCompanyToOneResidenceWorld() throws RushHourException {
        Residence r = RCON.create(new SimplePoint(10, 10));
        Company c = CCON.create(new SimplePoint(20, 10));

        assertEquals(1, inst.findAll().size());
        assertDirectlyNumEquals(1);
        assertIntoStationNumEquals(0);
        assertOutOfStationNumEquals(0);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);

        StepForHumanDirectly directly = findDirectly().get(0);
        assertTrue(directly.getUid().startsWith("directly"));
        assertTrue(directly.getCost() == 10.0);
        
        EM.flush();
        EM.refresh(r);
        EM.refresh(c);
        
        assertTrue(r.getInEdges().isEmpty());
        assertEquals(1, r.getOutEdges().size());
        assertEquals(1, c.getInEdges().size());
        assertTrue(c.getOutEdges().isEmpty());
    }

    /**
     * 会社1 に 住宅1 を追加すると Directly が追加される
     *
     * @throws net.rushhourgame.exception.RushHourException
     */
    @Test
    public void testAddResidenceToOneCompanyWorld() throws RushHourException {
        CCON.create(new SimplePoint(20, 10));
        RCON.create(new SimplePoint(10, 10));

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
        Station st = createStation();
        Company c = CCON.create(new SimplePoint(20, 10));

        assertEquals(3, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(0);
        assertToCompanyNumEquals(1);
        assertThroughTrainNumEquals(0);
        
        StepForHumanStationToCompany toStep = findToCompany().get(0);
        assertEquals(st.getTicketGate(), toStep.getFrom());
        assertEquals(c, toStep.getTo());
        assertTrue(toStep.getCost() > 0);
        assertTrue(toStep.getUid().startsWith("to"));
    }

    @Test
    public void testAddResidenceToStationWorld() throws RushHourException {
        Station st = createStation();
        Residence r = RCON.create(new SimplePoint(20, 10));

        assertEquals(3, inst.findAll().size());
        assertDirectlyNumEquals(0);
        assertIntoStationNumEquals(1);
        assertOutOfStationNumEquals(1);
        assertFromResidenceNumEquals(1);
        assertToCompanyNumEquals(0);
        assertThroughTrainNumEquals(0);
        
        StepForHumanResidenceToStation fromStep = findFromResidence().get(0);
        assertEquals(r, fromStep.getFrom());
        assertEquals(st.getTicketGate(), fromStep.getTo());
        assertTrue(fromStep.getCost() > 0);
        assertTrue(fromStep.getUid().startsWith("from"));
    }

    /**
     * 住宅2 * 会社2 で計4の Directly が作成される
     *
     * @throws RushHourException
     */
    @Test
    public void testTwoResidenceTwoCompany() throws RushHourException {
        RCON.create(TEST_POS);
        RCON.create(TEST_POS2);
        CCON.create(TEST_POS);
        CCON.create(TEST_POS2);

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
        RCON.create(TEST_POS);
        CCON.create(TEST_POS);
        Station st = createStation();

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
        
        EM.flush();
        EM.refresh(st.getTicketGate());
        EM.refresh(st.getPlatform());
        
        assertEquals(2, st.getTicketGate().getInEdges().size());
        assertEquals(2, st.getTicketGate().getOutEdges().size());
        assertEquals(1, st.getPlatform().getInEdges().size());
        assertEquals(1, st.getPlatform().getOutEdges().size());
    }
    
    @Test
    public void testAddCompletedLine() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        RailNode r2 = RAILCON.extend(owner, st.getPlatform().getRailNode(), TEST_POS);
        STCON.create(owner, r2, "_test2");
        EM.flush();
        EM.refresh(r2);
        RailEdge go = r2.getInEdges().get(0);
        RailEdge back = r2.getOutEdges().get(0);
        
        Line line = LCON.create(owner, "_test");
        LineStep start = LCON.start(line, owner, st);
        LineStep extend = LCON.extend(start, owner, go);
        LineStep tail = LCON.extend(extend, owner, back);
        
        // このなかで addCompletedLine がよばれる
        LCON.end(tail, owner);
        
        assertEquals(2, inst.findThroughTrainAll().size());
    }

    @Test
    public void testAddIncompletedLine() throws RushHourException {
        Station st = createStation();
        Player owner = st.getOwner();
        
        Line line = LCON.create(owner, "_test");
        LCON.start(line, owner, st);
        
        try {
            inst.addCompletedLine(line);
            fail();
        } catch (RushHourException e) {
            assertEquals(GAME_DATA_INCONSIST, e.getErrMsg().getTitleId());
        }
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

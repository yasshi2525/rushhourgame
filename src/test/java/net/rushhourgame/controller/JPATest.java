/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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

import static net.rushhourgame.controller.AbstractControllerTest.CCON;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.exception.RushHourException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * JPAの挙動をたしかめるためのテスト
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class JPATest extends AbstractControllerTest {
        
    /**
     * persistしたインスタンスと、JPQLで取得したインスタンスは同一
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrence() throws RushHourException {
        Company original = CCON.create(1, 1);
        Company fromJPQL = TCON.findAll("Company", Company.class).get(0);
        
        assertEquals(original, fromJPQL);
    }
    
    /**
     * JPQLを複数回実行しても、同じインスタンスが返ってくる
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrenceJPQL() throws RushHourException {
        CCON.create(1, 1);
        Company fromJPQL1 = TCON.findAll("Company", Company.class).get(0);
        Company fromJPQL2 = TCON.findAll("Company", Company.class).get(0);
        
        assertEquals(fromJPQL1, fromJPQL2);
    }
    
    /**
     * 関連で取得したインスタンスが他のJPQLで取得したインスタンスと同じものである
     * @throws RushHourException 
     */
    @Test
    public void testSameReferrenceJPQLrelation() throws RushHourException {
        CCON.create(1, 1);
        RCON.create(2, 2);
        Company fromCompanyJPQL = TCON.findAll("Company", Company.class).get(0);
        
        RelayPointForHuman fromEdgeJPQL = TCON.findAll("StepForHumanDirectly", StepForHumanDirectly.class).get(0).getTo();
        assertEquals(fromCompanyJPQL, fromEdgeJPQL);
    }
}

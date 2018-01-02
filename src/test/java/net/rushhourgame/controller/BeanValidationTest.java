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

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import static net.rushhourgame.controller.AbstractControllerTest.CCON;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.hroute.StepForHumanDirectly;
import net.rushhourgame.exception.RushHourException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Bean Validationの挙動をたしかめるためのテスト
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class BeanValidationTest extends AbstractControllerTest {
    /**
     * Client側でバリデーションテストするときのテンプレート
     * @throws RushHourException
     * @throws NoSuchMethodException 
     */    
    @Test
    public void testStationCreateNameNull() throws RushHourException, NoSuchMethodException {
        Player player = createPlayer();
        Player other = createOther();
        RailNode node = RAILCON.create(player, new SimplePoint(10, 10));
        
        Set<ConstraintViolation<StationController>> violations = validatorForExecutables.validateParameters(
                STCON,
                StationController.class.getMethod("create", Player.class, RailNode.class, String.class),
                new Object[]{player, node, null});
        
        assertViolatedValueIs(null, violations);
        assertViolatedAnnotationTypeIs(NotNull.class, violations);
    }
}

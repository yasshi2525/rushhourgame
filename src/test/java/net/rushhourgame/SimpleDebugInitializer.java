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
package net.rushhourgame;

import javax.persistence.EntityManager;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.TrainController;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class SimpleDebugInitializer extends DebugInitializer{
    public void initialize(
            AssistanceController aCon,
            CompanyController cCon,
            EntityManager em,
            HumanController hCon,
            LineController lCon,
            OAuthController oCon,
            PlayerController pCon,
            ResidenceController rCon,
            RailController railCon,
            StationController stCon,
            TrainController tCon
            ) {
        this.aCon = aCon;
        this.cCon = cCon;
        this.em = em;
        this.hCon = hCon;
        this.lCon = lCon;
        this.oCon = oCon;
        this.pCon = pCon;
        this.rCon = rCon;
        this.railCon = railCon;
        this.stCon = stCon;
        this.tCon = tCon;
    }
}

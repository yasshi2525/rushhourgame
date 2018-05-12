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
import net.rushhourgame.DebugInitializer;
import net.rushhourgame.GameMaster;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.HumanController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.TrainController;
import net.rushhourgame.controller.TrainController;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class SimpleGameMaster extends GameMaster {
    
    private static final long serialVersionUID = 1L;
    
    public void init(
            EntityManager em,
            DebugInitializer debug,
            HumanController hCon,
            RushHourProperties prop,
            ResidenceController rCon,
            RouteSearcher searcher,
            StationController stCon,
            TrainController tCon,
            CompanyController cCon
    ) {
        this.debug = debug;
        this.em = em;
        this.hCon = hCon;
        this.prop = prop;
        this.rCon = rCon;
        this.searcher = searcher;
        this.stCon = stCon;
        this.tCon = tCon;
        this.cCon = cCon;
    }

    public TrainController gettCon() {
        return tCon;
    }

    public void settCon(TrainController tCon) {
        this.tCon = tCon;
    }

    public ResidenceController getrCon() {
        return rCon;
    }

    public void setrCon(ResidenceController rCon) {
        this.rCon = rCon;
    }

    public StationController getStCon() {
        return stCon;
    }

    public void setStCon(StationController stCon) {
        this.stCon = stCon;
    }

    public HumanController gethCon() {
        return hCon;
    }

    public void sethCon(HumanController hCon) {
        this.hCon = hCon;
    }

    public RushHourProperties getProp() {
        return prop;
    }

    public void setProp(RushHourProperties prop) {
        this.prop = prop;
    }

    public RouteSearcher getSearcher() {
        return searcher;
    }

    public void setSearcher(RouteSearcher searcher) {
        this.searcher = searcher;
    }

    public DebugInitializer getDebug() {
        return debug;
    }

    public void setDebug(DebugInitializer debug) {
        this.debug = debug;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    
}

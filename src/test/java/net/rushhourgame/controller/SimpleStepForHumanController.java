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
package net.rushhourgame.controller;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.persistence.EntityManager;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class SimpleStepForHumanController extends StepForHumanController {

    private static final long serialVersionUID = 1L;

    public void init(
            EntityManager em,
            ErrorMessageBuilder builder,
            ManagedExecutorService service,
            LineRouteSearcher lSearcher,
            RouteSearcher searcher,
            RushHourProperties prop,
            ResidenceController rCon,
            StationController stCon
    ) {
        this.em = em;
        this.errMsgBuilder = builder;
        this.executorService = service;
        this.lSearcher = lSearcher;
        this.searcher = searcher;
        this.prop = prop;
        this.rCon = rCon;
        this.stCon = stCon;
    }
}

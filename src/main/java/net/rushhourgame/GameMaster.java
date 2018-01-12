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
package net.rushhourgame;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.transaction.Transactional;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.controller.TrainController;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Startup
@DependsOn("RushHourProperties")
@Singleton
public class GameMaster implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Resource
    protected TimerService timerService;
    @Inject
    protected TrainController tCon;
    @Inject
    protected RushHourProperties prop;
    
    Timer timer;
    
    protected long interval;
    
    @PostConstruct
    public void init() {
        interval = Long.parseLong(prop.get(GAME_INTERVAL));
        TimerConfig config = new TimerConfig("RushHour", true);
        timer = timerService.createIntervalTimer(0L, interval, config);
    }
    
    @Timeout
    @Transactional
    public void step() {
        tCon.findAll().forEach(t -> {
            tCon.step(t, interval);
        });
    }
}

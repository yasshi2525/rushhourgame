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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.RouteSearcher;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StepForHumanController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Startup
@Singleton
public class DebugInitializer {
    //@Inject
    //protected RouteSearcher searcher;

    @PersistenceContext
    protected EntityManager em;

    @Inject
    protected OAuthController oCon;
    @Inject
    protected PlayerController pCon;
    @Inject
    protected CompanyController cCon;
    @Inject
    protected ResidenceController rCon;
    @Inject
    protected RailController railCon;
    @Inject
    protected StationController stCon;
    @Inject
    protected LineController lCon;
    @Resource
    ManagedExecutorService executorService;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void init() {
        try {
            rCon.create(20, 5);
            cCon.create(50, 15);

            oCon.createOAuthBean("debug", "debug");
            Player owner = pCon.createPlayer("debug", "debug", "debug", new SimpleUserData());
            
            // r1 r2 r3
            //    r4
            
            RailNode r1 = railCon.create(owner, 10, 20);
            RailNode r2 = railCon.extend(owner, r1, 30, 20);
            RailNode r3 = railCon.extend(owner, r2, 40, 20);
            RailNode r4 = railCon.extend(owner, r2, 30, 30);
            
            Station st1 = stCon.create(owner, r1, "debug_west");
            Station st3 = stCon.create(owner, r3, "debug_east");
            Station st4 = stCon.create(owner, r4, "debug_south");
            
            em.flush();
            
            lCon.autocreate(owner, st1, "debug");
            
            //executorService.submit(searcher);
        } catch (RushHourException e) {
            Logger.getLogger(DebugInitializer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

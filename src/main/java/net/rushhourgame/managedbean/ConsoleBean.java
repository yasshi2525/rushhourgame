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
package net.rushhourgame.managedbean;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.LineStep;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.entity.Station;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "console")
@ViewScoped
public class ConsoleBean implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @PersistenceContext
    protected EntityManager em;    
    
    @Inject
    protected RushHourSession session;
    
    @Inject
    protected CompanyController cCon;
    
    @Inject
    protected ResidenceController rCon;
    
    @Inject
    protected PlayerController pCon;
    
    @Inject
    protected RailController railCon;
    
    @Inject
    protected StationController stCon;
    
    @Inject
    protected LineController lCon;
    
    protected Player player;
    
    protected SimplePoint p;
    protected String text;
    
    protected RailNode tailRail;
    protected Station tailStation;
    protected Line tailLine;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(session.getToken());
        p = new SimplePoint();
    }
    
    @Transactional
    public void createCompany() throws RushHourException{
        cCon.create(p);
    }
    
    @Transactional
    public void createResidence() throws RushHourException {
        rCon.create(p);
    }
    
    @Transactional
    public void createRail() throws RushHourException {
        tailRail = railCon.create(player, p);
    }
    
    @Transactional
    public void extendRail() throws RushHourException {
        tailRail = railCon.extend(player, tailRail, p);
    }
    
    @Transactional
    public void splitRail() throws RushHourException {
        tailRail = em.merge(tailRail);
        em.refresh(tailRail);
        tailRail = tailRail.getInEdges().get(tailRail.getInEdges().size() - 1).getFrom();
        tailRail = railCon.extend(player, tailRail, p);
    }
    
    @Transactional
    public void createStation() throws RushHourException {
        tailStation = stCon.create(player, tailRail, text);
    }
    
    @Transactional
    public void createLine() throws RushHourException {
        tailStation = em.merge(tailStation); // おまじない
        
        tailLine = lCon.autocreate(player, tailStation, text);
     }
    
    public double getX() {
        return p.getX();
    }

    public void setX(double x) {
        p.setX(x);
    }

    public double getY() {
        return p.getY();
    }

    public void setY(double y) {
        p.setY(y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public boolean hasTailRail() {
        return tailRail != null;
    }
    
    public boolean hasTailStation() {
        return tailStation != null;
    }
    
    @Transactional
    public boolean canSplit() {
        if (tailRail == null) {
            return false;
        }
        tailRail = em.merge(tailRail);
        em.refresh(tailRail);
        return !tailRail.getInEdges().isEmpty();
    }
}

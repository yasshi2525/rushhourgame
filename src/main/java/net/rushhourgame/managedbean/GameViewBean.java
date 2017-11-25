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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.CompanyController;
import net.rushhourgame.controller.LineController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.controller.ResidenceController;
import net.rushhourgame.controller.StationController;
import net.rushhourgame.controller.StepForHumanController;
import net.rushhourgame.entity.Company;
import net.rushhourgame.entity.Line;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.Residence;
import net.rushhourgame.entity.Station;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.managedbean.OperationType.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "game")
@ViewScoped
public class GameViewBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GameViewBean.class.getName());
    
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
    @Inject
    protected StepForHumanController sCon;
    @Inject
    protected RushHourSession rhSession;
    protected Player player;
    protected OperationType operation = NONE;
    
    protected double centerX;
    protected double centerY;
    protected double scale;
    
    protected int mouseX;
    protected int mouseY;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(rhSession.getToken());
        scale = 4;
    }
    
    @Transactional
    public void onClick() throws RushHourException{
        switch(operation){
            case CREATE_RAIL:
                railCon.create(player, mouseX, mouseY);
                break;
        }
    }
    
    public List<Company> getCompanies() {
        return cCon.findIn(centerX, centerY, scale);
    }
    
    public List<Residence> getResidences() {
        return rCon.findIn(centerX, centerY, scale);
    }
    
    public List<RailNode> getRailNodes() {
        return railCon.findNodeIn(centerX, centerY, scale);
    }
    
    public List<RailEdge> getRailEdges() {
        return railCon.findEdgeIn(centerX, centerY, scale);
    }
    
    public List<Station> getStations() {
        return stCon.findIn(centerX, centerY, scale);
    }
    
    public List<Line> getLines() {
        return lCon.findIn(centerX, centerY, scale);
    }
    
    public List<StepForHuman> getStepForHuman() {
        return sCon.findIn(centerX, centerY, scale);
    }
    
    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }
    
    public boolean isOperating(){
        return operation != NONE;
    }
}

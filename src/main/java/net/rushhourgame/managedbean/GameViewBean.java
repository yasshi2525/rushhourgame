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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.SlideEndEvent;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "game")
@ViewScoped
public class GameViewBean implements Serializable {

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
    protected RushHourSession session;
    @Inject
    protected RushHourResourceBundle msg;
    protected Player player;
    protected OperationType operation = NONE;

    protected double centerX;
    protected double centerY;
    protected double scale;

    protected double clickX;
    protected double clickY;

    protected RailNode tailNode;

    @PostConstruct
    public void init() {
        player = pCon.findByToken(session.getToken());
        centerX = session.getCenterX();
        centerY = session.getCenterY();
        scale = session.getScale();
    }
    
    public void registerClickPos() {
        Map<String, String> reqParam = getFacesContext().getExternalContext().getRequestParameterMap();
        clickX = Double.parseDouble(reqParam.get("gamePos.x"));
        clickY = Double.parseDouble(reqParam.get("gamePos.y"));
    }

    public void openClickMenu() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("clickX", Collections.singletonList(Double.toString(clickX)));
        params.put("clickY", Collections.singletonList(Double.toString(clickY)));
        params.put("scale", Collections.singletonList(Double.toString(scale)));

        RequestContext context = getRequestContext();
        Map<String, Object> options = new HashMap<>();
        options.put("width", 250);
        options.put("modal", true);

        context.openDialog("clickmenu", options, params);
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected RequestContext getRequestContext() {
        return RequestContext.getCurrentInstance();
    }

    public List<Company> getCompanies() {
        return cCon.findIn(centerX, centerY, getLoadScale());
    }

    public List<Residence> getResidences() {
        return rCon.findIn(centerX, centerY, getLoadScale());
    }

    public List<RailNode> getRailNodes() {
        return railCon.findNodeIn(centerX, centerY, getLoadScale());
    }

    public List<RailEdge> getRailEdges() {
        return railCon.findEdgeIn(centerX, centerY, getLoadScale());
    }

    public List<Station> getStations() {
        return stCon.findIn(centerX, centerY, getLoadScale());
    }

    @Transactional
    public List<Line> getLines() {
        return lCon.findIn(centerX, centerY, getLoadScale());
    }

    public List<StepForHuman> getStepForHuman() {
        return sCon.findIn(centerX, centerY, getLoadScale());
    }

    public double getClickX() {
        return clickX;
    }

    public void setClickX(double clickX) {
        this.clickX = clickX;
    }

    public double getClickY() {
        return clickY;
    }

    public void setClickY(double clickY) {
        this.clickY = clickY;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public boolean isOperating() {
        return operation != NONE;
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
        session.setCenterX(centerX);
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
        session.setCenterY(centerY);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        session.setScale(scale);
    }

    public void onSlideEnd(SlideEndEvent event) {
        setScale(event.getValue() / 100.0); // 100倍の値を入力させている
    }

    /**
     * 読み込む際は表示するより広い領域読み込む. スクロールしても表示が途切れないようにするため
     *
     * @return ロードするscale
     */
    protected double getLoadScale() {
        return scale + 1;
    }

    public void initGuide() {
        FacesContext context = getFacesContext();

        if (!railCon.hasRailNode(player)) {
            context.addMessage("guide",
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            msg.get(TUTORIAL, session.getLocale()),
                            msg.get(TUTORIAL_RAIL_CREATE, session.getLocale())));
        }
    }

    public void handleReturn(SelectEvent event) {
        if (event.getObject() instanceof RailNode) {
            tailNode = (RailNode) event.getObject();
            getFacesContext().addMessage("guide",
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            msg.get(GUIDE_RAIL_EXTEND, session.getLocale()),
                            ""));
        }
    }
}

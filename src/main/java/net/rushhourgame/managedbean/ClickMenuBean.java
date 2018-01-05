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
package net.rushhourgame.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.AssistanceController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Pointable;
import net.rushhourgame.entity.RailEdge;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.entity.SimplePoint;
import net.rushhourgame.exception.RushHourException;
import org.primefaces.context.RequestContext;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "menu")
@ViewScoped
public class ClickMenuBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @PersistenceContext
    EntityManager em;

    @Inject
    protected RushHourSession session;
    protected Player player;
    protected List<RailEdge> clickedEdges;

    @Inject
    protected PlayerController pCon;
    @Inject
    protected RailController rCon;
    @Inject
    protected AssistanceController aCon;
    @Inject
    protected RushHourResourceBundle msg;

    protected Pointable click;

    protected double scale;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(session.getToken());

        click = new SimplePoint(
                Double.parseDouble(getRequestMap().get("clickX")),
                Double.parseDouble(getRequestMap().get("clickY"))
        );
        scale = Double.parseDouble(getRequestMap().get("scale"));
        
        String e1id = getRequestMap().get("clickedEdge1");
        String e2id = getRequestMap().get("clickedEdge2");
        
        if (e1id != null && e2id != null) {
            try {
                clickedEdges = rCon.findEdge(player, Long.parseLong(e1id), Long.parseLong(e2id));
            } catch (RushHourException ex) {
                Logger.getLogger(ClickMenuBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isDisplayCreateRail() {
        return rCon.findNodeIn(player, click, scale - 4).isEmpty();
    }

    @Transactional
    public void createRail() throws RushHourException {
        getRequestContext().closeDialog(
                new OperationBean(
                        OperationBean.Type.RAIL_CREATE,
                        aCon.startWithStation(player, click, session.getLocale())));
    }

    public boolean isDisplayExtendRail() {
        return !rCon.findNodeIn(player, click, scale - 3).isEmpty();
    }

    public void extendRail() {
        getRequestContext().closeDialog(
                new OperationBean(
                        OperationBean.Type.RAIL_EXTEND,
                        rCon.findNodeIn(player, click, scale - 3).stream()
                                .min((n1, n2) -> {
                                    if (n1.distTo(click) > n2.distTo(click)) {
                                        return 1;
                                    } else if (n1.distTo(click) < n2.distTo(click)) {
                                        return -1;
                                    }
                                    return 0;
                                }).get()));
    }
    
    public boolean isDisplayRemoveRail() {
        return clickedEdges != null;
    }
    
    @Transactional
    public boolean isEnableRemoveRail() throws RushHourException {
        if (clickedEdges == null) {
            return false;
        } else {
            clickedEdges = clickedEdges.stream().map((e) -> em.merge(e)).collect(Collectors.toList());
            return rCon.canRemove(player, clickedEdges);
        }
    }
    
    public void removeRail() {
        getRequestContext().closeDialog(
                new OperationBean(OperationBean.Type.RAIL_REMOVE));
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected RequestContext getRequestContext() {
        return RequestContext.getCurrentInstance();
    }

    protected Map<String, String> getRequestMap() {
        return getFacesContext().getExternalContext().getRequestParameterMap();
    }
}

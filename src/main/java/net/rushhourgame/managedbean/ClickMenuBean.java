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
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RailNode;
import net.rushhourgame.exception.RushHourException;
import org.primefaces.context.RequestContext;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "menu")
@ViewScoped
public class ClickMenuBean  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    protected RushHourSession session;
    protected Player player;
    
    @Inject
    protected PlayerController pCon;
    @Inject
    protected RailController rCon;
    @Inject
    protected RushHourResourceBundle msg;
    
    protected double clickX;
    
    protected double clickY;
    
    protected double scale;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(session.getToken());
        clickX = Double.parseDouble(getRequestMap().get("clickX"));
        clickY = Double.parseDouble(getRequestMap().get("clickY"));
        scale = Double.parseDouble(getRequestMap().get("scale"));
    }

    public boolean canCreateRail() {
        return rCon.findNodeIn(player, clickX, clickY, scale - 3).isEmpty();
    }
    
    @Transactional
    public void createRail() throws RushHourException {
        getRequestContext().closeDialog(rCon.create(player, clickX, clickY));
    }
    
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    protected RequestContext getRequestContext() {
        return RequestContext.getCurrentInstance();
    }
    
    protected Map<String,String> getRequestMap() {
        return getFacesContext().getExternalContext().getRequestParameterMap();
    }
}

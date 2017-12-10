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
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import net.rushhourgame.controller.RailController;
import net.rushhourgame.entity.RailNode;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "menu")
@ViewScoped
public class ClickMenuBean  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    protected RailController rCon;

    public boolean canCreateRail() {
        return rCon.findNodeIn(getClickX(), getClickY(), getScale() - 3).isEmpty();
    }
    
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    protected Map<String,String> getRequestMap() {
        return getFacesContext().getExternalContext().getRequestParameterMap();
    }
    
    protected double getClickX() {
        return Double.parseDouble(getRequestMap().get("clickX"));
    }
    
    protected double getClickY() {
        return Double.parseDouble(getRequestMap().get("clickY"));
    }
    
    protected double getScale() {
        return Double.parseDouble(getRequestMap().get("scale"));
    }
}

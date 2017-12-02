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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.RushHourResourceBundle;
import net.rushhourgame.RushHourSession;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named("err")
@ViewScoped
public class ErrorBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ErrorBean.class.getName());
    
    @Inject
    protected RushHourResourceBundle msgProps;
    
    @Inject
    protected RushHourSession rushHourSession;

    protected ErrorMessage contents;

    @PostConstruct
    public void init(){
        Object obj = getFacesContext().getExternalContext().getRequestMap().get("error");
        if(obj instanceof ErrorMessage){
            contents = (ErrorMessage) obj;
        }else{
            contents = new ErrorMessage();
        }
    }
    
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    public String getTitle(){
        if(contents == null){
            return "No Contents";
        }
        return contents.buildTitle(msgProps, rushHourSession.getLocale());
    }
    
    public String getDetail(){
        if(contents == null){
            return "No Contents";
        }
        return contents.buildDetail(msgProps, rushHourSession.getLocale());
    }
    
    public String getAction(){
        if(contents == null){
            return "No Contents";
        }
        return contents.buildAction(msgProps, rushHourSession.getLocale());
    }
}

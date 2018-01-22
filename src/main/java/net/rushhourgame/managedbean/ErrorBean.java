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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.logging.Level;
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
import net.rushhourgame.exception.RushHourException;

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
    protected RushHourSession session;

    protected ErrorMessage contents;
    protected Throwable throwable;

    @PostConstruct
    public void init() {
        Object eobj = getFacesContext().getExternalContext().getRequestMap().get("errorMsg");
        Object tobj = getFacesContext().getExternalContext().getRequestMap().get("throwable");
        
        contents = (eobj instanceof ErrorMessage) ?  (ErrorMessage) eobj : new ErrorMessage();
        throwable = (tobj instanceof Throwable) ?  (Throwable) tobj : new Throwable("no contents");
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public String getTitle() {
        return contents.buildTitle(msgProps, session.getLocale());
    }

    public String getDetail() {
        return contents.buildDetail(msgProps, session.getLocale());
    }

    public String getAction() {
        return contents.buildAction(msgProps, session.getLocale());
    }

    public String getStackTrace() {
        try (
                StringWriter sw = getStringWriter();
                PrintWriter pw = new PrintWriter(sw);) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ex) {
            Logger.getLogger(ErrorBean.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }
    
    /**
     * モック化するために別メソッドにした。
     * @return 
     */
    protected StringWriter getStringWriter() {
        return new StringWriter();
    }
}

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
package net.rushhourgame;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class RushHourExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger LOG = Logger.getLogger(RushHourExceptionHandler.class.getName());

    private final ExceptionHandler wrapped;

    public RushHourExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = (ExceptionQueuedEvent) i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            Throwable t = context.getException();
            FacesContext fc = FacesContext.getCurrentInstance();

            try {
                RushHourException ex = extractRushHourException(t);
                ErrorMessage errMsg;
                if (ex != null) {
                    errMsg = ex.getErrMsg();
                } else {
                    errMsg = ErrorMessageBuilder.getInstance().createUnkownError();
                }
                fc.getExternalContext().getRequestMap().put("error", errMsg);
                NavigationHandler navHandler = fc.getApplication().getNavigationHandler();
                navHandler.handleNavigation(fc, null, "error.xhtml");
                fc.renderResponse();
            } finally {
                i.remove();
            }
            getWrapped().handle();
        }
    }

    public RushHourException extractRushHourException(Throwable parent) {
        if (parent == null) {
            return null;
        }
        if (parent instanceof RushHourException) {
            return (RushHourException) parent;
        }
        Throwable t = parent;
        while (t.getCause() != null) {
            if (t.getCause() instanceof RushHourException) {
                return (RushHourException) t.getCause();
            }
            t = t.getCause();
        }
        return null;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

}

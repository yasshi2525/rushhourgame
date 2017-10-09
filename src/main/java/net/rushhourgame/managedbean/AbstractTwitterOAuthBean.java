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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourResourceBundle;

/**
 * ログイン用
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public abstract class AbstractTwitterOAuthBean implements Serializable{

    private static final Logger LOG = Logger.getLogger(AbstractTwitterOAuthBean.class.getName());
    
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected RushHourResourceBundle msgProp;
    
    protected static final String ERR_PAGE = "error.xhtml";
    protected static final String MYPAGE = "index.xhtml";

    
    protected ExternalContext getExternalContext(){
        return FacesContext.getCurrentInstance().getExternalContext();
    }
}

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

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class RushHourSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RushHourSession.class.getName());
    protected static final String SESSION_NAME = "rushhour";
    
    @Inject
    HttpSession injectedSession;
    
    public void setLocale(Locale locale){
        findOrCreateBean().setLocale(locale);
    }
    
    public void setToken(String accessToken){
        findOrCreateBean().setToken(accessToken);
    }
    
    public Locale getLocale(){
        return findOrCreateBean().getLocale();
    }
    
    public String getToken(){
        return findOrCreateBean().getToken();
    }
    
    protected boolean hasValidBean(){
        return injectedSession.getAttribute(SESSION_NAME) != null
                && injectedSession.getAttribute(SESSION_NAME) instanceof RushHourSessionBean;
    }
    
    static public void setLocale(HttpSession session, Locale locale){
        findOrCreateBean(session).setLocale(locale);
    }
    
    static public void setToken(HttpSession session, String accessToken){
        findOrCreateBean(session).setToken(accessToken);
    }
    
    static public Locale getLocale(HttpSession session){
        return findOrCreateBean(session).getLocale();
    }
    
    static public String getToken(HttpSession session){
        return findOrCreateBean(session).getToken();
    }
    
    static protected boolean isValidBean(HttpSession session){
        return session != null 
                && session.getAttribute(SESSION_NAME) != null
                && session.getAttribute(SESSION_NAME) instanceof RushHourSessionBean;
    }
    
    /**
     * セッションデータからBeanを取得する。なければ作成する
     * @return 
     */
    protected RushHourSessionBean findOrCreateBean(){
        if(hasValidBean()){
            //すでに存在するので作成しない
            return (RushHourSessionBean) injectedSession.getAttribute(SESSION_NAME);
        }
        
        RushHourSessionBean ret;
        
        if (injectedSession.getAttribute(SESSION_NAME) == null) {
            //新規作成
            LOG.log(Level.FINE, "{0}#create", new Object[]{getClass().getSimpleName()});
            
            ret = new RushHourSessionBean();
            injectedSession.setAttribute(SESSION_NAME, ret);
            return ret;
        } else {
            // RushHourSessionBean以外の値があるので上書き
            LOG.log(Level.INFO, "{0}#create overwrite session.{1} from {2} (Class : {3})",
                    new Object[]{
                        getClass().getSimpleName(), 
                        SESSION_NAME,
                        injectedSession.getAttribute(SESSION_NAME), 
                        injectedSession.getAttribute(SESSION_NAME).getClass()});
           
            ret = new RushHourSessionBean();
            injectedSession.setAttribute(SESSION_NAME, ret);
            return ret;
        } 
    }
    
    /**
     * セッションデータからBeanを取得する。なければ作成する
     * @param session
     * @return 
     */
    static protected RushHourSessionBean findOrCreateBean(HttpSession session){
        if(isValidBean(session)){
            //すでに存在するので作成しない
            return (RushHourSessionBean) session.getAttribute(SESSION_NAME);
        }
        
        RushHourSessionBean ret;
        
        if (session.getAttribute(SESSION_NAME) == null) {
            //新規作成
            LOG.log(Level.FINE, "{0}#create", new Object[]{RushHourSession.class.getSimpleName()});
            
            ret = new RushHourSessionBean();
            session.setAttribute(SESSION_NAME, ret);
            return ret;
        } else {
            // RushHourSessionBean以外の値があるので上書き
            LOG.log(Level.INFO, "{0}#create overwrite session.{1} from {2} (Class : {3})",
                    new Object[]{
                        RushHourSession.class.getSimpleName(), 
                        SESSION_NAME,
                        session.getAttribute(SESSION_NAME), 
                        session.getAttribute("rushhour").getClass()});
           
            ret = new RushHourSessionBean();
            session.setAttribute(SESSION_NAME, ret);
            return ret;
        } 
    }
}

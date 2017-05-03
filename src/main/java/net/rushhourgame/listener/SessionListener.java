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
package net.rushhourgame.listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourSessionBean;
import net.rushhourgame.entity.PlayerController;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@WebListener
public class SessionListener implements HttpSessionListener{

    private static final Logger LOG = Logger.getLogger(SessionListener.class.getName());
    @Inject
    protected PlayerController controller;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.log(Level.INFO, "SessionListener#sessionCreated session is created : id = {0}", se.getSession().getId());
    }

    @Override
    @Transactional
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.log(Level.INFO, "SessionListener#sessionDestroyed session is destoroyed : id = {0}", se.getSession().getId());
        Object bean = se.getSession().getAttribute("rushhour");
        if(bean != null && bean instanceof RushHourSessionBean){
            controller.clearToken(((RushHourSessionBean)bean).getAccessToken());
            LOG.log(Level.INFO, "SessionListener#sessionDestroyed clear access_token = {0}", 
                    ((RushHourSessionBean)bean).getAccessToken());
        }
    }
    
}

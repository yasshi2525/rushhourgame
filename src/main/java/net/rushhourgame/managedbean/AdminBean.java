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
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.RoleType;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.TwitterUserData;
import net.rushhourgame.json.UserData;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named(value = "admin")
@ViewScoped
public class AdminBean implements Serializable {

    private final long serialVersionUID = 1;

    @Inject
    protected PlayerController pCon;
    @Inject
    protected OAuthController oCon;
    @Inject
    protected RushHourSession session;
    protected SimpleUserData userData;

    protected static final String ADMIN_USER = "admin";

    @Transactional
    public void init() throws RushHourException {
        if (!pCon.isValidToken(session.getToken())) {
            Player p;
            //ログインしていないときはAdminでログイン
            if (pCon.existsUserId(ADMIN_USER)) {
                p = pCon.findByUserId(ADMIN_USER);
            } else {
                userData = new SimpleUserData();
                oCon.createOAuthBean(ADMIN_USER, ADMIN_USER);
                userData.setName(ADMIN_USER);
                
                p = pCon.createPlayer(ADMIN_USER, ADMIN_USER, ADMIN_USER, userData, session.getLocale());
            }
            //Admin権限の付与
            if (!p.getRoles().contains(RoleType.ADMINISTRATOR)) {
                p.getRoles().add(RoleType.ADMINISTRATOR);
            }
            session.setToken(p.getToken());
        }
    }
}

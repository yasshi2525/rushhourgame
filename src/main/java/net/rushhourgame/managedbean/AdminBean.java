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
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.GameMaster;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.OAuthController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.json.SimpleUserData;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "admin")
@ViewScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    protected GameMaster gm;
    @Inject
    protected PlayerController pCon;
    @Inject
    protected RushHourSession session;

    protected static final String ADMIN_USER = "admin";

    @Transactional
    public void init() throws RushHourException {
        Player p = pCon.findByToken(session.getToken());

        if (p == null) {
            //ログインしていないときはAdminでログイン
            SimpleUserData data = new SimpleUserData();
            data.setName(ADMIN_USER);
            p = pCon.upsertPlayer(ADMIN_USER, ADMIN_USER, ADMIN_USER, SignInType.LOCAL, data, session.getLocale());
            session.setToken(p.getToken());
        }

        try {
            gm.constructTemplateWorld();
            gm.startGame();
        } catch (RushHourException e) {
            // do-nothing
        } 
    }
}

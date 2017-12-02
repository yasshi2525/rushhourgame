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
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SignInType;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterUserShowClient;
import net.rushhourgame.json.SimpleUserData;
import net.rushhourgame.json.UserData;

/**
 * 未ログイン時は未ログイン用のデータを返す
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named("player")
@ViewScoped
public class PlayerBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlayerBean.class.getName());
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected PlayerController pCon;
    @Inject
    protected RushHourSession rushHourSession;
    protected SimpleUserData emptyUser;

    protected Player player;
    /**
     * ログインしていない場合は未ログイン時用の値が格納
     */
    protected UserData userData;

    @PostConstruct
    public void init() {
        emptyUser = new SimpleUserData();

        player = pCon.findByToken(rushHourSession.getToken());
        userData = (player != null) ? player.getInfo() : emptyUser;
    }

    @Transactional
    public void logOut() throws IOException {
        ExternalContext context = getFacesContext().getExternalContext();
        LOG.log(Level.FINE, "{0}#logout clear {1}",
                new Object[]{this.getClass().getSimpleName(), rushHourSession.getToken()});
        context.invalidateSession();
        context.redirect("index.xhtml");
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public boolean isSignIn() {
        return player != null;
    }

    public String getName() {
        return userData.getName();
    }

    public String getIconUrl() {
        return userData.getIconUrl();
    }

    public String getColor() {
        return userData.getColor();
    }

    public String getTextColor() {
        return userData.getTextColor();
    }
}

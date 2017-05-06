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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterUserShowClient;
import net.rushhourgame.json.TwitterUserData;

/**
 * 未ログイン時は未ログイン用のデータを返す
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named("player")
@ViewScoped
public class PlayerBean implements Serializable {
    
    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(PlayerBean.class.getName());
    
    @Inject
    transient protected RushHourProperties prop;
    @Inject
    transient protected PlayerController pCon;
    @Inject
    transient protected RushHourSession rushHourSession;
    @Inject
    transient protected TwitterUserShowClient client;
    
    protected Player player;
    protected boolean isSignIn;
    /**
     * ログインしていない場合は未ログイン時用の値が格納
     */
    protected TwitterUserData userData;
    
    @PostConstruct
    public void init() {
        userData = new TwitterUserData();
        isSignIn = pCon.isValidToken(rushHourSession.getToken());
        if (isSignIn) {
            player = pCon.findByToken(rushHourSession.getToken());
            client.setPlayer(player);
            try {
                client.execute();
                userData = client.getUserData();
            } catch (RushHourException ex) {
                LOG.log(Level.SEVERE, this.getClass().getSimpleName() + "#init fail to client#execute", ex);
            }
        }
    }
    
    @Transactional
    public void logOut() throws IOException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (isSignIn) {
            LOG.log(Level.FINE, "{0}#logout clear {1}", 
                    new Object[]{this.getClass().getSimpleName(), rushHourSession.getToken()});
            context.invalidateSession();
        }
        context.redirect("index.xhtml");
    }
    
    public boolean isSignIn() {
        return isSignIn;
    }
    
    public String getDisplayName() {
        if (!isSignIn()) {
            return "no name";
        }
        return player.getDisplayName();
    }
    
    public String getName() {
        return userData.getName();
    }
    
    public String getImageUrl() {
        return userData.getProfile_image_url();
    }
    
    public String getBackGroundColor(){
        return userData.getProfile_link_color();
    }
    
    public String getColor(){
        return userData.getProfile_text_color();
    }
}

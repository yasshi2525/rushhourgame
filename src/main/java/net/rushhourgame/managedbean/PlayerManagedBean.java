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

import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.entity.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import net.rushhourgame.httpclient.TwitterUserShowClient;
import net.rushhourgame.json.TwitterUserData;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named("player")
@ViewScoped
public class PlayerManagedBean implements Serializable {

    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(PlayerManagedBean.class.getName());

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

    @PostConstruct
    public void init() {
        isSignIn = pCon.isValidToken(rushHourSession.getAccessToken());
        if (isSignIn) {
            player = pCon.findByToken(rushHourSession.getAccessToken());
            client.setPlayer(player);
        }
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public String getDisplayName() throws RushHourException {
        if (!isSignIn()) {
            throw new RushHourException(ErrorMessage.createInvalidToken());
        }
        return player.getDisplayName();
    }

    public TwitterUserData getData() throws RushHourException {
        if (!isSignIn()) {
            throw new RushHourException(ErrorMessage.createInvalidToken());
        }
        if (!client.isExecuted()) {
            client.execute();
        }
        return client.getUserData();
    }

    protected Player fetchPlayer(String accessToken) {
        return pCon.findByToken(accessToken);
    }
}

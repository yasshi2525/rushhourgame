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

import net.rushhourgame.entity.Player;
import net.rushhourgame.controller.PlayerController;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class LocalPlayerBean extends PlayerBean {

    protected boolean hasSessionData;
    protected String accessToken;

    public LocalPlayerBean(PlayerController pCon, boolean hasSessionData) {
        this.hasSessionData = hasSessionData;
        this.pCon = pCon;
    }

    public LocalPlayerBean(PlayerController pCon, boolean hasSessionData, String accessToken) {
        this(pCon, hasSessionData);
        this.accessToken = accessToken;
    }

    @Override
    public String getDisplayName() {
        if (!isSignIn()) {
            return null;
        }
        Player p = pCon.findByToken(accessToken);
        if (p == null) {
            return null;
        }
        return p.getDisplayName();
    }

    @Override
    public boolean isSignIn() {
        return pCon.isValidToken(accessToken);
    }
}

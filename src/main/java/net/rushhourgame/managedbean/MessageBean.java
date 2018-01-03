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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.RushHourSession;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "msg")
@ViewScoped
public class MessageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected RushHourSession session;

    @Inject
    protected RushHourResourceBundle res;

    public String getRushHour() {
        return res.get(LABEL_RUSHHOUR, session.getLocale());
    }

    public String getRushHourVer() {
        return res.get(LABEL_RUSHHOUR_VER, session.getLocale());
    }

    public String getError() {
        return res.get(LABEL_ERROR, session.getLocale());
    }

    public String getDetail() {
        return res.get(LABEL_DETAIL, session.getLocale());
    }

    public String getAction() {
        return res.get(LABEL_ACTION, session.getLocale());
    }

    public String getTopTitle() {
        return res.get(LABEL_TOP_TITLE, session.getLocale());
    }

    public String getTwitterSignIn() {
        return res.get(LABEL_TWITTER_SIGNIN, session.getLocale());
    }

    public String getFetchAccessToken() {
        return res.get(LABEL_FETCH_ACCESS_TOKEN, session.getLocale());
    }

    public String getWelcome() {
        return res.get(LABEL_WELCOME, session.getLocale());
    }

    public String getLang() {
        return res.get(LABEL_LANG, session.getLocale());
    }

    public String getLangEn() {
        return res.get(LABEL_LANG_EN, session.getLocale());
    }

    public String getLangJp() {
        return res.get(LABEL_LANG_JP, session.getLocale());
    }

    public String getLogOut() {
        return res.get(LABEL_LOGOUT, session.getLocale());
    }

    public String getRail() {
        return res.get(LABEL_RAIL, session.getLocale());
    }

    public String getStation() {
        return res.get(LABEL_STATION, session.getLocale());
    }

    public String getRoute() {
        return res.get(LABEL_LINE, session.getLocale());
    }

    public String getTrain() {
        return res.get(LABEL_TRAIN, session.getLocale());
    }

    public String getCreate() {
        return res.get(LABEL_CREATE, session.getLocale());
    }

    public String getEdit() {
        return res.get(LABEL_EDIT, session.getLocale());
    }

    public String getRemove() {
        return res.get(LABEL_REMOVE, session.getLocale());
    }

    public String getGoBack() {
        return res.get(LABEL_GO_BACK, session.getLocale());
    }
    
    public String getCancel() {
        return res.get(LABEL_CANCEL, session.getLocale());
    }
    
    public String getCreateRail() {
        return res.get(LABEL_RAIL_CREATE, session.getLocale());
    }
    
    public String getExtendRail () {
        return res.get(LABEL_RAIL_EXTEND, session.getLocale());
    }
    
    public String getRemoveRail () {
        return res.get(LABEL_RAIL_REMOVE, session.getLocale());
    }
    
    public String getConfirmation() {
        return res.get(LABEL_CONFIRM, session.getLocale());
    }
    
    public String getConfirmationMessage() {
        return res.get(LABEL_CONFIRM_MESSAGE, session.getLocale());
    }
}

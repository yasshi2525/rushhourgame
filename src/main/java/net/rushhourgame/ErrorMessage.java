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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * エラー情報を格納するBean.
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class ErrorMessage implements Serializable{
    private static final long serialVersionUID = 1L;

    protected String titleId;
    protected List<String> titleParams = new ArrayList<>();
    protected String detailId;
    protected List<String> detailParams = new ArrayList<>();
    protected String actionId;
    protected List<String> actionParams = new ArrayList<>();
    public static final String NO_CONTENTS = "No Contents";

    public ErrorMessage() {
        this(null, null, null);
    }

    public ErrorMessage(String titleId, String detailId, String actionId) {
        this.titleId = titleId;
        this.detailId = detailId;
        this.actionId = actionId;
    }
    
    
    public String buildTitle(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || titleId == null) {
            return NO_CONTENTS;
        }
        return MessageFormat.format(prop.get(titleId, locale), titleParams.toArray());
    }

    public String buildDetail(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || detailId == null) {
            return NO_CONTENTS;
        }
        return MessageFormat.format(prop.get(detailId, locale), detailParams.toArray());
    }

    public String buildAction(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || actionId == null) {
            return NO_CONTENTS;
        }
        return MessageFormat.format(prop.get(actionId, locale), actionParams.toArray());
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public List<String> getTitleParams() {
        return titleParams;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public List<String> getDetailParams() {
        return detailParams;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public List<String> getActionParams() {
        return actionParams;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" + "titleId=" + titleId + ", detailId=" + detailId + ", actionId=" + actionId + '}';
    }
}

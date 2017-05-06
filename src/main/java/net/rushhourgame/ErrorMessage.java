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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class ErrorMessage implements Serializable {

    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(ErrorMessage.class.getName());
    protected static ErrorMessage _UNKNOWN = new ErrorMessage(UNKNOWN, UNKNOWN_DETAIL, UNKNOWN_ACTION);
    protected static RushHourProperties prop = RushHourProperties.getInstance();
    public static final String NO_CONTENTS = "No Contents";
   
    protected String titleId;
    protected List<String> titleParams = new ArrayList<>();
    protected String detailId;
    protected List<String> detailParams = new ArrayList<>();
    protected String actionId;
    protected List<String> actionParams = new ArrayList<>();

    public ErrorMessage() {
        this(null, null, null);
    }

    public ErrorMessage(String titleId, String detailId, String actionId) {
        this.titleId = titleId;
        this.detailId = detailId;
        this.actionId = actionId;
    }
    
    /**
     * 原因が不明のエラー(カテゴリ不明)
     *
     * @return
     */
    public static ErrorMessage getUnkownError() {
        return _UNKNOWN;
    }

    /**
     * 原因が不明のエラー(カテゴリは分かる)
     *
     * @param titleId
     * @return
     */
    public static ErrorMessage createUnkownError(String titleId) {
        return new ErrorMessage(titleId, UNKNOWN_DETAIL, UNKNOWN_ACTION);
    }
    
    /**
     * バグによるエラー. プロパティ値の設定ミスなど
     *
     * @param titleId
     * @param detailId
     * @return
     */
    public static ErrorMessage createSystemError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SYSTEM_ERR_ACTION);
        errMsg.getActionParams().add(prop.get(ADMINISTRATOR));
        return errMsg;
    }

    /**
     * バグによるエラー. プロパティ値の設定ミスなど
     *
     * @param titleId
     * @param detailId
     * @param detailParams
     * @return
     */
    public static ErrorMessage createSystemError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SYSTEM_ERR_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        errMsg.getActionParams().add(prop.get(ADMINISTRATOR));
        return errMsg;
    }
    
    /**
     * 再サインインしないと直らないエラー. サインイン中のエラーなど
     *
     * @param titleId
     * @param detailId
     * @return
     */
    public static ErrorMessage createReSignInError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SIGNIN_FAIL_ACTION);
        return errMsg;
    }

    /**
     * 再サインインしないと直らないエラー. サインイン中のエラーなど
     *
     * @param titleId
     * @param detailId
     * @param detailParams
     * @return
     */
    public static ErrorMessage createReSignInError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SIGNIN_FAIL_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        return errMsg;
    }
    
    /**
     * 再リクエストすればなおるエラー
     *
     * @param titleId
     * @param detailId
     * @return
     */
    public static ErrorMessage createRetryError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SERVER_ERR_ACTION);
        return errMsg;
    }

    /**
     * 再リクエストすればなおるエラー
     *
     * @param titleId
     * @param detailId
     * @param detailParams
     * @return
     */
    public static ErrorMessage createRetryError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SERVER_ERR_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        return errMsg;
    }
    
    /**
     * トークンが古い/不正
     *
     * @return
     */
    public static ErrorMessage createInvalidToken() {
        ErrorMessage errMsg = new ErrorMessage(REQUEST_FAIL, REQUEST_FAIL_INVALID_TOKEN, SIGNIN_FAIL_ACTION);
        return errMsg;
    }
    
    public static ErrorMessage createNoPrivileged(String detailId){
        ErrorMessage errMsg = new ErrorMessage(GAME_NO_PRIVILEDGE, detailId, GAME_NO_PRIVILEDGE_ACTION);
        return errMsg;
    }

    public String buildTitle(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || titleId == null || prop.get(titleId, locale) == null) {
            return NO_CONTENTS;
        }
        return MessageFormat.format(prop.get(titleId, locale), titleParams.toArray());
    }

    public String buildDetail(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || detailId == null || prop.get(detailId, locale) == null) {
            return NO_CONTENTS;
        }
        return MessageFormat.format(prop.get(detailId, locale), detailParams.toArray());
    }

    public String buildAction(RushHourResourceBundle prop, Locale locale) {
        if (prop == null || actionId == null || prop.get(actionId, locale) == null) {
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

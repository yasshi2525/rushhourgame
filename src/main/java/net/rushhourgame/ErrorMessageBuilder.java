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
import java.util.Arrays;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import static net.rushhourgame.RushHourProperties.*;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.Pointable;

/**
 * エラーメッセージを作成する.
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public class ErrorMessageBuilder implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ErrorMessageBuilder.class.getName());

    protected static ErrorMessageBuilder instance;
    
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected RushHourResourceBundle msg;
    @Inject
    protected RushHourSession session;
    
    /**
     * 原因が不明のエラー(カテゴリ不明)
     *
     * @return ErrorMessage
     */
    public ErrorMessage createUnkownError() {
        return new ErrorMessage(UNKNOWN, UNKNOWN_DETAIL, UNKNOWN_ACTION);
    }

    /**
     * 原因が不明のエラー(カテゴリは分かる)
     *
     * @param titleId titleid
     * @return ErrorMessage
     */
    public ErrorMessage createUnkownError(String titleId) {
        return new ErrorMessage(titleId, UNKNOWN_DETAIL, UNKNOWN_ACTION);
    }
    
    /**
     * バグによるエラー. プロパティ値の設定ミスなど
     *
     * @param titleId titleid
     * @param detailId detailId
     * @return ErrorMessage
     */
    public ErrorMessage createSystemError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SYSTEM_ERR_ACTION);
        errMsg.getActionParams().add(prop.get(ADMINISTRATOR));
        return errMsg;
    }

    /**
     * バグによるエラー. プロパティ値の設定ミスなど
     *
     * @param titleId titleid
     * @param detailId detailId
     * @param detailParams detailParams
     * @return ErrorMessage
     */
    public ErrorMessage createSystemError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SYSTEM_ERR_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        errMsg.getActionParams().add(prop.get(ADMINISTRATOR));
        return errMsg;
    }
    
    /**
     * 再サインインしないと直らないエラー. サインイン中のエラーなど
     *
     * @param titleId titleId
     * @param detailId detailId
     * @return ErrorMessage
     */
    public ErrorMessage createReSignInError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SIGNIN_FAIL_ACTION);
        return errMsg;
    }

    /**
     * 再サインインしないと直らないエラー. サインイン中のエラーなど
     *
     * @param titleId titleId
     * @param detailId detailId
     * @param detailParams detailParams
     * @return ErrorMessage
     */
    public ErrorMessage createReSignInError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SIGNIN_FAIL_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        return errMsg;
    }
    
    /**
     * 再リクエストすればなおるエラー
     *
     * @param titleId titleId
     * @param detailId detailId
     * @return ErrorMessage
     */
    public ErrorMessage createRetryError(String titleId, String detailId) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SERVER_ERR_ACTION);
        return errMsg;
    }

    /**
     * 再リクエストすればなおるエラー
     *
     * @param titleId titleId
     * @param detailId detailId
     * @param detailParams detailParams
     * @return ErrorMessage
     */
    public ErrorMessage createRetryError(String titleId, String detailId, String... detailParams) {
        ErrorMessage errMsg = new ErrorMessage(titleId, detailId, SERVER_ERR_ACTION);
        errMsg.getDetailParams().addAll(Arrays.asList(detailParams));
        return errMsg;
    }
    
    public ErrorMessage createNoPrivileged(String detailId){
        ErrorMessage errMsg = new ErrorMessage(GAME_NO_PRIVILEDGE, detailId, GAME_NO_PRIVILEDGE_ACTION);
        return errMsg;
    }
    
    public ErrorMessage createDataInconsitency(String detailId){
        ErrorMessage errMsg = new ErrorMessage(GAME_DATA_INCONSIST, detailId, GAME_DATA_INCONSIST_ACTION);
        return errMsg;
    }
    
    public ErrorMessage createResidenceDuplication(Pointable p) {
        ErrorMessage errMsg = createDuplication();
        errMsg.getTitleParams().add(msg.get(LABEL_RESIDENCE, session.getLocale()));
        errMsg.getDetailParams().add(msg.get(LABEL_RESIDENCE, session.getLocale()));
        errMsg.getDetailParams().add(Arrays.toString(new Double[] {p.getX(), p.getY()}));
        return errMsg;
    }
    
    public ErrorMessage createCompanyDuplication(Pointable p) {
        ErrorMessage errMsg = createDuplication();
        errMsg.getTitleParams().add(msg.get(LABEL_COMPANY, session.getLocale()));
        errMsg.getDetailParams().add(msg.get(LABEL_COMPANY, session.getLocale()));
        errMsg.getDetailParams().add(Arrays.toString(new Double[] {p.getX(), p.getY()}));
        return errMsg;
    }
    
    public ErrorMessage createRailNodeDuplication(Pointable p) {
        ErrorMessage errMsg = createDuplication();
        errMsg.getTitleParams().add(msg.get(LABEL_RAIL, session.getLocale()));
        errMsg.getDetailParams().add(msg.get(LABEL_RAIL, session.getLocale()));
        errMsg.getDetailParams().add(Arrays.toString(new Double[] {p.getX(), p.getY()}));
        return errMsg;
    }
    
    public ErrorMessage createStationNameDuplication(String name) {
        ErrorMessage errMsg = createDuplication();
        errMsg.getTitleParams().add(prop.get(LABEL_STATION_NAME));
        errMsg.getDetailParams().add(prop.get(LABEL_STATION_NAME));
        errMsg.getDetailParams().add(prop.get(name));
        return errMsg;
    }
    
    public ErrorMessage createLineNameDuplication(String name) {
        ErrorMessage errMsg = createDuplication();
        errMsg.getTitleParams().add(prop.get(LABEL_LINE));
        errMsg.getDetailParams().add(prop.get(LABEL_LINE_NAME));
        errMsg.getDetailParams().add(prop.get(name));
        return errMsg;
    }
    
    protected ErrorMessage createDuplication() {
        return new ErrorMessage(GAME_DUP, GAME_DUP_DETAIL, GAME_DUP_ACTION);
    }
    
        
    /**
     * CDIが使えない環境用. 
     * @see RushHourExceptionHandler
     * @return ErrorMessageBuilder
     */
    public static ErrorMessageBuilder getInstance() {
        if (instance == null) {
            instance = new ErrorMessageBuilder();
            instance.prop = RushHourProperties.getInstance();
            instance.msg = RushHourResourceBundle.getInstance();
            instance.session = RushHourSession.getSimpleSession();
        }
        return instance;
    }

}

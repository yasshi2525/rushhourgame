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
package net.rushhourgame.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * プレイヤ
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"signIn", "userId"}),
    @UniqueConstraint(columnNames = {"token"}),
})
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Player.findByUserIdDigest",
            query = "SELECT x FROM Player x JOIN FETCH x.info WHERE x.userIdDigest = :userIdDigest AND x.signIn = :signIn")
    ,@NamedQuery(
            name = "Player.findByToken",
            query = "SELECT x FROM Player x JOIN FETCH x.info WHERE x.token = :token"
    )
})
public class Player extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    protected SignInType signIn;

    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String userId;
    
    @NotNull
    protected String userIdDigest;

    protected String token;

    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String accessToken;
    
    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String accessTokenSecret;

    @NotNull
    @OneToOne(cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    protected PlayerInfo info;

    public PlayerInfo getInfo() {
        return info;
    }

    public void setInfo(PlayerInfo info) {
        this.info = info;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    /**
     * 外部サービス接続時のユーザID
     *
     * @return String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 外部サービス接続時のユーザID
     *
     * @param userId userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIdDigest() {
        return userIdDigest;
    }

    public void setUserIdDigest(String userIdDigest) {
        this.userIdDigest = userIdDigest;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public SignInType getSignIn() {
        return signIn;
    }

    public void setSignIn(SignInType signIn) {
        this.signIn = signIn;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }
}

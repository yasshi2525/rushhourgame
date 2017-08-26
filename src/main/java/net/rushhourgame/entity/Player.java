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

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Table(indexes = {@Index(columnList = "ID"), @Index(columnList = "TOKEN")})
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Player.existsId",
            query = "SELECT CASE WHEN count(x.id) > 0 THEN true ELSE false END"
            + " FROM Player x WHERE x.id = :id")
    ,@NamedQuery(
            name = "Player.existsToken",
            query = "SELECT CASE WHEN count(x.id) > 0 THEN true ELSE false END"
            + " FROM Player x WHERE x.token = :token")
    ,@NamedQuery(
            name = "Player.findById",
            query = "SELECT x FROM Player x WHERE x.id = :id")
    ,@NamedQuery(
            name = "Player.findByToken",
            query = "SELECT x FROM Player x WHERE x.token = :token"
    )
})
public class Player extends OwnerEntity implements Serializable {
    private static final long serialVersionUID = 1;
    @Id
    protected String id;
    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String userId;
    @Column(unique = true)
    protected String token;
    @OneToOne(orphanRemoval = true)
    protected OAuth oauth;
    @NotNull
    protected SignInType signIn;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    /**
     * 外部サービス接続時のユーザID
     * @return 
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 外部サービス接続時のユーザID
     * @param userId 
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public SignInType getSignIn() {
        return signIn;
    }

    public void setSignIn(SignInType signIn) {
        if(signIn == null){
            signIn = SignInType.LOCAL;
        }
        this.signIn = signIn;
    }
}

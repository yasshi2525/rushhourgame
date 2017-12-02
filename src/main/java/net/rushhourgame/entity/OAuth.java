/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"requestToken", "signIn"}))
@NamedQueries({
    @NamedQuery(
            name = "OAuth.findByRequestTokenDigest",
            query = "SELECT x FROM OAuth x WHERE x.requestTokenDigest = :requestTokenDigest AND x.signIn = :signIn")
    ,
        @NamedQuery(
            name = "OAuth.purgeOld",
            query = "DELETE FROM OAuth o WHERE o.updated < :threshold"
    )
}
)
public class OAuth extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    protected String requestTokenDigest;

    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String requestToken;

    @NotNull
    @Convert(converter = EncryptConverter.class)
    protected String requestTokenSecret;

    @NotNull
    protected SignInType signIn;

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getRequestTokenDigest() {
        return requestTokenDigest;
    }

    public void setRequestTokenDigest(String requestTokenDigest) {
        this.requestTokenDigest = requestTokenDigest;
    }

    public String getRequestTokenSecret() {
        return requestTokenSecret;
    }

    public void setRequestTokenSecret(String requestTokenSecret) {
        this.requestTokenSecret = requestTokenSecret;
    }

    public SignInType getSignIn() {
        return signIn;
    }

    public void setSignIn(SignInType signIn) {
        this.signIn = signIn;
    }
}

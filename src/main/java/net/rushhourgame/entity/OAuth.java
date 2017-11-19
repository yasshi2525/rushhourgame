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

import java.util.logging.Logger;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Table(indexes = {@Index(columnList = "requestToken"), @Index(columnList = "accessToken")})
@NamedQueries({
    @NamedQuery(
            name = "OAuth.isValidRequestTokenDigest",
            query = "SELECT CASE WHEN count(x.requestTokenDigest) > 0 THEN true ELSE false END"
            + " FROM OAuth x WHERE x.requestTokenDigest = :requestTokenDigest"), 
    @NamedQuery(
            name = "OAuth.findByRequestTokenDigest",
            query = "SELECT x FROM OAuth x WHERE x.requestTokenDigest = :requestTokenDigest"),
    @NamedQuery(
            name = "OAuth.isValidRequestToken",
            query = "SELECT CASE WHEN count(x.requestTokenDigest) > 0 THEN true ELSE false END"
            + " FROM OAuth x WHERE x.requestToken = :requestToken"), 
    @NamedQuery(
            name = "OAuth.findByRequestToken",
            query = "SELECT x FROM OAuth x WHERE x.requestToken = :requestToken"),
    @NamedQuery(
            name = "OAuth.findByThreshold",
            query = "DELETE FROM OAuth o WHERE o.updated < :threshold AND o.player = NULL"
    )
})
@Entity
public class OAuth extends AbstractEntity {

    private static final Logger LOG = Logger.getLogger(OAuth.class.getName());

    private static final long serialVersionUID = 1L;

    protected String requestTokenDigest;
    @Convert(converter = EncryptConverter.class)
    protected String requestToken;
    @Convert(converter = EncryptConverter.class)
    protected String requestTokenSecret;
    @Convert(converter = EncryptConverter.class)
    protected String oAuthVerifier;
    @Convert(converter = EncryptConverter.class)
    protected String accessToken;
    protected String accessTokenSecret;
    @OneToOne(mappedBy = "oauth")
    protected Player player;
    
    public String getRequestTokenDigest() {
        return requestTokenDigest;
    }
    
    public void setRequestTokenDigest(String requestTokenDigest) {
        this.requestTokenDigest = requestTokenDigest;
    }

    public String getRequestToken() {
        return requestToken;
    }
    
    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getRequestTokenSecret() {
        return requestTokenSecret;
    }
    
    public void setRequestTokenSecret(String requestTokenSecret) {
        this.requestTokenSecret = requestTokenSecret;
    }

    public String getOAuthVerifier() {
        return oAuthVerifier;
    }

    public void setOAuthVerifier(String oAuthVerifier) {
        this.oAuthVerifier = oAuthVerifier;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }
}

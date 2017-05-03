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
import java.util.logging.Logger;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Table(indexes = {@Index(columnList = "requestToken"), @Index(columnList = "accessToken")})
@NamedQueries({
    @NamedQuery(
            name = "OAuth.isValidId",
            query = "SELECT CASE WHEN count(x.id) > 0 THEN true ELSE false END"
            + " FROM OAuth x WHERE x.id = :id")
    , @NamedQuery(
            name = "OAuth.findById",
            query = "SELECT x FROM OAuth x WHERE x.id = :id"),
    @NamedQuery(
            name = "OAuth.isValidRequestToken",
            query = "SELECT CASE WHEN count(x.id) > 0 THEN true ELSE false END"
            + " FROM OAuth x WHERE x.requestToken = :requestToken")
    , @NamedQuery(
            name = "OAuth.findByRequestToken",
            query = "SELECT x FROM OAuth x WHERE x.requestToken = :requestToken")
})
@Entity
public class OAuth extends AbstractEntity implements Serializable {

    private static final Logger LOG = Logger.getLogger(OAuth.class.getName());

    private static final long serialVersionUID = 1L;
    @Id
    protected String id;
    @Convert(converter = EncryptConverter.class)
    protected String requestToken;
    @Convert(converter = EncryptConverter.class)
    protected String requestTokenSecret;
    @Convert(converter = EncryptConverter.class)
    protected String oauthVerifier;
    @Convert(converter = EncryptConverter.class)
    protected String accessToken;
    protected String accessTokenSecret;
    
    /*@ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(
            foreignKey = @ForeignKey(
                    name = "fk_oauth_player_id",
                    foreignKeyDefinition = "FOREIGN KEY (player_id) REFERENCES player (id) ON UPDATE SET NULL"))
    protected Player player;*/

    protected String getId() {
        return id;
    }
    
    protected void setId(String id) {
        this.id = id;
    }

    protected String getRequestToken() {
        return requestToken;
    }
    
    protected void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getRequestTokenSecret() {
        return requestTokenSecret;
    }
    
    protected void setRequestTokenSecret(String requestTokenSecret) {
        this.requestTokenSecret = requestTokenSecret;
    }

    protected String getOauthVerifier() {
        return oauthVerifier;
    }

    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }

    protected String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }
}

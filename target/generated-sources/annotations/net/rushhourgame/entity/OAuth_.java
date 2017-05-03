package net.rushhourgame.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.4.v20160829-rNA", date="2017-05-04T07:18:35")
@StaticMetamodel(OAuth.class)
public class OAuth_ extends AbstractEntity_ {

    public static volatile SingularAttribute<OAuth, String> requestTokenSecret;
    public static volatile SingularAttribute<OAuth, String> oauthVerifier;
    public static volatile SingularAttribute<OAuth, String> id;
    public static volatile SingularAttribute<OAuth, String> requestToken;
    public static volatile SingularAttribute<OAuth, String> accessToken;
    public static volatile SingularAttribute<OAuth, String> accessTokenSecret;

}
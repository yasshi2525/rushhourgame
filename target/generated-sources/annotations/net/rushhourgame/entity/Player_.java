package net.rushhourgame.entity;

import java.util.Locale;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.rushhourgame.entity.OAuth;

@Generated(value="EclipseLink-2.6.4.v20160829-rNA", date="2017-05-04T07:18:35")
@StaticMetamodel(Player.class)
public class Player_ extends AbstractEntity_ {

    public static volatile SingularAttribute<Player, String> displayName;
    public static volatile SingularAttribute<Player, String> icon;
    public static volatile SingularAttribute<Player, String> id;
    public static volatile SingularAttribute<Player, Locale> locale;
    public static volatile SingularAttribute<Player, String> userId;
    public static volatile SingularAttribute<Player, OAuth> oauth;
    public static volatile SingularAttribute<Player, String> token;

}
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Startup
@Singleton
public class RushHourProperties implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RushHourProperties.class.getName());

    protected static RushHourProperties INSTANCE;

    // constants.properties ----------------------------------------------------
    public static final String CONFIG_PATH = "rushhour.config.path";

    public static final String DIGEST_ALGORITHM = "rushhour.digest.algorithm";
    public static final String ENCRYPT_ALGORITHM = "rushhour.encrypt.algorithm";
    public static final String ENCRYPT_TRANSFORM = "rushhour.encrypt.transform";

    public static final String TWITTER_API_REQ_TOKEN = "rushhour.twitter.api.request_token";
    public static final String TWITTER_API_ACCESS_TOKEN = "rushhour.twitter.api.access_token";
    public static final String TWITTER_API_AUTHENTICATE = "rushhour.twitter.api.authenticate";
    public static final String TWITTER_API_USERS_SHOW = "rushhour.twitter.api.users.show";

    //--------------------------------------------------------------------------
    // template_config.properties ----------------------------------------------
    public static final String TWITTER_CONSUMER_KEY = "rushhour.twitter.consumerKey";
    public static final String TWITTER_CONSUMER_SECRET = "rushhour.twitter.consumerSecret";
    public static final String TWITTER_CALLBACK_URL = "rushhour.twitter.callbackUrl";

    public static final String ROOT_PASSWORD = "rushhour.root.password";
    public static final String DIGEST_SALT = "rushhour.digest.salt";
    public static final String ENCRYPT_KEY = "rushhour.encrypt.key";

    public static final String PERMIT_ROOT_LOGIN = "rushhour.permitRootLogin";

    public static final String ADMINISTRATOR = "rushhour.administrator";
    public static final String LANG = "rushhour.lang";

    public static final String GAME_DEF_OAUTH_PURGE_DAY = "rushhour.game.default.oauth.purgeday";
    public static final String GAME_DEF_ABS_SCALE = "rushhour.game.default.absorber.scale";

    //--------------------------------------------------------------------------
    protected static final String CONSTANTS_PATH = "constants.properties";
    protected static final String TEMPLATE_CONFIG_PATH = "template_config.properties";

    protected Properties constants;
    protected Properties config;

    /**
     * デフォルトの設定とユーザの設定を読み込む。
     */
    @PostConstruct
    public void init() {
        LOG.log(Level.FINE, "{0}#init start", this.getClass().getSimpleName());

        constants = new Properties();
        config = new Properties();

        // デフォルトの設定をロード
        try {
            ClassLoader loader = RushHourProperties.class.getClassLoader();

            try (InputStream is = loader.getResourceAsStream(CONSTANTS_PATH)) {
                constants.load(is);
            }
            try (InputStream is = loader.getResourceAsStream(TEMPLATE_CONFIG_PATH)) {
                config.load(is);
            }

            LOG.log(Level.INFO, "{0}#init success to load default config", this.getClass().getSimpleName());

            // ユーザ設定のロード
            Path userConfig = FileSystems.getDefault()
                    .getPath(constants.getProperty(CONFIG_PATH));
            LOG.log(Level.FINE, "{0}#init user config path = {1}",
                    new Object[]{this.getClass().getSimpleName(), userConfig.toAbsolutePath()});

            if (Files.isDirectory(userConfig)) {
                //ディレクトリとして存在する場合は作成しない
                LOG.log(Level.WARNING, "{0}#init faiure to create user config file"
                        + " because path is already exists. (path = {1})",
                        new Object[]{this.getClass().getSimpleName()});
                return;
            }

            if (Files.exists(userConfig)) {
                // ユーザ設定ファイルがあれば、ロード
                try (InputStream is = Files.newInputStream(userConfig)) {
                    config.load(is);
                }
                LOG.log(Level.INFO, "{0}#init success to load user config", this.getClass().getSimpleName());

            } else {
                // ユーザ設定ファイルがなければ新規作成
                try (InputStream is = loader.getResourceAsStream(TEMPLATE_CONFIG_PATH)) {
                    Files.copy(is, userConfig, StandardCopyOption.REPLACE_EXISTING);
                }
                LOG.log(Level.INFO, "{0}#init success to create user config");
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, this.getClass().getSimpleName() + "#init error during default config loading.", ex);
        }
    }

    @PreDestroy
    public void autosave() {
        LOG.log(Level.INFO, "{0}#autosave", this.getClass().getSimpleName());
        store();
    }

    /**
     * CDIが使えない場合
     *
     * @return RushHourPropertiesインスタンス
     */
    public static RushHourProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RushHourProperties();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    synchronized public String get(String key) {
        if (config.containsKey(key)) {
            return config.getProperty(key);
        } else if (constants.containsKey(key)) {
            return constants.getProperty(key);
        } else {
            return null;
        }
    }

    synchronized public String get(String key, String defaultValue) {
        if (config.containsKey(key)) {
            return config.getProperty(key);
        } else if (constants.containsKey(key)) {
            return constants.getProperty(key);
        } else {
            return defaultValue;
        }
    }

    synchronized public void update(String key, String value) {
        if (config.containsKey(key)) {
            config.setProperty(key, value);
            return;
        } else if (constants.containsKey(key)) {
            constants.setProperty(key, value);
            return;
        }
        //更新対象がなければconfigに新規キーを作成
        config.setProperty(key, value);
    }

    synchronized public boolean store() {
        try {
            //Properties#storeは前のファイルの文字を置き換えていくため、以前の内容が残ってしまう
            Path userConfig = FileSystems.getDefault()
                    .getPath(constants.getProperty(CONFIG_PATH));
            Files.deleteIfExists(userConfig);
            Files.createFile(userConfig);

            try (OutputStream os = Files.newOutputStream(userConfig, StandardOpenOption.WRITE)) {
                config.store(os, null);
            }
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, this.getClass().getSimpleName()
                    + "#store failed.", ex);
            return false;
        }
    }
}

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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class RushHourPropertiesTest {

    private static final Logger LOG = Logger.getLogger(RushHourPropertiesTest.class.getName());
    protected static final String CONFIG_PATH = "tmp_test_config.properties";

    RushHourProperties inst;

    @BeforeClass
    public static void setUpClass() throws IOException {
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        Files.deleteIfExists(configPath);
    }

    @Before
    public void setUp() {
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        // 設定ファイルを出力するので、
        // 上書きしてしまわないよう、すでに存在する場合はテスト失敗
        if (Files.exists(configPath)) {
            fail("remove path to execute unit test : " + configPath.toAbsolutePath());
        }
        inst = new RushHourProperties();
    }

    @After
    public void tearDown() throws IOException {
        LOG.log(Level.INFO, "{0}#tearDown", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        
        // 作成したファイルを削除
        Files.deleteIfExists(configPath);
        if(Files.exists(configPath)){
            fail("fail to remove user config file.");
        }
        
        LOG.log(Level.INFO, "{0}#tearDown delete {1}",
                new Object[]{this.getClass().getSimpleName(), configPath});
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        LOG.log(Level.INFO, "{0}#tearDownClass", new Object[]{RushHourPropertiesTest.class.getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        Files.deleteIfExists(configPath);
        
        if(Files.exists(configPath)){
            fail("fail to remove user config file.");
        }
        
        LOG.log(Level.INFO, "{0}#tearDown delete {1}",
                new Object[]{RushHourPropertiesTest.class.getClass().getSimpleName(), configPath});
    }

    @Test
    public void testInit() {
        LOG.log(Level.INFO, "{0}#testInit", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        // ユーザ設定ファイルは作成されない
        assertFalse(Files.exists(configPath));
    }

    @Test
    public void testInitLoadingUserConfig() throws IOException {
        LOG.log(Level.INFO, "{0}#testInitLoadingUserConfig", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("user_config_base.properties");
        Files.copy(in, configPath, StandardCopyOption.REPLACE_EXISTING);
        inst.init();
        assertEquals("changedPassword", inst.get(RushHourProperties.ROOT_PASSWORD));
    }

    @Test
    public void testInitConfigExists() throws IOException {
        LOG.log(Level.INFO, "{0}#testInitConfigExists", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        Files.createFile(configPath);
        inst.init();
        assertTrue(Files.exists(configPath));

        assertTrue(inst.store());
    }

    /**
     * 設定ファイルと同名のディレクトリが存在する時失敗
     *
     * @throws IOException
     */
    @Test
    public void testInitDirExists() throws IOException {
        LOG.log(Level.INFO, "{0}#testInitDirExists", new Object[]{this.getClass().getSimpleName()});
        Path dirPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        Files.createDirectory(dirPath);
        
        Path filePath = FileSystems.getDefault().getPath(CONFIG_PATH + "/tmp");
        Files.createFile(filePath);
        inst.init();
        assertTrue(Files.exists(filePath));
        assertFalse(inst.store());
        
        Files.delete(filePath);
        Files.delete(dirPath);
    }

    @Test
    public void testStore() {
        LOG.log(Level.INFO, "{0}#testStore", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        assertTrue(inst.store());
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void testStoreOverwritten() throws IOException {
        LOG.log(Level.INFO, "{0}#testStoreOverwritten", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        Files.deleteIfExists(configPath);
        Files.createFile(configPath);
        assertTrue(inst.store());
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void testUpdate() {
        LOG.log(Level.INFO, "{0}#testUpdate", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        assertEquals("config", inst.get("rushhour.test.forupdate"));
        inst.update("rushhour.test.forupdate", "updated");
        assertEquals("updated", inst.get("rushhour.test.forupdate"));
        inst.store();

        inst = new RushHourProperties();
        inst.init();
        assertEquals("updated", inst.get("rushhour.test.forupdate"));
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void testUpdateNewKey() {
        LOG.log(Level.INFO, "{0}#testUpdateNewKey", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        assertNull(inst.get("rushhour.test.unexistkey"));
        inst.update("rushhour.test.unexistkey", "created");
        assertEquals("created", inst.get("rushhour.test.unexistkey"));
        inst.store();

        inst = new RushHourProperties();
        inst.init();
        assertEquals("created", inst.get("rushhour.test.unexistkey"));
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void testGet() {
        LOG.log(Level.INFO, "{0}#testGet", new Object[]{this.getClass().getSimpleName()});
        inst.init();
        assertEquals("test", inst.get("rushhour.test"));
        //存在しない値は読み込めない
        assertNull(inst.get("rushhour.unexistsparameter"));
    }

    @Test
    public void testGetDefault() {
        LOG.log(Level.INFO, "{0}#testGetDefault", new Object[]{this.getClass().getSimpleName()});
        inst.init();
        assertEquals("default", inst.get("rushhour.unexistsparameter", "default"));
        //存在する値にはデフォルト値無効
        assertEquals("constants", inst.get("rushhour.test.onlyConstants", "default"));
        assertEquals("config", inst.get("rushhour.test.onlyConfig", "default"));
    }

    /**
     * constantsとconfigに同じキーの値がある場合、configが優先
     */
    @Test
    public void testGetConfigPrinorToConstants() {
        LOG.log(Level.INFO, "{0}#testGetConfigPrinorToConstants", new Object[]{this.getClass().getSimpleName()});
        inst.init();
        assertEquals("config", inst.get("rushhour.test.file"));
    }
}

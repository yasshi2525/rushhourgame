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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
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
        if (Files.exists(configPath)) {
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

        if (Files.exists(configPath)) {
            fail("fail to remove user config file.");
        }

        LOG.log(Level.INFO, "{0}#tearDown delete {1}",
                new Object[]{RushHourPropertiesTest.class.getClass().getSimpleName(), configPath});
    }

    @Test
    public void testInit() throws IOException {
        LOG.log(Level.INFO, "{0}#testInit", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        // ユーザ設定ファイルが作成される
        assertTrue(Files.exists(configPath));

        Files.delete(configPath);
    }

    @Test
    public void testAutosave() {
        LOG.log(Level.INFO, "{0}#testAutosave", new Object[]{this.getClass().getSimpleName()});
        inst.init();
        inst.autosave();
    }
    
    @Test
    public void testAutosaveWatchServiceIOExeption() throws IOException {
        LOG.log(Level.INFO, "{0}#testAutosaveWatchServiceIOExeption", new Object[]{this.getClass().getSimpleName()});
        WatchService ws = mock(WatchService.class);
        inst.watchService = ws;
        doThrow(IOException.class).when(ws).close();
        
        inst.init();
        inst.autosave();
        
        verify(ws, times(1)).close();
    }

    @Test
    public void testInitIOException() throws IOException {
        LOG.log(Level.INFO, "{0}#testInitIOException", new Object[]{this.getClass().getSimpleName()});
        Properties propMock = mock(Properties.class);
        inst.constants = propMock;
        doThrow(IOException.class).when(propMock).load(any(InputStream.class));

        inst.init();

        verify(propMock, times(1)).load(any(InputStream.class));
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
    public void testUpdateConfig() {
        LOG.log(Level.INFO, "{0}#testUpdateConfig", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        assertEquals("config", inst.get("rushhour.test.forupdateconfig"));
        inst.update("rushhour.test.forupdateconfig", "updated");
        assertEquals("updated", inst.get("rushhour.test.forupdateconfig"));
        inst.store();

        inst = new RushHourProperties();
        inst.init();
        assertEquals("updated", inst.get("rushhour.test.forupdateconfig"));
        assertTrue(Files.exists(configPath));
    }

    @Test
    public void testUpdateConstants() {
        LOG.log(Level.INFO, "{0}#testUpdateConstants", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        inst.init();
        assertEquals("constants", inst.get("rushhour.test.forupdateconst"));
        inst.update("rushhour.test.forupdateconst", "updated");
        assertEquals("constants", inst.get("rushhour.test.forupdateconst"));
        inst.store();

        inst = new RushHourProperties();
        inst.init();
        assertEquals("constants", inst.get("rushhour.test.forupdateconst"));
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

    /**
     * コンフィグファイルの既存の値を更新する
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testConfigWatchingService() throws InterruptedException, IOException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingService", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);

        ExecutorService es = mock(ExecutorService.class);
        inst.executorService = es;
        
        inst.init();
        assertEquals("config", inst.get("rushhour.test.forupdateconfig"));

        new Thread(inst.new ConfigWatchingService(configPath, inst.watchService)).start();

        Thread.sleep(1000L);

        //ファイルを更新する
        try (BufferedWriter bw = Files.newBufferedWriter(configPath, StandardOpenOption.APPEND)) {
            bw.write("rushhour.test.forupdateconfig=updated");
        } catch (IOException ex) {
            Logger.getLogger(RushHourPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread.sleep(1000L);
        assertEquals("updated", inst.get("rushhour.test.forupdateconfig"));

        // watchserviceをclose.
        inst.autosave();
    }

    /**
     * WatchServiceに登録するときIOException.
     *
     * @throws InterruptedException
     */
    @Test
    public void testConfigWatchingServiceIOException() throws InterruptedException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingServiceIOException", new Object[]{this.getClass().getSimpleName()});

        Path configPath = mock(Path.class);
        doThrow(IOException.class).when(configPath).toAbsolutePath();

        WatchService watchService = mock(WatchService.class);

        RushHourProperties.ConfigWatchingService service
                = inst.new ConfigWatchingService(configPath, watchService);

        service.run();

        verify(watchService, times(0)).take();
    }

    /**
     * WatchServiceのイベントを取得したときInterruptedException.
     *
     * @throws InterruptedException
     */
    @Test
    public void testConfigWatchingServiceInterruptedException() throws InterruptedException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingServiceInterruptedException", new Object[]{this.getClass().getSimpleName()});
        Path configPath = mock(Path.class);
        WatchService watchService = mock(WatchService.class);

        when(configPath.toAbsolutePath()).thenReturn(configPath);
        when(configPath.getParent()).thenReturn(configPath);

        doThrow(InterruptedException.class).when(watchService).take();

        RushHourProperties.ConfigWatchingService service
                = inst.new ConfigWatchingService(configPath, watchService);

        service.run();

        verify(watchService, times(1)).take();
    }

    /**
     * Overflowイベントが発生して終了.
     *
     * @throws InterruptedException
     */
    @Test
    public void testConfigWatchingServiceOverflow() throws InterruptedException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingServiceOverflow", new Object[]{this.getClass().getSimpleName()});
        Path configPath = mock(Path.class);
        WatchService watchService = mock(WatchService.class);
        WatchKey watchKey = mock(WatchKey.class);
        WatchEvent watchEvent = mock(WatchEvent.class);

        List<WatchEvent<?>> eventList = new ArrayList<>();
        eventList.add(watchEvent);

        when(configPath.toAbsolutePath()).thenReturn(configPath);
        when(configPath.getParent()).thenReturn(configPath);

        when(watchService.take()).thenReturn(watchKey);

        when(watchKey.pollEvents()).thenReturn(eventList);

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.OVERFLOW);

        RushHourProperties.ConfigWatchingService service
                = inst.new ConfigWatchingService(configPath, watchService);

        service.run();

        verify(watchService, times(1)).take();
        verify(watchEvent, times(1)).kind();
    }

    /**
     * configにloadするときIOException.
     *
     * @throws InterruptedException
     */
    @Test
    public void testConfigWatchingServiceLoadIOException() throws InterruptedException, IOException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingServiceLoadIOException", new Object[]{this.getClass().getSimpleName()});
        Path configPath = FileSystems.getDefault().getPath(CONFIG_PATH);
        Files.createFile(configPath);
        Properties config = spy(Properties.class);
        doThrow(IOException.class).when(config).load(any(InputStream.class));

        inst.config = config;

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            new Thread(inst.new ConfigWatchingService(configPath, watchService)).start();

            Thread.sleep(1000L);

            //ファイルを更新する
            try (BufferedWriter bw = Files.newBufferedWriter(configPath, StandardOpenOption.APPEND)) {
                bw.write("rushhour.test.forupdateconfig=updated");
            } catch (IOException ex) {
                Logger.getLogger(RushHourPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Thread.sleep(1000L);
        }

        verify(config, times(1)).load(any(InputStream.class));
    }
    
    /**
     * watchkeyのresetに失敗.
     *
     * @throws InterruptedException
     */
    @Test
    public void testConfigWatchingServiceResetFail() throws InterruptedException {
        LOG.log(Level.INFO, "{0}#testConfigWatchingServiceResetFail", new Object[]{this.getClass().getSimpleName()});
        Path configPath = mock(Path.class);
        WatchService watchService = mock(WatchService.class);
        WatchKey watchKey = mock(WatchKey.class);

        when(configPath.toAbsolutePath()).thenReturn(configPath);
        when(configPath.getParent()).thenReturn(configPath);

        when(watchService.take()).thenReturn(watchKey);

        when(watchKey.pollEvents()).thenReturn(new ArrayList<>());

        when(watchKey.reset()).thenReturn(false);

        RushHourProperties.ConfigWatchingService service
                = inst.new ConfigWatchingService(configPath, watchService);

        service.run();

        verify(watchService, times(1)).take();
        verify(watchKey, times(1)).reset();
    }
}

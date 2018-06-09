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
package net.rushhourgame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import net.rushhourgame.controller.ControllerFactory;
import net.rushhourgame.exception.RushHourException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class DebugInitializerTest {

    protected ControllerFactory factory;
    protected static ValidatorFactory validatorFactory;
    protected static ExecutableValidator validatorForExecutables;
    
    protected DebugInitializer inst;
    protected ExecutorService executor;
    
    @BeforeClass
    public static void setUpClass() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validatorForExecutables = validatorFactory.getValidator().forExecutables();
    }
    
    @Before
    public void setUp() {
        factory = new ControllerFactory();
        
        executor = Executors.newSingleThreadExecutor();
        inst = factory.getDebugInitializer();
        
        factory.begin();
    }

    @After
    public void tearDown() {
        factory.rollback();
        
        executor.shutdown();
    }
    
    @AfterClass
    public static void tearDownClass() {
        validatorFactory.close();
    }
    

    @Test
    public void testInit() throws RushHourException {
        inst.init();
    }
}

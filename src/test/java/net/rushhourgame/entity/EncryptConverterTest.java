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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class EncryptConverterTest {

    EncryptConverter inst;

    @BeforeClass
    public static void setUpClass() throws NoSuchAlgorithmException {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }
    
    @Before
    public void setUp() {
        inst = new EncryptConverter();
    }

    @Test
    public void testEncrypt() throws Exception {
        String result = inst.encrypt("this_is_sample_data");
        assertNotNull(result);

        result = inst.encrypt("あいうえおかきくけこ");
        assertNotNull(result);
        
        result = inst.encrypt(null);
        assertNull(result);
    }
    
    @Test
    public void testRestore() throws Exception{
        String test = "this_is_sample_data";
        assertEquals(test, inst.decrypt(inst.encrypt(test)));
        test = "あいうえおかきくけこ";
        assertEquals(test, inst.decrypt(inst.encrypt(test)));
        assertNull(inst.decrypt(inst.encrypt(null)));
    }
}

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
import net.rushhourgame.RushHourProperties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class EncryptConverterTest {

    @Spy
    EncryptConverter inst;

    @BeforeClass
    public static void setUpClass() throws NoSuchAlgorithmException {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void testEncrypt() throws Exception {
        String result = inst.convertToDatabaseColumn("this_is_sample_data");
        assertNotNull(result);

        result = inst.convertToDatabaseColumn("あいうえおかきくけこ");
        assertNotNull(result);
        
        result = inst.convertToDatabaseColumn(null);
        assertNull(result);
    }
    
    @Test
    public void testRestore() throws Exception{
        String test = "this_is_sample_data";
        assertEquals(test, inst.convertToEntityAttribute(inst.convertToDatabaseColumn(test)));
        test = "あいうえおかきくけこ";
        assertEquals(test, inst.convertToEntityAttribute(inst.convertToDatabaseColumn(test)));
        assertNull(inst.decrypt(inst.encrypt(null)));
    }
    
    @Test
    public void testConvertToDatabaseColumn() throws Exception {
        doThrow(NoSuchAlgorithmException.class).when(inst).encrypt(anyString());
        doThrow(NoSuchAlgorithmException.class).when(inst).decrypt(anyString());
        
        assertNull(inst.convertToDatabaseColumn("hoge"));
        assertNull(inst.convertToEntityAttribute("hoge"));
    }
    
    @Test
    public void testGetProperties() {
        inst.prop = mock(RushHourProperties.class);
        
        assertEquals(inst.prop, inst.getProperties());
    }
}

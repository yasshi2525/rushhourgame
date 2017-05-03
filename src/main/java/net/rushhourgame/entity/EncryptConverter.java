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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourProperties.*;

/**
 * 暗号化してデータベースに登録する
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Converter
public class EncryptConverter implements AttributeConverter<String, String> {

    private static final Logger LOG = Logger.getLogger(EncryptConverter.class.getName());

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return encrypt(attribute);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            LOG.log(Level.SEVERE, "OAuthTokenConverter#convertToDatabaseColumn", ex);
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return decrypt(dbData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            LOG.log(Level.SEVERE, "OAuthTokenConverter#convertToEntityAttribute", ex);
            return null;
        }
    }

    /**
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if(input == null){
            return null;
        }
        Cipher c = getCipher();
        SecretKey key = loadSecretKey();
        IvParameterSpec ivp = createIvParameter();

        c.init(Cipher.ENCRYPT_MODE, key, ivp);
        byte[] output = c.doFinal(input.getBytes());

        return combineIv(output, ivp);
    }

    protected String combineIv(byte[] encrypted, IvParameterSpec ivp) {
        StringBuilder sb = new StringBuilder();
        sb.append(Base64.getEncoder().encodeToString(encrypted));
        sb.append(Base64.getEncoder().encodeToString(ivp.getIV()));
        return sb.toString();
    }

    public String decrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if(input == null){
            return null;
        }
        Cipher c = getCipher();
        SecretKey key = loadSecretKey();

        // 24 is ivp length
        String encrypted = input.substring(0, input.length() - 24);
        String ivpbase = input.substring(input.length() - 24);
        IvParameterSpec ivp = new IvParameterSpec(Base64.getDecoder().decode(ivpbase));

        c.init(Cipher.DECRYPT_MODE, key, ivp);
        byte[] output = c.doFinal(Base64.getDecoder().decode(encrypted));

        return new String(output);
    }

    protected Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String transform = RushHourProperties.getInstance().get(ENCRYPT_TRANSFORM);
        return Cipher.getInstance(transform);
    }

    protected SecretKey loadSecretKey() {
        String algorithm = RushHourProperties.getInstance().get(ENCRYPT_ALGORITHM);
        String encodedSecretKey = RushHourProperties.getInstance().get(ENCRYPT_KEY);

        byte[] secretKeyBin = Base64.getDecoder().decode(encodedSecretKey);
        return new SecretKeySpec(secretKeyBin, algorithm);
    }

    protected IvParameterSpec createIvParameter() {
        SecureRandom random = new SecureRandom();
        byte[] _iv = new byte[16];
        random.nextBytes(_iv);
        return new IvParameterSpec(_iv);
    }
}

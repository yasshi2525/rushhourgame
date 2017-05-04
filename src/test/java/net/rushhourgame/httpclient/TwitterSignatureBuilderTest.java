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
package net.rushhourgame.httpclient;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import net.rushhourgame.RushHourProperties;
import static net.rushhourgame.RushHourProperties.*;
import net.rushhourgame.RushHourResourceBundle;
import static net.rushhourgame.RushHourResourceBundle.*;
import static net.rushhourgame.httpclient.TwitterClientTest.prop;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class TwitterSignatureBuilderTest {

    protected TwitterSignatureBuilder inst;
    protected TwitterSignatureBuilder sample;
    protected static RushHourProperties prop;
    protected static RushHourResourceBundle resourceBundle;
    protected static final String HTTP_METHOD = "GET";
    protected static final String INVALID_URL = "http://127.0.0.1/";
    protected static final String ESCAPED_URL = "http%3A%2F%2F127.0.0.1%2F";
    protected static final String OATUTH_SECRET = "oauth_secret_dummy";
    @Rule
    public ExpectedException ex = ExpectedException.none();

    public TwitterSignatureBuilderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        prop = RushHourProperties.getInstance();
        resourceBundle = RushHourResourceBundle.getInstance();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws UnsupportedEncodingException {
        inst = new TwitterSignatureBuilder();
        inst.httpMethod = HTTP_METHOD;
        inst.baseUrl = INVALID_URL;
        inst.consumerSecret = prop.get(TWITTER_CONSUMER_SECRET);
        inst.oAuthTokenSecret = OATUTH_SECRET;
        
        sample = new TwitterSignatureBuilder();
        String status = "Hello Ladies + Gentlemen, a signed OAuth request!";
        sample.parameters.put("status", status);       
        sample.parameters.put("include_entities", "true");
        sample.parameters.put("oauth_consumer_key", "xvz1evFS4wEEPTGEFPHBog");
        sample.parameters.put("oauth_nonce", "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg");
        sample.parameters.put("oauth_signature_method", "HMAC-SHA1");
        sample.parameters.put("oauth_timestamp", "1318622958");
        sample.parameters.put("oauth_token", "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb");
        sample.parameters.put("oauth_version", "1.0");
        sample.httpMethod = "POST";
        sample.baseUrl = "https://api.twitter.com/1/statuses/update.json";
        sample.consumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        sample.oAuthTokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void testBuild() throws UnsupportedEncodingException {
        inst.parameters.put("oauth_callback", "http://localhost/sign-in-with-twitter/");
        inst.parameters.put("oauth_consumer_key", "cChZNFj6T5R0TigYB9yd1w");
        inst.parameters.put("oauth_nonce", "ea9ec8429b68d6b77cd5600adbbb0456");
        inst.parameters.put("oauth_signature_method", "HMAC-SHA1");
        inst.parameters.put("oauth_timestamp", "1318467427");
        inst.parameters.put("oauth_version", "1.0");
        assertEquals(6, inst.parameters.size());
        inst.httpMethod = "POST";
        inst.baseUrl = "https://api.twitter.com/oauth/request_token";
        inst.consumerSecret = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";
        assertEquals("F1Li3tvehgcraF8DMJ7OyxO4w9Y%3D", 
                URLEncoder.encode(inst.build(), "UTF-8").replace("+", "%20"));
    }

    @Test
    public void testCreateParameterString() throws UnsupportedEncodingException {
        inst.parameters.put("zzz", "xxx");
        inst.parameters.put("prm1", "val1");
        String expected = "prm1=val1&zzz=xxx";
        assertEquals(expected, inst.createParameterString());

        inst.parameters.clear();
        assertEquals("", inst.createParameterString());

        inst.parameters.put("prm1", "val1");
        expected = "prm1=val1";
        assertEquals(expected, inst.createParameterString());
    }
    
    @Test
    public void testCreateParameterStringSample() throws UnsupportedEncodingException{
        String actual = sample.createParameterString();
        String expected = "include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21";
        System.out.println(actual);
        System.out.println(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateSignatureBaseString() throws UnsupportedEncodingException {
        String prmString = "base";
        String expected = HTTP_METHOD + "&" + ESCAPED_URL + "&" + prmString;
        assertEquals(expected, inst.createSignatureBaseString(prmString));

        inst.baseUrl = null;
        expected = HTTP_METHOD + "&&" + prmString;
        assertEquals(expected, inst.createSignatureBaseString(prmString));

        inst.baseUrl = INVALID_URL;
        inst.httpMethod = null;
        expected = "&" + ESCAPED_URL + "&" + prmString;
        assertEquals(expected, inst.createSignatureBaseString(prmString));

        inst.httpMethod = HTTP_METHOD;
        expected = HTTP_METHOD + "&" + ESCAPED_URL + "&";
        assertEquals(expected, inst.createSignatureBaseString(null));
    }
    
    @Test
    public void testCreateSignatureBaseStringSample() throws UnsupportedEncodingException{
        String prmString = sample.createParameterString();
        String actual = sample.createSignatureBaseString(prmString);
        String expected = "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521";
        System.out.println("actual  ="+actual);
        System.out.println("expected="+expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateSigningKey() throws UnsupportedEncodingException {
        String expected = prop.get(TWITTER_CONSUMER_SECRET) + "&" + OATUTH_SECRET;
        assertEquals(expected, inst.createSigningKey());

        // no oauth secret key
        inst.oAuthTokenSecret = null;
        expected = prop.get(TWITTER_CONSUMER_SECRET) + "&";
        assertEquals(expected, inst.createSigningKey());
    }
    
    @Test
    public void testCreateSigninKeySample() throws UnsupportedEncodingException{
        String actual = sample.createSigningKey();
        String expected = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
        
        assertEquals(expected, actual);
    }   

    @Test
    public void testCalculateSignature() throws UnsupportedEncodingException  {
        String key = "key";
        String value = "value";
        assertNotNull(inst.calculateSignature(key, value));
    }
    
    @Test
    public void testCalculateSignatureSample() throws UnsupportedEncodingException{
        String prm = sample.createParameterString();
        String base = sample.createSignatureBaseString(prm);
        String key = sample.createSigningKey();
        String actual = sample.calculateSignature(key, base);
        String expected = "tnnArxj06cWHq44gCs1OSKk/jLY=";
        
        assertEquals(expected, actual);
    }
}

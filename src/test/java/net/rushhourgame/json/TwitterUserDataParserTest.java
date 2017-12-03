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
package net.rushhourgame.json;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class TwitterUserDataParserTest {
    
    protected static final String SAMPLE_JSON = "{\"id\":732572388206858240,\"id_str\":\"732572388206858240\",\"name\":\"\\u3084\\u3063\\u3057\\u30fc\",\"screen_name\":\"yasshi2525\",\"location\":\"\\u5e4c\\u7b75\\u6cca\\u5730\",\"profile_location\":null,\"description\":\"\\u8266\\u3053\\u308c\\u5e4c\\u7b75\\u6cca\\u5730\\/\\u5ac1\\u306f\\u9ad8\\u96c4 \\u5de5\\u696d\\u5316MOD\\u30de\\u30a4\\u30af\\u30e9\\u30de\\u30eb\\u30c1\\u3067\\u904a\\u3093\\u3060\\u308a\\u3057\\u3066\\u3044\\u307e\\u3059\\u3002\\u9244\\u9053\\u3092\\u5f15\\u3044\\u3066\\u4eba\\u304c\\u4e57\\u308b\\u69d8\\u5b50\\u3092\\u773a\\u3081\\u308b\\u30b2\\u30fc\\u30e0\\u3092\\u8da3\\u5473\\u3067\\u4f5c\\u3063\\u3066\\u307e\\u3059\",\"url\":\"https:\\/\\/t.co\\/hbYn55MmQf\",\"entities\":{\"url\":{\"urls\":[{\"url\":\"https:\\/\\/t.co\\/hbYn55MmQf\",\"expanded_url\":\"http:\\/\\/rushhourgame.net\\/\",\"display_url\":\"rushhourgame.net\",\"indices\":[0,23]}]},\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":35,\"friends_count\":51,\"listed_count\":1,\"created_at\":\"Tue May 17 14:04:02 +0000 2016\",\"favourites_count\":512,\"utc_offset\":32400,\"time_zone\":\"Tokyo\",\"geo_enabled\":false,\"verified\":false,\"statuses_count\":1048,\"lang\":\"ja\",\"status\":{\"created_at\":\"Thu May 04 07:59:06 +0000 2017\",\"id\":860041077910261760,\"id_str\":\"860041077910261760\",\"text\":\"Twitter\\u3067\\u30b5\\u30a4\\u30f3\\u30a4\\u30f3\\u6a5f\\u80fd\\u5b9f\\u88c5\\u306b\\u3066\\u307e\\u3069\\u308b https:\\/\\/t.co\\/QQpE4or2y7\",\"truncated\":false,\"entities\":{\"hashtags\":[],\"symbols\":[],\"user_mentions\":[],\"urls\":[],\"media\":[{\"id\":860041010696470528,\"id_str\":\"860041010696470528\",\"indices\":[23,46],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/C-96vhbUwAA3mqW.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/C-96vhbUwAA3mqW.jpg\",\"url\":\"https:\\/\\/t.co\\/QQpE4or2y7\",\"display_url\":\"pic.twitter.com\\/QQpE4or2y7\",\"expanded_url\":\"https:\\/\\/twitter.com\\/yasshi2525\\/status\\/860041077910261760\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"small\":{\"w\":640,\"h\":463,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"medium\":{\"w\":640,\"h\":463,\"resize\":\"fit\"},\"large\":{\"w\":640,\"h\":463,\"resize\":\"fit\"}}}]},\"extended_entities\":{\"media\":[{\"id\":860041010696470528,\"id_str\":\"860041010696470528\",\"indices\":[23,46],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/C-96vhbUwAA3mqW.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/C-96vhbUwAA3mqW.jpg\",\"url\":\"https:\\/\\/t.co\\/QQpE4or2y7\",\"display_url\":\"pic.twitter.com\\/QQpE4or2y7\",\"expanded_url\":\"https:\\/\\/twitter.com\\/yasshi2525\\/status\\/860041077910261760\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"small\":{\"w\":640,\"h\":463,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"medium\":{\"w\":640,\"h\":463,\"resize\":\"fit\"},\"large\":{\"w\":640,\"h\":463,\"resize\":\"fit\"}}}]},\"source\":\"\\u003ca href=\\\"http:\\/\\/twitter.com\\\" rel=\\\"nofollow\\\"\\u003eTwitter Web Client\\u003c\\/a\\u003e\",\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":1,\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"lang\":\"ja\"},\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"000000\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/732573159212244992\\/LLUrXNvV_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/732573159212244992\\/LLUrXNvV_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/732572388206858240\\/1475399580\",\"profile_link_color\":\"ABB8C2\",\"profile_sidebar_border_color\":\"000000\",\"profile_sidebar_fill_color\":\"000000\",\"profile_text_color\":\"000000\",\"profile_use_background_image\":false,\"has_extended_profile\":false,\"default_profile\":false,\"default_profile_image\":false,\"following\":false,\"follow_request_sent\":false,\"notifications\":false,\"translator_type\":\"none\",\"suspended\":false,\"needs_phone_verification\":false}";
    
    public TwitterUserDataParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testParse() {
        TwitterUserDataParser instance = new TwitterUserDataParser();
        TwitterUserData obj = instance.parse(SAMPLE_JSON);
        assertNotNull(obj);
        assertNotNull(obj.name);
        assertNotNull(obj.getColor());
        assertNotNull(obj.getIconUrl());
        assertNotNull(obj.getName());
        assertNotNull(obj.getProfile_background_color());
        assertNotNull(obj.getProfile_image_url());
        assertNotNull(obj.getProfile_image_url_https());
        assertNotNull(obj.getProfile_link_color());
        assertNotNull(obj.getProfile_text_color());
        assertNotNull(obj.getScreen_name());
        assertNotNull(obj.getTextColor());
        assertTrue(instance.isParsed());
        assertEquals(obj, instance.getCache());
    }
    
}

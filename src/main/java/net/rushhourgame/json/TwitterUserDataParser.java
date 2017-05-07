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

import java.io.Serializable;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.stream.JsonParser;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class TwitterUserDataParser implements Serializable {

    private final int serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(TwitterUserDataParser.class.getName());
    
    protected TwitterUserData cache;
    protected boolean parsed;
    
    public TwitterUserData parse(String jsonString) {
        JsonParser parser = Json.createParser(new StringReader(jsonString));
        TwitterUserData obj = new TwitterUserData();
        int depth = 0;
        String key = null;
        while (parser.hasNext()) {
            switch (parser.next()) {
                case START_OBJECT:
                    depth++;
                    break;
                case END_OBJECT:
                    depth--;
                    break;
                case KEY_NAME:
                    if (depth == 1) {
                        key = parser.getString();
                    }
                    break;
                case VALUE_STRING:
                    if (depth == 1) {
                        switch (key) {
                            case "name":
                                obj.name = parser.getString();
                                LOG.log(Level.FINE, "name="+obj.getName());
                                break;
                            case "screen_name":
                                obj.screen_name = parser.getString();
                                break;
                            case "profile_background_color":
                                obj.profile_background_color = "#" + parser.getString();
                                break;
                            case "profile_image_url":
                                obj.profile_image_url = parser.getString();
                                break;
                            case "profile_image_url_https":
                                obj.profile_image_url_https = parser.getString();
                                break;
                            case "profile_link_color":
                                obj.profile_link_color = "#" + parser.getString();
                                break;
                            case "profile_text_color":
                                obj.profile_text_color = "#" + parser.getString();
                                break;
                            case "default_profile":
                                obj.default_profile = parser.getString();
                                break;
                            case "default_profile_image":
                                obj.default_profile_image = parser.getString();
                                break;
                        }
                    }
            }
        }
        cache = obj;
        parsed = true;
        return obj;
    }

    public TwitterUserData getCache() {
        return cache;
    }

    public boolean isParsed() {
        return parsed;
    }
}

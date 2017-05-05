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

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class TwitterUserData implements Serializable {

    private final int serialVersionUID = 1;

    protected String name;
    protected String screen_name;
    protected String profile_background_color;
    protected String profile_image_url;
    protected String profile_image_url_https;
    protected String profile_link_color;
    protected String profile_text_color;
    protected String default_profile;
    protected String default_profile_image;

    public String getName() {
        if(name == null){
            return "no name";
        }
        return name;
    }

    public String getScreen_name() {
        if(screen_name == null){
            return "no name";
        }
        return screen_name;
    }

    public String getProfile_background_color() {
        if(profile_background_color == null){
            return "#AAAAAA";
        }
        return profile_background_color;
    }

    public String getProfile_image_url() {
        if(profile_image_url == null){
            return "no_image.png";
        }
        return profile_image_url;
    }

    public String getProfile_image_url_https() {
        if(profile_image_url_https == null){
            return "no_image.png";
        }
        return profile_image_url_https;
    }

    public String getProfile_link_color() {
        if(profile_link_color == null){
            return "#CCCCFF";
        }
        return profile_link_color;
    }

    public String getProfile_text_color() {
        if(profile_text_color == null){
            return "#000000";
        }
        return profile_text_color;
    }

    public String getDefault_profile() {
        return default_profile;
    }

    public String getDefault_profile_image() {
        return default_profile_image;
    }

    
}

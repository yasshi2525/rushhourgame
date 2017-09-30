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
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class SimpleUserData implements UserData, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SimpleUserData.class.getName());

    @NotNull
    protected String name;
    @NotNull
    protected String iconUrl;
    @NotNull
    @Pattern(regexp = "^#[0-9a-fA-F]{6}+$")
    protected String color;
    @NotNull
    @Pattern(regexp = "^#[0-9a-fA-F]{6}+$")
    protected String textColor;

    public SimpleUserData() {
        name = "NoName";
        iconUrl = "no_image.png";
        color = "#AAAAAA";
        textColor = "#000000";
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getTextColor() {
        return textColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}

/*
 * The MIT License
 *
 * Copyright 2018 yasshy2.
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
package net.rushhourgame.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author yasshy2
 */
@WebServlet(urlPatterns = {"/access_token"})
public class AccessServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        
        Pattern p = Pattern.compile(".+oauth_token=\"(.+)\", *oauth_verifier=\"(.+)\".+");
        Matcher m = p.matcher(request.getHeader("authorization"));
        
        String requestToken = "_";
        String verifier = "_";
        
        if (m.matches()) {
            requestToken = m.group(1);
            verifier = m.group(2);
        }
        
        String accessToken = requestToken;
        String accessTokenSecret = verifier;
        String userId = "ittest";
        String screenName = userId;

        try (PrintWriter out = response.getWriter()) {
            out.print("oauth_token=" + accessToken);
            out.print("&oauth_token_secret=" + accessTokenSecret);
            out.print("&user_id=" + userId);
            out.print("&screen_name=" + screenName);
        }
    }
}

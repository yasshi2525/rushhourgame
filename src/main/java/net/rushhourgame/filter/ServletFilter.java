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
package net.rushhourgame.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.rushhourgame.RushHourSession;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class ServletFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(ServletFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        handleLocale(request);
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            LOG.log(Level.SEVERE, "ServletFilter#doFilter fail", e);
        }
    }

    protected void handleLocale(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Locale locale = RushHourSession.getLocale(session);
        if (locale != null) {
            return;
        }

        //ブラウザの値を設定
        locale = request.getLocale();

        //GETパラメタの値を設定
        if (request.getParameter("lang") != null) {
            switch (request.getParameter("lang")) {
                case "jp":
                    locale = Locale.JAPANESE;
                    break;
                case "en":
                    locale = Locale.ENGLISH;
                    break;
                default:
                    LOG.log(Level.FINE, "{0}#handleLocale Unsuppoert language : {0}",
                            new Object[]{this.getClass().getSimpleName(), request.getParameter("lang")});
                    locale = Locale.ENGLISH;
                    break;
            }
        }

        //セッションに記憶
        RushHourSession.setLocale(session, locale);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

}

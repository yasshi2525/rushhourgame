/*
 * The MIT License
 *
 * Copyright 2018 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rushhourgame.GameMaster;
import net.rushhourgame.exception.RushHourException;

/**
 * Initial Start にすると Context Not Found がでるため、設定の口を別途設けた
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@WebServlet(name = "ControlGameMasterServlet", urlPatterns = {"/gm"})
public class ControlGameMasterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    GameMaster gm;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ControlGameMasterServlet</title>");
            out.println("</head>");
            out.println("<body>");

            String op = request.getParameter("op");
            if (op == null) {
                out.println("no GET parameter \"op\" was specified.");
            } else {
                boolean res;
                switch (op) {
                    case "construct": {
                        try {
                            gm.constructTemplateWorld();
                            res = true;
                        } catch (ExecutionException | InterruptedException | RushHourException ex) {
                            Logger.getLogger(ControlGameMasterServlet.class.getName()).log(Level.SEVERE, null, ex);
                            res = false;
                        } 
                    }
                    break;
                    case "start":
                        res = gm.startGame();
                        break;
                    case "stop":
                        res = gm.stopGame();
                        break;
                    default:
                        res = false;
                        out.println("invalid GET parameter \"op\" : " + op);
                        break;
                }

                if (res) {
                    out.println("success");
                } else {
                    out.println("failed. show log in detail.");
                }
            }

            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

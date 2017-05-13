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
package net.rushhourgame.controller;

import java.util.List;
import javax.enterprise.context.Dependent;

/**
 * 領域内にあるEntityを取得できる
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public abstract class GeometricController extends AbstractController{
    private final long serialVersionUID = 1;
    
    @SuppressWarnings("unchecked")
    protected <T> List<T> findIn(String tableName, double centerX, double centerY, double scale){
        double width = Math.pow(2.0, scale);
        double height = Math.pow(2.0, scale);
        
        return em.createQuery(buildQueryAreaIn(tableName))
                .setParameter("x1", centerX - width / 2.0)
                .setParameter("x2", centerX + width / 2.0)
                .setParameter("y1", centerY - height / 2.0)
                .setParameter("y2", centerY + height / 2.0)
                .getResultList();
    }
    
    protected String buildQueryAreaIn(String tableName){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT obj FROM ");
        sb.append(tableName);
        sb.append(" obj WHERE obj.x > :x1 AND obj.x < :x2 AND obj.y > :y1 AND obj.y < :y2");
        return sb.toString();
    }
}
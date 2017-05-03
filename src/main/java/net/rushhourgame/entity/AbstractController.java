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

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public abstract class AbstractController implements Serializable{

    private final int serialVersionUID = 1;
    @PersistenceContext
    transient protected EntityManager em;
    private static final Logger LOG = Logger.getLogger(AbstractController.class.getName());

    /**
     * テーブルに指定した id のエンティティが存在するか返す
     *
     * @param query
     * @param key
     * @param value
     * @return
     */
    protected boolean exists(String query, String key, String value) {
        return em.createNamedQuery(query, Integer.class)
                .setParameter(key, value)
                .getSingleResult() == 1;
    }

    @SuppressWarnings("unchecked")
    protected <T> T findBy(String query, String key, String value, T dummyInst) {
        try {
            return (T) em.createNamedQuery(query, dummyInst.getClass())
                    .setParameter(key, value)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

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

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.rushhourgame.ErrorMessageBuilder;
import net.rushhourgame.RushHourProperties;
import net.rushhourgame.entity.Player;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Dependent
public abstract class AbstractController implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AbstractController.class.getName());
    
    @PersistenceContext
    protected EntityManager em;    
    @Inject
    protected RushHourProperties prop;
    @Inject
    protected ErrorMessageBuilder errMsgBuilder;

    protected boolean exists(String query, Player owner) {
        return (em.createNamedQuery(query, Number.class)
                .setParameter("owner", owner)
                .getSingleResult()).longValue() == 1L;
    }
}

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
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.OAuth;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.Player;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class LocalTableController {
    EntityManager em;

    public LocalTableController(){
        this.em = Persistence.createEntityManagerFactory("test_rushhour_RushHour1.0_war_1.0-SNAPSHOTPU").createEntityManager();
    }

    public void clean(){
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Absorber x").executeUpdate();
        em.createQuery("DELETE FROM GameMaster x").executeUpdate();
        em.createQuery("DELETE FROM Player x").executeUpdate();
        em.createQuery("DELETE FROM OwnerInfo x").executeUpdate();
        em.createQuery("DELETE FROM OAuth x").executeUpdate();
        em.getTransaction().commit();
    }
    
    public List<Player> findPlayers(){
        return em.createQuery("SELECT x FROM Player x", Player.class).getResultList();
    }
    
    public List<OAuth> findOAuths(){
        return em.createQuery("SELECT x FROM OAuth x", OAuth.class).getResultList();
    }
}
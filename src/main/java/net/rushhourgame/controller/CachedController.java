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
package net.rushhourgame.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.AbstractEntity;
import net.rushhourgame.entity.GeoEntity;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Pointable;

/**
 * ゲーム中、Entityをメモリ上で管理するコントローラ. GameMasterのrunメソッドを繰り返すたびにメモリ使用量が増えるため作成
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 * @param <T> 格納するEntity 
 */
@Dependent
public abstract class CachedController<T extends GeoEntity> extends AbstractController {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CachedController.class.getName());

    protected List<T> entities;

    
    public List<T> findAll() {
        if (entities == null) {
            LOG.log(Level.WARNING, "{0}#findAll controller never synchronize database", new Object[]{HumanController.class});
            return new ArrayList<>();
        }
        return entities;
    }

    public List<T> findIn(@NotNull Pointable center, double scale) {
        if (entities == null) {
            LOG.log(Level.WARNING, "{0}#findAll controller never synchronize database", new Object[]{HumanController.class});
            return new ArrayList<>();
        }
        return entities.stream().filter(e -> e.isAreaIn(center, scale)).collect(Collectors.toList());
    }
            
    public abstract void synchronizeDatabase();

    protected void synchronizeDatabase(String query, Class<T> entityClass) {
        if (entities == null) {
            entities = em.createNamedQuery("Human.findAll", entityClass).getResultList();
            LOG.log(Level.INFO, "{0}#fetchAll fetched {1} entities successfully.", new Object[]{CachedController.class, entities.size()});
        } else {
            entities.forEach((e) -> em.merge(e));
            LOG.log(Level.INFO, "{0}#synchronizeDatabase merged {1} entities successfully.", new Object[]{CachedController.class, entities.size()});
        }
    }

    protected void persistEntity(T newEntity) {
        em.persist(newEntity);
        if (entities != null) {
            entities.add(newEntity);
        } else {
            LOG.log(Level.WARNING, "{0}#persistEntity skip because never synchronize database", new Object[]{CachedController.class});
        }
    }
}

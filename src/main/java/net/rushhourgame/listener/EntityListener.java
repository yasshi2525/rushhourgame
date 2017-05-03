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
package net.rushhourgame.listener;

import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import net.rushhourgame.exception.RushHourRuntimeException;
import net.rushhourgame.entity.AbstractEntity;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
public class EntityListener{

    private static final Logger LOG = Logger.getLogger(EntityListener.class.getName());
    
    @PrePersist
    public void prePersist(Object obj){
        if(obj instanceof AbstractEntity){
            AbstractEntity entity = (AbstractEntity) obj;
            if(entity.getCreated() != null){
                throw new RushHourRuntimeException("\"created\" parameter is set even if before persist");
            }
            if(entity.getUpdated() != null){
                throw new RushHourRuntimeException("\"updated\" parameter is set even if before persist");
            }
            entity.setCreated(new Date());
            entity.setUpdated(new Date());
        }
    }
    
    @PreUpdate
    public void preUpdate(Object obj){
        if(obj instanceof AbstractEntity){
            AbstractEntity entity = (AbstractEntity) obj;
            if(entity.getCreated() == null){
                throw new RushHourRuntimeException("\"created\" is null even if state is preUpdate");
            }
            entity.setUpdated(new Date());
        }
    }
}

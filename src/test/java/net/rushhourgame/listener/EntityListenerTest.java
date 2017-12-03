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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import net.rushhourgame.exception.RushHourRuntimeException;
import net.rushhourgame.entity.AbstractEntity;
import net.rushhourgame.entity.SimpleEntity;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
public class EntityListenerTest {
    protected EntityListener inst;
    protected AbstractEntity entity;
    
    @Rule
    public ExpectedException expectException = ExpectedException.none();
    
    @Before
    public void setUp() {
        inst = new EntityListener();
        entity = new SimpleEntity();
    }

    @Test
    public void testPrePersist() {
        inst.prePersist(null);
        inst.prePersist("invalidObject");
        inst.prePersist(entity);
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
    }

    @Test
    public void testPreUpdate() {
        inst.preUpdate(null);
        inst.preUpdate("invalidObject");
        
        inst.prePersist(entity);
        inst.preUpdate(entity);
        
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
    }
    
    @Test
    public void testSetCreatedManuallyPrePersist(){
        entity.setCreated(new Date());
        expectException.expect(RushHourRuntimeException.class);
        expectException.expectMessage("\"created\" parameter is set even if before persist");
        inst.prePersist(entity);
    }
    
    @Test
    public void testSetUpdatedManuallyPrePersist(){
        entity.setUpdated(new Date());
        expectException.expect(RushHourRuntimeException.class);
        expectException.expectMessage("\"updated\" parameter is set even if before persist");
        inst.prePersist(entity);
    }
    
    @Test
    public void testDoublePersist(){
        expectException.expect(RushHourRuntimeException.class);
        expectException.expectMessage("\"created\" parameter is set even if before persist");
        inst.prePersist(entity);
        inst.prePersist(entity);
    }
    
    /**
     * updateしたら entity#updated が更新されるか
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testUpdateUpdatedDate() throws InterruptedException{
        inst.prePersist(entity);
        inst.preUpdate(entity);
        
        Date beforeCreated = entity.getCreated();
        Date beforeUpdated = entity.getUpdated();
        
        Thread.sleep(1000L);
        inst.preUpdate(entity);
        
        Date afterCreated = entity.getCreated();
        Date afterUpdated = entity.getUpdated();
        
        assertEquals(beforeCreated, afterCreated);
        assertNotEquals(beforeUpdated, afterUpdated);
    }
    
    @Test
    public void testPreUpdateCreateNull() {
        expectException.expect(RushHourRuntimeException.class);
        expectException.expectMessage("\"created\" is null even if state is preUpdate");
        
        inst.preUpdate(entity);
    }
}

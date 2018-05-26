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
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import net.rushhourgame.entity.Human;
import net.rushhourgame.entity.Player;
import net.rushhourgame.entity.SimpleGeoEntity;
import net.rushhourgame.entity.SimplePoint;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import org.mockito.Spy;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CachedControllerTest {

    @Spy
    protected CachedController<SimpleGeoEntity> inst;
    
    @Mock
    protected EntityManager em;
    
    @Mock
    protected TypedQuery<SimpleGeoEntity> query;
    
    @Spy
    protected SimpleGeoEntity e;

    @Before
    public void setUp() {
        inst.em = em;
        doReturn(query).when(em).createNamedQuery(anyString(), eq(SimpleGeoEntity.class));
        doReturn(query).when(query).setParameter(anyString(), any(SimpleGeoEntity.class));
        doReturn(0).when(query).executeUpdate();
        doReturn(new ArrayList<>()).when(query).getResultList();
        inst.init();
        
        inst.readLock = spy(inst.readLock);
        inst.writeLock = spy(inst.writeLock);
    }
    
    @Test
    public void testFindInNull() {
        assertTrue(inst.findIn(mock(Player.class), new SimplePoint(), 0).isEmpty());
    }
    
    @Test
    public void testFindNull() {
        assertEquals(e, inst.find(e));
    }
    
    @Test
    public void testExistsEntitiesNull() {
        assertFalse(inst.exists(new SimplePoint()));
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }
    
    @Test
    public void testExistsPlayerArgEntitiesNull() {
        assertFalse(inst.exists(mock(Player.class), new SimplePoint()));
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }
    
    @Test
    public void testExistsPlayerArg() {
        Player owner = mock(Player.class);
        inst.entities = new ArrayList<>();
        inst.entities.add(e);
        doReturn(true).when(e).isOwnedBy(owner);
        doReturn(0d).when(e).distTo(any(SimplePoint.class));
        
        assertTrue(inst.exists(owner, new SimplePoint()));
        
        verify(inst.readLock, times(1)).lock();
        verify(inst.readLock, times(1)).unlock();
    }
    
    @Test
    public void testSynchronizeDatabase() {
        inst.entities = new ArrayList<>();
        inst.entities.add(e);

        inst.synchronizeDatabase("hoge", SimpleGeoEntity.class);

        verify(inst.em, never()).createNamedQuery(anyString());
        verify(inst.em, times(1)).merge(eq(e));
        assertNotNull(inst.entities);
        
        verify(inst.writeLock, times(1)).lock();
        verify(inst.writeLock, times(1)).unlock();
    }

    @Test
    public void testSynchronizeDatabaseNull() {
        inst.entities = null;

        inst.synchronizeDatabase("hoge", SimpleGeoEntity.class);

        verify(inst.em, times(1)).createNamedQuery(anyString(), eq(SimpleGeoEntity.class));
        assertNotNull(inst.entities);
        
        verify(inst.writeLock, times(1)).lock();
        verify(inst.writeLock, times(1)).unlock();
    }
    
    @Test
    public void testRemoveEntity() {
        inst.entities = new ArrayList<>();
        inst.entities.add(e);
        
        inst.removeEntity("hoge", SimpleGeoEntity.class, e);
        
        assertTrue(inst.entities.isEmpty());
        
        verify(inst.writeLock, times(1)).lock();
        verify(inst.writeLock, times(1)).unlock();
    }
    
    @Test
    public void testBean() {
        assertNotNull(inst.getReadLock());
    }
}

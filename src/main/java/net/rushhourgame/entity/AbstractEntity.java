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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@MappedSuperclass
public abstract class AbstractEntity implements Identifiable, Ownable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AbstractEntity.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updated;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created == null ? null : new Date(created.getTime());
    }

    public Date getUpdated() {
        return updated == null ? null : new Date(updated.getTime());
    }

    public void setCreated(Date created) {
        this.created = created == null ? null : new Date(created.getTime());
    }

    public void setUpdated(Date updated) {
        this.updated = updated == null ? null : new Date(updated.getTime());
    }
    
    @Override
    public boolean equalsId(Identifiable other) {
        if (other == null) {
            return false;
        }
        if (!this.getClass().equals(other.getClass())) {
            LOG.log(Level.WARNING, "{0}#equalsId class is difference {1} ({2}) and {3} ({4})",
                    new Object[]{AbstractEntity.class, this, this.getClass(), other, other.getClass()});
        }
        return this.getClass().equals(other.getClass()) && this.id == other.getId();
    }

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return false;
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return false;
    }

    protected boolean hasPrivilege(@NotNull Player owner, Player other) {
        return isOwn(owner, other);
    }

    protected boolean isOwn(@NotNull Player owner, Player other) {
        if (other == null) {
            return false;
        }
        return owner.getId() == other.getId();
    }
    
    protected String _toString(StepForHuman step) {
        return step.getUid() + "{" + step.getFrom() + "_=>_" + step.getTo() + "}";
    }
}

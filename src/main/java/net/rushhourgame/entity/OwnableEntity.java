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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@MappedSuperclass
public abstract class OwnableEntity extends AbstractEntity implements Ownable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    /*@JoinColumn(
            foreignKey = @ForeignKey(
                    name = "fk_owner_info_id",
                    foreignKeyDefinition = "FOREIGN KEY (info_id) REFERENCES ownerinfo (id) ON DELETE CASCADE"))*/
    protected Player player;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return player;
    }

    public void setOwner(Owner owner) {
        if (owner == null) {
            throw new NullPointerException("owner is null");
        }
        if (owner instanceof Player) {
            player = (Player) owner;
        } else {
            throw new IllegalArgumentException("owner is not GameMaster nor Player");
        }
    }

    /**
     * 管理者か自分の所有者ならtrue
     *
     * @param owner owner
     * @return boolean
     */
    @Override
    public boolean isPrivilegedBy(Owner owner) {
        if (owner == null) {
            return false;
        }
        if (owner.getRoles().contains(RoleType.ADMINISTRATOR)) {
            return true;
        }
        return isOwnedBy(owner);
    }

    @Override
    public boolean isOwnedBy(Owner owner) {
        if (owner == null) {
            return false;
        }

        if (owner instanceof Player && player != null) {
            return player.id.equals(((Player) owner).id);
            
        } else {
            return false;
        }
    }

}

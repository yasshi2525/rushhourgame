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

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * 路線
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "Line.existsName",
            query = "SELECT CASE WHEN count(obj.id) > 0 THEN true ELSE false END"
                    + " FROM Line obj WHERE obj.owner = :owner AND obj.name = :name"
    )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
public class Line extends AbstractEntity implements Ownable {
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @ManyToOne
    protected Player owner;
    
    @NotNull
    protected String name;
    
    @OneToMany(mappedBy = "parent")
    List<LineStep> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LineStep> getSteps() {
        return steps;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPrivilegedBy(Player owner) {
        return hasPrivilege(this.owner, owner);
    }

    @Override
    public boolean isOwnedBy(Player owner) {
        return isOwn(this.owner, owner);
    }
    
    public boolean hasVisited(RailEdge edge) {
        return steps.stream().anyMatch(step -> {
            return edge.equals(step.getOnRailEdge());
        });
    }
}

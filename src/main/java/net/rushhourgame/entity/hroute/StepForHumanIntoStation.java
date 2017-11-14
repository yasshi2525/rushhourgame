/*
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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
package net.rushhourgame.entity.hroute;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import net.rushhourgame.entity.AbstractEntity;
import net.rushhourgame.entity.Platform;
import net.rushhourgame.entity.RelayPointForHuman;
import net.rushhourgame.entity.StepForHuman;
import net.rushhourgame.entity.TicketGate;

/**
 * 人用移動ステップ駅入場
 *
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"_from_id", "_to_id"}))
@NamedQueries({
    @NamedQuery(
            name = "StepForHumanIntoStation.findAll",
            query = "SELECT x FROM StepForHumanIntoStation x"
    )
})
public class StepForHumanIntoStation extends AbstractEntity implements StepForHuman {

    private static final long serialVersionUID = 1;

    @NotNull
    @ManyToOne
    protected TicketGate _from;

    @NotNull
    @ManyToOne
    protected Platform _to;
    
    @Override
    public RelayPointForHuman getFrom() {
        return _from;
    }

    @Override
    public RelayPointForHuman getTo() {
        return _to;
    }

    public void setFrom(TicketGate _from) {
        this._from = _from;
    }

    public void setTo(Platform _to) {
        this._to = _to;
    }
    
    @Override
    public double getCost() {
        return _from.distTo(_to);
    }
}

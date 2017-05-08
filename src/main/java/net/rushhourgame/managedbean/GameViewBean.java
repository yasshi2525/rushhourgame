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
package net.rushhourgame.managedbean;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.AbsorberController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named(value = "game")
@ViewScoped
public class GameViewBean implements Serializable{
    private final long serialVersionUID = 1;
    @Inject
    protected PlayerController pCon;
    
    @Inject
    protected AbsorberController aCon;
    
    @Inject
    protected RushHourSession rhSession;
    
    protected Player player;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(rhSession.getToken());
    }
    
    public void onClick() throws RushHourException{
        aCon.create(player, Math.random() * 100, Math.random() * 100);
    }
}

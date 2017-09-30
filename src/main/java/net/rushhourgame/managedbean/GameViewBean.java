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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.AbsorberController;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.managedbean.OperationType.*;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Named(value = "game")
@ViewScoped
public class GameViewBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GameViewBean.class.getName());
    
    @Inject
    protected PlayerController pCon;
    @Inject
    protected AbsorberController aCon;
    @Inject
    protected RushHourSession rhSession;
    protected Player player;
    protected OperationType operation = NONE;
    
    protected int mouseX;
    protected int mouseY;
    
    @PostConstruct
    public void init() {
        player = pCon.findByToken(rhSession.getToken());
    }
    
    public void onClick() throws RushHourException{
        switch(operation){
            case CREATE_RAIL:
                
        }
        aCon.create(player, mouseX, mouseY);
    }

    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }
    
    public boolean isOperating(){
        return operation != NONE;
    }
}

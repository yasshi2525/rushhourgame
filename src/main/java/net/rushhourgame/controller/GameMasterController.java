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
package net.rushhourgame.controller;

import java.util.Locale;
import javax.enterprise.context.Dependent;
import net.rushhourgame.ErrorMessage;
import net.rushhourgame.entity.GameMaster;
import net.rushhourgame.exception.RushHourException;
import static net.rushhourgame.RushHourResourceBundle.*;
import net.rushhourgame.entity.OwnerInfo;
import net.rushhourgame.entity.RoleType;

/**
 *
 * @author yasshi2525 <https://twitter.com/yasshi2525>
 */
@Dependent
public class GameMasterController extends AbstractController{
    private static final long serialVersionUID = 1L;
    
    public boolean exists(){
        return em.createNamedQuery("GameMaster.count", Long.class).getSingleResult() > 0;
    }
    
    public GameMaster create() throws RushHourException{
        if(exists()){
            throw new RushHourException(ErrorMessage.createDataInconsitency(GAME_DATA_INCONSIST_DUP_GM));
        }
        OwnerInfo info = new OwnerInfo();
        info.setColor("#888888");
        info.setIconUrl("no_image.png");
        info.setLocale(Locale.getDefault());
        info.setName("GameMaster");
        
        GameMaster inst = new GameMaster();
        inst.getRoles().add(RoleType.ADMINISTRATOR);
        inst.setInfo(info);
        em.persist(inst);
        return inst;
    }
}

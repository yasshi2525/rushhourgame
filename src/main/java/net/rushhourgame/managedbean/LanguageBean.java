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
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import net.rushhourgame.RushHourSession;
import net.rushhourgame.controller.PlayerController;
import net.rushhourgame.entity.Player;

/**
 * 言語選択リンク押下時の処理
 * @author yasshi2525 (https://twitter.com/yasshi2525)
 */
@Named(value = "lang")
@ViewScoped
public class LanguageBean implements Serializable{
    private static final long serialVersionUID = 1L;
    @Inject
    RushHourSession rushHourSession;
    @Inject
    PlayerController pCon;
    Player player;
    
    @PostConstruct
    public void init(){
        player = pCon.findByToken(rushHourSession.getToken());
    }
    
    @Transactional
    public void change(String lang){
        if (lang != null) {
            switch (lang) {
                case "jp":
                    if(player != null){
                        player.getInfo().setLocale(Locale.JAPANESE);
                    }
                    rushHourSession.setLocale(Locale.JAPANESE);
                    break;

                default:
                    if(player != null){
                        player.getInfo().setLocale(Locale.ENGLISH);
                    }
                    rushHourSession.setLocale(Locale.ENGLISH);
                    break;
            }
        }
    }
}

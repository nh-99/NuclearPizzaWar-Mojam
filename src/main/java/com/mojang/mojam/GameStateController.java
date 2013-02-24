/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 
 * @author Johan
 */
public class GameStateController extends StateBasedGame {
    public GameStateController() {
        super("Cyborg-Hippos");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new LogoState());
        addState(new StartScreenState());
        addState(new GameState());
        addState(new GameOverState());
    }

}

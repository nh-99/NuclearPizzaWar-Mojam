/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.*;

import com.mojang.mojam.world.Starfield;

/**
 * 
 * @author Johan
 */
public class StartScreenState extends BasicGameState {
    public static final int ID = 2;
    private Starfield starfield;
    Image splashImage;
    Image startButtonImage;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {

    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        splashImage = new Image("res/GUI/splash.png");
        startButtonImage = new Image("res/GUI/button_start.png");
        starfield = new Starfield(800, 600);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
        Camera cam = new Camera();
        starfield.render(gc, grphcs, cam);
        grphcs.drawImage(splashImage, 0, 0);
        Rectangle buttonRect = getStartGameButtonRect(gc);
        grphcs.drawImage(startButtonImage, buttonRect.getX(), buttonRect.getY());
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int deltaMS) throws SlickException {
        Input input = gc.getInput();
        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            Rectangle buttonRect = getStartGameButtonRect(gc);
            if (buttonRect.contains(input.getMouseX(), input.getMouseY())) {
                sbg.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition());
            }
        }
        starfield.update(gc, deltaMS);
    }

    private Rectangle getStartGameButtonRect(GameContainer slickContainer) {
        int boxWidth = 194;
        int boxHeight = 59;
        return new Rectangle(slickContainer.getWidth() / 2 - boxWidth / 2, slickContainer.getHeight() - boxHeight - 80, boxWidth, boxHeight);
    }
}

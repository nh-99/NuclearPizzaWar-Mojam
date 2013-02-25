package com.mojang.mojam;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.*;

public class GameOverState extends BasicGameState {

    public static final int ID = 4;
    private Image failImage;
    private Image winImage;
    private boolean isVictory;

    public GameOverState() {
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        failImage = new Image("res/GUI/game_over.png");
        winImage = new Image("res/GUI/victory.png");
    }

    @Override
    public void render(GameContainer container, StateBasedGame sbg, Graphics g) throws SlickException {

        g.setColor(Color.black);
        g.fillRect(0, 0, container.getWidth(), container.getHeight());
        if (isVictory) {
            winImage.draw(0, 0);
        } else {
            failImage.draw(0, 0);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int deltaMS) throws SlickException {

        Input input = gc.getInput();
        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            sbg.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition());
        }

    }

    @Override
    public int getID() {
        return ID;
    }

    public void setVictory(boolean victory) {
        isVictory = victory;
    }

}

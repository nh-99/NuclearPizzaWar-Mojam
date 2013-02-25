package com.mojang.mojam.gui;

import org.newdawn.slick.*;

/**
 * 
 * @author Johan
 */
public abstract class ProgressBar {

    private final float width;
    private final float height;

    public ProgressBar(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public abstract float getProgress();

    protected abstract Color getProgressColor(float progress);

    public void update(GameContainer slickContainer, int deltaMS) {

    }

    public void render(GameContainer slickContainer, Graphics g, float centerX, float topY) {

        float left = centerX - width * .5f;
        float progress = getProgress();

        g.setColor(Color.black);
        g.fillRect(left, topY, width, height);

        g.setColor(getProgressColor(progress));
        g.fillRect(left + 1, topY + 1, width * progress - 2, height - 2);
    }
}

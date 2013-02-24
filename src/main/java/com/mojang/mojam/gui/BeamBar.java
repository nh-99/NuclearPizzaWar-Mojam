package com.mojang.mojam.gui;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.entity.Player;

public class BeamBar extends ProgressBar {

    private static final Color barColor = new Color(.3f, .4f, 1.0f, 1.0f);
    private final Player owner;

    public BeamBar(Player owner) {
        super(32, 5);
        this.owner = owner;
    }

    @Override
    public float getProgress() {
        return owner.getBeamTime() / owner.getMaxBeamTime();
    }

    @Override
    protected Color getProgressColor(float progress) {
        return barColor;
    }

    public void render(GameContainer container, Graphics g, Camera c) {
        if (owner.getBeamTime() < owner.getMaxBeamTime()) {
            super.render(container, g, owner.getX() - c.getX(), owner.getZ() - c.getY() + 10 - owner.getY());
        }
    }

}

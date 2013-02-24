/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.gui;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.entity.HealthEntity;

/**
 * 
 * @author Johan
 */
public class HealthBar extends ProgressBar {

    private static final Color[] healthColors = new Color[] {
            new Color(.8f, .0f, .0f), new Color(.7f, .6f, .1f), new Color(.2f, .9f, .1f)
    };

    private final HealthEntity owner;

    public HealthBar(HealthEntity owner) {
        super(32, 5);
        this.owner = owner;
    }

    @Override
    public float getProgress() {
        return ((float) owner.getHealth()) / owner.getMaxHealth();
    }

    @Override
    protected Color getProgressColor(float progress) {
        if (progress < .3f) {
            return healthColors[0];
        } else if (progress < .6f) {
            return healthColors[1];
        }
        return healthColors[2];
    }

    public void update(GameContainer slickContainer, int deltaMS) {

    }

    public void render(GameContainer slickContainer, Graphics g, Camera c) {
        if (owner != null && owner.getHealth() < owner.getMaxHealth()) {
            super.render(slickContainer, g, owner.getX() - c.getX(), owner.getZ() - c.getY() + 4 - owner.getY());
        }
    }
}

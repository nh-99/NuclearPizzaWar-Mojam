package com.mojang.mojam.world;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;

public class PizzaDoodad {

    private final float x;
    private final float z;
    private final Image sprite;

    public PizzaDoodad(Image sprite, float x, float z) {
        this.sprite = sprite;
        this.x = x;
        this.z = z;
    }

    public void render(GameContainer container, Graphics g, Camera c) {
        g.drawImage(sprite, x - c.getX() - sprite.getWidth() / 2, z - c.getY() - sprite.getHeight() / 2);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }
}

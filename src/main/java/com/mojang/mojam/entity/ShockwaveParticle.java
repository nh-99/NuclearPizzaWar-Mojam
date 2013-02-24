package com.mojang.mojam.entity;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.world.PizzaWorld;

public class ShockwaveParticle extends Particle {

    private float size = .3f;
    private Color fadeColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private final float maxSize;

    public ShockwaveParticle(PizzaWorld world, float x, float z, float maxSize) {
        super(world, x, z);
        this.maxSize = maxSize;

        try {
            activeImage = new Image("res/actors/shockwave.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        size += (float) deltaMS * .001f * 4.25f * maxSize;

        if (size > maxSize) {
            return false;
        }

        return super.update(slickContainer, deltaMS);
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        // do nothing
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {

        float w = activeImage.getWidth() * size;
        float h = activeImage.getHeight() * size;
        float cx = x - camera.getX() - w / 2;
        float cy = z - camera.getY() - h / 2;

        float alpha = 1.0f - (size / maxSize) * .5f;
        fadeColor.a = alpha;
        g.drawImage(activeImage, cx, cy, cx + w, cy + h, 0, 0, activeImage.getWidth(), activeImage.getHeight(), fadeColor);

    }

    public static class AlienWarning extends ShockwaveParticle {

        public AlienWarning(PizzaWorld world, float x, float z) {
            super(world, x, z, 1.0f);
            try {
                activeImage = new Image("res/actors/alien_shockwave.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }


    }

}

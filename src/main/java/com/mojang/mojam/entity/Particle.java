package com.mojang.mojam.entity;

import java.util.List;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.world.PizzaWorld;

public class Particle extends Entity {

    protected Image activeImage;
    protected float xOffset, yOffset;

    public Particle(PizzaWorld world) {
        super(world);
    }

    public Particle(PizzaWorld world, float x, float z) {
        super(world, x, z);
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        float deltaS = deltaMS * .001f;
        velocity.x += acceleration.x * deltaS;
        velocity.y += acceleration.y * deltaS;
        velocity.z += acceleration.z * deltaS;

        x += velocity.x * deltaS;
        y += velocity.y * deltaS;
        z += velocity.z * deltaS;

        return !isOutOfWorld() && y >= 0;
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        if (activeImage != null) {
            activeImage.setColor(0, 1, 1, 1, 1);
            activeImage.setColor(1, 1, 1, 1, 1);
            activeImage.setColor(2, 1, 1, 1, 1);
            activeImage.setColor(3, 1, 1, 1, 1);
            g.drawImage(activeImage, x - camera.getX() - xOffset, z - camera.getY() - y - yOffset);
        }
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        if (y > 0 && activeImage != null) {
            activeImage.setColor(0, 0, 0, 0, .25f);
            activeImage.setColor(1, 0, 0, 0, .25f);
            activeImage.setColor(2, 0, 0, 0, .25f);
            activeImage.setColor(3, 0, 0, 0, .25f);
            g.drawImage(activeImage, x - camera.getX() - xOffset, z - camera.getY() - yOffset);
        }
    }

    @Override
    public void checkCollisions(List<Entity> entities) {
    }
}

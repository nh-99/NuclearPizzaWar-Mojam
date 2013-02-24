package com.mojang.mojam.entity;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.world.PizzaWorld;

public class FlyingSlice extends Particle {

    public FlyingSlice(PizzaWorld world) {
        super(world, 0, 0);

        boolean flipped = random.nextBoolean();
        try {
            activeImage = new Image("res/pizzarelated/ansjovalienbotslice.png");
            if (flipped) {
                activeImage = activeImage.getFlippedCopy(true, false);
            }
            xOffset = activeImage.getWidth() / 2;
            yOffset = activeImage.getHeight();
        } catch (SlickException e) {
            e.printStackTrace();
        }

        float width = world.getWidth();
        float height = world.getHeight();

        {
            double angle = random.nextDouble() * Math.PI * .5f;
            if (flipped) {
                angle = Math.PI - angle;
            }
            float dist = width * .5f + activeImage.getWidth() + random.nextFloat() * 800.0f;
            x = width * .5f + (float) Math.cos(angle) * dist;
            z = height * .5f + -(float) Math.sin(angle) * dist * .5f;
        }

        y = 50 + random.nextFloat() * 30;
        float speedMod = .8f + random.nextFloat() * .6f;
        velocity.x = -260 * speedMod * ((flipped) ? -1.0f : 1.0f);
        velocity.z = 100 * speedMod;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        if (world.isOnPizza(x, z)) {
            acceleration.y += deltaMS * .001f * 30f;
        }

        return super.update(slickContainer, deltaMS);
    }

    public boolean isOutOfWorld() {
        return z > (world.getHeight() + activeImage.getHeight() * 2);
    }

    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        if (y < 0) {
            return;
        }
        if (y > 0 && activeImage != null) {
            activeImage.setColor(0, 0, 0, 0, .25f);
            activeImage.setColor(1, 0, 0, 0, .25f);
            activeImage.setColor(2, 0, 0, 0, .25f);
            activeImage.setColor(3, 0, 0, 0, .25f);

            float shadowScale = (float) Math.pow(1 - Math.min(y, 400) / 400, 2);
            int halfShadowX = (int) (((float) activeImage.getWidth() / 2.0f) * shadowScale);
            int halfShadowY = (int) (((float) activeImage.getHeight() / 2.0f) * shadowScale);
            g.drawImage(activeImage, x - camera.getX() - halfShadowX, z - camera.getY() - halfShadowY, x - camera.getX() + halfShadowX, z - camera.getY() + halfShadowY, 0, 0, activeImage.getWidth(),
                    activeImage.getHeight(), shadowColorMult);
        }
    }


}

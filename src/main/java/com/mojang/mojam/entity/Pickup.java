package com.mojang.mojam.entity;

import java.util.Random;

import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.*;

public class Pickup extends Entity {

    private Animation anim;
    private int life;
    private final int resourceType;

    public Pickup(PizzaWorld world, float x, float z, int type) {
        super(world, x, z);
        this.resourceType = type;

        try {
            anim = new Animation(new SpriteSheet(PizzaResource.iconNames[type], 32, 32), 200);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        this.life = 30000;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        Player player = world.getPlayer();
        float dx = player.x - x;
        float dz = player.z - z;
        if (dx * dx + dz * dz < 128.0f * 128.0f) {
            float dist = (float) Math.sqrt(dx * dx + dz * dz);
            dx /= dist;
            dz /= dist;
            velocity.x += dx * deltaMS * .001f * 120;
            velocity.z += dz * deltaMS * .001f * 120;
        }
        velocity.x -= velocity.x * deltaMS * .001f * 2;
        velocity.z -= velocity.z * deltaMS * .001f * 2;
        x += velocity.x * deltaMS * .001f;
        z += velocity.z * deltaMS * .001f;

        y = 5 + (float) Math.cos(life * .002) * 5.f;
        life -= deltaMS;
        return life > 0;
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        Image current = anim.getCurrentFrame();
        if (life < 5000) {
            float alpha = life / 5000.0f;
            current.setColor(0, 1, 1, 1, alpha);
            current.setColor(1, 1, 1, 1, alpha);
            current.setColor(2, 1, 1, 1, alpha);
            current.setColor(3, 1, 1, 1, alpha);
        } else {
            current.setColor(0, 1, 1, 1, 1);
            current.setColor(1, 1, 1, 1, 1);
            current.setColor(2, 1, 1, 1, 1);
            current.setColor(3, 1, 1, 1, 1);
        }
        current.draw(x - camera.getX() - 16, z - camera.getY() - 32 - y);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return other instanceof Player;
    }

    @Override
    protected void onCollide(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.addResource(resourceType);
            Sounds.getInstance().playSound(Sounds.PICKUP_HEALTH, x, y, z);
            setRemoved();
        }
    }

    public static int randomType(Random random) {
        int selectRoll = random.nextInt(14);
        if (selectRoll <= 2) {
            return PizzaResource.TYPE_PEPPERONI;
        } else if (selectRoll <= 6) {
            return PizzaResource.TYPE_BASIL;
        }
        return PizzaResource.TYPE_FETA;
    }
}
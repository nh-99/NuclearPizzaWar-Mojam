package com.mojang.mojam.entity;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.data.OffsetImage;
import com.mojang.mojam.world.PizzaWorld;

public class AlienProjectile extends Projectile {

    public AlienProjectile(PizzaWorld world, float x, float y, float z, Vector3f direction, float speed) {
        super(world, x, y, z, direction, speed);

        damage = 40;
        try {
            bulletImage = new OffsetImage("res/actors/alienbullet.png");
            bulletOffset = new Vector2f(bulletImage.getWidth() / 2, bulletImage.getHeight() / 2);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCollide(Entity entity) {
        if (entity instanceof Player || entity instanceof PizzaBubble) {
            HealthEntity healthEntity = (HealthEntity) entity;
            healthEntity.push(new Vector3f(direction.x * 5, 0, direction.z * 5));
            healthEntity.hurt(damage);
            setRemoved();
        }
    }

    @Override
    protected void onDeath() {
    }

}

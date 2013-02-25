package com.mojang.mojam.entity;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.data.OffsetImage;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.PizzaWorld;

/**
 * 
 * @author Johan
 */
public class Projectile extends Entity {
    protected int damage;
    protected Vector3f direction;
    protected float speed;
    protected float blastRadius;
    protected Image bulletImage;
    Vector2f bulletOffset;

    protected Projectile(PizzaWorld world, float x, float y, float z, Vector3f direction, float speed) {
        super(world, x, z);
        this.y = y;
        this.speed = speed;
        this.direction = direction;
        entityType = EntityType.ProjectileEntity;
    }

    public Projectile(PizzaWorld world, int projectileLevel, float x, float y, float z, Vector3f direction, float speed) {
        this(world, x, y, z, direction, speed);
        try {
            if (projectileLevel == 0) {
                bulletImage = new OffsetImage("res/actors/bullet.png");
                blastRadius = 0;
                damage = 20;
            } else if (projectileLevel == 1) {
                bulletImage = new OffsetImage("res/actors/bullet1.png");
                blastRadius = 30.0f;
                damage = 35;
            } else if (projectileLevel == 2) {
                bulletImage = new OffsetImage("res/actors/bullet2.png");
                blastRadius = 50.0f;
                damage = 45;
            } else {
                bulletImage = new OffsetImage("res/actors/bullet3.png");
                blastRadius = 50.0f;
                damage = 50;
            }
            bulletOffset = new Vector2f(bulletImage.getWidth() / 2, bulletImage.getHeight() / 2);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {
        x += direction.x * speed * deltaMS * .001f;
        y += direction.y * speed * deltaMS * .001f;
        z += direction.z * speed * deltaMS * .001f;
        if (!isOutOfWorld() && !isRemoved() && (y > 0 || !world.isOnPizza(x, z))) {
            return true;
        } else {
            onDeath();
            return false;
        }
    }

    protected void onDeath() {
        float bulletY = Math.max(0, y);
        world.addParticle(new AnimationParticle(world, x, bulletY, z, AnimationParticle.bulletImpactAnimation));
        Sounds.getInstance().playSound(Sounds.SHOT_LAND, x, y, z, 1.0f, .5f);
    }

    protected void renderSelf(GameContainer slickContainer, Graphics g, Camera camera, float yOff) {
        double angleDegrees = Math.atan2(direction.z, direction.x) * 180.0 / Math.PI;
        g.pushTransform();
        g.rotate(x - camera.getX(), z - camera.getY() - yOff, (float) angleDegrees + 90);
        g.drawImage(bulletImage, x - camera.getX() - bulletOffset.x, z - camera.getY() - bulletOffset.y - yOff);
        g.popTransform();

    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        bulletImage.setColor(0, 1, 1, 1, 1);
        bulletImage.setColor(1, 1, 1, 1, 1);
        bulletImage.setColor(2, 1, 1, 1, 1);
        bulletImage.setColor(3, 1, 1, 1, 1);
        renderSelf(slickContainer, g, camera, y);
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        bulletImage.setColor(0, 0, 0, 0, .5f);
        bulletImage.setColor(1, 0, 0, 0, .5f);
        bulletImage.setColor(2, 0, 0, 0, .5f);
        bulletImage.setColor(3, 0, 0, 0, .5f);
        renderSelf(slickContainer, g, camera, 0);
    }

    @Override
    public void onCollide(Entity entity) {
        if (!isRemoved()) {
            if (entity.entityType == EntityType.EnemyEntity && entity instanceof Alien) {
                Alien alien = (Alien) entity;
                float pushAmount = 200.0f;
                alien.hurt(damage);
                if (blastRadius > 1.0f) {
                    world.createExplosion(damage / 2, blastRadius, this, alien);
                }
                entity.push(new Vector3f(direction.x * pushAmount, 0, direction.z * pushAmount));
                setRemoved();
            } else if (entity instanceof PizzaBubble) {
                ((PizzaBubble) entity).hurt(damage);
                setRemoved();
            }
        }
    }

    @Override
    public float getCollisionRadius() {
        return 8;
    }

    @Override
    public float getEntityHeight() {
        return 10.0f;
    }
}

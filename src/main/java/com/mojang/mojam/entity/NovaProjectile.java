/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.entity;

import java.util.*;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.util.ColorTools;
import com.mojang.mojam.world.PizzaWorld;

/**
 * 
 * @author Johan
 */
public class NovaProjectile extends Projectile {
    Image novaParticleImage;
    Vector2f startPosition;
    float maxDistance;
    Color colorMult = new Color(Color.white);
    List<Entity> hitEntities = new ArrayList<Entity>();

    public NovaProjectile(PizzaWorld world, float x, float y, float z, Vector3f direction, float speed, float maxDistance, int damage) {
        super(world, x, y, z, direction, speed);
        this.damage = damage;
        startPosition = new Vector2f(x, z);
        this.maxDistance = maxDistance;
        try {
            bulletImage = new Image("res/actors/nova.png");
            bulletOffset = new Vector2f(bulletImage.getWidth() / 2, bulletImage.getHeight() / 2);
        } catch (SlickException e) {
            int a = 0;
        }
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {
        x += direction.x * speed * deltaMS * .001f;
        y += direction.y * speed * deltaMS * .001f;
        z += direction.z * speed * deltaMS * .001f;
        Vector2f curPos = new Vector2f(x, z);
        float currentDistance = curPos.distance(startPosition);
        if (currentDistance / maxDistance > 0.2f) {
            float dt = (float) deltaMS / 1000;
            ColorTools.visualSeekAlpha(colorMult, 0.0f, 0.2f);
        }
        if (currentDistance > maxDistance) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        bulletImage.setColor(0, 1, 1, 1, colorMult.a);
        bulletImage.setColor(1, 1, 1, 1, colorMult.a);
        bulletImage.setColor(2, 1, 1, 1, colorMult.a);
        bulletImage.setColor(3, 1, 1, 1, colorMult.a);
        renderSelf(slickContainer, g, camera, 0);
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
    }

    @Override
    public boolean collidesWith(Entity other) {
        if (other.getType() != EntityType.EnemyEntity) {
            return false;
        } else {
            for (Entity e : hitEntities) {
                if (e == other) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void onCollide(Entity entity) {
        if (!isRemoved()) {
            if (entity instanceof Alien) {
                Alien alien = (Alien) entity;
                float pushAmount = 400.0f;
                alien.hurt(damage);
                entity.push(new Vector3f(direction.x * pushAmount, 0, direction.z * pushAmount));
                setRemoved();
            } else if (entity instanceof PizzaBubble) {
                ((PizzaBubble) entity).hurt(damage);
                setRemoved();
            }
        }
    }
}

package com.mojang.mojam.entity;

import java.util.*;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.world.PizzaWorld;

public abstract class Entity {
    public enum EntityType {
        GenericEntity, PlayerEntity, EnemyEntity, ProjectileEntity
    }

    private static int nextId = 0;
    protected static Random random = new Random();

    protected int id;
    protected float x, y, z;

    protected Vector3f acceleration = new Vector3f(0, 0, 0);
    protected Vector3f velocity = new Vector3f(0, 0, 0);

    protected final PizzaWorld world;
    public static Image shadowImage;
    protected static final Color shadowColorMult = new Color(0, 0, 0, 0.5f);
    private boolean removed;

    protected EntityType entityType = EntityType.GenericEntity;

    public Entity(PizzaWorld world) {
        this.world = world;
        this.id = ++nextId;
    }

    public Entity(PizzaWorld world, float x, float z) {
        this(world);
        this.x = x;
        this.z = z;
    }

    public EntityType getType() {
        return entityType;
    }

    public boolean isFixedPosition() {
        return false;
    }

    /**
     * 
     * @param slickContainer
     * @param deltaMS
     * @return true while the entity is active, false when it should be removed
     */
    public abstract boolean update(GameContainer slickContainer, int deltaMS);

    public abstract void render(GameContainer slickContainer, Graphics g, Camera camera);

    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        if (y < 0) {
            return;
        }
        float shadowScale = (float) Math.pow(1 - Math.min(y, 400) / 400, 2);
        int halfShadowX = (int) (((float) shadowImage.getWidth() / 2.0f) * shadowScale);
        int halfShadowY = (int) (((float) shadowImage.getHeight() / 2.0f) * shadowScale);
        g.drawImage(shadowImage, x - camera.getX() - halfShadowX, z - camera.getY() - halfShadowY, x - camera.getX() + halfShadowX, z - camera.getY() + halfShadowY, 0, 0, 32, 32, shadowColorMult);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float distanceToSqr(Entity other) {
        float dx = x - other.x;
        float dz = z - other.z;
        return dx * dx + dz * dz;
    }

    public float perspectiveDistanceToSqr(Entity other) {
        float dx = x - other.x;
        float dz = (z - other.z) * 2;
        return dx * dx + dz * dz;
    }

    public void checkCollisions(List<Entity> entities) {

        float thisRadius = getCollisionRadius();
        for (Entity e : world.getEntities()) {
            if (collidesWith(e) && e.collidesWith(this)) {
                float radius = thisRadius + e.getCollisionRadius();
                if (perspectiveDistanceToSqr(e) < radius * radius) {
                    onCollide(e);
                }
//                // positions may have changed, so recalcuate
//                if (perspectiveDistanceToSqr(e) < radius * radius) {
//                    e.onCollide(this);
//                }
            }
        }
    }

    public boolean collidesWith(Entity other) {
        return other != this;
    }

    public float getCollisionRadius() {
        return 16;
    }

    public float getEntityHeight() {
        return 32.0f;
    }

    public boolean isOutOfWorld() {
        return x < 0 || z < 0 || x > world.getWidth() || z > world.getHeight();
    }

    protected void onCollide(Entity entity) {
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved() {
        this.removed = true;
    }

    public void push(Vector3f push) {
        velocity.x += push.x;
        velocity.y += push.y;
        velocity.z += push.z;
    }

    public void resolveCollisionWithFixedEntity(Entity entity) {
        float radius = getCollisionRadius() + entity.getCollisionRadius();

        // push away... or something like that
        double dx = (entity.x - x);
        double dz = (entity.z - z) * 2;
        double dist = Math.sqrt(dx * dx + dz * dz);
        x = entity.x - (float) (dx / dist) * radius;
        z = entity.z - (float) (dz / dist) * radius * .5f;
    }
}

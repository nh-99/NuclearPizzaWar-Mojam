package com.mojang.mojam.entity;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.PizzaWorld;

public class SpiderMine extends HealthEntity {

    private Animation anim;
    private float destinationX = -1, destinationZ;
    private int nextTalkMS;
    private int lifeMS;

    public SpiderMine(PizzaWorld world, float x, float z) {
        super(world, x, z);

        try {
            anim = new Animation(new SpriteSheet("res/actors/spider.png", 16, 16), 100);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        nextTalkMS = 5000 + random.nextInt(7) * 1000;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        float targetX = 0, targetZ = 0;
        Alien closest = world.getClosestEnemy(this, 128.0f);
        if (closest == null) {
            if (destinationX < 0 || (Math.abs(x - destinationX) < 10 && Math.abs(z - destinationZ) < 10)) {
                float radiusScale = 1.0f + (float) lifeMS * .00001f;
                float radius = (.25f + random.nextFloat() * .6f) * radiusScale;
                Vector2f pos = world.pizzaPositionFromRad(random.nextFloat() * (float) Math.PI * 2.0f, radius);
                destinationX = pos.x;
                destinationZ = pos.y;
            }
            targetX = destinationX;
            targetZ = destinationZ;
        } else {
            targetX = closest.x;
            targetZ = closest.z;
        }

        boolean isOnPizza = world.isOnPizza(x, z);
        if (isOnPizza) {
            float tx = targetX - x;
            float tz = targetZ - z;
            float dist = (float) Math.sqrt((double) (tx * tx + tz * tz));
            float speed = 90;

            tx = speed * tx / dist;
            tz = speed * tz / dist;
            velocity.x += (tx - velocity.x) * .1f;
            velocity.z += (tz - velocity.z) * .1f;
            velocity.y = 0;

            velocity.x *= .97f;
            velocity.z *= .97f;
        } else {
            acceleration.y = -9.8f;
            velocity.y += acceleration.y * deltaMS * .001f;
        }

        x += velocity.x * deltaMS * .001f;
        z += velocity.z * deltaMS * .001f;
        y += velocity.y * deltaMS * .001f;

        if (y < -100) {
            hurt(-y * deltaMS * .001f);
        }

        lifeMS += deltaMS;
        nextTalkMS -= deltaMS;
        if (nextTalkMS <= 0) {
            nextTalkMS = 5000 + random.nextInt(7) * 1000 + random.nextInt(1000);
            Sounds.getInstance().playSound(Sounds.SPIDER_TALK, x, y, z);
            world.addParticle(new ShockwaveParticle(world, x, z, .7f));
        }

        return super.update(slickContainer, deltaMS);
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        anim.draw(x - camera.getX() - 8, z - camera.getY() - 13);
        super.render(slickContainer, g, camera);
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        if (y < 0) {
            return;
        }
        float shadowScale = (float) Math.pow(1 - Math.min(y, 400) / 400, 2);
        shadowScale *= .5f;
        int halfShadowX = (int) (((float) shadowImage.getWidth() / 2.0f) * shadowScale);
        int halfShadowY = (int) (((float) shadowImage.getHeight() / 2.0f) * shadowScale);
        g.drawImage(shadowImage, x - camera.getX() - halfShadowX, z - camera.getY() - halfShadowY, x - camera.getX() + halfShadowX, z - camera.getY() + halfShadowY, 0, 0, 32, 32, shadowColorMult);
    }

    @Override
    public float getMaxHealth() {
        return 20.0f;
    }

    @Override
    public float getCollisionRadius() {
        return 8.0f;
    }

    @Override
    protected void onCollide(Entity entity) {
        if (entity.isFixedPosition()) {
            resolveCollisionWithFixedEntity(entity);
        } else if (entity instanceof Alien) {
            world.addParticle(new AnimationParticle(world, x, 0, z, AnimationParticle.explodeAnimation));
            Sounds.getInstance().playSound(Sounds.BIG_EXP, x, y, z);
            for (Entity e : world.getEntitiesInRange(x, z, 40.0f)) {
                if (e instanceof Alien || e instanceof PizzaBubble) {
                    ((HealthEntity) e).hurt(15);
                }
            }
            setRemoved();
        }
    }

}

package com.mojang.mojam.entity;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.PizzaWorld;

public class Alien extends HealthEntity {

    public static final int TYPE_ATTACK_PLAYER = 0;
    public static final int TYPE_ATTACK_NUKE = 1;
    public static final int TYPE_SHOOTER = 2;
    public static final int TYPE_BIGGUS = 3;

    private static final String[] animations = new String[] {
            "res/actors/alien1.png", "res/actors/alien2.png", "res/actors/alien3.png", "res/actors/alien4.png"
    };

    private int type;
    private int challengeLevel;
    private Animation alienAnim;
    private int nextDamageMS;
    private int nextTalkMS;

    private float destinationX = -1, destinationZ;
    private int nextShootMS;
    private int suicideTimeMS;

    public Alien(PizzaWorld world, float x, float z, int type, int challengeLevel) {
        super(world, x, z);
        this.type = type;
        this.challengeLevel = challengeLevel;
        health = getMaxHealth();
        entityType = EntityType.EnemyEntity;
        try {
            int size = 32;
            if (type == TYPE_BIGGUS) {
                size = 64;
            }
            alienAnim = new Animation(new SpriteSheet(animations[type], size, size), 200);
            alienAnim.setLooping(true);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        nextTalkMS = 3000 + random.nextInt(6) * 1000;
        nextShootMS = 8000 + random.nextInt(3) * 1000;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        float targetX = 0, targetZ = 0;
        if (type == TYPE_ATTACK_PLAYER) {
            Entity target = world.getPlayer();
            targetX = target.getX();
            targetZ = target.getZ();
        } else if (type == TYPE_ATTACK_NUKE) {
            Entity target = world.getArtichoke();
            targetX = target.getX();
            targetZ = target.getZ();
        } else if (type == TYPE_SHOOTER || type == TYPE_BIGGUS) {
            if (destinationX < 0 || (Math.abs(x - destinationX) < 10 && Math.abs(z - destinationZ) < 10)) {
                Vector2f pos = world.pizzaPositionFromRad(random.nextFloat() * (float) Math.PI * 2.0f, .25f + random.nextFloat() * .6f);
                destinationX = pos.x;
                destinationZ = pos.y;
            }
            targetX = destinationX;
            targetZ = destinationZ;
        }
        boolean isOnPizza = world.isOnPizza(x, z);

        if (isOnPizza) {
            float tx = targetX - x;
            float tz = targetZ - z;
            float dist = (float) Math.sqrt((double) (tx * tx + tz * tz));
            float speed = 40;
            if (type == TYPE_ATTACK_PLAYER) {
                speed = 70 + 15 * challengeLevel;
            }

            tx = speed * tx / dist;
            tz = speed * tz / dist;
            velocity.x += (tx - velocity.x) * .1f;
            velocity.z += (tz - velocity.z) * .1f;
        }

        velocity.x *= .97f;
        velocity.z *= .97f;
        x += velocity.x * deltaMS * .001f;
        z += velocity.z * deltaMS * .001f;

        acceleration.y = -9.8f;
        velocity.y += acceleration.y * deltaMS * .001f;
        y += velocity.y;
        if (y < 0 && isOnPizza) {
            y = 0;
            velocity.y = 0;
        }
        alienAnim.update(deltaMS);

        nextDamageMS -= deltaMS;

        if (!super.update(slickContainer, deltaMS)) {
            Sounds.getInstance().playSound(Sounds.ALIEN_DEATH, x, y, z);
            world.addParticle(new AnimationParticle(world, x, y, z, AnimationParticle.explodeAnimation2));

            if (random.nextInt(7) == 0) {
                world.addEntity(new Pickup(world, x, z, Pickup.randomType(random)));
            }
            world.getCamera().addScreenShake(type == TYPE_BIGGUS ? 2.0f : 1.0f);
            return false;
        }

        nextTalkMS -= deltaMS;
        if (nextTalkMS <= 0) {
            nextTalkMS = 3000 + random.nextInt(6) * 1000;
            Sounds.getInstance().playSound(Sounds.ALIEN_TALK, x, y, z);
        }

        nextShootMS -= deltaMS;
        if (type == TYPE_SHOOTER) {
            if (nextShootMS <= 0) {
                nextShootMS = 4000 + random.nextInt(3) * 1000;

                Player player = world.getPlayer();
                float dx = player.x - x;
                float dz = player.z - z;

                Vector3f projectileDirection = new Vector3f(dx, 0, dz);
                projectileDirection.normalise();
                Projectile bullet = new AlienProjectile(world, x, 10, z, projectileDirection, 150);
                world.addEntity(bullet);

                if (challengeLevel > 0) {
                    double angle = Math.atan2(projectileDirection.z, projectileDirection.x);

                    projectileDirection = new Vector3f();
                    projectileDirection.x = (float) Math.cos(angle + Math.PI * .05);
                    projectileDirection.z = (float) Math.sin(angle + Math.PI * .05);
                    bullet = new AlienProjectile(world, x, 10, z, projectileDirection, 150);
                    world.addEntity(bullet);

                    projectileDirection = new Vector3f();
                    projectileDirection.x = (float) Math.cos(angle - Math.PI * .05);
                    projectileDirection.z = (float) Math.sin(angle - Math.PI * .05);
                    bullet = new AlienProjectile(world, x, 10, z, projectileDirection, 150);
                    world.addEntity(bullet);
                }
                if (challengeLevel > 1) {
                    // at level 2, withdraw one second of delay
                    nextShootMS -= 1000;
                }

                Sounds.getInstance().playSound(Sounds.ALIEN_SHOOT, x, y, z);
            }
        } else if (type == TYPE_BIGGUS) {
            if (nextShootMS <= 0) {
                nextShootMS = 4000 + random.nextInt(3) * 1000;

                double startAngle = random.nextDouble() * Math.PI;
                for (double a = 0; a < Math.PI * 2; a += Math.PI * .15f) {
                    double angle = startAngle + a;
                    Vector3f projectileDirection = new Vector3f();
                    projectileDirection.x = (float) Math.cos(angle + Math.PI * .05);
                    projectileDirection.y = (float) (Math.PI * .025);
                    projectileDirection.z = (float) Math.sin(angle + Math.PI * .05);
                    Projectile bullet = new AlienProjectile(world, x, 10, z, projectileDirection, 150);
                    world.addEntity(bullet);
                }
                Sounds.getInstance().playSound(Sounds.ALIEN_SHOOT, x, y, z);
            }
        } else if (type == TYPE_ATTACK_NUKE && suicideTimeMS > 0) {
            int beepTime = suicideTimeMS / 500;
            suicideTimeMS -= deltaMS;

            if (beepTime != suicideTimeMS / 500) {
                Sounds.getInstance().playSound(Sounds.ALIEN_WARNING, x, y, z);
                world.addParticle(new ShockwaveParticle.AlienWarning(world, x, z));
            }
            if (suicideTimeMS <= 0) {
                // if still within range of the artichoke, deal damage
                Artichoke artichoke = world.getArtichoke();
                float distanceToSqr = perspectiveDistanceToSqr(artichoke);
                float maxDist = getCollisionRadius() + artichoke.getCollisionRadius() + 10;
                if (distanceToSqr < maxDist * maxDist) {
                    artichoke.hurt(150);
                }
                Sounds.getInstance().playSound(Sounds.ALIEN_SUICIDE, x, y, z);
                world.addParticle(new AnimationParticle(world, x, y, z, AnimationParticle.explodeAnimation3));
                world.getCamera().addScreenShake(3.0f);
                setRemoved();
            }
        }

        if (y < -100) {
            hurt(-y * deltaMS * .001f);
        }

        return true;
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        Image alienImage = alienAnim.getCurrentFrame();
        int yOff = -32;
        if (type == TYPE_BIGGUS) {
            yOff = -44;
        }
        g.drawImage(alienImage, x - camera.getX() - alienImage.getWidth() / 2, z - camera.getY() + yOff - y, getHurtColor());
        super.render(slickContainer, g, camera);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return other != this;
    }

    @Override
    protected void onCollide(Entity entity) {
        if (entity.isFixedPosition()) {
            resolveCollisionWithFixedEntity(entity);
        } else if (entity instanceof Alien) {
            float radius = getCollisionRadius() + entity.getCollisionRadius();

            // push away... or something like that
            double dx = (entity.x - x);
            double dz = (entity.z - z) * 2;
            double angle = Math.atan2(dz, dx);
            double distScale = 1.0f - Math.sqrt(dx * dx + dz * dz) / radius;
            velocity.x += -(float) Math.cos(angle) * distScale * 2;
            velocity.z += -(float) Math.sin(angle) * distScale * 2;
            entity.velocity.x += (float) Math.cos(angle) * distScale * 2;
            entity.velocity.z += (float) Math.sin(angle) * distScale * 2;
        }
        if (entity instanceof Player) {
            if (nextDamageMS <= 0) {
                ((HealthEntity) entity).hurt(10);
                nextDamageMS = 200;
            }
        } else if (type == TYPE_ATTACK_NUKE && entity instanceof Artichoke && suicideTimeMS == 0) {
            suicideTimeMS = 1500;
        }

    }

    @Override
    public float getMaxHealth() {
        if (type == TYPE_ATTACK_NUKE) {
            return 100.0f + 50.0f * challengeLevel;
        }
        if (type == TYPE_BIGGUS) {
            return 500.0f + 100.0f * challengeLevel;
        }
        return 100.0f;
    }

    @Override
    public float getCollisionRadius() {
        if (type == TYPE_BIGGUS) {
            return 32.0f;
        }
        return super.getCollisionRadius();
    }

    @Override
    public void push(Vector3f push) {
        if (type == TYPE_BIGGUS) {
            push = new Vector3f(push.x * .2f, push.y * .2f, push.z * .2f);
        }
        super.push(push);
    }
}

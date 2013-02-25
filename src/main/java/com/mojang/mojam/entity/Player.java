package com.mojang.mojam.entity;



import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import com.mojang.mojam.Camera;
import com.mojang.mojam.gui.BeamBar;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.*;

/**
 * 
 * @author Johan
 */
public class Player extends HealthEntity {

    private SpriteSheet playerImageSheet;
    private Animation[] playerAnim = new Animation[8];
    private int currentAnimation = 0;
    private long currentLifeTimeMS = 0;
    private long lastShotFiredTimeMS = 0;
    private int firingArm = 0;
    private final float playerGroudFriction = 1.8f;
    private final float playerAirFriction = 0.9f;
    private float playerFriction = 1.8f;
    // Just use a random direction when starting for now.
    Vector2f playerDirection = new Vector2f(1, 0);
    private long nextEngineParticleMS;
    private float engineStrength;
    private int engineTapTimeMS;

    private Attributes attributes = new Attributes();
    private float aimX;
    private float aimZ;

    private float beamAnimation;
    private long beamShootTimeMS = 0;
    private long beamSoundTimeMS = 0;
    private boolean activatedBeam;
    private float beamTime;
    private float beamTimeRechargeAmount = 0;
    private BeamBar beamBar;

    private Vector2f[] moveDirections = new Vector2f[] {
            new Vector2f(-45 - 90), new Vector2f(45 - 90), new Vector2f(-135 - 90), new Vector2f(135 - 90), new Vector2f(-90 - 90), new Vector2f(90 - 90), new Vector2f(0 - 90), new Vector2f(180 - 90)
    };
    private static final float[][] shootOffsetX = new float[][] {
            {
                    -12, 0, 4, 14, -12, 12, -10, 12
            }, {
                    8, 8, -14, -4, -12, 12, 14, -10
            }
    };
    private static final float[][] shootOffsetZ = new float[][] {
            {
                    -2, -5, 5, -3, 2, -5, -5, 4
            }, {
                    -5, -1, 0, 3, -5, 1, -5, 4
            }
    };


    private int resources[] = new int[PizzaResource.NUM_RESOURCES];

    public Player(PizzaWorld world, float x, float z) throws SlickException {
        super(world, x, z);
        health = getMaxHealth();
        entityType = EntityType.PlayerEntity;
        playerImageSheet = new SpriteSheet("res/actors/player1_1.png", 32, 32);

        Image[] imagesUpLeft = new Image[4];
        Image[] imagesUpRight = new Image[4];
        for (int i = 0; i < 4; i++) {
            imagesUpLeft[i] = playerImageSheet.getSprite(i, 1);
            imagesUpRight[i] = imagesUpLeft[i].getFlippedCopy(true, false);
        }
        playerAnim[0] = new Animation(imagesUpLeft, 200, true);
        playerAnim[1] = new Animation(imagesUpRight, 200, true);

        Image[] imagesDownLeft = new Image[4];
        Image[] imagesDownRight = new Image[4];
        for (int i = 0; i < 4; i++) {
            imagesDownLeft[i] = playerImageSheet.getSprite(i, 3);
            imagesDownRight[i] = imagesDownLeft[i].getFlippedCopy(true, false);
        }
        playerAnim[2] = new Animation(imagesDownLeft, 200, true);
        playerAnim[3] = new Animation(imagesDownRight, 200, true);

        Image[] imagesLeft = new Image[4];
        Image[] imagesRight = new Image[4];
        for (int i = 0; i < 4; i++) {
            imagesLeft[i] = playerImageSheet.getSprite(i, 2);
            imagesRight[i] = imagesLeft[i].getFlippedCopy(true, false);
        }
        playerAnim[4] = new Animation(imagesLeft, 200, true);
        playerAnim[5] = new Animation(imagesRight, 200, true);

        Image[] imagesUp = new Image[4];
        Image[] imagesDown = new Image[4];
        for (int i = 0; i < 4; i++) {
            imagesUp[i] = playerImageSheet.getSprite(i, 0);
            imagesDown[i] = playerImageSheet.getSprite(i, 4);
        }
        playerAnim[6] = new Animation(imagesUp, 200, true);
        playerAnim[7] = new Animation(imagesDown, 200, true);
        beamBar = new BeamBar(this);
    }

    // We currently assume this will be singelplayer, need to change
    // some stuff if we change to multiplayer.
    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        acceleration.set(0, 0, 0);
        Input input = slickContainer.getInput();
        Vector2f direction = new Vector2f(0, 0);
        boolean isMoving = false;

        if (input.isKeyDown(Input.KEY_W)) {
            direction.y -= 1;
            isMoving = true;
        }
        if (input.isKeyDown(Input.KEY_S)) {
            direction.y += 1;
            isMoving = true;
        }
        if (input.isKeyDown(Input.KEY_A)) {
            direction.x -= 1;
            isMoving = true;
        }
        if (input.isKeyDown(Input.KEY_D)) {
            direction.x += 1;
            isMoving = true;
        }
        // normalise so that moving diagonal isn't faster than orthogonal
        direction.normalise();
        if (input.isKeyDown(Input.KEY_SPACE)) {
            if (y == 0) {
                velocity.y = 5.0f;
            }
            engineTapTimeMS += deltaMS;

            velocity.y += engineStrength * deltaMS * .001f * .75f;
            float engineFuelDecrease = .8f - .2f * attributes.levels[Attributes.JETPACK];
            engineStrength += (0 - engineStrength) * engineFuelDecrease * deltaMS * .001f;

            nextEngineParticleMS -= deltaMS * 5;
            if (y == 0 && engineStrength > 14.0f) {
                // Sounds.getInstance().playSound(Sounds.PLAYER_JETPACK, x, y,
// z);
            }
            if (nextEngineParticleMS < 0) {
                Sounds.getInstance().playSound(Sounds.PLAYER_ACC, x, y, z);
            }
        } else {
            if (y > 40.0f && engineTapTimeMS > 0 && engineTapTimeMS < 200) {
                velocity.y = -20.0f;
                Sounds.getInstance().playSound(Sounds.JETPACK_FAIL, x, y, z);
            }
            engineTapTimeMS = 0;

            engineStrength += (16.0f - engineStrength) * 4.0f * deltaMS * .001f;
        }
        updateFriction(isMoving);
        {
            acceleration.x = -(velocity.x * playerFriction) + direction.x * getPlayerSpeed();
            acceleration.z = -(velocity.z * playerFriction) + direction.y * getPlayerSpeed();
        }
        acceleration.y = -9.8f;
        velocity.x += acceleration.x * deltaMS * .001f;
        velocity.y += acceleration.y * deltaMS * .001f;
        velocity.z += acceleration.z * deltaMS * .001f;

        x += velocity.x * (deltaMS * .001f) * 20;
        y += velocity.y * (deltaMS * .001f) * 20;
        z += velocity.z * (deltaMS * .001f) * 20;
        if (y < 0) {
            if (world.isOnPizza(x, z)) {
                if (velocity.y < -13) {
                    world.addParticle(new ShockwaveParticle(world, x, z, 2.0f + attributes.levels[Attributes.SHOCKWAVE] * 1.0f));
                    Sounds.getInstance().playSound(Sounds.PLAYER_LAND, x, y, z);
                    world.getCamera().addScreenShake(3.0f);

                    // push aliens away from player
                    // thanks Quargos!
                    float range = 128.0f + 64 * attributes.levels[Attributes.SHOCKWAVE];
                    for (Entity e : world.getEntitiesInRange(x, z, range)) {
                        if (e instanceof Alien || e instanceof PizzaBubble) {
                            float dx = e.getX() - x;
                            float dz = e.getZ() - z;
                            float distSqr = dx * dx + dz * dz;
                            {
                                float scale = (1.0f - (float) Math.sqrt(distSqr) / range);
                                float force = 400.0f * scale;
                                Vector3f push = (Vector3f) new Vector3f(dx, 0, dz).normalise();
                                push.x *= force;
                                push.z *= force;
                                e.push(push);
                                ((HealthEntity) e).hurt((20 + attributes.levels[Attributes.SHOCKWAVE] * 20) * scale);
                            }
                        }
                    }
                }

                y = 0;
                velocity.y = 0;
            }
        }

        float tx = x - slickContainer.getWidth() / 2;
        float ty = z - slickContainer.getHeight() / 2;
        Camera camera = world.getCamera();
        camera.setTargetX(tx);
        camera.setTargetY(ty);

        currentLifeTimeMS += deltaMS;

        this.aimX = camera.getViewX(input.getMouseX());
        this.aimZ = camera.getViewY(input.getMouseY());
        playerDirection = (new Vector2f(-(x - aimX), -(z - aimZ))).normalise();
        currentAnimation = 0;
        double minTheta = 360;
        Vector3f playerDirectionAsVector3 = new Vector3f(playerDirection.x, playerDirection.y, 0);
        for (int a = 0; a < 8; ++a) {
            Vector3f asVector3 = new Vector3f(moveDirections[a].x, moveDirections[a].y, 0);
            float angle = Vector3f.angle(asVector3, playerDirectionAsVector3);
            if (angle < minTheta) {
                minTheta = angle;
                currentAnimation = a;
            }
        }

        updateBeamWeapon(slickContainer, deltaMS);

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            if (currentLifeTimeMS > lastShotFiredTimeMS + getFireDelay()) {
                lastShotFiredTimeMS = currentLifeTimeMS;

                firingArm = firingArm ^ 1;
                if (attributes.levels[Attributes.FIRERATE] == 5) {
                    // shoot double from both arms!!!
                    shootBullet(aimX, aimZ, 0);
                    shootBullet(aimX, aimZ, 1);

                    double angle = Math.atan2(playerDirection.y, playerDirection.x);
                    Vector3f shootDir = new Vector3f((float) Math.cos(angle - Math.PI * .05), 0, (float) Math.sin(angle - Math.PI * .05));
                    shootBullet(aimX, aimZ, 0, shootDir);
                    shootDir = new Vector3f((float) Math.cos(angle + Math.PI * .05), 0, (float) Math.sin(angle + Math.PI * .05));
                    shootBullet(aimX, aimZ, 1, shootDir);
                } else if (attributes.levels[Attributes.FIRERATE] == 4) {
                    // shoot from both arms
                    shootBullet(aimX, aimZ, 0);
                    shootBullet(aimX, aimZ, 1);
                } else {
                    shootBullet(aimX, aimZ, firingArm);
                }

                if (attributes.levels[Attributes.FIRE_DAMAGE_LEVEL] <= 1) {
                    Sounds.getInstance().playSound(Sounds.SHOT, x, y, z, .95f + random.nextFloat() * .1f, 1.0f);
                } else {
                    Sounds.getInstance().playSound(Sounds.SHOT2, x, y, z, .95f + random.nextFloat() * .1f, 1.0f);
                }
            }
        }

        nextEngineParticleMS -= deltaMS;
        if (nextEngineParticleMS <= 0) {
            nextEngineParticleMS = 400;
            world.addParticle(new AnimationParticle(world, x, y, z, AnimationParticle.engineAnimation));
        }

        if (y < -100) {
            // take damage when falling out of the world
            hurt(-y * deltaMS * .001f);
        } else if (health > 0 && health < getMaxHealth()) {
            health += deltaMS * .005f * (1.0f + attributes.levels[Attributes.REGENERATE] * .5f);
        }
        if (!super.update(slickContainer, deltaMS)) {
            Sounds.getInstance().playSound(Sounds.PLAYER_DEATH, x, y, z);
            return false;
        }
        return true;
    }

    private void shootBullet(float tx, float tz, int shootArm) {
        Vector3f projectileDirection = new Vector3f(playerDirection.x, 0, playerDirection.y);
        shootBullet(tx, tz, shootArm, projectileDirection);
    }

    private void shootBullet(float tx, float tz, int shootArm, Vector3f projectileDirection) {

        double targetDistance = Math.sqrt((x - tx) * (x - tx) + (z - tz) * (z - tz));
        float shootHeight = y + 10;
        projectileDirection.y = -shootHeight / ((float) targetDistance + 40.0f);
        float bulletSpeed = 800;

        Projectile bullet = new Projectile(world, attributes.levels[Attributes.FIRE_DAMAGE_LEVEL], x + shootOffsetX[shootArm][currentAnimation], shootHeight, z
                + shootOffsetZ[shootArm][currentAnimation], projectileDirection, bulletSpeed);
        world.addEntity(bullet);
    }

    private void updateBeamWeapon(GameContainer container, int deltaMS) {
        beamAnimation += deltaMS * .001f;
        if (!activatedBeam && beamTime > 200 && container.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            activatedBeam = true;
            beamTimeRechargeAmount = 0;
        }
        if (activatedBeam) {
            beamTime -= deltaMS;
            if (beamTime <= 0) {
                activatedBeam = false;
            } else {
                beamShootTimeMS -= deltaMS;
                beamSoundTimeMS -= deltaMS;
                if (beamSoundTimeMS <= 0) {
                    beamSoundTimeMS = 75;
                    Sounds.getInstance().playSound(Sounds.BEAM, aimX, 0, aimZ);
                }

                if (beamShootTimeMS <= 0) {
                    beamShootTimeMS = 180;
                    world.addParticle(new AnimationParticle(world, aimX, 0, aimZ, AnimationParticle.explodeAnimation));
                    for (Entity e : world.getEntitiesInRange(aimX, aimZ, 64.0f)) {
                        if (e instanceof Alien || e instanceof PizzaBubble) {
                            ((HealthEntity) e).hurt(10);
                        }
                    }
                }
            }
        } else if (beamTime < getMaxBeamTime()) {
            beamTimeRechargeAmount += deltaMS * .00005f;
            beamTime += deltaMS * beamTimeRechargeAmount * beamTimeRechargeAmount;
        }
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        Animation a = playerAnim[currentAnimation];
        Color playerColor = getHurtColor();
        if (playerDirection.y >= 0) {
            a.draw(x - camera.getX() - a.getWidth() / 2, z - camera.getY() - a.getHeight() - y, playerColor);
            super.render(slickContainer, g, camera);
        }

        if (beamTime > 0 && activatedBeam) {
            double angle = playerDirection.getTheta() * Math.PI / 180.0 + Math.PI * .5;
            float armY = y + 10;
            float armX = x + shootOffsetX[0][currentAnimation] - camera.getX();
            float armZ = z + shootOffsetZ[0][currentAnimation] - camera.getY() - armY;
            renderLaserBeam(g, angle, armX, armZ, aimX - camera.getX(), aimZ - camera.getY());
            armX = x + shootOffsetX[1][currentAnimation] - camera.getX();
            armZ = z + shootOffsetZ[1][currentAnimation] - camera.getY() - armY;
            renderLaserBeam(g, angle, armX, armZ, aimX - camera.getX(), aimZ - camera.getY());
        }

        if (playerDirection.y < 0) {
            a.draw(x - camera.getX() - a.getWidth() / 2, z - camera.getY() - a.getHeight() - y, playerColor);
            super.render(slickContainer, g, camera);
        }

        beamBar.render(slickContainer, g, camera);
    }

    private void renderLaserBeam(Graphics g, double angle, float sx, float sy, float ex, float ey) {

        float animScale = (1.0f + (float) Math.cos(beamAnimation * 15) * .25f);
        float startCos = (float) Math.cos(angle) * 4f * animScale;
        float startSin = (float) Math.sin(angle) * 4f * animScale;

        for (int b = 0; b < 3; b++) {

            float c = startCos * (1.0f - b * .4f);
            float s = startSin * (1.0f - b * .4f);

            g.setColor(new Color(.5f + .25f * b, .33f * b * animScale, .2f * b * animScale, .75f));
            /* @formatter:off */
            Polygon poly = new Polygon(new float[] {
                    ex - c * .75f,
                    ey - s * .75f,
                    sx - c, sy - s,
                    sx + c, sy + s,
                    ex + c * .75f,
                    ey + s * .75f,
            });
            /* @formatter:on */
            g.fill(poly);
        }

    }

    @Override
    public boolean collidesWith(Entity other) {
        if ((other instanceof Alien) || (other instanceof Pickup) || (other instanceof AlienProjectile)) {
            return ((y < other.y + other.getEntityHeight()) && (y + this.getEntityHeight()) > (other.y));
        } else if (y < 16 && other instanceof Artichoke) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCollide(Entity entity) {
        if (entity instanceof Artichoke) {
            float radius = getCollisionRadius() + entity.getCollisionRadius();

            double dx = (entity.x - x);
            double dz = (entity.z - z) * 2;
            {
                double dist = Math.sqrt(dx * dx + dz * dz);
                x = entity.x - (float) (dx / dist) * radius;
                z = entity.z - (float) (dz / dist) * radius * .5f;
            }
        }
    }

    private void updateFriction(boolean isMoving) {
        if (y == 0) {
            playerFriction = playerGroudFriction;
        } else {
            playerFriction = playerAirFriction;
        }
    }

    @Override
    public float getMaxHealth() {
        if (attributes == null) {
            return 500.0f;
        }
        return 500.0f + attributes.levels[Attributes.HEALTH] * 100.0f;
    }

    public float getPlayerSpeed() {
        return 10 + 2.0f * attributes.levels[Attributes.SPEED];
    }

    public int getFireDelay() {
        int level = attributes.levels[Attributes.FIRERATE];
        if (level > 3) {
            level = 3;
        }
        return 250 - 25 * attributes.levels[Attributes.FIRERATE];
    }

    public static class Attributes {

        public static final int HEALTH = 0;
        public static final int SPEED = 1;
        public static final int JETPACK = 2;
        public static final int FIRERATE = 3;
        public static final int FIRE_DAMAGE_LEVEL = 4;
        public static final int SHOCKWAVE = 5;
        public static final int REGENERATE = 6;
        public static final int BEAM_DURATION = 7;
        public static final int COUNT = 8;

        public static final String[] DESCS = {
                "Max Health", "Speed", "Jetpack Power", "Fire Rate", "Fire Damage", "Shockwave Damage", "Regeneration Rate", "Beam Ammo"
        };
        public static final String[] ICONS = {
                "res/GUI/shopicons_health.png", "res/GUI/shopicons_speed.png", "res/GUI/shopicons_jetpack.png", "res/GUI/shopicons_firerate.png", "res/GUI/shopicons_fire_damage_level.png",
                "res/GUI/shopicons_nova.png", "res/GUI/shopicons_regenerate.png", "res/GUI/shopicons_count.png",
        };

        /* @formatter:off */
        private static final int[][][] UPGRADE_COSTS = {
                // health
                {
                    {2, 0, 0},
                    {4, 0, 0},
                    {5, 1, 0},
                },
                // speed
                {
                    {1, 1, 0},
                    {1, 3, 0},
                    {1, 3, 0},
                    {1, 4, 1},
                    {2, 5, 1},
                },
                // jet
                {
                    {0, 1, 0},
                    {0, 4, 0},
                },
                // firerate
                {
                    {0, 0, 1},
                    {2, 0, 2},
                    {4, 0, 2},
                    {8, 0, 5},
                    {10, 0, 7},
                },
                // firerate
                {
                    {2, 0, 3},
                    {6, 0, 5},
                    {12, 0, 6},
                },
                // shock
                {
                    {0, 0, 2},
                    {0, 1, 3},
                    {0, 1, 4},
                },
                // regen
                {
                    {2, 1, 0},
                    {4, 2, 0},
                    {5, 3, 1},
                    {8, 2, 1},
                    {12, 2, 1},
                },
                // beam
                {
                    {0, 1, 3},
                    {0, 1, 6},
                    {0, 1, 8},
                },
        };
        /* @formatter:on */

        public int[] levels = new int[COUNT];

        public int getTotalLevel() {
            int total = 0;
            for (int i : levels) {
                total += i;
            }
            return total;
        }

        public int[] getUpgradeCost(int i) {
            int currentLevel = levels[i];
            int[][] upgrades = UPGRADE_COSTS[i];
            if (currentLevel < upgrades.length) {
                return upgrades[currentLevel];
            }
            return null;
        }

        public int getMaxLevel(int i) {
            return UPGRADE_COSTS[i].length;
        }
    }

    public int getNextUpgradeCost() {
        return 500 + attributes.getTotalLevel() * 700;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void enableBeamPowerup() {
        this.beamTime = 10000;
    }

    public boolean isBeamActive() {
        return activatedBeam;
    }

    public float getBeamTime() {
        return beamTime;
    }

    public float getMaxBeamTime() {
        return 5000 + attributes.levels[Attributes.BEAM_DURATION] * 1500;
    }

    public void addResource(int resourceType) {
        resources[resourceType]++;
    }

    public void removeResource(int resourceType, int count) {
        resources[resourceType] -= count;
    }

    public int getResource(int resourceType) {
        return resources[resourceType];
    }

    public int[] getResources() {
        return resources;
    }

    public int[] getUpgradeCost(int i) {
        return attributes.getUpgradeCost(i);
    }

    public void buyUpgrade(int selectedUpgrade) {
        int[] upgradeCost = attributes.getUpgradeCost(selectedUpgrade);
        if (upgradeCost != null) {
            for (int r = 0; r < PizzaResource.NUM_RESOURCES; r++) {
                resources[r] -= upgradeCost[r];
            }
            attributes.levels[selectedUpgrade]++;
            if (selectedUpgrade == Attributes.HEALTH) {
                this.health = this.getMaxHealth();
            }
        }
        Sounds.getInstance().playSound(Sounds.UPGRADE, 0, 0, 0);
    }
}

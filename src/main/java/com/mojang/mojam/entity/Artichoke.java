package com.mojang.mojam.entity;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.*;

public class Artichoke extends HealthEntity {

    private Animation anim;
    private Animation wall1;
    private Animation wall2;
    private Animation wall3;
    private Image shadow;
    // 120 is all full walls
    private final float wallLifeLevel1 = 500;
    private final float wallLifeLevel2 = 1200;
    private final float wallLifeLevel3 = 2300;
    public float wallLevel = wallLifeLevel2;

    private int damageTime;
    private long lastBlasterFireTime = 0;
    private long lastNovaFireTime = 0;
    private long lastSpiderTime = 0;
    protected ArtichokeAttributes attributes = new ArtichokeAttributes(this);

    public Artichoke(PizzaWorld world, float x, float z) {
        super(world, x, z);
        try {
            anim = new Animation(new SpriteSheet("res/actors/artichoke.png", 128, 128), 200);
            wall1 = new Animation(new SpriteSheet("res/actors/wall1.png", 192, 192), 200);
            wall2 = new Animation(new SpriteSheet("res/actors/wall2.png", 192, 192), 200);
            wall3 = new Animation(new SpriteSheet("res/actors/wall3.png", 192, 192), 200);
            shadow = new Image("res/actors/largeshadow.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFixedPosition() {
        return true;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {
        if (lastBlasterFireTime + getTowerBlasterFireRate() < world.getGameTime()) {
            lastBlasterFireTime = world.getGameTime();
            if (attributes.levels[ArtichokeAttributes.TOWERBLASTER] > 0) {
                Alien closestEnemy = world.getClosestEnemy(this, getTowerBlasterRange());
                if (closestEnemy != null) {
                    Vector3f direction = new Vector3f(closestEnemy.x - (x - 2), closestEnemy.y - (y + 80), closestEnemy.z - z);
                    direction.normalise();
                    int projectileLevel = Math.max(0, attributes.levels[ArtichokeAttributes.TOWERBLASTER] - 3);
                    Projectile p = new Projectile(world, projectileLevel, x - 2, y + 80, z, direction, 400);
                    world.addEntity(p);
                    Sounds.getInstance().playSound(Sounds.BASE_SHOT, x, 80, z);
                }
            }
        }
        if (lastNovaFireTime + getNovaFireRate() < world.getGameTime()) {
            lastNovaFireTime = world.getGameTime();
            if (attributes.levels[ArtichokeAttributes.SHOCKWAVE] > 0) {
                Alien closestEnemy = world.getClosestEnemy(this, 100);
                if (closestEnemy != null) {
                    for (int a = 0; a < 36; ++a) {
                        Vector2f direction = new Vector2f(a * 10);
                        Vector3f direction3d = new Vector3f(direction.x, 0.3f, direction.y);
                        direction3d.normalise();
                        NovaProjectile nProjectile = new NovaProjectile(world, x + direction.x * getCollisionRadius() / 2, y, z + direction.y * getCollisionRadius() / 2, direction3d, 400, 150,
                                getNovaDamage());
                        world.addEntity(nProjectile);
                    }
                }
            }
        }
        if (lastSpiderTime + 4000 < world.getGameTime()) {
            lastSpiderTime = world.getGameTime();

            if (attributes.levels[ArtichokeAttributes.SPIDERS] > 0) {
                for (int i = 0; i < attributes.levels[ArtichokeAttributes.SPIDERS]; i++) {
                    double angle = random.nextDouble() * Math.PI * 2.0;
                    float range = getCollisionRadius() * .7f;
                    world.addEntity(new SpiderMine(world, x + (float) Math.cos(angle) * range, z + (float) Math.sin(angle) * range * .5f));
                }
                Sounds.getInstance().playSound(Sounds.SPIDER_TALK, x, y, z);
            }
        }
        if (health > 0 && health < getMaxHealth()) {
            if (world.getNumberOfEnimies() == 0) {
                health += (float) deltaMS * .001f * 10f;
            } else {
                health += (float) deltaMS * .001f;
            }
        }
        attributes.levels[ArtichokeAttributes.FORTIFICATION] = getFortificationLevel();
        damageTime -= deltaMS;
        return super.update(slickContainer, deltaMS);
    }

    @Override
    public void render(GameContainer slickContainer, Graphics g, Camera camera) {
        anim.draw(x - camera.getX() - 64, z - camera.getY() - 92);
        Animation outmostWall = null;
        if (wallLevel > wallLifeLevel2) {
            wall1.draw(x - camera.getX() - 100, z - camera.getY() - 92);
            wall2.draw(x - camera.getX() - 100, z - camera.getY() - 92);
            outmostWall = wall3;
        } else if (wallLevel > wallLifeLevel1) {
            wall1.draw(x - camera.getX() - 100, z - camera.getY() - 92);
            outmostWall = wall2;
        } else if (wallLevel > 0) {
            outmostWall = wall1;
        }
        if (outmostWall != null) {
            Image frame = outmostWall.getCurrentFrame();
            if (damageTime > 0) {
                frame.setColor(0, 1, .25f, .25f);
                frame.setColor(1, 1, .25f, .25f);
                frame.setColor(2, 1, .25f, .25f);
                frame.setColor(3, 1, .25f, .25f);
            } else {
                frame.setColor(0, 1, 1, 1);
                frame.setColor(1, 1, 1, 1);
                frame.setColor(2, 1, 1, 1);
                frame.setColor(3, 1, 1, 1);

            }
            frame.draw(x - camera.getX() - 100, z - camera.getY() - 92);
        }
        super.render(slickContainer, g, camera);
    }

    @Override
    public void renderGroundLayer(GameContainer slickContainer, Graphics g, Camera camera) {
        shadow.setColor(0, 1, 1, 1, .5f);
        shadow.setColor(1, 1, 1, 1, .5f);
        shadow.setColor(2, 1, 1, 1, .5f);
        shadow.setColor(3, 1, 1, 1, .5f);
        g.drawImage(shadow, x - camera.getX() - 64, z - camera.getY() - 64);
    }

    @Override
    public float getCollisionRadius() {
        if (wallLevel <= 0) {
            return 58.0f;
        } else if (wallLevel <= wallLifeLevel1) {
            return 58.0f + 16.0f;
        } else if (wallLevel <= wallLifeLevel2) {
            return 58.0f + 32.0f;
        } else {
            return 58.0f + 38.0f;
        }
    }

    @Override
    public float getMaxHealth() {
        return 1000.0f;
    }

    @Override
    public void hurt(float damage) {
        wallLevel -= damage;
        if (wallLevel < 0) {
            damage = -wallLevel;
            wallLevel = 0;
        } else {
            damage = 0;
        }
        Sounds.getInstance().playSound(Sounds.BASE_TAKES_DAMAGE, x, y, z);
        damageTime = 300;
        super.hurt(damage);
    }

    protected int getFortificationLevelFromWallLevel() {
        if (wallLevel == 0) {
            return 0;
        } else if (wallLevel < wallLifeLevel2) {
            return 1;
        } else if (wallLevel < wallLifeLevel3) {
            return 2;
        } else {
            return 3;
        }
    }

    protected int getMaxFortificationLevel() {
        return (int) (wallLifeLevel3 / 100.0f);
    }

    protected int getFortificationLevel() {
        return (int) ((wallLevel + 50.0f) / 100.0f);
    }

    public int[] getUpgradeCost(int i) {
        return attributes.getUpgradeCost(i);
    }

    public ArtichokeAttributes getAttributes() {
        return attributes;
    }

    private float getTowerBlasterRange() {
        return 100.0f + attributes.levels[ArtichokeAttributes.TOWERBLASTER] * 100.0f;
    }

    private int getTowerBlasterFireRate() {
        return 900 - attributes.levels[ArtichokeAttributes.TOWERBLASTER] * 100;
    }

    private int getNovaFireRate() {
        return 4500 - attributes.levels[ArtichokeAttributes.SHOCKWAVE] * 500;
    }

    private int getNovaDamage() {
        return 20 * attributes.levels[ArtichokeAttributes.SHOCKWAVE];
    }

    public static class ArtichokeAttributes {

        public static final int FORTIFICATION = 0;
        public static final int TOWERBLASTER = 1;
        public static final int SHOCKWAVE = 2;
        public static final int SPIDERS = 3;
        public static final int COUNT = 4;

        public static final String[] DESCS = {
                "Fortification", "Tower Blaster", "Nova Defense", "Defense Spiders"
        };
        public static final String[] ICONS = {
                "res/GUI/shopicons_base_walls.png", "res/GUI/shopicons_base_shot.png", "res/GUI/shopicons_base_blast.png", "res/GUI/shopicons_base_blast.png"
        };

        /* @formatter:off */
        private static final int[][][] UPGRADE_COSTS = {
                // fortification
                {
                    {2, 0, 0},
                },
                // tower blaster
                {
                    {0, 0, 1},
                    {2, 0, 2},
                    {4, 0, 3},
                    {4, 4, 4},
                    {4, 8, 6},
                },
                // shockwave
                {
                    {0, 1, 1},
                    {0, 1, 3},
                    {0, 1, 5},
                },
                // spiders
                {
                    {0, 5, 0},
                    {0, 9, 0},
                    {0, 12, 0},
                },
        };
        /* @formatter:on */

        public int[] levels = new int[COUNT];
        private final Artichoke owner;

        public ArtichokeAttributes(Artichoke owner) {
            this.owner = owner;
        }

        public int[] getUpgradeCost(int i) {
            if (i == FORTIFICATION) {
                if (owner.getFortificationLevel() < owner.getMaxFortificationLevel()) {
                    return UPGRADE_COSTS[i][0];
                }
                return null;
            }
            int currentLevel = levels[i];
            int[][] upgrades = UPGRADE_COSTS[i];
            if (currentLevel < upgrades.length) {
                return upgrades[currentLevel];
            }
            return null;
        }

        public int getMaxLevel(int i) {
            if (i == FORTIFICATION) {
                return owner.getMaxFortificationLevel();
            }
            return UPGRADE_COSTS[i].length;
        }
    }

    public ArtichokeAttributes getArtichokeAttributes() {
        return attributes;
    }

    public void buyUpgrade(int selectedUpgrade) {
        int[] upgradeCost = attributes.getUpgradeCost(selectedUpgrade);
        if (upgradeCost != null) {
            Player p = world.getPlayer();
            for (int r = 0; r < PizzaResource.NUM_RESOURCES; r++) {
                p.removeResource(r, upgradeCost[r]);
            }
            attributes.levels[selectedUpgrade]++;
            if (selectedUpgrade == ArtichokeAttributes.FORTIFICATION) {
                wallLevel += 100.0f;
                if (wallLevel > wallLifeLevel3) {
                    wallLevel = wallLifeLevel3;
                }
            }
        }
        Sounds.getInstance().playSound(Sounds.UPGRADE, 0, 0, 0);
    }
}

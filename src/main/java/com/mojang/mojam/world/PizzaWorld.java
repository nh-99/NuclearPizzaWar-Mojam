package com.mojang.mojam.world;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.Camera;
import com.mojang.mojam.entity.*;
import com.mojang.mojam.gui.*;
import com.mojang.mojam.sound.Sounds;

public class PizzaWorld {

    private List<Entity> entities = new ArrayList<Entity>();
    private List<Entity> newEntities = new ArrayList<Entity>();
    private List<Entity> particles = new ArrayList<Entity>();
    private List<Entity> newParticles = new ArrayList<Entity>();
    private List<PizzaDoodad> doodads = new ArrayList<PizzaDoodad>();
    private GameDirector gameDirector;
    private UpgradeMenu upgradeMenu;
    private Player player;
    private Artichoke artichoke;
    private Image pizzaImage;
    private final Camera camera;
    private Starfield starfield;
    private float width;
    private float height;
    private float pizzaWorldCenterZ;
    private float pizzaWorldCutZ;
    private long gameTime;

    private boolean gameOver;
    private boolean isVictory;
    private int winTime;

    public PizzaWorld(Camera camera) {
        this.camera = camera;
        gameDirector = new GameDirector(this);
    }

    public void init(GameContainer container) throws SlickException {
        pizzaImage = new Image("res/pizzarelated/pizza_big.png");
        Entity.shadowImage = new Image("res/actors/shadow.png");
        AnimationParticle.initAnimations();
        PizzaResource.init();

        float cx = pizzaImage.getWidth() / 2;
        float cy = pizzaImage.getHeight() / 2;
        addEntity(player = new Player(this, pizzaImage.getWidth() / 2 + 10, pizzaImage.getHeight() / 2));
        addEntity(artichoke = new Artichoke(this, pizzaImage.getWidth() / 2, pizzaImage.getHeight() / 2));
        upgradeMenu = new UpgradeMenu(player, artichoke);

        pizzaWorldCenterZ = cy;
        pizzaWorldCutZ = pizzaWorldCenterZ;

        Random random = new Random();
        float radius = pizzaImage.getWidth() * .40f;
        /*
         * for (int i = 0; i < 10; i++) { float angle = random.nextFloat() *
         * (float) Math.PI * 2.0f; float dist = random.nextFloat() * radius;
         * addEntity(new Alien(this, cx + (float) Math.cos(angle) * dist, cy +
         * (float) Math.sin(angle) * dist * .5f, random.nextInt(2))); }
         */
        this.width = (float) pizzaImage.getWidth();
        this.height = (float) pizzaImage.getHeight();

        this.starfield = new Starfield((int) width, (int) height);

        this.camera.setConstraints(0, 0, width - container.getWidth(), height - container.getHeight());
        this.camera.centerOnConstraints();

        Image[] doodadImages = new Image[] {
                new Image("res/pizzarelated/shrooms_1.png"), new Image("res/pizzarelated/shrooms_1.png").getFlippedCopy(true, false), new Image("res/pizzarelated/salami_1.png"),
                new Image("res/pizzarelated/salami_2.png"), new Image("res/pizzarelated/olive_1.png"),
        };
        ArrayList<Image> doodadImageSelection = new ArrayList<Image>();
        doodadImageSelection.add(doodadImages[0]);
        doodadImageSelection.add(doodadImages[0]);
        doodadImageSelection.add(doodadImages[1]);
        doodadImageSelection.add(doodadImages[1]);
        doodadImageSelection.add(doodadImages[2]);
        doodadImageSelection.add(doodadImages[3]);
        doodadImageSelection.add(doodadImages[4]);
        doodadImageSelection.add(doodadImages[4]);
        doodadImageSelection.add(doodadImages[4]);
        doodadImageSelection.add(doodadImages[4]);
        for (int i = 0; i < 75; i++) {
            float angle, dist, x, z;
            boolean ok;

            do {
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                dist = (.3f + random.nextFloat() * .7f) * radius * .95f;
                x = cx + (float) Math.cos(angle) * dist;
                z = cy + (float) Math.sin(angle) * dist * .5f;

                ok = true;
                for (PizzaDoodad doodad : doodads) {
                    float dx = x - doodad.getX();
                    float dz = z - doodad.getZ();
                    if (dx * dx + dz * dz < 50 * 50) {
                        ok = false;
                        break;
                    }
                }
            } while (!ok);

            doodads.add(new PizzaDoodad(doodadImageSelection.get(random.nextInt(doodadImageSelection.size())), x, z));
        }
        gameDirector.init(container);
    }

    public void update(GameContainer container, int deltaMS) {
        if (container.getInput().isKeyPressed(Input.KEY_E)) {
            upgradeMenu.setVisible(!upgradeMenu.isVisible());
        }
        if (upgradeMenu.isVisible()) {
            upgradeMenu.update(container, deltaMS);

        } else {
            gameTime += deltaMS;

            gameDirector.update(container, deltaMS);
            Sounds.getInstance().setListenerPosition(camera.getX() + container.getWidth() / 2, 100.0f, camera.getY() + container.getHeight() / 2);

            starfield.update(container, deltaMS);

            updateEntityList(container, deltaMS, entities, newEntities, true);
            updateEntityList(container, deltaMS, particles, newParticles, false);

            if (player.isRemoved() || artichoke.isRemoved()) {
                gameOver = true;
                isVictory = false;
            } else if (gameDirector.isWon()) {
                winTime += deltaMS;
                if (winTime > 4000) {
                    gameOver = true;
                    isVictory = true;
                }
            }
        }

    }

    private void updateEntityList(GameContainer container, int deltaMS, List<Entity> ents, List<Entity> newEnts, boolean checkCollisions) {
        Iterator<Entity> e = ents.iterator();
        while (e.hasNext()) {
            Entity entity = e.next();

            if (!entity.update(container, deltaMS) || entity.isRemoved()) {
                entity.setRemoved();
                e.remove();
            } else if (checkCollisions) {
                entity.checkCollisions(entities);
            }
        }
        ents.addAll(newEnts);
        newEnts.clear();
    }

    public void render(GameContainer container, Graphics g) {

        g.setColor(Color.black);
        g.drawRect(0, 0, container.getWidth(), container.getHeight());

        starfield.render(container, g, camera);

        ArrayList<Entity> renderableEntities = new ArrayList<Entity>();
        renderableEntities.addAll(entities);
        renderableEntities.addAll(particles);
        Collections.sort(renderableEntities, entitySorter);


        // render entities that are "under" the pizza
        Iterator<Entity> iterator = renderableEntities.iterator();
        while (iterator.hasNext()) {
            Entity r = iterator.next();
            if (r.getY() < 0 && r.getZ() < pizzaWorldCutZ) {
                r.render(container, g, camera);
                iterator.remove();
            }
        }

        g.drawImage(pizzaImage, -camera.getX(), -camera.getY(), -camera.getX() + pizzaImage.getWidth(), -camera.getY() + pizzaImage.getHeight(), 0, 0, pizzaImage.getWidth(), pizzaImage.getHeight());
        for (PizzaDoodad doodad : doodads) {
            doodad.render(container, g, camera);
        }

        for (Entity r : renderableEntities) {
            r.renderGroundLayer(container, g, camera);
        }
        for (Entity r : renderableEntities) {
            r.render(container, g, camera);
        }
        if (upgradeMenu.isVisible()) {
            upgradeMenu.render(container, g);
        } else {
            gameDirector.renderGUI(container, g);
            Gui.renderResourceList(g, player.getResources(), 10, container.getHeight() - 25, "Wealth: ", container.getWidth());
        }
    }

    public long getGameTime() {
        return gameTime;
    }

    public void addEntity(Entity e) {
        newEntities.add(e);
    }

    public void addParticle(Entity e) {
        newParticles.add(e);
    }

    public Camera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public Artichoke getArtichoke() {
        return artichoke;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Entity> getEntitiesInRange(float x, float z, float range) {
        float rangeSqr = range * range;
        ArrayList<Entity> results = new ArrayList<Entity>();

        for (Entity e : entities) {
            if (!e.isRemoved()) {
                float dx = e.getX() - x;
                float dz = e.getZ() - z;
                float distSqr = dx * dx + dz * dz;
                if (distSqr < rangeSqr) {
                    results.add(e);
                }
            }
        }
        return results;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void createExplosion(int damage, float distance, Entity bullet, Entity excludeEntity) {
        for (Entity e : entities) {
            if (!e.isRemoved() && e != excludeEntity && e.getType() == Entity.EntityType.EnemyEntity) {
                float distanceBetween = (float) Math.sqrt(e.distanceToSqr(bullet));
                if (distanceBetween < distance) {
                    float explosionDamage = (1.0f - (distanceBetween / distance)) * damage;
                    Alien alien = (Alien) e;
                    alien.hurt(explosionDamage);
                }
            }
        }
    }

    public Alien getClosestEnemy(Entity sourceEntity, float searchDistance) {
        Alien closestAlien = null;
        float closestAlienDistance = 30000.0f;
        for (Entity e : entities) {
            if (!e.isRemoved() && e.getType() == Entity.EntityType.EnemyEntity) {
                float distanceBetween = (float) Math.sqrt(e.distanceToSqr(sourceEntity));
                if (distanceBetween < searchDistance && distanceBetween < closestAlienDistance) {
                    closestAlienDistance = distanceBetween;
                    closestAlien = (Alien) e;
                }
            }
        }
        return closestAlien;
    }

    private class EntityZedSorter implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            if (a.getY() < 0 && b.getY() >= 0 && a.getZ() < pizzaWorldCutZ) {
                return -1;
            } else if (a.getY() >= 0 && b.getY() < 0 && b.getZ() < pizzaWorldCutZ) {
                return 1;
            }
            if (a.getZ() < b.getZ()) {
                return -1;
            }
            if (a.getZ() > b.getZ()) {
                return 1;
            }
            return 0;
        }
    }

    private final EntityZedSorter entitySorter = new EntityZedSorter();

    public boolean isOnPizza(float x, float z) {

        float cx = pizzaImage.getWidth() / 2 + 10;
        float cz = pizzaWorldCenterZ;
        float pizzaRadiusX = 1560 / 2;
        float pizzaRadiusZ = 840 / 2;

        float dx = (x - cx);
        float dz = (z - cz) * pizzaRadiusX / pizzaRadiusZ;

        return ((dx * dx) + (dz * dz)) <= pizzaRadiusX * pizzaRadiusX;
    }

    public Vector2f pizzaPositionFromRad(float rad, float distanceFromCenterPercentage) {
        float pizzaRadiusX = 1560 / 2;
        float pizzaRadiusZ = 840 / 2;
        float x = (float) Math.sin(rad) * distanceFromCenterPercentage;
        float z = (float) Math.cos(rad) * distanceFromCenterPercentage;
        return new Vector2f(x * pizzaRadiusX + pizzaImage.getWidth() / 2 + 10, z * pizzaRadiusZ + pizzaWorldCenterZ);
    }

    public Vector2f getDoodadSafePizzaPosition(Random random) {
        float angle, dist, x, z;
        boolean ok;
        float cx = pizzaImage.getWidth() / 2;
        float cy = pizzaImage.getHeight() / 2;
        float radius = pizzaImage.getWidth() * .40f;
        int tries = 50;

        do {
            angle = random.nextFloat() * (float) Math.PI * 2.0f;
            dist = (.3f + random.nextFloat() * .7f) * radius * .95f;
            x = cx + (float) Math.cos(angle) * dist;
            z = cy + (float) Math.sin(angle) * dist * .5f;

            ok = true;
            for (PizzaDoodad doodad : doodads) {
                float dx = x - doodad.getX();
                float dz = z - doodad.getZ();
                if (dx * dx + dz * dz < 50 * 50) {
                    ok = false;
                    break;
                }
            }
            tries--;
        } while (!ok && tries > 0);

        return new Vector2f(x, z);
    }

    public int getNumberOfEnimies() {
        int numOfEnemies = 0;
        Iterator<Entity> e = entities.iterator();
        while (e.hasNext()) {
            Entity entity = e.next();
            if (entity.getType() == Entity.EntityType.EnemyEntity) {
                numOfEnemies++;
            }
        }
        return numOfEnemies;
    }

    public boolean isVictory() {
        return isVictory;
    }
}

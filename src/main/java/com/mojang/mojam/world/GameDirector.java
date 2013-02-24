/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.world;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.entity.*;
import com.mojang.mojam.gui.Announcer;

/**
 * 
 * @author Johan
 */
public class GameDirector {
    private PizzaWorld gameWorld;
    Random random = new Random();
    final long levelPause = 10000;
    List<EnemyWave> enemyWaves = new ArrayList<EnemyWave>();
    int currentEnemyWave = 0;
    private int timeUntilNextRandomPickup;
    private Announcer announcer;
    private Announcer warningAnnouncer;
    boolean announcedLevel = false;
    long levelClearedTime = 0;

    public GameDirector(PizzaWorld gameWorld) {
        this.gameWorld = gameWorld;
        // name, gameWorld, waveDuration, minGroupSize, maxGroupSize,
// numSubWaves, minSubWaveWait, maxSubWaveWait, types
        // "LE." gw 80 2 4 4 6 8 {0, 1}
        /*
         * enemyWaves.add(new EnemyWave("Level 1", gameWorld, 40, 2, 4, 4, 6, 8,
         * new int[] { 0 }, 0)); enemyWaves.add(new EnemyWave("Level 2",
         * gameWorld, 40, 4, 7, 3, 8, 12, new int[] { 1 }, 0));
         * enemyWaves.add(new EnemyWave("Level 3", gameWorld, 80, 6, 8, 6, 8,
         * 16, new int[] { 0, 1 }, 0)); enemyWaves.add(new EnemyWave("Level 4",
         * gameWorld, 80, 8, 12, 12, 15, 16, new int[] { 2 }, 0));
         * enemyWaves.add(new EnemyWave("Level 5", gameWorld, 80, 10, 14, 6, 8,
         * 16, new int[] { 0, 1, 2 }, 1)); enemyWaves.add(new
         * EnemyWave("Level 6", gameWorld, 80, 3, 5, 4, 2, 6, new int[] { 3 },
         * 1)); enemyWaves.add(new EnemyWave("Level 7", gameWorld, 80, 8, 17, 8,
         * 5, 9, new int[] { 0, 1, 2, 3 }, 2)); enemyWaves.add(new
         * EnemyWave("Level 8", gameWorld, 80, 12, 17, 9, 5, 9, new int[] { 0,
         * 1, 2, 3 }, 2)); enemyWaves.add(new EnemyWave("Level 9", gameWorld,
         * 80, 14, 17, 10, 5, 9, new int[] { 0, 1, 2, 3 }, 3));
         */
        createEnemyWave("Level 1").addGroup(new EnemyGroup(0).addEnemy(1, 0, 0)).addGroup(new EnemyGroup(5).addEnemy(5, 0, 0));

        createEnemyWave("Level 2").addGroup(new EnemyGroup(0).addEnemy(5, 0, 0).addEnemy(5, 0, 0)).addGroup(new EnemyGroup(15).addEnemy(5, 0, 0).addEnemy(5, 0, 0));

        createEnemyWave("Level 3").addGroup(new EnemyGroup(0).addEnemy(10, 0, 0)).addGroup(new EnemyGroup(15).addEnemy(8, 0, 0).addEnemy(8, 0, 0));

        createEnemyWave("Level 4").addGroup(new EnemyGroup(0).addEnemy(5, 1, 0));

        createEnemyWave("Level 5").addGroup(new EnemyGroup(0).addEnemy(10, 0, 0)).addGroup(new EnemyGroup(10).addEnemy(5, 1, 0).addEnemy(5, 1, 0));

        createEnemyWave("Level 6").addGroup(new EnemyGroup(0).addEnemy(8, 2, 0));

        createEnemyWave("Level 7").addGroup(new EnemyGroup(0).addEnemy(8, 2, 0)).addGroup(new EnemyGroup(10).addEnemy(5, 1, 0).addEnemy(8, 2, 0)).addGroup(new EnemyGroup(10).addEnemy(8, 2, 0));

        createEnemyWave("Level 8 - LARGE INVASION").addGroup(new EnemyGroup(0).addEnemy(10, 0, 0).addEnemy(10, 0, 0)).addGroup(new EnemyGroup(10).addEnemy(8, 2, 0).addEnemy(8, 2, 0))
                .addGroup(new EnemyGroup(5).addEnemy(8, 1, 0));

        createEnemyWave("Level 9").addGroup(new EnemyGroup(0).addEnemy(20, 0, 0)).addGroup(new EnemyGroup(10).addEnemy(5, 2, 1).addEnemy(5, 2, 1))
                .addGroup(new EnemyGroup(10).addEnemy(5, 1, 1).addEnemy(5, 0, 1)).addGroup(new EnemyGroup(10).addEnemy(3, 2, 1).addEnemy(7, 2, 1)).addGroup(new EnemyGroup(10).addEnemy(20, 0, 1));

        createEnemyWave("Level 10 - BEWARE").addGroup(new EnemyGroup(0).addEnemy(1, 3, 0)).addGroup(new EnemyGroup(10).addEnemy(1, 3, 0).addEnemy(1, 3, 0))
                .addGroup(new EnemyGroup(10).addEnemy(1, 3, 0).addEnemy(1, 3, 0).addEnemy(1, 3, 0));

        createEnemyWave("Level 11").addGroup(new EnemyGroup(0).addEnemy(20, 1, 1));

        createEnemyWave("Level 12").addGroup(new EnemyGroup(0).addEnemy(2, 3, 0)).addGroup(new EnemyGroup(10).addEnemy(3, 1, 0).addEnemy(3, 1, 0).addEnemy(3, 1, 0));

        createEnemyWave("Level 13").addGroup(new EnemyGroup(0).addEnemy(20, 0, 1)).addGroup(new EnemyGroup(10).addEnemy(5, 2, 2).addEnemy(5, 2, 2))
                .addGroup(new EnemyGroup(10).addEnemy(5, 1, 2).addEnemy(5, 0, 2)).addGroup(new EnemyGroup(10).addEnemy(3, 2, 2).addEnemy(7, 2, 2)).addGroup(new EnemyGroup(10).addEnemy(20, 0, 1));

        createEnemyWave("Level 14").addGroup(new EnemyGroup(0).addEnemy(12, 2, 1).addEnemy(12, 2, 1));

        createEnemyWave("Level 15 - PATIENCE").addGroup(new EnemyGroup(0).addEnemy(10, 0, 2)).addGroup(new EnemyGroup(25).addEnemy(2, 3, 1).addEnemy(2, 3, 1).addEnemy(2, 3, 1));

        createEnemyWave("Level 16").addGroup(new EnemyGroup(0).addEnemy(10, 1, 2).addEnemy(10, 2, 2)).addGroup(new EnemyGroup(20).addEnemy(15, 2, 2).addEnemy(10, 1, 2));

        createEnemyWave("Level 17").addGroup(new EnemyGroup(0).addEnemy(1, 3, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 0, 2).addEnemy(10, 0, 2)).addGroup(new EnemyGroup(20).addEnemy(5, 1, 3));

        createEnemyWave("Level 18 - THE ELITE").addGroup(new EnemyGroup(0).addEnemy(1, 3, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 0, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 1, 3))
                .addGroup(new EnemyGroup(3).addEnemy(1, 2, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 1, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 1, 3))
                .addGroup(new EnemyGroup(3).addEnemy(1, 1, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 3, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 0, 3))
                .addGroup(new EnemyGroup(3).addEnemy(1, 0, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 2, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 2, 3))
                .addGroup(new EnemyGroup(3).addEnemy(1, 2, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 1, 3)).addGroup(new EnemyGroup(3).addEnemy(1, 1, 3))
                .addGroup(new EnemyGroup(3).addEnemy(1, 3, 3)).addGroup(new EnemyGroup(10).addEnemy(20, 1, 3));

        createEnemyWave("Level 19").addGroup(new EnemyGroup(0).addEnemy(4, 0, 2).addEnemy(4, 1, 2).addEnemy(4, 2, 2).addEnemy(4, 3, 1));

        createEnemyWave("Level 20 - NUKES").addGroup(new EnemyGroup(0).addEnemy(30, 1, 2));

        createEnemyWave("Level 21").addGroup(new EnemyGroup(0).addEnemy(8, 2, 2)).addGroup(new EnemyGroup(10).addEnemy(8, 2, 2)).addGroup(new EnemyGroup(10).addEnemy(8, 2, 2))
                .addGroup(new EnemyGroup(15).addEnemy(3, 3, 3));

        createEnemyWave("Level 22 - THE HORDE").addGroup(new EnemyGroup(0).addEnemy(30, 0, 2)).addGroup(new EnemyGroup(30).addEnemy(10, 1, 3));

        createEnemyWave("Level 23").addGroup(new EnemyGroup(0).addEnemy(15, 1, 2)).addGroup(new EnemyGroup(10).addEnemy(20, 0, 2)).addGroup(new EnemyGroup(10).addEnemy(20, 2, 2))
                .addGroup(new EnemyGroup(10).addEnemy(5, 2, 3).addEnemy(5, 2, 3).addEnemy(5, 2, 3).addEnemy(5, 2, 3));

        createEnemyWave("Level 24 - DEATH").addGroup(new EnemyGroup(0).addEnemy(2, 3, 2).addEnemy(2, 3, 2).addEnemy(2, 3, 2).addEnemy(2, 3, 2)).addGroup(new EnemyGroup(20).addEnemy(30, 1, 3));

        createEnemyWave("Level 25 - AFTERLIFE").addGroup(new EnemyGroup(0).addEnemy(10, 0, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 1, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 2, 3))
                .addGroup(new EnemyGroup(10).addEnemy(10, 1, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 2, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 2, 3))
                .addGroup(new EnemyGroup(10).addEnemy(10, 2, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 1, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 1, 3))
                .addGroup(new EnemyGroup(10).addEnemy(20, 0, 3)).addGroup(new EnemyGroup(10).addEnemy(10, 3, 3));
        int tmpCurLevel = 1;
        for (EnemyWave wave : enemyWaves) {
            wave.setCurLevel(tmpCurLevel);
            tmpCurLevel++;
        }
    }

    public void init(GameContainer container) {
        announcer = new Announcer(container, container.getHeight() / 2 + 140, Color.white);
        announcer.postMessage(container, "Prepare for Battle!");

        warningAnnouncer = new Announcer(container, container.getHeight() / 2 - 180, Color.red);
    }

    public void update(GameContainer container, int deltaMS) {
        EnemyWave currentWave = enemyWaves.get(currentEnemyWave);
        if (currentWave.isWaveDone() && announcedLevel && gameWorld.getGameTime() > levelClearedTime + levelPause) {
            if (currentEnemyWave + 1 < enemyWaves.size()) {
                currentEnemyWave++;
                currentWave = enemyWaves.get(currentEnemyWave);
                announcer.postMessage(container, currentWave.getName());
                announcedLevel = false;

                for (int i = 0; i < 1 + (currentEnemyWave / 5); i++) {
                    gameWorld.addParticle(new FlyingSlice(gameWorld));
                }
            }
        }
        if (!announcedLevel && currentWave.isWaveDone() && gameWorld.getNumberOfEnimies() == 0) {
            levelClearedTime = gameWorld.getGameTime();
            announcedLevel = true;
            announcer.postMessage(container, "Level Cleared!");
        }
        enemyWaves.get(currentEnemyWave).update(container, deltaMS);

        timeUntilNextRandomPickup -= deltaMS;
        if (timeUntilNextRandomPickup < 0) {
            Vector2f pos = gameWorld.getDoodadSafePizzaPosition(random);
            gameWorld.addEntity(new PizzaBubble(gameWorld, pos.x, pos.y));

            timeUntilNextRandomPickup = 4000 + random.nextInt(11) * 1000;
        }

        // check warnings
        Player player = gameWorld.getPlayer();
        if (player.getHealth() < player.getMaxHealth() * .25f) {
            warningAnnouncer.updateMessage(container, "Warning: Low Health!");
        }

        announcer.update(container, deltaMS);
        warningAnnouncer.update(container, deltaMS);
    }

    public void renderGUI(GameContainer container, Graphics g) {
        String message = "";
        if (gameWorld.getNumberOfEnimies() == 0 && enemyWaves.get(currentEnemyWave).isWaveDone()) {
            if (currentEnemyWave + 1 < enemyWaves.size()) {
                long secondsUntilNextLevel = ((levelClearedTime + levelPause) - gameWorld.getGameTime()) / 1000;
                message = String.format("%s starts in %d seconds", enemyWaves.get(currentEnemyWave + 1).getName(), secondsUntilNextLevel);
            } else {
                message = "You won, I guess...";
            }
        } else {
            // message = enemyWaves.get(currentEnemyWave).getName();
        }
        int width = g.getFont().getWidth(message);
        int height = g.getFont().getHeight(message);
        g.setColor(Color.white);
        g.drawString(message, container.getWidth() / 2 - width / 2, container.getHeight() / 6 - height / 2);
        announcer.render(container, g);
        warningAnnouncer.render(container, g);
    }

    private EnemyWave createEnemyWave(String name) {
        EnemyWave wave = new EnemyWave(name, gameWorld);
        enemyWaves.add(wave);
        return wave;
    }

    public boolean isWon() {
        return currentEnemyWave >= (enemyWaves.size() - 1) && gameWorld.getNumberOfEnimies() == 0 && enemyWaves.get(currentEnemyWave).isWaveDone();
    }
}

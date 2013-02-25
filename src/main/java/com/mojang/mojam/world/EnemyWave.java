package com.mojang.mojam.world;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.entity.Alien;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Johan
 */
public class EnemyWave {
    private Random random = new Random();
    private PizzaWorld gameWorld;
    long nextSpawnTime = 0;
    long waveStartTime = 0;
    int currentWave = -1;
    int curlevel = 1;
    String name;
    List<EnemyGroup> subGroups = new ArrayList<EnemyGroup>();
    
    public EnemyWave(String name, PizzaWorld gameWorld) {
        this.name = name;
        this.gameWorld = gameWorld;
    }

    public void update(GameContainer container, int deltaMS) {
        if (waveStartTime == 0) {
            waveStartTime = gameWorld.getGameTime();
        }
        if (gameWorld.getGameTime() > nextSpawnTime) {
            currentWave++;
            if (currentWave < subGroups.size()) {
                if(currentWave + 1 < subGroups.size()) {
                    nextSpawnTime = gameWorld.getGameTime() + subGroups.get(currentWave + 1).timoutSinceLastWave;
                }
                EnemyGroup currentGroup = subGroups.get(currentWave);
                for(int a = 0; a < curlevel / 5 + 1; ++a) {
                    spawnEnemies(currentGroup);
                }
            }
        }
    }
    
    private void spawnEnemies(EnemyGroup currentGroup) {
        float randomRad = (random.nextFloat() * 3.14f * 2);
        Vector2f groupCenterPosition = gameWorld.pizzaPositionFromRad(randomRad, 0.85f);
        for(int[] spawnData :  currentGroup.enemyTypeInfo) {
            for(int a = 0; a < spawnData[0]; ++a) {
                gameWorld.addEntity(new Alien(gameWorld, groupCenterPosition.x, groupCenterPosition.y, spawnData[1], spawnData[2]));
            }
        }
    }

    public boolean isWaveDone() {
        return currentWave >= subGroups.size();
    }

    public String getName() {
        return name;
    }
    
    public EnemyWave addGroup(EnemyGroup group) {
        subGroups.add(group);
        return this;
    }
    
    public void setCurLevel(int curlevel) {
        this.curlevel = curlevel;
    }
}

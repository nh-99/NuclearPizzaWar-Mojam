/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.world;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johan
 */
public class EnemyGroup {
    public List<int[]> enemyTypeInfo = new ArrayList<int[]>();
    public int timoutSinceLastWave;
    public EnemyGroup(float timeOutSinceLastGroup) {
        this.timoutSinceLastWave = (int)(timeOutSinceLastGroup * 1000);
    }
    public EnemyGroup addEnemy(int count, int type, int level) {
        enemyTypeInfo.add(new int[]{count, type, level});
        return this;
    }
}

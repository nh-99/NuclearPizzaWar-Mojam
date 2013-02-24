package com.mojang.mojam.world;

import org.newdawn.slick.*;

public class PizzaResource {

    public static final int TYPE_FETA = 0;
    public static final int TYPE_BASIL = 1;
    public static final int TYPE_PEPPERONI = 2;

    public static final int NUM_RESOURCES = 3;

    public static final String[] iconNames = {
            "res/pickups/coin.png", "res/pickups/health.png", "res/pickups/beam.png",
    };
    public static final Animation[] icons = new Animation[NUM_RESOURCES];

    public static void init() throws SlickException {
        for (int i = 0; i < NUM_RESOURCES; i++) {
            icons[i] = new Animation(new SpriteSheet(iconNames[i], 32, 32), 200);
        }
    }
}

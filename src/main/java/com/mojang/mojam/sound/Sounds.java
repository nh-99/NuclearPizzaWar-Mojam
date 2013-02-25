package com.mojang.mojam.sound;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.*;

public class Sounds {

    private static final String FOLDER = "res/sound/";

    public static final String ALIEN_DEATH = "ai_death.wav";
    public static final String ALIEN_TALK = "ai_voice.wav";
    public static final String ALIEN_SHOOT = "alienshot.wav";
    public static final String ALIEN_SUICIDE = "alien_suicide.wav";
    public static final String ALIEN_WARNING = "alien_warning.wav";
    public static final String JETPACK_FAIL = "jetpack_fail.wav";
    public static final String BIG_EXP = "big_explosion.wav";
    public static final String PLAYER_ACC = "player_accelerating.wav";
    public static final String PLAYER_DEATH = "player_death.wav";
    public static final String PLAYER_HIT = "player_gethit.wav";
    public static final String PLAYER_JETPACK = "player_jetpack.wav";
    public static final String PLAYER_LAND = "cheesy_land2.wav";
    public static final String POWERUP1 = "powerup_1.wav";
    public static final String POWERUP2 = "powerup_2.wav";
    public static final String SHOT = "shot.wav";
    public static final String SHOT2 = "shot2.wav";
    public static final String SHOT_LAND = "shot_land.wav";
    public static final String SLICE_APPROACHING = "slice_approaching.wav";
    public static final String SLICE_DOCKING = "slice_docking.wav";
    public static final String START_LEVEL = "start_level.wav";
    public static final String BEAM = "beam.wav";
    public static final String PICKUP_COIN = "coin.wav";
    public static final String PICKUP_HEALTH = "health.wav";
    public static final String BASE_TAKES_DAMAGE = "basedamage.wav";
    public static final String BASE_SHOT = "baseshot.wav";
    public static final String PIZZA_PLOP = "pizzaplop.wav";
    public static final String UPGRADE = "upgrade.wav";
    public static final String SPIDER_TALK = "spider_talk.wav";

    private static Sounds instance;

    private HashMap<String, Sound> sounds = new HashMap<String, Sound>();
    private Vector3f listenerPosition = new Vector3f();

    public static Sounds getInstance() {
        if (instance == null) {
            instance = new Sounds();
        }
        return instance;
    }

    public void setListenerPosition(float x, float y, float z) {
        listenerPosition.x = x;
        listenerPosition.y = y;
        listenerPosition.z = z;
    }

    public void playSound(String name, float x, float y, float z) {
        playSound(name, x, y, z, 1.0f, 1.0f);
    }

    public void playSound(String name, float x, float y, float z, float pitch, float volume) {

        Sound sound = sounds.get(name);
        if (sound == null) {
            try {
                sound = new Sound(FOLDER + name);
            } catch (SlickException e) {
                System.err.println("Error, unable to load sound \"" + name + "\"");
                return;
            }
            sounds.put(name, sound);
        }
        sound.play(pitch, volume);
    }

}

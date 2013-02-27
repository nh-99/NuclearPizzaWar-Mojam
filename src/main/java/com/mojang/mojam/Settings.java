package com.mojang.mojam;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

// TODO: input settings (movement, shockwave, shoot)

/**
 * 
 * @author luto
 */
public class Settings {
	private static int width;
	private static int height;
	private static boolean fullscreen;
    private static float volume = 1.0f;

    public static void load(String file) throws Exception {
    	width = 800;
    	height = 600;
    	fullscreen = false;

    	BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split("=");
			String key = parts[0];
			String value = parts[1];

			if(key.equals("width"))
				width = Integer.parseInt(value);
			else if(key.equals("height"))
				height = Integer.parseInt(value);
			else if(key.equals("fullscreen"))
				fullscreen = Boolean.parseBoolean(value);
            else if(key.equals("volume")) {
                float volumeValue = Float.parseFloat(value);
                if (volumeValue >= 0 && volumeValue <= 10) {
                    volume = volumeValue / 10f;
                }
            }
			else
				System.out.println("Unkown key: " + key);
		}
		br.close();
    }

    public static int getWidth() {
    	return width;
    }

    public static int getHeight() {
    	return height;
    }

    public static boolean getFullscreen() {
    	return fullscreen;
    }

    public static float getVolume() {
        return volume;
    }
}

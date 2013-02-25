package com.mojang.mojam;

import org.newdawn.slick.AppGameContainer;

public class MainClass {

    public static void main(String[] args) throws Exception {
        System.out.println("I'm a little teapot. That is all.");

        try {
        	Settings.load("gamesettings.ini");
        }
        catch(Throwable tr) {
        	System.out.println("Invalid settings-file.");
        	return;
        }

        AppGameContainer app = new AppGameContainer(new GameStateController());

        app.setDisplayMode(Settings.getWidth(), Settings.getHeight(), Settings.getFullscreen());
        app.start();
    }
}

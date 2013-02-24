package com.mojang.mojam;

import org.newdawn.slick.AppGameContainer;

public class MainClass {

    public static void main(String[] args) throws Exception {
        System.out.println("I'm a little teapot. That is all.");

        AppGameContainer app = new AppGameContainer(new GameStateController());

        app.setDisplayMode(800, 600, false);
        app.start();
    }
}

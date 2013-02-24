/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.gui;

import com.mojang.mojam.util.ColorTools;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;

/**
 * 
 * @author Johan
 */
public class Announcer {
    private String message = "";
    private long startTime = 0;
    private final float fadeInDuration = 1.0f;
    private final float fadeOutDuration = 0.5f;
    private final long duration = 4000;
    private Color textColor = new Color(Color.white);
    UnicodeFont announcementFont;
    private final int yPosition;
    private final Color baseColor;

    public Announcer(GameContainer container, int yPosition, Color baseColor) {
        this.yPosition = yPosition;
        this.baseColor = baseColor;
        try {
            announcementFont = new UnicodeFont("res/fonts/Franchise-Bold.ttf", 72, false, false);
            announcementFont.addAsciiGlyphs();
            announcementFont.getEffects().add(new ColorEffect());
            announcementFont.loadGlyphs();
        } catch (Exception e) {
        }
    }

    public void updateMessage(GameContainer container, String message) {
        if (this.message.equals(message)) {
            long currentTime = container.getTime();
            if (currentTime > startTime + duration + 1000) {
                postMessage(container, message);
            }
        } else {
            postMessage(container, message);
        }
    }

    public void postMessage(GameContainer container, String message) {
        this.message = message;
        this.startTime = container.getTime();
        textColor = new Color(baseColor.r, baseColor.g, baseColor.b, 0.0f);
    }

    public void update(GameContainer container, int deltaMS) {
        float dt = (float) deltaMS / 1000;
        updateTextColor(container, dt);
    }

    public void render(GameContainer container, Graphics g) {
        long currentTime = container.getTime();
        if (currentTime > startTime && currentTime < startTime + duration) {
            int width = announcementFont.getWidth(message);
            int height = announcementFont.getHeight(message);
            g.setColor(textColor);
            g.setFont(announcementFont);
            int x = container.getWidth() / 2 - width / 2;
            int y = yPosition;
            g.drawString(message, x, y);
            g.resetFont();
        }
    }

    private void updateTextColor(GameContainer container, float dt) {
        // Check if fade in
        if (container.getTime() < startTime + (long) (fadeInDuration * 1000)) {
            ColorTools.visualSeekAlpha(textColor, 1.0f, fadeInDuration * 0.1f);
        } else if (container.getTime() > startTime + duration - (long) (fadeOutDuration * 1000)) {
            ColorTools.visualSeekAlpha(textColor, 0.0f, fadeOutDuration * 0.2f);
        } else {
            textColor = new Color(baseColor);
        }
    }
}

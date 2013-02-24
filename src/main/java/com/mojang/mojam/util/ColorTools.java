/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.util;

import org.newdawn.slick.Color;

/**
 *
 * @author Johan
 */
public class ColorTools {
    public static void visualSeekAlpha(Color currentColor, float targetAlpha, float animationSpeed) {
        // This is Måns favorit easing algoritm, this makes it awesome!s
        currentColor.a += (targetAlpha - currentColor.a) * animationSpeed;
    }
}

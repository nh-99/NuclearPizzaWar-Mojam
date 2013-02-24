package com.mojang.mojam.data;

import org.newdawn.slick.*;

public class OffsetImage extends Image {

    private int offsetX;
    private int offsetY;

    public OffsetImage(String ref) throws SlickException {
        super(ref);
    }

    public OffsetImage(Image src) throws SlickException {
        super(src);
    }

    public OffsetImage(String ref, int ox, int oy) throws SlickException {
        super(ref);
        this.offsetX = ox;
        this.offsetY = oy;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

}

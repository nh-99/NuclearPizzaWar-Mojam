package com.mojang.mojam.entity;

import org.newdawn.slick.*;

import com.mojang.mojam.world.PizzaWorld;

public class AnimationParticle extends Particle {

    public static AnimInfo engineAnimation = new AnimInfo();
    public static AnimInfo bulletImpactAnimation = new AnimInfo();
    public static AnimInfo explodeAnimation = new AnimInfo();
    public static AnimInfo explodeAnimation2 = new AnimInfo();
    public static AnimInfo explodeAnimation3 = new AnimInfo();
    public static AnimInfo pizzaPop = new AnimInfo();

    private final Animation animation;

    public AnimationParticle(PizzaWorld world, float x, float y, float z, AnimInfo animInfo) {
        super(world, x, z);
        this.y = y;
        this.animation = animInfo.animation.copy();
        this.xOffset = animInfo.xo;
        this.yOffset = animInfo.yo;
    }

    @Override
    public boolean update(GameContainer slickContainer, int deltaMS) {

        int prevFrame = animation.getFrame();
        animation.update(deltaMS);
        if (prevFrame > animation.getFrame()) {
            // the animation has looped, so remove
            return false;
        }
        activeImage = animation.getCurrentFrame();

        return super.update(slickContainer, deltaMS);
    }

    public static void initAnimations() throws SlickException {
        engineAnimation.setAnimation(new Animation(new SpriteSheet("res/particles/engine.png", 16, 16), 100));
        bulletImpactAnimation.setAnimation(new Animation(new SpriteSheet("res/particles/bullet_impact.png", 16, 16), 100));
        explodeAnimation.setAnimation(new Animation(new SpriteSheet("res/actors/explosion1.png", 64, 64), 100));
        explodeAnimation.yo = 50;
        explodeAnimation2.setAnimation(new Animation(new SpriteSheet("res/actors/explosion2.png", 64, 64), 50));
        explodeAnimation2.yo = 50;
        explodeAnimation3.setAnimation(new Animation(new SpriteSheet("res/actors/explosion3.png", 64, 192), 50));
        explodeAnimation3.yo = 178;
        pizzaPop.setAnimation(new Animation(new SpriteSheet("res/actors/bubble_pop.png", 64, 64), 70));
        pizzaPop.yo = 45;
    }

    public static class AnimInfo {
        public Animation animation;
        public float xo, yo;

        private void setAnimation(Animation animation) {
            this.animation = animation;
            this.xo = animation.getWidth() / 2;
            this.yo = animation.getHeight() / 2;
        }
    }
}

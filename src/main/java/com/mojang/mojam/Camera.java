package com.mojang.mojam;


public class Camera {

    private float x, y;
    private float targetX, targetY;
    private float minX, minY, maxX, maxY;
    private float screenShakeAmplitude;
    private float screenShakeAnim;

    public Camera() {

    }

    private float clamp(float a, float min, float max) {
        if (a < min) {
            return min;
        }
        if (a > max) {
            return max;
        }
        return a;
    }

    public void update(int deltaMS) {

        x += (targetX - x) * (float) deltaMS * .01f;
        y += (targetY - y) * (float) deltaMS * .01f;

        x = clamp(x, minX, maxX);
        y = clamp(y, minY, maxY);

        screenShakeAnim += deltaMS * .001f;
        screenShakeAmplitude += -screenShakeAmplitude * deltaMS * .001f * 2.5f;
    }

    public float getX() {
        return (float) (Math.floor(x) + Math.cos(screenShakeAnim * Math.PI * 30) * screenShakeAmplitude);
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return (float) (Math.floor(y) + Math.sin(screenShakeAnim * Math.PI * 13) * screenShakeAmplitude * .7);
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getViewX(float x) {
        return x + this.x;
    }

    public float getViewY(float y) {
        return y + this.y;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
        targetX = clamp(targetX, minX, maxX);
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
        targetY = clamp(targetY, minY, maxY);
    }

    public void setConstraints(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void centerOnConstraints() {
        this.x = this.targetX = minX + (maxX - minX) * .5f;
        this.y = this.targetY = minY + (maxY - minY) * .5f;
    }

    public void addScreenShake(float amount) {
        screenShakeAmplitude += amount;
    }
}

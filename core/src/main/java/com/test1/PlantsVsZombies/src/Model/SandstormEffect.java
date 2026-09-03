package com.test1.PlantsVsZombies.src.Model;

public class SandstormEffect {
    private final float x;
    private final float y;
    private float animTime = 0f;
    private final float duration = 2.0f;

    public SandstormEffect(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        animTime += delta;
    }

    public boolean isFinished() {
        return animTime >= duration;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAnimTime() {
        return animTime;
    }
}

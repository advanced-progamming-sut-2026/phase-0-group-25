package com.test1.PlantsVsZombies.src.Model;

public class IcyWindEffect {
    private final int row;
    private float animTime = 0f;
    private final float duration = 2.5f;

    public IcyWindEffect(int row) {
        this.row = row;
    }

    public void update(float delta) {
        animTime += delta;
    }

    public boolean isFinished() {
        return animTime >= duration;
    }

    public int getRow() {
        return row;
    }

    public float getAnimTime() {
        return animTime;
    }
}

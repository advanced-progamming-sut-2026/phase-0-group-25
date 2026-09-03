package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

public class AnimationState {

    private String currentClip;
    private float stateTime;

    public AnimationState() {
        this.currentClip = null;
        this.stateTime = 0f;
    }


    public void update(String newClip, float delta) {

        if (newClip == null) {
            return;
        }


        if (!newClip.equals(currentClip)) {
            currentClip = newClip;
            stateTime = 0f;
        }

        else {
            stateTime += delta;
        }
    }

    public String getCurrentClip() {
        return currentClip;
    }

    public float getStateTime() {
        return stateTime;
    }


    public void restart() {
        stateTime = 0f;
    }
}

package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

public class AnimationState {

    private String currentClip;
    private float stateTime;

    public AnimationState() {
        this.currentClip = null;
        this.stateTime = 0f;
    }

    /**
     * Updates the animation state.
     * <p>
     * If the clip has changed:
     * restart it from frame 0.
     * <p>
     * If the clip is the same:
     * continue from its previous frame.
     */
    public void update(String newClip, float delta) {

        if (newClip == null) {
            return;
        }

        // Animation changed -> restart
        if (!newClip.equals(currentClip)) {
            currentClip = newClip;
            stateTime = 0f;
        }
        // Same animation -> continue
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

    /**
     * Useful when you explicitly want to restart
     * the currently playing animation.
     */
    public void restart() {
        stateTime = 0f;
    }
}

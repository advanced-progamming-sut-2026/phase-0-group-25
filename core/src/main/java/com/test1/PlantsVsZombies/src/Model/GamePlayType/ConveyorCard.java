package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;

public class ConveyorCard {
    private final BattlePlant plant;
    private final float speed = 250f;
    private float currentY;
    private float targetY;

    public ConveyorCard(BattlePlant plant, float startY) {
        this.plant = plant;
        this.currentY = startY;
        this.targetY = startY;
    }

    public void update(float delta) {
        if (currentY < targetY) {
            currentY = Math.min(targetY, currentY + speed * delta);
        } else if (currentY > targetY) {
            currentY = Math.max(targetY, currentY - speed * delta);
        }
    }

    public BattlePlant getPlant() {
        return plant;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }
}

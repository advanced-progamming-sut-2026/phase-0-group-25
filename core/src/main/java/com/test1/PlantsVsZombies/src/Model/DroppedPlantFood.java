package com.test1.PlantsVsZombies.src.Model;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class DroppedPlantFood {
    private Position position;
    private boolean isCollected = false;

    public DroppedPlantFood(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}

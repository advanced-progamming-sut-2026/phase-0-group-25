package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;

public interface IZombieHudInputState {
    Vector3 getMouseWorldPos();

    BattlePlant getSelectedPlantCard();

    String getSelectedZombieCardType();

    boolean isReactionDrawerOpen();

    default int getSelectedZombieLane() {
        return -1;
    }
}

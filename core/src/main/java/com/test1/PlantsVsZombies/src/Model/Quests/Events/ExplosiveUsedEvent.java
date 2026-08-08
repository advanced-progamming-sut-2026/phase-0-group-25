package com.test1.PlantsVsZombies.src.Model.Quests.Events;

public class ExplosiveUsedEvent extends Event {
    private final String plantName;

    public ExplosiveUsedEvent(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantName() {
        return plantName;
    }
}

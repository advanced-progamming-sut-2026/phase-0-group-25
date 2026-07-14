package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;

import java.sql.Struct;

public abstract class Plant extends Entity {
    protected PlantCategory category;
    private boolean hasBoost;
    private int price;
    private int cooldown = 40;
    private boolean isPlantable = false;

    public abstract void update();

    public String getName() {
        return name;
    }

    public boolean checkingPlantable (int sun) {
        return sun >= this.price && this.cooldown == 0;
    }
}

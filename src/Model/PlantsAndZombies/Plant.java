package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;

import java.sql.Struct;

public abstract class Plant extends Entity {
    protected PlantCategory category;
    private boolean hasBoost;
    private int price;
    private int cooldown = 40;
    private Boolean activeCooldown = true;

    public abstract void update();

    public boolean checkingPlantable (int sun) {
        return (sun >= this.price) && (this.cooldown==0 || !this.activeCooldown);
    }

    public void inactivateCooldown() {
        activeCooldown = false;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}

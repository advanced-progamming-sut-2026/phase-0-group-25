package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;
import src.Model.Tile;

public abstract class Plant extends Entity {
    protected PlantCategory category;
    protected boolean hasBoost;
    protected int price;
    protected int cooldown;
    protected Boolean activeCooldown = true;

    public Plant() {
    }

    public abstract void update();

    public boolean checkingSunCooldown(int sun) {
        return (sun >= this.price) && (this.cooldown == 0 || !this.activeCooldown);
    }

    public void inactivateCooldown() {
        activeCooldown = false;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getPrice() {
        return price;
    }

    public PlantCategory getCategory() {
        return category;
    }
}

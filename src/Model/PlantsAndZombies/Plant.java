package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;
import src.Model.Tile;

import java.sql.Struct;

public abstract class Plant extends Entity {
    protected PlantCategory category;
    private boolean hasBoost;
    private int price;
    private int cooldown;
    private Boolean activeCooldown = true;

    public abstract void update();

    public boolean checkingPlantable (int sun, Tile thisTile) {
        BattlePlant upperPlant = thisTile.getPlants().get(thisTile.getPlants().size());
        boolean isStack = upperPlant.getPlantStats().getTags().contains("Stack") || thisTile.getPlants().isEmpty();
        return (sun >= this.price) && (this.cooldown==0 || !this.activeCooldown) && isStack;
    }

    public boolean checkingSunCooldown (int sun) {
        return (sun >= this.price) && (this.cooldown==0 || !this.activeCooldown);
    }

    public void inactivateCooldown() {
        activeCooldown = false;
    }

    public int getCooldown() {
        return cooldown;
    }

    public PlantCategory getCategory() {
        return category;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}

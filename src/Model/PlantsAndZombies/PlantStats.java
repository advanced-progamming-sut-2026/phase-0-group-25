package src.Model.PlantsAndZombies;

import src.Enums.Tag;
import src.Model.PlantsAndZombies.Abilities.Ability;

import java.util.*;

public class PlantStats {
    private String name;
    private String category;
    private int level;
    private int cost;
    private int baseHP;
    private double actionInterval;
    private double rechargeTime;
    private ArrayList<String> abilities;
    private ArrayList<String> tags;

    private Map<String, Object> attributes;
    private Map<String, Object> plantFoodEffect;


    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getActionInterval() {
        return actionInterval;
    }

    public int getCost() {
        return cost;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getPlantFoodEffect() {
        return plantFoodEffect;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void upgradePlant() {

    }

}

package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import java.util.ArrayList;
import java.util.Map;

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
    private String animation;
    private Map<String, String> status;

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
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

    public void setCost(int cost) {
        this.cost = cost;
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

    public double getRechargeTime() {
        return rechargeTime;
    }

    public String getAnimation() {
        return animation;
    }

    public Map<String, String> getStatus() {
        return status;
    }
}

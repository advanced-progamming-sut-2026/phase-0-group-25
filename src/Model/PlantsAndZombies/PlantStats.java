package src.Model.PlantsAndZombies;

import src.Enums.Tag;
import src.Model.PlantsAndZombies.Abilities.Ability;

import java.util.*;

public class PlantStats {
    private int level;
    private int cost;
    private int baseHP;
    private double actionInterval;
    private double rechargeTime;
    private ArrayList<String> abilities;
    private ArrayList<String> tags;

    private Map<String, Integer> attributes;


    public void setLevel(int level) {
        this.level = level;
    }

    public int getCost() {
        return this.cost;
    }

    public void upgradePlant() {

    }

}

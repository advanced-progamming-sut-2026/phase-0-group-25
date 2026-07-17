package Model.PlantsAndZombies;

import Model.PlantsAndZombies.Armors.Armor;

import java.util.*;

public class ZombieStats {
    private String name;
    private int baseHP;
    private double eatdps;
    private double velocity;
    private ArrayList<Armor> armors;
    private int waveCost;

    private Map<String, Object> attributes;



    public String getName() {
        return name;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public double getEatdps() {
        return eatdps;
    }

    public double getVelocity() {
        return velocity;
    }

    public int getWaveCost() {
        return waveCost;
    }

    public ArrayList<Armor> getArmors() {
        return armors;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

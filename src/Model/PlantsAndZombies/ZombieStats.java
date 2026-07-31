package src.Model.PlantsAndZombies;

import src.Model.PlantsAndZombies.Armors.Armor;

import java.util.ArrayList;
import java.util.Map;

public class ZombieStats {
    private String name;
    private int baseHP;
    private double eatdps;
    private double velocity;
    private ArrayList<Armor> armors;
    private int waveCost;

    private Map<String, Object> attributes;
    private ArrayList<String> abilities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public double getEatdps() {
        return eatdps;
    }

    public void setEatdps(double eatdps) {
        this.eatdps = eatdps;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
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

    public ArrayList<String> getAbilities() {
        return abilities;
    }
}

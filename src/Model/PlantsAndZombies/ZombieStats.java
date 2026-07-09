package src.Model.PlantsAndZombies;

import src.Model.PlantsAndZombies.Armors.Armor;

import java.util.*;

public class ZombieStats {
    private int baseHP;
    private double eatdps;
    private double velocity;
    private ArrayList<Armor> armors;
    private int waveCost;


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
}

package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import java.util.ArrayList;
import java.util.Map;

public class ZombieStats {
    private String name;
    private int baseHP;
    private String category;
    private double eatdps;
    private double velocity;
    private ArrayList<String> armor;
    private int waveCost;

    private Map<String, Object> attributes;
    private ArrayList<String> abilities;
    private String animation;
    private Map<String, String> status;

    //just for gargantuar zombie
    private double thrownTime;
    private boolean isThrown = false;
    private boolean isFinished = false;

    //just for snorkel zombie
    private boolean isSubmarine = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public String getCategory() {
        return category;
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

    public ArrayList<String> getArmor() {
        return armor;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public String getAnimation() {
        return animation;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isThrown() {
        return isThrown;
    }

    public ZombieStats setThrown(boolean thrown) {
        isThrown = thrown;
        return this;
    }

    public double getThrownTime() {
        return thrownTime;
    }

    public ZombieStats setThrownTime(double thrownTime) {
        this.thrownTime = thrownTime;
        return this;
    }

    public boolean isSubmarine() {
        return isSubmarine;
    }

    public ZombieStats setSubmarine(boolean submarine) {
        isSubmarine = submarine;
        return this;
    }
}

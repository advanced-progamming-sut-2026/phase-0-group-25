package com.test1.PlantsVsZombies.src.Model;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class Tile {
    private final ArrayList<BattlePlant> plants;
    private final ArrayList<Zombie> zombies;
    private final ArrayList<Projectile> projectiles;
    private int HP;
    private final Position position;
    private boolean isArable;
    private boolean firing;
    private boolean isHole = false;
    private GraveType graveType = GraveType.NORMAL;
    private boolean isNecromancy = false;
    private boolean necromancyTriggered = false;
    private boolean isLowTide = false;
    private boolean lowTideTriggered = false;
    public Tile(Position position, Boolean isArable, int HP) {
        this.position = position;
        this.isArable = isArable;
        this.HP = HP;
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
    }

    public boolean isLowTide() {
        return isLowTide;
    }

    public void setLowTide(boolean lowTide) {
        this.isLowTide = lowTide;
    }

    public boolean isLowTideTriggered() {
        return lowTideTriggered;
    }

    public void setLowTideTriggered(boolean lowTideTriggered) {
        this.lowTideTriggered = lowTideTriggered;
    }

    public GraveType getGraveType() {
        return graveType;
    }

    public void setGraveType(GraveType graveType) {
        this.graveType = graveType;
    }

    public boolean isNecromancy() {
        return isNecromancy;
    }

    public void setNecromancy(boolean necromancy) {
        this.isNecromancy = necromancy;
    }

    public boolean isNecromancyTriggered() {
        return necromancyTriggered;
    }

    public void setNecromancyTriggered(boolean triggered) {
        this.necromancyTriggered = triggered;
    }

    public Position getPosition() {
        return position;
    }

    public void addPlant(BattlePlant plant) {
        this.plants.add(plant);
    }

    public boolean isArable() {
        return isArable;
    }

    public void setArable(boolean arable) {
        isArable = arable;
    }

    public boolean isHole() {
        return isHole;
    }

    public void setHole(boolean hole) {
        isHole = hole;
    }

    public void removePlant() {
        for (BattlePlant plant : plants) {
            plant.setAlive(false);
        }
        plants.remove(0);
    }

    public ArrayList<BattlePlant> getPlants() {
        return plants;
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public boolean isFiring() {
        return firing;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
    }

    public enum GraveType {NORMAL, PLANT_FOOD, SUN}
}

package com.test1.PlantsVsZombies.src.Model;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class Tile {
    private ArrayList<BattlePlant> plants;
    private ArrayList<Zombie> zombies;
    private ArrayList<Projectile> projectiles;


    private int HP;
    private Position position;
    private boolean isArable;
    private boolean isHole = false;

    public Tile(Position position, Boolean isArable, int HP) {
        this.position = position;
        this.isArable = isArable;
        this.HP = HP;
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
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
}

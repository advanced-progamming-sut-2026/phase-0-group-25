package src.Model;

import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class Tile {
    private ArrayList<BattlePlant> plants;
    private ArrayList<Zombie> zombies;
    private ArrayList<Projectile> projectiles;


    private int HP;
    private Position position;
    private boolean isArable;
    private String kindOfTile;

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

    public void removePlant() {
        plants.remove(0);
    }

    public ArrayList<BattlePlant> getPlants() {
        return plants;
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public void setArable(boolean arable) {
        isArable = arable;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }
}

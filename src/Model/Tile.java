package src.Model;

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
    private Grave grave;

    private Position position;
    private boolean isArable;
    private String kindOfTile;

    public Tile(Position position, Boolean isArable) {
        this.position = position;
        this.isArable = isArable;
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
}

package src.Model.PlantsAndZombies;

import src.Model.PlantsAndZombies.Abilities.Ability;

import java.util.*;

public class Zombie extends Entity {
    private ZombieStats zombieStats;
    private double velocity;
    protected ArrayList<Ability> abilities;

    public void update() {

    }

    public Position getPosition() {
        return position;
    }

}

package src.Model.PlantsAndZombies;

import src.Model.PlantsAndZombies.Abilities.Ability;
import src.Model.PlantsAndZombies.Armors.Armor;

import java.util.*;

public class Zombie extends Entity {
    private int currentHP;
    private ZombieStats zombieStats;
    private double currentVelocity;
    protected ArrayList<Ability> abilities;
    private boolean isHalated;
    private ArrayList<Armor> activeArmors;
    //private HashMap<String, Double> effectsInfo;

    public Zombie(ZombieStats zombieStats, Position position) {
        this.zombieStats = zombieStats;
        this.position = position;
        this.currentHP = zombieStats.getBaseHP();
        this.currentVelocity = zombieStats.getVelocity();
        this.isHalated = false;

        this.activeArmors = new ArrayList<>();
        if (zombieStats.getArmors() != null) {
            for (Armor armor : zombieStats.getArmors()) {
                this.activeArmors.add(new Armor(armor.getType(),
                        armor.getCurrentHP(), armor.isMetallic()));
            }
        }
    }


    public void update() {

    }

    public void takeDamage(int damage) {
        int leftoverDamage = damage;

        for (Armor armor : activeArmors) {
            leftoverDamage = armor.takeDamage(leftoverDamage);

            if (armor.isDisarmed()) {
                activeArmors.remove(armor);
            }

            if (leftoverDamage <= 0) {
                break;
            }
        }

        if (leftoverDamage > 0) {
            this.currentHP -= leftoverDamage;
            checkLife();
        }
    }

    public void checkLife() {
        if (this.currentHP <= 0) {
            this.currentHP = 0;
            this.isAlive = false;
        }
    }

    public Position getPosition() {
        return position;
    }

}

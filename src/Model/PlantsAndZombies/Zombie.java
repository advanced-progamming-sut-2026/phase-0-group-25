package src.Model.PlantsAndZombies;

import src.Model.PlantsAndZombies.Abilities.Ability;
import src.Model.PlantsAndZombies.Abilities.StealingSun;
import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.Sun.Sun;

import java.util.*;

public class Zombie extends Entity {
    private ZombieStats zombieStats;
    private double currentVelocity;
    private ArrayList<String> abilities;
    private ArrayList<Ability> originalAbilities;
    private boolean isHalated;
    private ArrayList<Armor> activeArmors;
    private int lastActionTime;
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
            checkSteal();
        }
    }

    public void checkSteal() {
        if (this.abilities.contains("steal sun")) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof StealingSun) {
                    double stolenSun = ((StealingSun) ability).getStolenSun();

                    if (zombieStats.getName().equals("Turquoise")) {
                        Sun sun = new Sun((int) (stolenSun / 2), this.position);
                        //todo: add this sun to board;
                    } else if (zombieStats.getName().equals("Ra")) {
                        //todo: define a function which gives and sets current sun amount;
                        game.setSunAmount(game.getSunAmount() + (int) stolenSun);
                    }
                }
            }
        }
    }

    public ZombieStats getZombieStats() {
        return zombieStats;
    }

    public int getCost() {
        return this.zombieStats.getWaveCost();
    }

    public double getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(double currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public int getLastActionTime() {
        return lastActionTime;
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void setLastActionTime(int lastActionTime) {
        this.lastActionTime = lastActionTime;
    }
}

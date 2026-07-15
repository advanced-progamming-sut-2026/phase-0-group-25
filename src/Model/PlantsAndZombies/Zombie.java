package src.Model.PlantsAndZombies;

import src.Enums.Status;
import src.Model.PlantsAndZombies.Abilities.Ability;
import src.Model.PlantsAndZombies.Abilities.Eating;
import src.Model.PlantsAndZombies.Abilities.Moving;
import src.Model.PlantsAndZombies.Abilities.StealingSun;
import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Sun.Sun;

import javax.imageio.plugins.tiff.BaselineTIFFTagSet;
import java.util.*;

public class Zombie extends Entity {
    private ZombieStats zombieStats;
    private Status status;
    private Entity rival;

    private double currentVelocity;
    private ArrayList<String> abilities;
    private ArrayList<Ability> originalAbilities;

    private boolean isHalated;
    private boolean isHypnotized;

    private ArrayList<Armor> activeArmors;
    private int lastActionTime;
    //private HashMap<String, Double> effectsInfo;

    public Zombie(ZombieStats zombieStats, Position position) {
        this.zombieStats = zombieStats;
        this.status = Status.MOVING;

        this.position = position;
        this.currentHP = zombieStats.getBaseHP();
        this.currentVelocity = zombieStats.getVelocity();
        this.isHalated = false;
        this.isHypnotized = false;

        this.activeArmors = new ArrayList<>();
        if (zombieStats.getArmors() != null) {
            for (Armor armor : zombieStats.getArmors()) {
                this.activeArmors.add(new Armor(armor.getType(),
                        armor.getCurrentHP(), armor.isMetallic()));
            }
        }
    }


    public void update() {
        if (this.status.equals(Status.MOVING)) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof Moving) {
                    ability.executeAbility(this);
                }
            }
        } else if (this.status.equals(Status.EATING)) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof Eating) {
                    ability.executeAbility(this);
                }
            }
        } else if (this.status.equals(Status.EXECUTING_ABILITY)) {
            for (Ability ability : this.originalAbilities) {
                if ((ability instanceof Moving) || (ability instanceof Eating)) {
                    continue;
                } else {
                    ability.executeAbility(this);
                }
            }
        }
    }

    public void disarmament() {
        for (Armor armor : this.activeArmors) {
            armor.stripArmor();
        }
    }

    public void takeDamage(Projectile projectile, int damage) {
        int leftoverDamage = damage;

        if (!projectile.isPoisonous()) {
            for (int i = 0; i < activeArmors.size(); i++) {
                Armor armor = activeArmors.get(i);

                leftoverDamage = armor.takeDamage(leftoverDamage);

                if (armor.isDisarmed()) {
                    activeArmors.remove(armor);
                    i -= 1;
                }

                if (leftoverDamage <= 0) {
                    break;
                }
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Entity getRival() {
        return rival;
    }

    public void setRival(Entity rival) {
        this.rival = rival;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isHypnotized() {
        return isHypnotized;
    }

    public void setHypnotized(boolean hypnotized) {
        isHypnotized = hypnotized;
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

    public void setLastActionTime(int lastActionTime) {
        this.lastActionTime = lastActionTime;
    }


}

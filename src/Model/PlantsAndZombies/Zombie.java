package src.Model.PlantsAndZombies;

import src.Enums.Status;
import src.Model.PlantsAndZombies.Abilities.Ability;
import src.Model.PlantsAndZombies.Abilities.Eating;
import src.Model.PlantsAndZombies.Abilities.Moving;
import src.Model.PlantsAndZombies.Abilities.StealingSun;
import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.PlantsAndZombies.Armors.ArmorConfig;
import src.Model.PlantsAndZombies.Projectiles.Dynamite;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Sun.Sun;

import java.util.*;


public class Zombie extends Entity {
    private static int FROZEN_TIME = 3;
    private static int TILE_X_LENGTH = 200;
    private static Random RANDOM = new Random();


    private ZombieStats zombieStats;
    private Status status;
    private Entity rival;

    private double currentVelocity;
    private ArrayList<String> abilities;
    private ArrayList<Ability> originalAbilities;

    private boolean isHalated;
    private boolean isHypnotized;
    private boolean isFrozen;
    private int frozenTime;


    private ArrayList<Armor> activeArmors;
    private int lastActionTime;
    private int spawnTime;
    //private HashMap<String, Double> effectsInfo;

    public Zombie(ZombieStats zombieStats, Position position) {
        this.zombieStats = zombieStats;
        this.status = Status.MOVING;
        this.name = name;

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
        //todo: function for current time
        this.spawnTime = game.getCurrentTime();
    }


    public void update() {
        checkFreeze();

        if ((this.zombieStats.getName().equals("PROSPECTOR")) &&
                (this.zombieStats.getAttributes().get("dynamite").equals("on"))) {
            if ((game.getCurrentTime() - this.spawnTime) >= 10) {
                Dynamite dynamite = new Dynamite(new Position());//todo: initialize with proper coordinates

                //todo: add this recently-generated dynamite to active dynamites arraylist of game
            }

        }


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
            if (armor.isMetallic()) {
                armor.stripArmor();
            }
        }
    }

    public void makeKnight() {
        this.activeArmors.add(ArmorConfig.CROWN.createArmor());
        this.activeArmors.add(ArmorConfig.SHOULDER_ARMOR.createArmor());
    }

    public void changeRow() {
        Position rowAndColumn = Position.getRowAndColumn(this.position);
        int column = (int) rowAndColumn.getX();
        int row = (int) rowAndColumn.getY();

        if (row == 1) {
            this.position = new Position(
                    this.position.getX() + TILE_X_LENGTH,
                    this.position.getY());
        } else if (row == 5) {
            this.position = new Position(
                    this.position.getX() - TILE_X_LENGTH,
                    this.position.getY());
        } else {
            int randomIndex = RANDOM.nextInt(2);
            int difference = (randomIndex == 1) ? TILE_X_LENGTH : (-1) * TILE_X_LENGTH;

            this.position = new Position(
                    this.position.getX() + TILE_X_LENGTH,
                    this.position.getY());
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
                    if (this.name.equals("NEWSPAPER")) { //increasing velocity & damage per second of NEWSPAPER_ZOMBIE
                        this.zombieStats.setVelocity(this.zombieStats.getVelocity() * 2.5);
                        this.zombieStats.setEatdps(this.zombieStats.getEatdps() * 2.5);
                    }
                }

                if (leftoverDamage <= 0) {
                    break;
                }
            }
        }

        if (leftoverDamage > 0) {
            if (this.zombieStats.getName().equals("EXPLORER")) {
                if (projectile.isFiring()) {
                    this.zombieStats.getAttributes().replace("torch", "on");
                } else if (projectile.isIcy()) {
                    this.zombieStats.getAttributes().replace("torch", "off");
                }
            } else if (this.zombieStats.getName().equals("PROSPECTOR")) {
                if (projectile.isIcy()) {
                    this.zombieStats.getAttributes().replace("dynamite", "off");
                }
            }


            if (projectile.isIcy()) {
                if (!this.zombieStats.getName().equals("IMP_DRAGON")) {
                    freeze();
                }
            } else if (projectile.isFiring()) {
                unfreeze();
            }

            if ((!this.zombieStats.getName().equals("IMP_DRAGON")) || (!projectile.isFiring())) {
                this.currentHP -= leftoverDamage;
                checkLife();
            }
        }
    }

    public void checkFreeze() {
        if (this.isFrozen) {
            //todo: getter for current game time
            if ((game.getCurrentTime() - this.frozenTime) >= FROZEN_TIME) {
                this.isFrozen = false;
                this.currentVelocity = this.zombieStats.getVelocity();
            }
        }
    }

    public void freeze() {
        //todo: getter for current game time
        this.frozenTime = game.getCurrentTime();
        this.isFrozen = true;
        this.currentVelocity = (this.zombieStats.getVelocity() * 0.7) //decreasing the zombie velocity after collision with icy projectiles
    }

    public void unfreeze() {
        this.isFrozen = false;
        this.currentVelocity = (this.zombieStats.getVelocity()); //setting the velocity to its base
    }

    public void checkLife() {
        if (this.name.equals("GARGANTUAR")) {
            if (this.currentHP <= (this.zombieStats.getBaseHP() / 2)) {
                //todo: throw a imp zombie to third column of its own row
            }
        }

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

    public ArrayList<Armor> getActiveArmors() {
        return activeArmors;
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

    public void setLastActionTime(int lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public int getSpawnTime() {
        return spawnTime;
    }


}

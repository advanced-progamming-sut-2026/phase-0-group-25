package src.Model.PlantsAndZombies;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Abilities.*;
import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.PlantsAndZombies.Armors.ArmorConfig;
import src.Model.PlantsAndZombies.Projectiles.Dynamite;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Quests.Events.ZombieKilledEvent;
import src.Model.Quests.QuestManager;
import src.Model.Sun.Sun;

import java.util.ArrayList;
import java.util.Random;


public class Zombie extends Entity {
    private static int FROZEN_TIME = 3;
    private static int TILE_X_LENGTH = 200;
    private static Random RANDOM = new Random();
    private static GamePlay GAME = GamePlayMenu.getGamePlay();


    private ZombieStats zombieStats;
    private Entity rival;
    private int waveNum;

    private double currentVelocity;
    private ArrayList<Ability> originalAbilities = new ArrayList<>();

    private boolean isHalated;
    private boolean isHypnotized;
    private boolean isFrozen;
    private double timeWhenFrozen;
    private int frozenTime;


    private ArrayList<Armor> activeArmors;
    private double lastActionTime;
    private double spawnTime;

    public Zombie(ZombieStats zombieStats, String name) {
        this.zombieStats = zombieStats;
        this.name = name;

        addAbilities();
    }

    public Zombie(ZombieStats zombieStats, String name, Position position) {
        this.zombieStats = zombieStats;
        this.name = name;
        this.zombieStats.setName(name);
        GAME = GamePlayMenu.getGamePlay();

        this.position = position;
        this.currentHP = zombieStats.getBaseHP();
        this.currentVelocity = zombieStats.getVelocity();
        this.isHalated = false;
        this.isHypnotized = false;

        addAbilities();

        this.activeArmors = new ArrayList<>();
        if (zombieStats.getArmors() != null) {
            for (Armor armor : zombieStats.getArmors()) {
                this.activeArmors.add(new Armor(armor.getType(),
                        armor.getCurrentHP(), armor.isMetallic()));
            }
        }

        this.spawnTime = GAME.getTotalTimePassed();
    }

    private void addAbilities() {
        for (String ability : this.zombieStats.getAbilities()) {
            if (ability.equals("moving")) {
                this.originalAbilities.add(new Moving());
            } else if (ability.equals("eating")) {
                this.originalAbilities.add(new Eating());
            } else if (ability.equals("fatalDamage")) {
                this.originalAbilities.add(new FatalDamage());
            } else if (ability.equals("flying")) {
                this.originalAbilities.add(new Flying());
            } else if (ability.equals("stealingSun")) {
                this.originalAbilities.add(new StealingSun());
            } else if (ability.equals("throwing")) {
                this.originalAbilities.add(new Throwing());
            } else if (ability.equals("repelLobbers")) {
                this.originalAbilities.add(new RepelLobbers());
            }
        }
    }


    public void update() {
        checkFreeze();
        if ((this.zombieStats.getName().equals("PROSPECTOR")) &&
                (this.zombieStats.getAttributes().get("dynamite").equals("on"))) {
            if ((GAME.getTotalTimePassed() - this.spawnTime) >= 10) {
                Dynamite dynamite = new Dynamite(new Position(20, this.position.getY()));

                GAME.getDynamites().add(dynamite);
            }

        }


        for (Ability ability : this.originalAbilities) {
            ability.executeAbility(this);
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

        int row = this.getRow();

        if (row == 1) {
            this.position = new Position(
                    this.position.getX(),
                    this.position.getY() + TILE_X_LENGTH);
        } else if (row == 5) {
            this.position = new Position(
                    this.position.getX(),
                    this.position.getY() - TILE_X_LENGTH);
        } else {
            int randomIndex = RANDOM.nextInt(2);
            int difference = (randomIndex == 1) ? TILE_X_LENGTH : (-1) * TILE_X_LENGTH;

            this.position = new Position(
                    this.position.getX(),
                    this.position.getY() + difference);
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
                if ((!this.zombieStats.getName().equals("IMP_DRAGON")) &&
                        !(this.zombieStats.getCategory().equals(ChapterType.DARK_AGE.getName()))) {
                    freeze();
                }
            } else if (projectile.isFiring()) {
                unfreeze();
            }

            if ((!this.zombieStats.getName().equals("IMP_DRAGON")) || (!projectile.isFiring())) {
                this.setCurrentHP(this.getCurrentHP() - leftoverDamage);
                if (!this.isAlive) {
                    checkKiller(projectile);
                }
            }
        }
    }

    public void takeDamage(double damage) {
        int leftoverDamage = (int) Math.ceil(damage);

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

        if (leftoverDamage > 0) {
            this.setCurrentHP(this.getCurrentHP() - leftoverDamage);
        }
    }

    public void takeDamage(BattlePlant plant, double damage) {
        int leftoverDamage = (int) Math.ceil(damage);

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

        if (leftoverDamage > 0) {
            this.setCurrentHP(this.getCurrentHP() - leftoverDamage);
            if (!this.isAlive) {
                checkKiller(plant);
            }
        }
    }

    private void checkKiller(Projectile projectile) {
        BattlePlant plant = projectile.getPlant();
        if (plant != null) {
            QuestManager.getInstance().notifyEvent(new ZombieKilledEvent(GAME.getChapterType(), GAME.getTotalTimePassed(), plant.getName()));
        }
    }

    private void checkKiller(BattlePlant plant) {
        if (plant != null) {
            QuestManager.getInstance().notifyEvent(new ZombieKilledEvent(GAME.getChapterType(), GAME.getTotalTimePassed(), plant.getName()));
        }
    }

    public void checkFreeze() {
        if (this.isFrozen) {
            if ((GAME.getTotalTimePassed() - this.timeWhenFrozen) >= this.frozenTime) {
                this.isFrozen = false;
                this.currentVelocity = this.zombieStats.getVelocity();
            }
        }
    }

    public void freeze(int frozenTime) {
        this.timeWhenFrozen = GAME.getTotalTimePassed();
        this.isFrozen = true;
        this.frozenTime = frozenTime;
        this.currentVelocity = (this.zombieStats.getVelocity() * 0.7); //decreasing the zombie velocity after collision with icy projectiles
    }

    public void freeze() {
        this.timeWhenFrozen = GAME.getTotalTimePassed();
        this.isFrozen = true;
        this.frozenTime = FROZEN_TIME;
        this.currentVelocity = (this.zombieStats.getVelocity() * 0.7); //decreasing the zombie velocity after collision with icy projectiles
    }

    public void unfreeze() {
        this.isFrozen = false;
        this.currentVelocity = (this.zombieStats.getVelocity()); //setting the velocity to its base
    }

    public void checkLife() {
        if (this.name.equals("GARGANTUAR")) {
            if (this.currentHP <= (this.zombieStats.getBaseHP() / 2)) {
                Position impPosition = new Position((this.getColumn() * 200 - 80), this.position.getY());
                Zombie imp = ZombieFactory.createZombie("Imp", impPosition);

                GAME.getGameZombies().add(imp);
            }
        }

        if (this.currentHP <= 0) {
            this.currentHP = 0;
            this.isAlive = false;
            checkSteal();
        }

    }

    public void checkSteal() {
        if (this.getZombieStats().getAbilities().contains("stealingSun")) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof StealingSun) {
                    double stolenSun = ((StealingSun) ability).getStolenSun();

                    if (zombieStats.getName().equals("Turquoise")) {
                        Sun sun = new Sun((int) (stolenSun / 2), this.position);
                        GAME.getActiveSuns().add(sun);

                    } else if (zombieStats.getName().equals("Ra")) {
                        GAME.setMySuns(GAME.getMySuns() + (int) stolenSun);
                    }
                }
            }
        }
    }

    public ZombieStats getZombieStats() {
        return zombieStats;
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

    public double getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(double lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public double getSpawnTime() {
        return spawnTime;
    }

    public int getWaveNum() {
        return waveNum;
    }

    public void setWaveNum(int waveNum) {
        this.waveNum = waveNum;
    }

    public ArrayList<Ability> getOriginalAbilities() {
        return originalAbilities;
    }
}

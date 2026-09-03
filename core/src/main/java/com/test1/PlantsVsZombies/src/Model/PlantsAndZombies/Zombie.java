package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.badlogic.gdx.graphics.Color;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.Armor;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.ArmorConfig;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Zombie extends Entity {
    private static int FROZEN_TIME = 8;
    private static int TILE_Y_LENGTH = 150;
    private static int BUTTER_TIME = 8;
    private static int CHILL_TIME = 3;
    private static Random RANDOM = new Random();
    private GamePlay GAME = GamePlay.activeInstance;
    private final AnimationState animationState = new AnimationState();

    protected ZombieStats zombieStats;
    private Entity rival;
    private int waveNum;

    protected double currentVelocity;
    private ArrayList<Ability> originalAbilities = new ArrayList<>();

    private boolean isHalated;
    private boolean isHypnotized = false;
    private boolean isFrozen;
    private double timeWhenFrozen;
    private int frozenTime;
    private boolean isButtered = false;
    private double timeWhenButtered;
    private boolean isChilled = false;
    private double timeWhenChilled;


    private ArrayList<Armor> activeArmors;
    protected double lastActionTime;
    private double spawnTime;
    private double dieTime;
    private boolean isDeadByExplosion = false;

    private double eatdps;

    //just for gargantuar zombie
    private double thrownTime;
    private boolean isThrown = false;
    private boolean isFinished = false;

    //just for snorkel zombie
    private boolean isSubmarine = false;

    //just for explorer zombie
    private boolean isTorchOn = true;

    //just for prospector zombie
    private boolean isDynamiteOn = true;

    public Zombie(ZombieStats zombieStats, String name) {
        this.zombieStats = zombieStats;
        this.name = name;

        this.activeArmors = new ArrayList<>();
        if (zombieStats.getArmor() != null) {
            for (String armorName : zombieStats.getArmor()) {
                Armor armor = Armor.findArmor(armorName);
                this.activeArmors.add(new Armor(armor.getType(),
                    armor.getCurrentHP(), armor.isMetallic(), armor.getAnimations()));
            }
        }


        addAbilities();
    }

    public Zombie(ZombieStats zombieStats, String name, Position position) {
        this.zombieStats = zombieStats;
        this.name = name;
        this.zombieStats.setName(name);
        GAME = GamePlay.activeInstance;

        this.position = position;
        this.currentHP = zombieStats.getBaseHP();
        this.currentVelocity = zombieStats.getVelocity();


        this.spawnTime = GAME.getTotalTimePassed();
        this.lastActionTime = this.spawnTime;
        this.eatdps = this.zombieStats.getEatdps();

        addAbilities();

        this.activeArmors = new ArrayList<>();
        if (zombieStats.getArmor() != null) {
            for (String armorName : zombieStats.getArmor()) {
                Armor armor = Armor.findArmor(armorName);
                this.activeArmors.add(new Armor(armor.getType(),
                    armor.getCurrentHP(), armor.isMetallic(), armor.getAnimations()));
            }
        }
    }

    private void addAbilities() {
        for (String ability : this.zombieStats.getAbilities()) {
            if (ability.equals("fatalDamage")) {
                this.originalAbilities.add(new FatalDamage());
            } else if (ability.equals("moving")) {
                this.originalAbilities.add(new Moving());
            } else if (ability.equals("eating")) {
                this.originalAbilities.add(new Eating());
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
        checkFreezeAndButter();

        // System.out.println(this.zombieStats.getEatdps());
        if (this.isFrozen ||
            this.isButtered) {
            return;
        }

        checkLife();
        if ((this.zombieStats.getName().equals("PROSPECTOR")) &&
            (this.isDynamiteOn)) {
            double difference = GAME.getTotalTimePassed() - this.spawnTime;
            if ((Math.abs(difference) >= 10) && (Math.abs(difference) <= 10.1)) {
                Dynamite dynamite = new Dynamite(new Position(495, this.position.getY()));

                GAME.getDynamites().add(dynamite);
            }

        }


        for (Ability ability : this.originalAbilities) {
            ability.executeAbility(this);
        }
    }


    public void disarmament() {
        for (int i = 0; i <= activeArmors.size(); i++) {
            try {
                Armor armor = activeArmors.get(i);
                if (armor.isMetallic()) {
                    armor.stripArmor();
                    activeArmors.remove(armor);
                    i -= 1;
                }
            } catch (IndexOutOfBoundsException e) {

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
                this.position.getY() + TILE_Y_LENGTH);
        } else if (row == 5) {
            this.position = new Position(
                this.position.getX(),
                this.position.getY() - TILE_Y_LENGTH);
        } else if ((row > 1) && (row < 5)) {
            int randomIndex = RANDOM.nextInt(2);
            int difference = (randomIndex == 1) ? TILE_Y_LENGTH : (-1) * TILE_Y_LENGTH;

            this.position = new Position(
                this.position.getX(),
                this.position.getY() + difference);
        }
    }

    public void takeDamage(Projectile projectile, int damage) {
        if (projectile.getName().equals("butter")) {
            this.setButtered(true);
        }

        int leftoverDamage = damage;
        if (!projectile.isPoisonous()) {
            for (int i = 0; i < activeArmors.size(); i++) {
                Armor armor = activeArmors.get(i);

                leftoverDamage = armor.takeDamage(leftoverDamage);
                if (armor.isDisarmed()) {
                    activeArmors.remove(armor);
                    i -= 1;
                    if (this.name.equals("NEWSPAPER")) { //increasing velocity & damage per second of NEWSPAPER_ZOMBIE
                        this.currentVelocity = this.zombieStats.getVelocity() * 2.5;
                        this.eatdps = this.zombieStats.getEatdps() * 2.5;
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
                    this.isTorchOn = true;
                } else if (projectile.isIcy()) {
                    this.isTorchOn = false;
                }
            } else if (this.zombieStats.getName().equals("PROSPECTOR")) {
                if (projectile.isIcy()) {
                    this.isDynamiteOn = false;
                }
            }

            if (projectile.isIcy()) {
                if ((!this.zombieStats.getName().equals("IMP_DRAGON")) &&
                    !(this.zombieStats.getCategory().equals(ChapterType.DARK_AGE.getName()))) {
                    chill();
                }
            } else if (projectile.isFiring()) {
                unfreeze();
            }

            if ((!this.zombieStats.getName().equals("IMP_DRAGON")) || (!projectile.isFiring())) {
                boolean wasAlive = this.currentHP > 0;
                this.setCurrentHP(this.getCurrentHP() - leftoverDamage);
                if (wasAlive &&
                    this.getCurrentHP() <= 0) {
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
                    this.currentVelocity = this.zombieStats.getVelocity() * 2.5;
                    this.eatdps = this.zombieStats.getEatdps() * 2.5;
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
                    this.currentVelocity = this.zombieStats.getVelocity() * 2.5;
                    this.eatdps = this.zombieStats.getEatdps() * 2.5;
                }
            }

            if (leftoverDamage <= 0) {
                break;
            }
        }

        if (leftoverDamage > 0) {
            boolean wasAlive = this.currentHP > 0;
            this.setCurrentHP(this.getCurrentHP() - leftoverDamage);
            if (wasAlive &&
                this.getCurrentHP() <= 0) {
                checkKiller(plant);
                if (plant.getPlantStats().getCategory().equals("Explosive")) {
                    this.isDeadByExplosion = true;
                }
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

    public void checkFreezeAndButter() {
        if (this.isFrozen) {
            if ((GAME.getTotalTimePassed() - this.timeWhenFrozen) >= this.frozenTime) {
                this.isFrozen = false;
                this.currentVelocity = this.zombieStats.getVelocity();
            }
        }

        if (this.isButtered) {
            if ((GAME.getTotalTimePassed() - this.timeWhenButtered) >= BUTTER_TIME) {
                this.isButtered = false;
                this.currentVelocity = this.zombieStats.getVelocity();
            }
        }

        if (this.isChilled) {
            if ((GAME.getTotalTimePassed() - this.timeWhenChilled) >= CHILL_TIME) {
                this.isChilled = false;
                this.currentVelocity = this.zombieStats.getVelocity();
            }
        }
    }

    public void freeze(int frozenTime) {
        if (this.zombieStats.getCategory().equals("frostbite caves")) {
            return;
        }

        this.timeWhenFrozen = GAME.getTotalTimePassed();
        this.isFrozen = true;
        this.frozenTime = frozenTime;
        this.currentVelocity = 0; //decreasing the zombie velocity after collision with icy projectiles
        this.isTorchOn = false;
    }

    public void freeze() {
        if (this.zombieStats.getCategory().equals("frostbite caves")) {
            return;
        }

        this.timeWhenFrozen = GAME.getTotalTimePassed();
        this.isFrozen = true;
        this.frozenTime = FROZEN_TIME;
        this.currentVelocity = 0; //decreasing the zombie velocity after collision with icy projectiles
        this.isTorchOn = false;
    }

    public void chill() {
        if (this.zombieStats.getCategory().equals("frostbite caves")) {
            return;
        }

        this.timeWhenChilled = GAME.getTotalTimePassed();
        this.setChilled(true);
        this.isTorchOn = false;

    }

    public void unfreeze() {
        this.isFrozen = false;
        this.isChilled = false;
        this.currentVelocity = this.zombieStats.getVelocity(); //setting the velocity to its base
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void checkLife() {
        if (this.name.equals("GARGANTUAR")) {
            if (this.currentHP <= (this.zombieStats.getBaseHP() / 2)) {
                if (!this.isThrown()) {
                    this.setThrownTime(GAME.getTotalTimePassed());
                    this.setThrown(true);
                }
            }
        }

        if (this.name.equals("ARCADE")) {
            if (this.currentHP <= (this.zombieStats.getBaseHP() / 2)) {
                try {
                    for (int i = 0; i < this.originalAbilities.size(); i++) {
                        Ability ability = this.originalAbilities.get(i);
                        if (ability instanceof FatalDamage) {
                            this.originalAbilities.remove(i);
                            i -= 1;
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        if (this.isHypnotized) {
            try {
                for (int i = 0; i < this.originalAbilities.size(); i++) {
                    Ability ability = this.originalAbilities.get(i);
                    if ((ability instanceof Moving) || (ability instanceof Eating) || (ability instanceof FatalDamage)) {
                        continue;
                    }
                    this.originalAbilities.remove(ability);
                    i -= 1;
                }
            } catch (IndexOutOfBoundsException e) {

            }
        }


        if (this.currentHP <= 0) {
            this.currentHP = 0;
        }

    }

    public void checkSteal() {
        if (this.getZombieStats().getAbilities().contains("stealingSun")) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof StealingSun) {
                    double stolenSun = ((StealingSun) ability).getStolenSun();

                    if (zombieStats.getName().equals("TURQUOISE")) {
                        Sun sun = new Sun((int) (stolenSun / 2), this.position);
                        GAME.getActiveSuns().add(sun);

                    } else if (zombieStats.getName().equals("RA")) {
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

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        if (!this.isAlive) {
            checkSteal();
        }
    }

    public String getAnimationPath() {
        if (this.isDeadByExplosion) {
            return "768/INITIAL/EFFECTS/ZOMBIE_ASH/ZOMBIE_ASH.PAM";
        }
        return this.zombieStats.getAnimation();
    }

    public String getCurrentAnimationName() {
        AnimationDecider decider = new AnimationDecider();
        return decider.zombieDecider(this);
    }

    public HashMap<String, Boolean> getVisibility() {
        AnimationDecider decider = new AnimationDecider();
        return decider.zombieVisibilities(this);
    }

    @Override
    public void setCurrentHP(double currentHP) {
        super.setCurrentHP(currentHP);
        if (currentHP <= 0) {
            dieTime = GAME.getTotalTimePassed();
        }
    }

    public double getDieTime() {
        return dieTime;
    }

    public Color getColor() {
        if (this instanceof Zomboss) {
            return Color.WHITE;
        }

        if (this.isFrozen) {
            return Color.BLUE;
        } else if (this.isButtered) {
            return Color.YELLOW;
        } else if (this.isChilled) {
            return Color.SKY;
        } else if (this.column <= 2) {
            return Color.RED;
        } else if (this.isHalated) {
            return Color.GREEN;
        } else if (this.name.equals("SNORKEL")) {
            if (this.isSubmarine) {
                return Color.FOREST;
            }
        }

        return Color.WHITE;
    }

    public boolean isButtered() {
        return isButtered;
    }


    public void setButtered(boolean buttered) {
        isButtered = buttered;
        if (this.isButtered) {
            this.currentVelocity = 0;
            this.timeWhenButtered = GAME.getTotalTimePassed();
        }
    }

    public boolean isDeadByExplosion() {
        return isDeadByExplosion;
    }

    public boolean isHalated() {
        return isHalated;
    }

    public void setHalated(boolean isHalated) {
        this.isHalated = isHalated;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isThrown() {
        return isThrown;
    }

    public void setThrown(boolean thrown) {
        this.isThrown = thrown;
    }

    public double getThrownTime() {
        return thrownTime;
    }

    public void setThrownTime(double thrownTime) {
        this.thrownTime = thrownTime;
    }

    public boolean isSubmarine() {
        return isSubmarine;
    }

    public void setSubmarine(boolean submarine) {
        this.isSubmarine = submarine;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public boolean isChilled() {
        return isChilled;
    }

    public void setChilled(boolean chilled) {
        isChilled = chilled;
        if (this.isChilled) {
            this.currentVelocity = this.zombieStats.getVelocity() * 0.35;
            this.timeWhenChilled = GAME.getTotalTimePassed();
        } else {
            this.currentVelocity = this.zombieStats.getVelocity();
        }
    }

    public double getTimeWhenChilled() {
        return timeWhenChilled;
    }

    public void setTimeWhenChilled(double timeWhenChilled) {
        this.timeWhenChilled = timeWhenChilled;
    }

    public boolean isTorchOn() {
        return isTorchOn;
    }

    public void setTorchOn(boolean torchOn) {
        isTorchOn = torchOn;
    }

    public boolean isDynamiteOn() {
        return isDynamiteOn;
    }

    public void setDynamiteOn(boolean dynamiteOn) {
        isDynamiteOn = dynamiteOn;
    }

    public double getEatdps() {
        return eatdps;
    }

    public void setEatdps(double eatdps) {
        this.eatdps = eatdps;
    }
}

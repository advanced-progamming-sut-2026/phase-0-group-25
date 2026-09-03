package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.*;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ExplosiveUsedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.HashMap;

public class BattlePlant extends Plant {
    private static int PLANT_FOOD_EFFECT_TIME = 2;
    private static int OCTOPUS_BASE_HP = 100;
    private boolean isEffected = false;
    private double effectedTime = 0;
    private double effectedLifeSpan;
    private double currentCoolDown;

    private double lastActionTime;
    private double plantTime;
    private PlantStats plantStats;

    private ArrayList<Ability> originalAbilities;

    private boolean frozen;
    private int iceTime;
    private double iceHP;

    private boolean isOctopusated = false;
    private double octopusHP;

    private String status = "idle";
    private double armorHP = 0.0;
    private double dieTime;

    private double attackTime;

    private GamePlay GAME = GamePlay.activeInstance;
    private final AnimationState animationState = new AnimationState();

    public BattlePlant(PlantStats plantStats, String name) {
        this.plantStats = plantStats;
        this.name = name;
    }

    public BattlePlant(PlantStats plantStats, String name, Position position) {
        super();
        this.lastActionTime = 0;

        this.isAlive = true;
        this.currentHP = plantStats.getBaseHP();
        this.plantTime = GAME.getTotalTimePassed();
        this.plantStats = plantStats;
        this.plantStats.setName(name);

        this.lastActionTime = this.plantTime;

        this.name = name;
        this.position = position;
        this.price = this.plantStats.getCost();
        this.originalAbilities = addAbilities();
    }


    @Override
    public void update() {
        if (checkOctopusAndIced()) {
            return;
        }

        checkEffected();

        if (this.plantStats.getAttributes().containsKey("life-span")) {
            double lifespan = (double) this.plantStats.getAttributes().get("life-span");
            if ((GAME.getTotalTimePassed() - this.plantTime) >= lifespan) {
                this.setAlive(false);
                return;
            }
        }

        if (this.plantStats.getTags().contains("fire")) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof Heating) {
                    ability.executeAbility(this);
                }
            }
        }

        if ((this.plantStats.getCategory().equals("Wall-nut") ||
            this.plantStats.getAbilities().contains("explosion") &&
                (!this.name.equals(PlantType.HYPNO_SHROOM.getName())))) {
            if (this.isEffected) {
                double difference = GAME.getTotalTimePassed() - this.effectedTime;
                if (difference >= (this.effectedLifeSpan / 2)) {

                    PlantFood plantFood = new PlantFood();
                    plantFood.plantFoodEffect(this, this.plantStats.getTags());

                    this.isEffected = false;
                }
            }

            if (this.plantStats.getAbilities().contains("mint")) {
                if (isTimeForAction()) {
                    for (Ability ability : this.originalAbilities) {
                        ability.executeAbility(this);
                    }
                }
            }
        }


        if (!this.plantStats.getCategory().equals("Wall-nut") &&
            !this.plantStats.getAbilities().contains("explosion")) {


            if (this.isEffected) {
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }
                return;
            }

            if (isTimeForAction()) {

                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }

                if (this.name.equals(PlantType.CITRON.getName())) {

                    this.lastActionTime =
                        GAME.getTotalTimePassed();

                    this.status = "charge";

                    return;
                }

                if (this.plantStats.getCategory().equals("Sun Producer") ||
                    this.name.equals(PlantType.CHOMPER.getName())) {
                    return;
                }

                this.lastActionTime =
                    GAME.getTotalTimePassed();
            } else {
                this.status = "idle";
            }

        }
    }

    public boolean checkingPlantable(int sun, Tile thisTile) {
        BattlePlant upperPlant = null;
        if (!thisTile.getPlants().isEmpty()) {
            upperPlant = thisTile.getPlants().get(thisTile.getPlants().size() - 1);
        }
        boolean isStack = (upperPlant != null && upperPlant.getPlantStats().getTags().contains("bottomStack")) ||
            thisTile.getPlants().isEmpty() || this.getPlantStats().getTags().contains("upperStack");
        return (sun >= this.plantStats.getCost()) && (this.currentCoolDown <= 0 || !this.activeCooldown) && isStack;
    }

    public PlantStats getPlantStats() {
        return plantStats;
    }

    public int getCooldown() {
        return (int) this.plantStats.getRechargeTime() * 10;
    }

    public double getPlantTime() {
        return plantTime;
    }

    public void setPlantTime(double plantTime) {
        this.plantTime = plantTime;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        if (this.plantStats.getTags().contains("fire")) {
            return;
        }

        this.frozen = frozen;
        if (this.frozen) {
            if (this.iceHP <= 0) {
                this.iceHP = 600;
            }
        } else {
            this.iceTime = 0;
            this.iceHP = 0;
        }
    }

    public void takeIceDamage(int damage) {
        this.iceHP -= damage;
        if (this.iceHP <= 0) {
            this.setFrozen(false);
        }
    }

    public int getIceTime() {
        return iceTime;
    }

    public void setIceTime(int iceTime) {
        if (this.plantStats.getTags().contains("fire")) {
            return;
        }
        this.iceTime = iceTime;
        if (this.iceTime >= 3) {
            this.setFrozen(true);
            this.iceTime = 0;
        }
    }

    public boolean isEffected() {
        return isEffected;
    }

    public void setEffected(boolean effected) {
        isEffected = effected;
        if (this.isEffected) {
            this.effectedTime = GAME.getTotalTimePassed();
            this.effectedLifeSpan = 5;
        }
    }

    public void setEffected(boolean effected, int effectedLifeSpan) {
        if (this.getName().equals(PlantType.ENLIGHTEN_MINT.getName())) {
            return;
        }
        this.isEffected = effected;
        this.effectedLifeSpan = effectedLifeSpan;
        this.effectedTime = GAME.getTotalTimePassed();

    }


    public double getEffectedTime() {
        return effectedTime;
    }

    public void setEffectedTime(double effectedTime) {
        this.effectedTime = effectedTime;
    }

    private ArrayList<Ability> addAbilities() {
        ArrayList<Ability> abilities = new ArrayList<>();
        for (String ability : this.plantStats.getAbilities()) {
            if (ability.equals("producing sun")) {
                abilities.add(new ProducingSun());
            } else if (ability.equals("shooting")) {
                abilities.add(new Shooting());
            } else if (ability.equals("lobbing")) {
                abilities.add(new Lobbing());
            } else if (ability.equals("homing")) {
                abilities.add(new Homing());
            } else if (ability.equals("explosion")) {
                abilities.add(new Explosion());
            } else if (ability.equals("meleeAttacking")) {
                abilities.add(new MeleeAttacking());
            } else if (ability.equals("explosionWithLifeSpan")) {
                abilities.add(new ExplosionWithLifespan());
            } else if (ability.equals("wall-nut")) {
                abilities.add(new WallNutAbility());
            } else if (ability.equals("Modifier")) {
                abilities.add(new Modifier());
            } else if (ability.equals("mint")) {
                abilities.add(new Mint());
            }
        }

        if (this.plantStats.getTags().contains("fire")) {
            abilities.add(new Heating());
        }
        return abilities;
    }

    public double getCurrentCoolDown() {
        return currentCoolDown;
    }

    public void setCurrentCoolDown(double currentCoolDown) {
        this.currentCoolDown = currentCoolDown;
    }

    public ArrayList<Ability> getOriginalAbilities() {
        return originalAbilities;
    }

    public int getLevel() {
        return this.plantStats.getLevel();
    }

    @Override
    public void setCurrentHP(double currentHP) {
        super.setCurrentHP(currentHP);
        if (currentHP < 0) {
            this.isAlive = false;
        }





    }

    public double getLastActionTime() {
        return lastActionTime;
    }

    public boolean isTimeForAction() {
        boolean actionIntervalBoolean = ((GAME.getTotalTimePassed() - this.lastActionTime) >= this.plantStats.getActionInterval());

        return actionIntervalBoolean;
    }

    public void takeDamage(double damage) {
        if (this.armorHP > 0) {
            this.armorHP -= damage;
            if (this.armorHP < 0) {
                this.armorHP = 0;
            }

            return;
        }

        this.currentHP -= damage;
        if (this.currentHP <= 0) {
            this.setAlive(false);
        }
    }

    public String getCurrentAnimationName(float stateTime) {
        AnimationDecider decider = new AnimationDecider();
        return decider.plantDecider(this, stateTime);
    }

    public String getCurrentAnimationName() {
        AnimationDecider decider = new AnimationDecider();
        return decider.plantDecider(this, (float) GAME.getTotalTimePassed());
    }

    public String getAnimationPath() {
        if (this.plantStats.getAbilities().contains("explosionWithLifeSpan")) {
            double difference = GAME.getTotalTimePassed() - this.plantTime;
            double attackTime = (double) this.plantStats.getAttributes().get("attackTime");

            if (difference >= attackTime) {
                return (String) this.plantStats.getAttributes().get("explosionAnimation");
            }

            return this.plantStats.getAnimation();
        } else if ((this.name.equals(PlantType.POTATO_MINE.getName())) ||
            (this.name.equals(PlantType.PRIMAL_POTATO_MINE.getName()))) {
            if (this.getCurrentHP() <= 0) {
                double difference = GAME.getTotalTimePassed() - this.dieTime;

                if (difference <= 1.17) {
                    return (String) this.plantStats.getAttributes().get("explosionAnimation");
                }
                return this.plantStats.getAnimation();
            }
        }

        return this.plantStats.getAnimation();
    }

    public HashMap<String, Boolean> getVisibilities() {
        AnimationDecider decider = new AnimationDecider();
        return decider.plantVisibilities(this);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastActionTime(double lastActionTime) {
        this.lastActionTime = lastActionTime;
    }


    public void checkSunProducer() {
        float difference = (float) (GAME.getTotalTimePassed() - this.lastActionTime);
        float trigger = this.plantStats.getTrigger();
        float total = difference + trigger;
        if ((total >= this.plantStats.getActionInterval())) {
            this.status = "action";
        } else {
            this.status = "idle";
        }

    }

    public void checkMelee() {
        if (!this.name.equals(PlantType.CHOMPER.getName())) {
            ArrayList<Zombie> zombiesInRange = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                Tile tile = GAME.getTileByPosition(this.column + i, this.row);
                if (tile == null) {
                    continue;
                }
                if (GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
                    GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
                    GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {
                    if ((!tile.isArable()) && (tile.getHP() > 0)) {
                        this.status = "action";
                        return;
                    }
                }
                zombiesInRange.addAll(tile.getZombies());
            }

            for (Zombie zombie : GAME.getGameZombies()) {
                if (zombie instanceof Zomboss) {
                    int secondRow = ((Zomboss) zombie).getCurrentSecondRow();
                    if (secondRow == this.getRow()) {
                        for (int i = -1; i <= 1; i++) {
                            Tile tile = GAME.getTileByPosition(this.column + i, this.row);
                            if (tile == null) {
                                continue;
                            }

                            for (Zombie zombie1 : tile.getZombies()) {
                                if (zombie1 instanceof Zomboss) {
                                    this.status = "action";
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (zombiesInRange.size() != 0) {
                this.status = "action";
            } else {
                this.status = "idle";
            }
        } else {
            double difference = GAME.getTotalTimePassed() - this.lastActionTime;
            if (difference >= this.plantStats.getActionInterval()) {
                ArrayList<Zombie> zombiesInRange = findZombies();

                if (!zombiesInRange.isEmpty()) {
                    this.attackTime = GAME.getTotalTimePassed();
                    this.status = "action";
                } else {
                    this.status = "idle";
                }
            } else {
                double attackDifference = GAME.getTotalTimePassed() - this.attackTime;
                if (attackDifference <= 0.75) {
                    this.status = "action";
                } else {
                    this.status = "idle";
                }
            }
        }
    }

    public void checkSquash() {
        ArrayList<Zombie> zombiesInRange = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            Tile tile = GAME.getTileByPosition(this.column, this.row);
            if (tile == null) {
                continue;
            }
            for (Zombie zombie : tile.getZombies()) {
                if (this.position.distance(zombie.position) <= 50f) {
                    zombiesInRange.add(zombie);
                }
            }
        }

        if (!zombiesInRange.isEmpty()) {
            this.status = "action";
        } else {
            this.status = "idle";
        }
    }

    private void checkEffected() {
        float difference = (float) (GAME.getTotalTimePassed() - this.effectedTime);

        if (difference >= this.effectedLifeSpan) {
            this.isEffected = false;
        }
    }

    public double getArmorHP() {
        return armorHP;
    }

    public void setArmorHP(double armorHP) {
        this.armorHP = armorHP;
    }

    public double getDieTime() {
        return dieTime;
    }

    public void setDieTime(double dieTime) {
        this.dieTime = dieTime;
    }

    public double getEffectedLifeSpan() {
        return effectedLifeSpan;
    }

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    private ArrayList<Zombie> findZombies() {
        int plantRow = this.getRow();
        int plantColumn = this.getColumn();


        ArrayList<Zombie> properZombies = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow);
            properZombies.addAll(tile.getZombies());
        }
        return properZombies;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isOctopusated() {
        return isOctopusated;
    }

    public void setOctopusated(boolean octopusated) {
        isOctopusated = octopusated;
        if (this.isOctopusated) {
            setOctopusHP(OCTOPUS_BASE_HP);
        } else {
            setOctopusHP(0);
        }
    }

    public double getOctopusHp() {
        return octopusHP;
    }

    public void setOctopusHP(double octopusHp) {
        this.octopusHP = octopusHp;
        if (this.octopusHP == 0) {
            this.isOctopusated = false;
        }
    }

    public boolean checkOctopusAndIced() {
        boolean condition = ((this.frozen) || (this.isOctopusated));
        if (condition) {
            this.lastActionTime = GAME.getTotalTimePassed();
        }

        return condition;
    }
}

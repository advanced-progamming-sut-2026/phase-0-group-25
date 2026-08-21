package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.*;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ExplosiveUsedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BattlePlant extends Plant {
    private static int PLANT_FOOD_EFFECT_TIME = 2;
    private boolean isEffected = false;
    private double effectedTime;
    private double effectedLifeSpan;
    private double currentCoolDown = 0;

    private double lastActionTime;
    private double plantTime;
    private PlantStats plantStats;

    private ArrayList<Ability> originalAbilities;

    private boolean frozen;
    private int iceTime;
    private double iceHP;

    private String status = "idle";

    private GamePlay GAME = GamePlay.activeInstance;

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

        this.name = name;
        this.position = position;
        this.price = this.plantStats.getCost();
        this.originalAbilities = addAbilities();
    }


    @Override
    public void update() {
        if (this.plantStats.getAttributes().containsKey("life-span")) {
            double lifespan = (double) this.plantStats.getAttributes().get("life_span");
            if ((GAME.getTotalTimePassed() - this.plantTime) >= lifespan) {
                this.setCurrentHP(0);
            }
            return;
        }

        if (this.plantStats.getTags().contains("fire")) {
            for (Ability ability : this.originalAbilities) {
                if (ability instanceof Heating) {
                    ability.executeAbility(this);
                }
            }
        }

        if (!this.plantStats.getCategory().equals("Wall-nut") &&
            !this.plantStats.getCategory().equals("Explosive")) {

            checkStatus();

            if (this.isEffected) {
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }
                if ((GAME.getTotalTimePassed() - this.effectedTime) >= this.effectedLifeSpan) {
                    this.isEffected = false;
                    return;
                }
            }

            if (isTimeForAction()) {
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }
                if ((this.plantStats.getCategory().equals("Sun Producer")) ||
                    (this.name.equals(PlantType.CHOMPER.getName()))) {
                    return;
                }

                this.lastActionTime = GAME.getTotalTimePassed();
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
    }

    public int getIceTime() {
        return iceTime;
    }

    public void setIceTime(int iceTime) {
        this.iceTime = iceTime;
        if (this.iceTime >= 3) {
            System.out.println("This Plant Was Frozen! (" + this.getPosition().getX() + " , " + this.getPosition().getY() + ")");
            this.setFrozen(true);
            this.iceTime = 0;
        }
    }

    public boolean isEffected() {
        return isEffected;
    }

    public void setEffected(boolean effected) {
        isEffected = effected;
    }

    public void setEffected(boolean effected, int effectedLifeSpan) {
        this.isEffected = effected;
        this.effectedLifeSpan = effectedLifeSpan;
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
            } else if (ability.equals("modifier")) {
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
        if (currentHP <= 0) {
            this.isAlive = false;
        }
        if (!this.isAlive) {
            if (this.plantStats.getCategory().equals(PlantCategory.EXPLOSIVE.name())) {
                QuestManager.getInstance().notifyEvent(new ExplosiveUsedEvent(this.name));
            }
        }
    }

    public double getLastActionTime() {
        return lastActionTime;
    }

    public boolean isTimeForAction() {
        return ((GAME.getTotalTimePassed() - this.lastActionTime) >= this.plantStats.getActionInterval());
    }

    public void takeDamage(double damage) {
        double armorHP = (int) this.plantStats.getAttributes().getOrDefault("armorHP", 0);
        if (armorHP > 0) {
            double finalArmorHP = armorHP - damage;
            if (finalArmorHP < 0) {
                finalArmorHP = 0;
            }
            this.plantStats.getAttributes().put("armorHP", finalArmorHP);

            return;
        }

        this.currentHP -= damage;
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

    private void checkStatus() {
        if (this.plantStats.getCategory().equals("Sun Producer")) {
            checkSunProducer();
        }

        if (this.plantStats.getCategory().equals("Melee")) {
            checkMelee();
        }

        if (this.name.equals(PlantType.SQUASH.getName())) {
            checkSquash();
        }
    }

    public void checkSunProducer() {
        float difference = (float) (GAME.getTotalTimePassed() - this.lastActionTime);
        float trigger = this.plantStats.getTrigger();
        float total = difference + trigger;
        System.out.println(difference + "        " + total + "    sdfsfd" + this.plantStats.getActionInterval());
        if ((total >= this.plantStats.getActionInterval())) {
            this.status = "action";
        } else {
            this.status = "idle";
        }

        System.out.println(this.status);
    }

    public void checkMelee() {
        if (!this.name.equals(PlantType.CHOMPER.getName())) {
            ArrayList<Zombie> zombiesInRange = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                Tile tile = GAME.getTileByPosition(this.column + i, this.row);
                if (tile == null) {
                    continue;
                }
                zombiesInRange.addAll(tile.getZombies());
            }

            if (zombiesInRange.size() != 0) {
                this.status = "action";
            } else {
                this.status = "idle";
            }
        } else {
            Tile tile = GAME.getTileByPosition(this.column, this.row);
            ArrayList<Zombie> zombiesInRange = new ArrayList<>();
            for (Zombie zombie : tile.getZombies()) {
                if (zombie.getPosition().distance(this.position) <= 20) {
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
}

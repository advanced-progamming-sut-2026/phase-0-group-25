package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;
import src.Enums.PlantType;
import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Abilities.*;
import src.Model.Quests.Events.ExplosiveUsedEvent;
import src.Model.Quests.QuestManager;
import src.Model.Tile;

import java.util.ArrayList;

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
    private int zombieKilled = 0;

    private static GamePlay GAME = GamePlayMenu.getGamePlay();

    public BattlePlant(PlantStats plantStats, String name) {
        this.plantStats = plantStats;
        this.name = name;
    }

    public BattlePlant(PlantStats plantStats, String name, Position position) {
        super();
        this.lastActionTime = 0;
        GAME = GamePlayMenu.getGamePlay();

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


        if (this.plantStats.getAttributes().containsKey("life_span")) {
            int lifespan = (int) this.plantStats.getAttributes().get("life_span");
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

            if (this.isEffected) {
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }
                if ((GAME.getTotalTimePassed() - this.effectedTime) >= this.effectedLifeSpan) {
                    this.isEffected = false;
                    return;
                }
            }

            if ((GAME.getTotalTimePassed() - this.lastActionTime) >= this.plantStats.getActionInterval()) {
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
                }
                this.lastActionTime = GAME.getTotalTimePassed();
            }

        }
    }

    public boolean checkingPlantable(int sun, Tile thisTile) {
        BattlePlant upperPlant = null;
        if (!thisTile.getPlants().isEmpty()) {
            upperPlant = thisTile.getPlants().get(thisTile.getPlants().size() - 1);
        }
        boolean isStack = (upperPlant != null && upperPlant.getPlantStats().getTags().contains("Stack")) || thisTile.getPlants().isEmpty();
        return (sun >= this.plantStats.getCost()) && (this.currentCoolDown == 0 || !this.activeCooldown) && isStack;
    }

    public PlantStats getPlantStats() {
        return plantStats;
    }

    public int getCooldown() {
        return (int) this.plantStats.getRechargeTime()*10 ;
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
            this.iceTime = 0;
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
            } else if (ability.equals("melee")) {
                abilities.add(new MeleeAttacking());
            } else if (ability.equals("explosionWithLifeSpan")) {
                abilities.add(new ExplosionWithLifespan());
            } else if (ability.equals("wall-nut")) {
                abilities.add(new MeleeAttacking());
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
        if (!this.isAlive) {
            if (this.plantStats.getCategory().equals(PlantCategory.EXPLOSIVE.name())) {
                QuestManager.getInstance().notifyEvent(new ExplosiveUsedEvent(this.name));
            }
        }
    }

    public int getZombieKilled() {
        return zombieKilled;
    }

    public void setZombieKilled(int zombieKilled) {
        this.zombieKilled = zombieKilled;
    }
}

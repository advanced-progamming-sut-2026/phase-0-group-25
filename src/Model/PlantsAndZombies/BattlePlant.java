package src.Model.PlantsAndZombies;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Abilities.*;

import java.util.ArrayList;

public class BattlePlant extends Plant {
    private static int PLANT_FOOD_EFFECT_TIME = 2;
    private boolean isEffected = false;
    private double effectedTime;
    private double effectedLifeSpan;


    private double lastActionTime;
    private double plantTime;
    private PlantStats plantStats;

    private ArrayList<Ability> originalAbilities;

    private boolean frozen;
    private int iceTime;
    private double iceHP;

    private static GamePlay GAME = GamePlayMenu.getGamePlay();

    public BattlePlant(PlantStats plantStats, String name, Position position) {
        this.lastActionTime = 0;

        this.plantTime = GAME.getTotalTimePassed();
        this.plantStats = plantStats;
        this.name = name;
        this.position = position;

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
                if ((GAME.getTotalTimePassed() - this.effectedTime) >= this.effectedLifeSpan) {
                    this.isEffected = false;
                    return;
                }
                for (Ability ability : this.originalAbilities) {
                    ability.executeAbility(this);
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


    public PlantStats getPlantStats() {
        return plantStats;
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

    public ArrayList<Ability> getOriginalAbilities() {
        return originalAbilities;
    }

    public int getLevel() {
        return this.plantStats.getLevel();
    }
}

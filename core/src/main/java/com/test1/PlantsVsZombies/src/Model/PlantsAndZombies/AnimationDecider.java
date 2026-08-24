package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.Armor;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimationDecider {
    private GamePlay GAME = GamePlay.activeInstance;

    public String plantDecider(BattlePlant plant, float stateTime) {
        Map<String, String> status = plant.getPlantStats().getStatus();
        if (plant.isEffected()) {
            return status.get("plantfood");
        }

        if (plant.getPlantStats().getTags().contains("wramp-up")) {
            return getWrampUpPlantsAnimation(plant, status, stateTime);
        }

        if (plant.getPlantStats().getTags().contains("charge")) {
            return getChargePlantsAnimation(plant, status, stateTime);
        }

        if (plant.getPlantStats().getAbilities().contains("mint")) {
            return getMintAnimation(plant, stateTime);
        }

        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            return getWallNutAnimation(plant);
        }

        if (plant.getPlantStats().getCategory().equals("Modifier")) {
            return getModifierAnimation(plant);
        }

        if (plant.getPlantStats().getCategory().equals("Melee")) {
            return getMeleeAnimation(plant);
        }

        if (plant.getPlantStats().getCategory().equals("Sun Producer")) {
            return getSunProducerAnimation(plant);
        }

        if (plant.getPlantStats().getCategory().equals("Homing")) {
            return getHomingAnimation(plant);
        }
        if (plant.getPlantStats().getCategory().equals("Explosive")) {
            return getExplosiveAnimation(plant, stateTime);
        }
        if ((plant.getPlantStats().getCategory().equals("Shooter")) ||
            (plant.getPlantStats().getCategory().equals("Strike-through")) ||
            (plant.getPlantStats().getCategory().equals("Lobber"))) {
            return getShootingAnimation(plant, status);
        }

        return null;
    }

    public HashMap<String, Boolean> plantVisibilities(BattlePlant plant) {
        HashMap<String, Boolean> visibilities = new HashMap<>();
        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            visibilities = wallNutVisibility(plant);
        }

        return visibilities;
    }

    public String zombieDecider(Zombie zombie) {
        Map<String, String> status = zombie.getZombieStats().getStatus();
        if (zombie.getCurrentHP() <= 0) {
            return handleZombieDeath(zombie, status);
        }

        if (zombie.getName().equals("GARGANTUAR")) {
            if ((zombie.getZombieStats().isThrown()) && (!zombie.getZombieStats().isFinished())) {
                for (Ability ability : zombie.getOriginalAbilities()) {
                    if (ability instanceof Moving) {
                        ((Moving) ability).setActivated(false);
                    } else if (ability instanceof FatalDamage) {
                        ((FatalDamage) ability).setActivated(false);
                    }
                }
                return handleGargantuarThrow(zombie, status);
            }
            return handleGargantuar(zombie, status);
        }

        if (zombie.getName().equals("ALL_STAR")) {
            return handleAllStar(zombie, status);
        }

        if (zombie.getZombieStats().getAbilities().contains("throwing")) {
            return handleZombieThrowing(zombie, status);
        }

        for (Ability ability : zombie.getOriginalAbilities()) {
            if ((ability instanceof StealingSun) && (((StealingSun) ability).isActivated())) {
                return status.get("action");
            } else if ((ability instanceof Moving) && (((Moving) ability).isActivated())) {
                return status.get("walk");
            } else if ((ability instanceof Eating) && (((Eating) ability).isActivated())) {
                return status.get("eat");
            }
        }
        return status.get("idle");
    }

    public HashMap<String, Boolean> zombieVisibilities(Zombie zombie) {
        HashMap<String, Boolean> visibility = new HashMap<>();

        for (Armor armor : zombie.getActiveArmors()) {
            String currentArmorStage = armor.getCurrentAnimation();
            if (armor.getType().equals("Shoulder Armor")) {
                visibility.put("zombie_shoulder_armor", true);
            } else if (armor.getType().equals("Crown")) {
                visibility.put("_zombie_armor_crown_states", true);
            }
            visibility.put(currentArmorStage, true);
        }

        return visibility;
    }


    private String getWrampUpPlantsAnimation(BattlePlant plant, Map<String, String> status, float stateTime) {
        ArrayList<Integer> growthTimeStages = (ArrayList<Integer>) plant.getPlantStats().getAttributes().get("growth_time");

        double differenceTime = (double) (GAME.getTotalTimePassed() - plant.getPlantTime());
        String stage = "";

        if (differenceTime >= growthTimeStages.get(1)) {
            stage += "3";
        } else if (differenceTime >= growthTimeStages.get(0)) {
            stage += "2";
        } else {
            stage += "1";
        }

        return status.get(plant.getStatus() + stage);
    }

    private String getChargePlantsAnimation(BattlePlant plant, Map<String, String> status, float stateTime) {
        int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
        if ((stateTime - plant.getPlantTime()) < armTime) {
            return status.get("disarmed");
        }

        return status.get("armed");
    }

    private boolean isTimeForAction(BattlePlant plant, float stateTime) {
        float difference = (float) (stateTime - plant.getLastActionTime());
        float triggerTime = plant.getPlantStats().getTrigger();
        boolean goodTime = plant.isTimeForAction();
        boolean finalAction = false;

        if (goodTime) {
            finalAction = true;
        }
        if (finalAction) {
            if (difference <= triggerTime) {
                return true;
            } else {
                finalAction = false;
            }
        }

        return false;
    }

    private String getMintAnimation(BattlePlant plant, float stateTime) {
        double introTime = (double) plant.getPlantStats().getAttributes().get("intro");
        double loopTime = (double) plant.getPlantStats().getAttributes().get("loop");
        Map<String, String> status = plant.getPlantStats().getStatus();

        double timeDifference = stateTime - plant.getPlantTime();
        if (timeDifference >= (loopTime + introTime)) {
            return status.get("outro");
        } else if (timeDifference >= introTime) {
            return status.get("loop");
        } else {
            return status.get("intro");
        }
    }

    private String getWallNutAnimation(BattlePlant plant) {
        int baseHP = plant.getPlantStats().getBaseHP();
        double currentHP = plant.getCurrentHP();
        Map<String, String> status = plant.getPlantStats().getStatus();

        double armorHP = plant.getArmorHP();
        if (armorHP != 0) {
            if (plant.getName().equals(PlantType.PUMPKIN.getName())) {
                return getPumpkinAnimation(armorHP, baseHP);
            }
        }

        float HPRatio = (float) currentHP / baseHP;
        if (HPRatio >= 0.75) {
            return status.get("idle");
        } else if (HPRatio >= 0.50) {
            return status.get("damage1");
        } else if (HPRatio >= 0.25) {
            return status.get("damage2");
        }

        return status.get("damage3");

    }

    private HashMap<String, Boolean> wallNutVisibility(BattlePlant plant) {
        HashMap<String, Boolean> visibilities = new HashMap<>();
        double armorHP = plant.getArmorHP();
        int armorBaseHP = (int) plant.getPlantStats().getPlantFoodEffect().getOrDefault("armor", 0);

        if (armorHP > 0) {
            float HPRatio = (float) armorHP / armorBaseHP;
            HashMap<String, String> visibilityMap = (HashMap<String, String>) plant.getPlantStats().getAttributes().get("visibility");
            if (plant.getName().equals(PlantType.PUMPKIN.getName())) {
                return getPumpkinVisibilities(armorHP, armorBaseHP);
            } else {
                checkArmorVisibility(plant, visibilities);
            }

            if (HPRatio >= 0.67) {
                visibilities.put(visibilityMap.get("first"), true);
            } else if (HPRatio >= 0.33) {
                visibilities.put(visibilityMap.get("second"), true);
            } else if (HPRatio > 0) {
                visibilities.put(visibilityMap.get("third"), true);
            }
        }

        return visibilities;
    }

    private String getPumpkinAnimation(double armorHP, int baseHP) {
        float HPRatio = (float) armorHP / baseHP;
        String status = "idle_plantfood";

        if (HPRatio >= 0.75) {
            status += "";
        } else if (HPRatio >= 0.50) {
            status += "2";
        } else if (HPRatio >= 0.25) {
            status += "3";
        } else {
            status += "4";
        }

        return status;
    }

    private HashMap<String, Boolean> getPumpkinVisibilities(double armorHP, int baseHP) {
        HashMap<String, Boolean> visibilities = new HashMap<>();
        float HPRatio = (float) armorHP / baseHP;

        if (HPRatio >= 0.75) {
            visibilities.put("pumpkin_armor_01", true);
        } else if (HPRatio >= 0.50) {
            visibilities.put("pumpkin_armor_02", true);
        } else if (HPRatio >= 0.25) {
            visibilities.put("pumpkin_armor_03", true);
        } else {
            visibilities.put("pumpkin_armor_04", true);
        }

        return visibilities;
    }

    private String getMeleeAnimation(BattlePlant plant) {
        plant.checkMelee();
        return plant.getPlantStats().getStatus().get(plant.getStatus());
    }

    private String getSunProducerAnimation(BattlePlant plant) {
        plant.checkSunProducer();
        return plant.getPlantStats().getStatus().get(plant.getStatus());
    }

    private String getModifierAnimation(BattlePlant plant) {
        Map<String, String> status = plant.getPlantStats().getStatus();

        return status.get("idle");
    }

    private String getHomingAnimation(BattlePlant plant) {
        Map<String, String> status = plant.getPlantStats().getStatus();

        return status.get("idle");
    }

    private String getExplosiveAnimation(BattlePlant plant, float stateTime) {
        if (plant.getPlantStats().getAbilities().contains("explosionWithLifeSpan")) {
            return handleExplosionLifeSpan(plant, stateTime);
        }
        if (plant.getName().equals(PlantType.SQUASH.getName())) {
            return getSquashAnimation(plant);
        }

        return "idle";
    }

    private String handleExplosionLifeSpan(BattlePlant plant, float stateTime) {
        double attackTime = (double) plant.getPlantStats().getAttributes().get("attackTime");
        Map<String, String> status = plant.getPlantStats().getStatus();

        double timeDifference = stateTime - plant.getPlantTime();
        if (timeDifference >= attackTime) {
            return status.get("explosion");
        } else {
            return status.get("attack");
        }
    }

    private String getSquashAnimation(BattlePlant plant) {
        plant.checkSquash();
        return plant.getPlantStats().getStatus().get(plant.getStatus());
    }

    private String getShootingAnimation(BattlePlant plant, Map<String, String> status) {
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();
        for (int i = plantColumn; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, plantRow);

            if (!tile.getZombies().isEmpty()) {
                return status.get("action");
            }
        }

        return status.get("idle");
    }

    private void checkArmorVisibility(BattlePlant plant, HashMap<String, Boolean> visibilities) {
        if (plant.getName().equals(PlantType.WALL_NUT.getName())) {
            visibilities.put("_wallnut_armor_states", true);
        } else if (plant.getName().equals(PlantType.TALL_NUT.getName())) {
            visibilities.put("_tallnut_plantfood_armor", true);
        } else if (plant.getName().equals(PlantType.ENDURIAN.getName())) {
            visibilities.put("endurian_plantfood_armor", true);
        } else if (plant.getName().equals(PlantType.EXPLODE_O_NUT.getName())) {
            visibilities.put("_wallnut_armor_states", true);
        }
    }

    private String handleZombieDeath(Zombie zombie, Map<String, String> status) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(false);
            } else if (ability instanceof Eating) {
                ((Eating) ability).setActivated(false);
            } else if (ability instanceof StealingSun) {
                ((StealingSun) ability).setActivated(false);
            } else if (ability instanceof FatalDamage) {
                ((FatalDamage) ability).setActivated(false);
            }
        }
        double dieSpan;
        double difference = GAME.getTotalTimePassed() - zombie.getDieTime();


        if (zombie.isDeadByExplosion()) {
            dieSpan = 3.50;
        } else {
            dieSpan = (double) zombie.getZombieStats().getAttributes().get("dieSpan");
        }

        if (difference >= dieSpan / 2) {
            zombie.setAlive(false);
        }

        if (zombie.isDeadByExplosion()) {
            return "animation";
        }

        return status.get("die");
    }

    private String handleZombieThrowing(Zombie zombie, Map<String, String> status) {
        double difference = GAME.getTotalTimePassed() - zombie.getLastActionTime();
        double throwTime = (double) zombie.getZombieStats().getAttributes().get("throwTime");
        if ((difference + throwTime) >= 20) {
            for (Ability ability : zombie.getOriginalAbilities()) {
                if (ability instanceof Moving) {
                    ((Moving) ability).setActivated(false);
                } else if (ability instanceof Eating) {
                    ((Eating) ability).setActivated(false);
                }
            }

            return status.get("action");
        } else {
            if ((zombie.getName().equals("KING")) || (zombie.getName().equals("FISHERMAN"))) {
                return status.get("idle");
            } else {
                if ((zombie.getRival() != null) && (zombie.getRival().currentHP > 0)) {
                    for (Ability ability : zombie.getOriginalAbilities()) {
                        if (ability instanceof Eating) {
                            ((Eating) ability).setActivated(true);
                        }
                    }
                    return status.get("eat");
                } else {
                    for (Ability ability : zombie.getOriginalAbilities()) {
                        if (ability instanceof Moving) {
                            ((Moving) ability).setActivated(true);
                        }
                    }
                    return status.get("walk");
                }
            }
        }
    }

    private String handleGargantuarThrow(Zombie zombie, Map<String, String> status) {
        double throwTime = zombie.getZombieStats().getThrownTime();
        double difference = GAME.getTotalTimePassed() - throwTime;
        double firstThrow = (double) zombie.getZombieStats().getAttributes().get("firstThrow");
        double secondThrow = (double) zombie.getZombieStats().getAttributes().get("secondThrow");


        if (difference <= firstThrow) {
            return status.get("firstThrow");
        } else if (difference <= (firstThrow + secondThrow)) {
            return status.get("secondThrow");
        } else {
            zombie.getZombieStats().setFinished(true);

            Position impPosition = new Position(820, ((zombie.getRow() - 1) * 150) + 205);
            Zombie imp = ZombieFactory.createZombie("IMP", impPosition);
            GAME.getGameZombies().add(imp);

            if ((zombie.getRival() != null) && (zombie.getRival().currentHP > 0)) {
                for (Ability ability : zombie.getOriginalAbilities()) {
                    if (ability instanceof FatalDamage) {
                        ((FatalDamage) ability).setActivated(true);
                    }
                }
                return status.get("firstEat");
            } else {
                for (Ability ability : zombie.getOriginalAbilities()) {
                    if (ability instanceof Moving) {
                        ((Moving) ability).setActivated(true);
                    }
                }
                return status.get("walk");
            }

        }
    }

    private String handleGargantuar(Zombie zombie, Map<String, String> status) {
        for (Ability ability : zombie.getOriginalAbilities()) {

            if ((ability instanceof FatalDamage) && (((FatalDamage) ability).isActivated())) {
                double firstEat = (double) zombie.getZombieStats().getAttributes().get("firstEat");
                double secondEat = (double) zombie.getZombieStats().getAttributes().get("secondEat");

                float difference = (float) (GAME.getTotalTimePassed() - zombie.getLastActionTime());

                if (difference <= firstEat) {
                    return status.get("firstEat");
                } else if (difference <= (firstEat + secondEat)) {
                    return status.get("secondEat");
                } else {
                    for (Ability ability1 : zombie.getOriginalAbilities()) {
                        if (ability1 instanceof Moving) {
                            ((Moving) ability1).setActivated(true);
                            break;
                        }
                    }
                    return status.get("idle");
                }

            } else if ((ability instanceof Moving) && (((Moving) ability).isActivated())) {
                return status.get("walk");
            }
        }
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(true);
                break;
            }
        }
        return status.get("idle");
    }

    private String handleAllStar(Zombie zombie, Map<String, String> status) {
        for (Ability ability : zombie.getOriginalAbilities()) {

            if ((ability instanceof FatalDamage) && (((FatalDamage) ability).isActivated())) {
                double fatalTime = (double) zombie.getZombieStats().getAttributes().get("fatalTime");

                float difference = (float) (GAME.getTotalTimePassed() - zombie.getLastActionTime());

                if (difference <= fatalTime) {
                    return status.get("eat");
                } else {
                    return status.get("idle");
                }

            } else if ((ability instanceof Moving) && (((Moving) ability).isActivated())) {
                return status.get("walk");
            }
        }

        return status.get("idle");
    }
}

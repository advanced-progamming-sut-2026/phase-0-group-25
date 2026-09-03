package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.Armor;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.ArmorConfig;
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
        if (plant.checkOctopusAndIced()) {
            return status.get("idle");
        }

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
        if (zombie.isButtered() ||
            zombie.isFrozen()) {
            return status.get("idle");
        }

        if (zombie.getName().equals("GARGANTUAR")) {
            if ((zombie.isThrown()) && (!zombie.isFinished())) {
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
        if (zombie.getName().equals("PROSPECTOR") &&
            !zombie.isDynamiteOn()) {
            visibility.put("_dynamite_damage_states", true);
            visibility.put("coconut_fuse_spark_01", false);
            visibility.put("_dynamite_burning_03", false);
            visibility.put("_dynamite_burning_02", false);
        }

        if (zombie.getName().equals("EXPLORER") &&
            !zombie.isTorchOn()) {
            visibility.put("torch_fire_frame_01", false);
            visibility.put("torch_fire_fire_frame_01", false);
            visibility.put("torch_fire_frame_02", false);
            visibility.put("torch_fire_frame_03", false);
            visibility.put("torch_fire_frame_04", false);

        }

        if ((zombie.getName().equals("NEWSPAPER")) &&
            zombie.getActiveArmors().isEmpty()) {

            Armor armor = ArmorConfig.NEWSPAPER.createArmor();
            visibility.put(armor.getAnimations().get(0), false);
            visibility.put(armor.getAnimations().get(1), false);
            visibility.put(armor.getAnimations().get(2), false);

            return visibility;
        }

        for (Armor armor : zombie.getActiveArmors()) {
            String currentArmorStage = armor.getCurrentAnimation();
            if (armor.getType().equals("Shoulder Armor")) {
                visibility.put("zombie_shoulder_armor", true);
            } else if (armor.getType().equals("Crown")) {
                visibility.put("_zombie_armor_crown_states", true);
            } else if (armor.getType().equals("Newspaper")) {
                float HPRatio = (float) armor.getCurrentHP() / armor.getBaseHP();

                if (HPRatio >= 0.67) {
                    return visibility;
                } else if (HPRatio >= 0.33) {
                    visibility.put(armor.getAnimations().get(0), false);
                    return visibility;
                } else {
                    visibility.put(armor.getAnimations().get(0), false);
                    visibility.put(armor.getAnimations().get(1), false);
                    return visibility;
                }
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

        if (plant.getName().equals(PlantType.KIWIBEAST.getName())) {
            plant.checkMelee();
        }

        return status.get(plant.getStatus() + stage);
    }

    private String getChargePlantsAnimation(BattlePlant plant, Map<String, String> status, float stateTime) {
        if ((plant.getName().equals(PlantType.POTATO_MINE.getName())) ||
            (plant.getName().equals(PlantType.PRIMAL_POTATO_MINE.getName())) ||
            (plant.getName().equals(PlantType.ICEBERG_LETTUCE.getName()))) {
            if (plant.getCurrentHP() <= 0) {
                double difference = GAME.getTotalTimePassed() - plant.getDieTime();
                if (difference >= 1.00) {
                    plant.setAlive(false);
                }
                return status.get("explosion");
            }
        }

        if (plant.getName().equals(PlantType.CITRON.getName())) {
            return getCitronAnimation(plant, status);
        }

        int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
        if ((stateTime - plant.getPlantTime()) < armTime) {
            return status.get("disarmed");
        }

        return status.get("armed");
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

            if (plant.getName().equals(PlantType.SUN_BEAN.getName())) {
                return visibilities;
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

        if ((plant.getName().equals(PlantType.ICEBERG_LETTUCE.getName()))) {
            if (plant.getCurrentHP() <= 0) {
                double difference = GAME.getTotalTimePassed() - plant.getDieTime();
                if (difference >= 1.00) {
                    plant.setAlive(false);
                }
                return "attack";
            }
        }

        return "idle";
    }

    private String handleExplosionLifeSpan(BattlePlant plant, float stateTime) {
        double attackTime = (double) plant.getPlantStats().getAttributes().get("attackTime");
        Map<String, String> status = plant.getPlantStats().getStatus();

        double timeDifference = GAME.getTotalTimePassed() - plant.getPlantTime();

        System.out.println(timeDifference + "   " + attackTime);
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
            if (GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
                GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
                GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {
                if ((!tile.isArable()) && (tile.getHP() > 0)) {
                    return status.get("action");
                }
            }
            if (!plant.getPlantStats().getCategory().equals("Lobber")) {
                for (BattlePlant plant1 : tile.getPlants()) {
                    if (plant.checkOctopusAndIced()) {
                        return status.get("action");
                    }
                }
            }

            for (Zombie zombie : GAME.getGameZombies()) {
                if (zombie instanceof Zomboss) {
                    int secondRow = ((Zomboss) zombie).getCurrentSecondRow();
                    if (secondRow == plant.getRow()) {
                        return status.get("action");
                    }
                }
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
        double throwTime = zombie.getThrownTime();
        double difference = GAME.getTotalTimePassed() - throwTime;
        double firstThrow = (double) zombie.getZombieStats().getAttributes().get("firstThrow");
        double secondThrow = (double) zombie.getZombieStats().getAttributes().get("secondThrow");


        if (difference <= firstThrow) {
            return status.get("firstThrow");
        } else if (difference <= (firstThrow + secondThrow)) {
            return status.get("secondThrow");
        } else {
            zombie.setFinished(true);

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

    private boolean haveTarget(BattlePlant plant) {
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();
        for (int i = plantColumn; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, plantRow);

            if (!tile.getZombies().isEmpty()) {
                return true;
            }
            if (GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
                GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
                GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {
                if ((!tile.isArable()) && (tile.getHP() > 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    private String getCitronAnimation(
        BattlePlant plant,
        Map<String, String> status) {

        double now = GAME.getTotalTimePassed();






        if (plant.getStatus().equals("charge")) {

            double chargeTime =
                now - plant.getLastActionTime();

            if (chargeTime < 7.0) {
                return status.get("charge");
            }

            plant.setStatus("idle");
        }






        if (plant.getStatus().equals("idle")) {

            if (haveTarget(plant)) {
                plant.setStatus("attack");


                return status.get("attack");
            }

            return status.get("idle");
        }






        if (plant.getStatus().equals("attack")) {


            if (!isCitronAttackFinished(plant)) {
                return status.get("attack");
            }


            plant.setLastActionTime(now);
            plant.setStatus("charge");

            return status.get("charge");
        }


        return status.get("idle");
    }

    private boolean isCitronAttackFinished(
        BattlePlant plant) {

        return plant.getAnimationState()
            .getStateTime() >= 1.0f;
    }
}

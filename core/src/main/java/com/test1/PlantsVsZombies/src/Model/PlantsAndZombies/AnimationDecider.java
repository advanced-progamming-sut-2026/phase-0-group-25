package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.Ability;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.Eating;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.Moving;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors.Armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimationDecider {
    //private GamePlay GAME = GamePlayMenu.getGamePlay();

    public String plantDecider(BattlePlant plant, float stateTime) {
        Map<String, String> status = plant.getPlantStats().getStatus();

        if (plant.getPlantStats().getCategory().equals(PlantCategory.MINT.name())) {
            return getMintAnimation(plant, stateTime);
        }

        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            return getWallNutAnimation(plant);
        }

        if (plant.getPlantStats().getTags().contains("wramp_up")) {
            return getWrampUpPlantsAnimation(plant, status, stateTime);
        } else {
            if (isTimeForAction(plant, stateTime)) {
                return status.get("action");
            }
            return status.get("idle");
        }
    }

    public HashMap<String, Boolean> plantVisibilities(BattlePlant plant) {
        HashMap<String, Boolean> visibilities = new HashMap<>();
        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            visibilities = wallNutVisibility(plant);
        }

        return visibilities;
    }

    public String zombieDecider(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if ((ability instanceof Moving) && (((Moving) ability).isActivated())) {
                return "walk";
            } else if ((ability instanceof Eating) && (((Eating) ability).isActivated())) {
                return "eat";
            }
        }
        return "idle";
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

        double differenceTime = plant.getPlantTime();

        if (differenceTime >= growthTimeStages.get(1)) {
            if (isTimeForAction(plant, stateTime)) {
                return status.get("action3");
            }
            return status.get("idle3");
        } else if (differenceTime >= growthTimeStages.get(0)) {
            if (isTimeForAction(plant, stateTime)) {
                return status.get("action2");
            }
            return status.get("idle2");
        } else {
            if (isTimeForAction(plant, stateTime)) {
                return status.get("action1");
            }
            return status.get("idle1");
        }
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
        double currentHP = plant.getPlantStats().getBaseHP();
        Map<String, String> status = plant.getPlantStats().getStatus();

        int armorHP = (int) plant.getPlantStats().getAttributes().getOrDefault("armorHP", 0);
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
        int armorHP = (int) plant.getPlantStats().getAttributes().getOrDefault("armorHP", 0);
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
            } else {
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

}

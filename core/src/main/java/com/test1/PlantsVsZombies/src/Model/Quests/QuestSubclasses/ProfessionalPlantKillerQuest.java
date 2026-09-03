package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.Random;

public class ProfessionalPlantKillerQuest extends Quest {
    private final int required = 10;
    private PlantType targetPlant;

    public ProfessionalPlantKillerQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_KNOCKBACK");
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        PlantType[] attackers = {PlantType.PEASHOOTER, PlantType.REPEATER, PlantType.CABBAGE_PULT, PlantType.MELON_PULT, PlantType.CACTUS};
        this.targetPlant = attackers[new Random().nextInt(attackers.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Professional Plant Killer";
        this.description = "Kill 10 zombies using only " + (targetPlant != null ? targetPlant.getName() : "Unknown") + ".";
        this.reward = new Reward(RewardType.UNLOCK_PLANT, 1);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof ZombieKilledEvent e) {
            if (targetPlant != null && targetPlant.getName().equalsIgnoreCase(e.getPlantName())) {
                incrementProgress(1);
            }
        }
    }

    @Override
    public String getQuestVariable() {
        return targetPlant.name();
    }

    @Override
    public void setQuestVariable(String v) {
        this.targetPlant = PlantType.valueOf(v);
        updateDetails();
    }
}

package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

public class OnlyCactusQuest extends Quest {
    private final int required = 10;

    public OnlyCactusQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        setIcon("IMAGE_UI_HUD_LOD_LOD_CACTUS");
        this.name = "Only Cactus";
        this.description = "Kill 10 zombies using CACTUS.";
        this.reward = new Reward(RewardType.GEMS, 20);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof ZombieKilledEvent) {
            ZombieKilledEvent e = (ZombieKilledEvent) event;
            if ("CACTUS".equalsIgnoreCase(e.getPlantName())) {
                incrementProgress(1);
            }
        }
    }
}

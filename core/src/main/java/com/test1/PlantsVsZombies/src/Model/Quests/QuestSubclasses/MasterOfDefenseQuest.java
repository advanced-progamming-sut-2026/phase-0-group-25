package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

public class MasterOfDefenseQuest extends Quest {
    private final int required = 1;

    public MasterOfDefenseQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_ARENA");
        this.name = "Master of Defense";
        this.description = "Finish a level with exactly zero sun left.";
        this.reward = new Reward(RewardType.GEMS, 200);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof LevelWonEvent && ((LevelWonEvent) event).getFinalSun() == 0) incrementProgress(1);
    }
}

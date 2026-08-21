package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ExplosiveUsedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

public class ProfessionalDemolisherQuest extends Quest {
    private final int required = 3;

    public ProfessionalDemolisherQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_EXPANSIONLEVEL");
        this.name = "Professional Demolisher";
        this.description = "Use 3 explosive plants in a single level.";
        this.reward = new Reward(RewardType.COINS, 100);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof ExplosiveUsedEvent) incrementProgress(1);
    }
}

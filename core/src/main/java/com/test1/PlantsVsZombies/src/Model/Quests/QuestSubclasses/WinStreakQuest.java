package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

public class WinStreakQuest extends Quest {
    private final int required = 5;

    public WinStreakQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_LOTD");
        this.name = "Win Streak";
        this.description = "Win 5 levels in a row on the highest difficulty.";
        this.reward = new Reward(RewardType.COINS, 5000);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof LevelWonEvent && ((LevelWonEvent) event).getDifficulty() == 5) incrementProgress(1);
    }
}

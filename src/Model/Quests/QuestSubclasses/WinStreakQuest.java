package src.Model.Quests.QuestSubclasses;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Enums.RewardType;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

public class WinStreakQuest extends Quest {
    private final int required = 5;

    public WinStreakQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
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
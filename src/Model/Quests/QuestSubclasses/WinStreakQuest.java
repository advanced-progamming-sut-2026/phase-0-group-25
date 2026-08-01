package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class WinStreakQuest extends Quest {
    private final int required = 5;

    public WinStreakQuest(String id, QuestCategory category, QuestPriority priority,  boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        this.name = "Win Streak";
        this.description = "Win 5 levels in a row on the highest difficulty.";
        this.reward = new Reward(RewardType.COINS, 5000);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event event) {
        if (event instanceof LevelWonEvent && ((LevelWonEvent) event).getDifficulty() == 5) incrementProgress(1);
    }
}
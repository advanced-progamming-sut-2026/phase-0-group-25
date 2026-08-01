package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class CloudyDayQuest extends Quest {
    private final int required = 1;

    public CloudyDayQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority,  dailyReset, page);
        this.name = "Cloudy Day";
        this.description = "Win a level using exactly 3 sun-producing plants.";
        this.reward = new Reward(RewardType.GEMS, 10);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event event) {
        if (event instanceof LevelWonEvent && ((LevelWonEvent) event).getSunProducersCount() == 3) incrementProgress(1);
    }
}
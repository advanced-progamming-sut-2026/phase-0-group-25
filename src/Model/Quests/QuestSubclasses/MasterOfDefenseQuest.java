package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class MasterOfDefenseQuest extends Quest {
    private final int required = 1;

    public MasterOfDefenseQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        this.name = "Master of Defense";
        this.description = "Finish a level with exactly zero sun left.";
        this.reward = new Reward(RewardType.GEMS, 200);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event event) {
        if (event instanceof LevelWonEvent && ((LevelWonEvent) event).getFinalSun() == 0) incrementProgress(1);
    }
}
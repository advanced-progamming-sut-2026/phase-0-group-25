package src.Model.Quests.QuestSubclasses;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Enums.RewardType;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

public class MasterOfDefenseQuest extends Quest {
    private final int required = 1;

    public MasterOfDefenseQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
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
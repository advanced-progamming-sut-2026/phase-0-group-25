package src.Model.Quests.QuestSubclasses;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Enums.RewardType;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.Random;

public class OneColumnLessQuest extends Quest {
    private int targetColumn;

    public OneColumnLessQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.targetColumn = new Random().nextInt(9) + 1;
        updateDetails();
    }

    private void updateDetails() {
        this.name = "One Column Less (" + targetColumn + ")";
        this.description = "Win a level without planting anything in column " + targetColumn + ".";
        this.reward = new Reward(RewardType.GEMS, 10);
    }

    @Override
    public int getRequiredCount() {
        return 1;
    }

    @Override
    public void check(Event e) {
        if (e instanceof LevelWonEvent) {
            boolean[] emptyCols = ((LevelWonEvent) e).getEmptyColumns();
            if (emptyCols != null && emptyCols.length > targetColumn && emptyCols[targetColumn]) incrementProgress(1);
        }
    }

    @Override
    public String getQuestVariable() {
        return String.valueOf(targetColumn);
    }

    @Override
    public void setQuestVariable(String v) {
        this.targetColumn = Integer.parseInt(v);
        updateDetails();
    }
}
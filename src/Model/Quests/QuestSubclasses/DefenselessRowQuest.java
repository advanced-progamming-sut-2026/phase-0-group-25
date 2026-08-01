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

public class DefenselessRowQuest extends Quest {
    private int targetRow;

    public DefenselessRowQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.targetRow = new Random().nextInt(5) + 1;
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Defenseless Row (" + targetRow + ")";
        this.description = "Win a level without planting anything in row " + targetRow + ".";
        this.reward = new Reward(RewardType.GEMS, 20);
    }

    @Override
    public int getRequiredCount() {
        return 1;
    }

    @Override
    public void check(Event e) {
        if (e instanceof LevelWonEvent) {
            boolean[] emptyRows = ((LevelWonEvent) e).getEmptyRows();
            if (emptyRows != null && emptyRows.length > targetRow && emptyRows[targetRow]) incrementProgress(1);
        }
    }

    @Override
    public String getQuestVariable() {
        return String.valueOf(targetRow);
    }

    @Override
    public void setQuestVariable(String v) {
        this.targetRow = Integer.parseInt(v);
        updateDetails();
    }
}
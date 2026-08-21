package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.Random;

public class DefenselessRowQuest extends Quest {
    private int targetRow;

    public DefenselessRowQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_ARENA");
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

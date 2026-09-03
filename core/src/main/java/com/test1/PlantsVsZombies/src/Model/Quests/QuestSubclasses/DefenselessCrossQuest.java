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

public class DefenselessCrossQuest extends Quest {
    private int target;

    public DefenselessCrossQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_ARENA");
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.target = new Random().nextInt(5) + 1;
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Defenseless Cross (" + target + ")";
        this.description = "Win a level keeping both column " + target + " and row " + target + " completely empty.";
        this.reward = new Reward(RewardType.GEMS, 25);
    }

    @Override
    public int getRequiredCount() {
        return 1;
    }

    @Override
    public void check(Event e) {
        if (e instanceof LevelWonEvent) {
            boolean[] emptyCols = ((LevelWonEvent) e).getEmptyColumns();
            boolean[] emptyRows = ((LevelWonEvent) e).getEmptyRows();
            if (emptyCols != null && emptyRows != null && emptyCols.length > target && emptyRows.length > target && emptyCols[target - 1] && emptyRows[target - 1]) {
                incrementProgress(1);
            }
        }
    }

    @Override
    public String getQuestVariable() {
        return String.valueOf(target);
    }

    @Override
    public void setQuestVariable(String v) {
        this.target = Integer.parseInt(v);
        updateDetails();
    }
}

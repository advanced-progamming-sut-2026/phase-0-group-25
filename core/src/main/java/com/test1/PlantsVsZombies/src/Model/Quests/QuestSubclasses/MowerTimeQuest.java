package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.MowerTriggeredEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.Random;

public class MowerTimeQuest extends Quest {
    private static final int[] TIERS = {10, 20, 30, 40, 50};
    private int required;

    public MowerTimeQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.required = TIERS[new Random().nextInt(TIERS.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Mower Time";
        this.description = "Kill at least " + required + " zombies using mowers.";
        this.reward = new Reward(RewardType.GEMS, required);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event e) {
        if (e instanceof MowerTriggeredEvent) incrementProgress(((MowerTriggeredEvent) e).getKilledCount());
    }

    @Override
    public String getQuestVariable() {
        return String.valueOf(required);
    }

    @Override
    public void setQuestVariable(String v) {
        this.required = Integer.parseInt(v);
        updateDetails();
    }
}

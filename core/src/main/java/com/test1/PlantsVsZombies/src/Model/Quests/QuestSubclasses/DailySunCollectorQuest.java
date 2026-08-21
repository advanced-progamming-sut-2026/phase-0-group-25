package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.SunCollectedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.Random;

public class DailySunCollectorQuest extends Quest {
    private static final int[] TIERS = {3000, 4000, 5000};
    private int required;

    public DailySunCollectorQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_PLANT");
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.required = TIERS[new Random().nextInt(TIERS.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Daily Sun Collector";
        this.description = "Collect " + required + " suns in a single day.";
        this.reward = new Reward(RewardType.COINS, required / 100);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event e) {
        if (e instanceof SunCollectedEvent) incrementProgress(((SunCollectedEvent) e).getAmount());
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

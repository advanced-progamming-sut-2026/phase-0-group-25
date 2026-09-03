package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ExplosiveUsedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelStartedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.HashSet;

public class ProfessionalDemolisherQuest extends Quest {
    private final int required = 3;
    private final HashSet<String> explosivesUsed;

    public ProfessionalDemolisherQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        explosivesUsed = new HashSet<>();
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_EXPANSIONLEVEL");
        this.name = "Professional Demolisher";
        this.description = "Use 3 explosive plants in a single level.";
        this.reward = new Reward(RewardType.COINS, 100);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event event) {
        if (event instanceof ExplosiveUsedEvent) {
            if (!explosivesUsed.contains(((ExplosiveUsedEvent) event).getPlantName())) {
                incrementProgress(1);
                explosivesUsed.add(((ExplosiveUsedEvent) event).getPlantName());
            }
        } else if (event instanceof LevelStartedEvent && !isCompleted()) {
            setCurrentProgress(0);
            explosivesUsed.clear();
        }
    }
}

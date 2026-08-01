package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;

public class OnlyCactusQuest extends Quest {
    private final int required = 10;

    public OnlyCactusQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        this.name = "Only Cactus";
        this.description = "Kill 10 zombies using CACTUS.";
        this.reward = new Reward(RewardType.GEMS, 100);
    }

    @Override
    public int getRequiredCount() { return required; }

    @Override
    public void check(Event event) {
        if (event instanceof ZombieKilledEvent) {
            ZombieKilledEvent e = (ZombieKilledEvent) event;
            if ("CACTUS".equalsIgnoreCase(e.getPlantName())) {
                incrementProgress(1);
            }
        }
    }
}
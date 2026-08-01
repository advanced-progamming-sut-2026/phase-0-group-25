package src.Model.Quests.QuestSubclasses;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Enums.RewardType;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.ZombieKilledEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

public class OnlyCactusQuest extends Quest {
    private final int required = 10;

    public OnlyCactusQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        this.name = "Only Cactus";
        this.description = "Kill 10 zombies using CACTUS.";
        this.reward = new Reward(RewardType.GEMS, 100);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

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
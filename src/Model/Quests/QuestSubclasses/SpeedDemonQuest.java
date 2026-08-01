package src.Model.Quests.QuestSubclasses;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Enums.RewardType;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.ZombieKilledEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

public class SpeedDemonQuest extends Quest {
    public SpeedDemonQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        this.name = "Speed Demon";
        this.description = "Kill 10 zombies in less than 30 seconds after the first wave.";
        this.reward = new Reward(RewardType.COINS, 500);
    }

    @Override
    public int getRequiredCount() {
        return 10;
    }

    @Override
    public void check(Event e) {
        if (e instanceof ZombieKilledEvent && ((ZombieKilledEvent) e).getTimeSinceFirstWave() <= 30.0) {
            incrementProgress(1);
        }
    }
}
package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Enums.QuestPriority;
import com.test1.PlantsVsZombies.src.Enums.RewardType;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

public class SpeedDemonQuest extends Quest {
    public SpeedDemonQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_ASH");
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

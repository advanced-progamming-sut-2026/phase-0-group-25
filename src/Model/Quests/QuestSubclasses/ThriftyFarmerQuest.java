package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.Random;

public class ThriftyFarmerQuest extends Quest {
    private int maxLoss;

    public ThriftyFarmerQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        this.maxLoss = new Random().nextInt(6);
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Thrifty Farmer";
        this.description = "Win a level without losing more than " + maxLoss + " plants.";
        this.reward = new Reward(RewardType.SEED_PACKETS, 20 - maxLoss, PlantType.SUNFLOWER);
    }

    @Override
    public int getRequiredCount() {
        return 1;
    }

    @Override
    public void check(Event e) {
        if (e instanceof LevelWonEvent && ((LevelWonEvent) e).getLostPlants() <= maxLoss) {
            incrementProgress(1);
        }
    }

    @Override
    public String getQuestVariable() {
        return String.valueOf(maxLoss);
    }

    @Override
    public void setQuestVariable(String v) {
        this.maxLoss = Integer.parseInt(v);
        updateDetails();
    }
}
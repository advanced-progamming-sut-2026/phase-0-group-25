package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.Reward;

import java.util.Random;

public class ChapterHunterQuest extends Quest {
    private final int required = 50;
    private ChapterType targetChapter;

    public ChapterHunterQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg);
        setIcon("IMAGE_UI_QUESTS_QUESTICONS_ZOMBIE");
        randomizeVariable();
    }

    @Override
    public void randomizeVariable() {
        ChapterType[] chapters = ChapterType.values();
        this.targetChapter = chapters[1 + new Random().nextInt(chapters.length - 1)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Hunter: " + targetChapter.getName();
        this.description = "Defeat 50 zombies from the " + targetChapter.getName() + " chapter.";
        this.reward = new Reward(RewardType.SEED_PACKETS, 10, PlantType.SUNFLOWER);
    }

    @Override
    public int getRequiredCount() {
        return required;
    }

    @Override
    public void check(Event e) {
        if (e instanceof ZombieKilledEvent && ((ZombieKilledEvent) e).getChapter() == targetChapter) {
            incrementProgress(1);
        }
    }

    @Override
    public String getQuestVariable() {
        return targetChapter.name();
    }

    @Override
    public void setQuestVariable(String v) {
        this.targetChapter = ChapterType.valueOf(v);
        updateDetails();
    }
}

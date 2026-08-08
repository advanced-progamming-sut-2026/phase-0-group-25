package com.test1.PlantsVsZombies.src.Model.Quests;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.RewardType;

public class Reward {
    private RewardType type;
    private int amount;
    private PlantType plantType;
    private ChapterType chapterType;

    public Reward(RewardType type, int amount) {
        this(type, amount, null, null);
    }

    public Reward(RewardType type, int amount, PlantType plantType) {
        this(type, amount, plantType, null);
    }

    public Reward(RewardType type, int amount, ChapterType chapterType) {
        this(type, amount, null, chapterType);
    }

    private Reward(RewardType type, int amount, PlantType plantType, ChapterType chapterType) {
        this.type = type;
        this.amount = amount;
        this.plantType = plantType;
        this.chapterType = chapterType;
    }

    public RewardType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }
}

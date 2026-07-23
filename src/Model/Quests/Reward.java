package src.Model.Quests;

import src.Enums.PlantType;
import src.Enums.RewardType;
import src.Enums.ChapterType;

public class Reward {
    private RewardType type;
    private int amount;
    private PlantType plantType;    // for SEED_PACKETS / UNLOCK_PLANT
    private ChapterType chapterType; // for UNLOCK_CHAPTER

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

    // Getters
    public RewardType getType() { return type; }
    public int getAmount() { return amount; }
    public PlantType getPlantType() { return plantType; }
    public ChapterType getChapterType() { return chapterType; }
}
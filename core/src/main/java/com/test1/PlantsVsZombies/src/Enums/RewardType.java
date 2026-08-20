package com.test1.PlantsVsZombies.src.Enums;

public enum RewardType {
    COINS("IMAGE_UI_QUESTS_EPIC_REWARD_COINS"),
    GEMS("IMAGE_UI_QUESTS_EPIC_REWARD_GEMS"),
    SEED_PACKETS("IMAGE_UI_QUESTS_QUESTICONS_PREMIUMSEEDS2"),
    UNLOCK_PLANT("IMAGE_UI_QUESTS_QUESTICONS_RENT_A_PLANT"),
    UNLOCK_CHAPTER(""),
    PLANT_FOOD(""),
    POTS("");


    private final String id;
    RewardType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

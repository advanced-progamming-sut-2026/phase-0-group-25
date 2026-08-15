package com.test1.PlantsVsZombies.src.Enums;

public enum LevelIslandAsset {

    ANCIENT_EGYPT(
        ChapterType.ANCIENT_EGYPT,
        "IMAGE_WORLDMAP_EGYPT_ISLAND4"
    ),

    DARK_AGE(
        ChapterType.DARK_AGE,
        "IMAGE_WORLDMAP_DARK_ISLAND6"
    ),

    FROSTBITE_CAVES(
        ChapterType.FROSTBITE_CAVES,
        "IMAGE_WORLDMAP_ICEAGE_ISLAND24"
    ),

    BIG_WAVE_BEACH(
        ChapterType.BIG_WAVE_BEACH,
        "IMAGE_WORLDMAP_BEACH_ISLAND24"
    );

    private final ChapterType chapterType;
    private final String assetId;

    LevelIslandAsset(ChapterType chapterType, String assetId) {
        this.chapterType = chapterType;
        this.assetId = assetId;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public String getAssetId() {
        return assetId;
    }

    public static String getAssetId(ChapterType chapterType) {
        for (LevelIslandAsset asset : values()) {
            if (asset.chapterType == chapterType) {
                return asset.assetId;
            }
        }

        return null;
    }
}

package com.test1.PlantsVsZombies.src.Enums;

/**
 * The big chapter-icon asset shown for each chapter (e.g. on the Choose
 * Chapter screen, and reused as the chapter icon on the level-select
 * GameScreen). Kept in one place, mirroring LevelIslandAsset, so both
 * screens stay in sync instead of duplicating the same id strings.
 */
public enum ChapterIslandAsset {

    ANCIENT_EGYPT(
        ChapterType.ANCIENT_EGYPT,
        "IMAGE_WORLDMAP_EGYPT_ISLAND3"
    ),

    DARK_AGE(
        ChapterType.DARK_AGE,
        "IMAGE_WORLDMAP_ZOMBOSS_NODE_DARK_ZOMBOSS_NODE_DARK_905X1096"
    ),

    FROSTBITE_CAVES(
        ChapterType.FROSTBITE_CAVES,
        "IMAGE_WORLDMAP_ICEAGE_ANIM3_ANIM3_1307X1318"
    ),

    BIG_WAVE_BEACH(
        ChapterType.BIG_WAVE_BEACH,
        "IMAGE_WORLDMAP_ZOMBOSS_NODE_BEACH_ZOMBOSS_NODE_BEACH_905X1096"
    );

    private final ChapterType chapterType;
    private final String assetId;

    ChapterIslandAsset(ChapterType chapterType, String assetId) {
        this.chapterType = chapterType;
        this.assetId = assetId;
    }

    public static String getAssetId(ChapterType chapterType) {
        for (ChapterIslandAsset asset : values()) {
            if (asset.chapterType == chapterType) {
                return asset.assetId;
            }
        }
        return null;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public String getAssetId() {
        return assetId;
    }
}

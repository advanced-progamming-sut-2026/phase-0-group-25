package com.test1.PlantsVsZombies.src.Enums;

public enum ChapterType {
    MINI_GAME("mini game", 0),
    ANCIENT_EGYPT("ancient egypt", 1),
    DARK_AGE("dark age", 2),
    FROSTBITE_CAVES("frostbite caves", 3),
    BIG_WAVE_BEACH("big wave beach", 4);

    /**
     * Single source of truth for how many levels each (non-minigame) chapter has.
     * Used anywhere level counts, "locked level" checks, or progress ratios
     * (e.g. "4/4") need this number instead of a hardcoded literal.
     */
    public static final int LEVELS_PER_CHAPTER = 4;

    private final String name;
    private int chapterNumber;

    ChapterType(String name, int chapterNumber) {
        this.name = name;
        this.chapterNumber = chapterNumber;
    }

    public static ChapterType getByName(String name) {
        for (ChapterType chapterType : values()) {
            if (chapterType.name.equalsIgnoreCase(name)) {
                return chapterType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
}

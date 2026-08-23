package com.test1.PlantsVsZombies.src.Model.Quests.Events;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;


public class LevelStartedEvent extends Event {
    private final ChapterType chapterType;
    private final int level;
    private final int difficulty;

    public LevelStartedEvent(ChapterType chapterType, int level, int difficulty) {
        this.chapterType = chapterType;
        this.level = level;
        this.difficulty = difficulty;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public int getLevel() {
        return level;
    }

    public int getDifficulty() {
        return difficulty;
    }
}

package src.Model.Quests.Events;

import src.Enums.ChapterType;

public class ZombieKilledEvent extends Event {
    private final ChapterType chapter;
    private final double timeSinceFirstWave;

    public ZombieKilledEvent(ChapterType chapter, double timeSinceFirstWave) {
        this.chapter = chapter;
        this.timeSinceFirstWave = timeSinceFirstWave;
    }

    public ChapterType getChapter() { return chapter; }
    public double getTimeSinceFirstWave() { return timeSinceFirstWave; }
}
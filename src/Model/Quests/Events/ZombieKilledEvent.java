package src.Model.Quests.Events;

import src.Enums.ChapterType;

public class ZombieKilledEvent extends Event {
    private final ChapterType chapter;
    private final double timeSinceFirstWave;
    private final String plantName;

    public ZombieKilledEvent(ChapterType chapter, double timeSinceFirstWave, String plantName) {
        this.chapter = chapter;
        this.plantName = plantName;
        this.timeSinceFirstWave = timeSinceFirstWave;
    }

    public String getPlantName() { return plantName; }
    public ChapterType getChapter() { return chapter; }
    public double getTimeSinceFirstWave() { return timeSinceFirstWave; }
}
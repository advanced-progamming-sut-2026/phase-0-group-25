package src.Model.Quests.Events;

import src.Enums.ChapterType;

public class ZombieKilledEvent extends Event {
    private final ChapterType chapter;
    private final String killerPlantName;
    private final int column;
    private final int row;
    private final boolean mowerUsedInRow;
    private final double timeSinceFirstWave;

    public ZombieKilledEvent(ChapterType chapter, String killerPlantName,
                             int column, int row, boolean mowerUsedInRow,
                             double timeSinceFirstWave) {
        this.chapter = chapter;
        this.killerPlantName = killerPlantName;
        this.column = column;
        this.row = row;
        this.mowerUsedInRow = mowerUsedInRow;
        this.timeSinceFirstWave = timeSinceFirstWave;
    }

    public ChapterType getChapter() { return chapter; }
    public String getKillerPlantName() { return killerPlantName; }
    public int getColumn() { return column; }
    public int getRow() { return row; }
    public boolean isMowerUsedInRow() { return mowerUsedInRow; }
    public double getTimeSinceFirstWave() { return timeSinceFirstWave; }
}
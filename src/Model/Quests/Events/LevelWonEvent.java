package src.Model.Quests.Events;
import src.Enums.ChapterType;

public class LevelWonEvent extends Event {
    private final int lostPlants;
    private final int finalSun;
    private final int difficulty;
    private final int sunProducersCount;
    private final boolean[] emptyColumns;
    private final boolean[] emptyRows;

    public LevelWonEvent(int lostPlants, int finalSun, int difficulty, int sunProducersCount, boolean[] emptyColumns, boolean[] emptyRows) {
        this.lostPlants = lostPlants;
        this.finalSun = finalSun;
        this.difficulty = difficulty;
        this.sunProducersCount = sunProducersCount;
        this.emptyColumns = emptyColumns.clone();
        this.emptyRows = emptyRows.clone();
    }

    public int getLostPlants() { return lostPlants; }
    public int getFinalSun() { return finalSun; }
    public int getDifficulty() { return difficulty; }
    public int getSunProducersCount() { return sunProducersCount; }
    public boolean[] getEmptyColumns() { return emptyColumns.clone(); }
    public boolean[] getEmptyRows() { return emptyRows.clone(); }
}
package src.Model.Quests.Events;

import src.Enums.ChapterType;
import java.util.Set;

public class LevelWonEvent extends Event {
    private final ChapterType chapter;
    private final int levelNumber;
    private final int lostPlants;
    private final int finalSun;
    private final boolean isSymmetric;
    private final boolean onlyMushrooms;
    private final int difficulty;
    private final int sunProducersCount;
    private final boolean[] emptyColumns;
    private final boolean[] emptyRows;
    private final Set<String> usedPlantFamilies;
    private final Set<String> killingFamilies;

    public LevelWonEvent(ChapterType chapter, int levelNumber,
                         int lostPlants, int finalSun,
                         boolean isSymmetric, boolean onlyMushrooms,
                         int difficulty, int sunProducersCount,
                         boolean[] emptyColumns, boolean[] emptyRows,
                         Set<String> usedPlantFamilies, Set<String> killingFamilies) {
        this.chapter = chapter;
        this.levelNumber = levelNumber;
        this.lostPlants = lostPlants;
        this.finalSun = finalSun;
        this.isSymmetric = isSymmetric;
        this.onlyMushrooms = onlyMushrooms;
        this.difficulty = difficulty;
        this.sunProducersCount = sunProducersCount;
        this.emptyColumns = emptyColumns.clone();
        this.emptyRows = emptyRows.clone();
        this.usedPlantFamilies = usedPlantFamilies;
        this.killingFamilies = killingFamilies;
    }

    public ChapterType getChapter() { return chapter; }
    public int getLevelNumber() { return levelNumber; }
    public int getLostPlants() { return lostPlants; }
    public int getFinalSun() { return finalSun; }
    public boolean isSymmetric() { return isSymmetric; }
    public boolean isOnlyMushrooms() { return onlyMushrooms; }
    public int getDifficulty() { return difficulty; }
    public int getSunProducersCount() { return sunProducersCount; }
    public boolean[] getEmptyColumns() { return emptyColumns.clone(); }
    public boolean[] getEmptyRows() { return emptyRows.clone(); }
    public Set<String> getUsedPlantFamilies() { return usedPlantFamilies; }
    public Set<String> getKillingFamilies() { return killingFamilies; }
}
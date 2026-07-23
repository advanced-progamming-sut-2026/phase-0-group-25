package src.Model.ChaptersAndLevels;

import src.Enums.ChapterType;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.User.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Chapter {
    private ChapterType chapterType;
    private ArrayList<Level> levels;

    public Chapter(ChapterType chapterType) {
        this.chapterType = chapterType;
        this.levels = new ArrayList<>();
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void addLevel(Level level) {
        this.levels.add(level);
    }

    public Level getLevel(int levelNumber) {
        for (Level level : levels) {
            if (level.getLevelNumber() == levelNumber) {
                return level;
            }
        }
        return null;
    }

    public GamePlay makeGame(int levelNumber, int difficulty, User user, ArrayList<String> plants, ArrayList<String > zombies
            , Set<String > boosted) {
        Level level = getLevel(levelNumber);
        if (level != null) {
            GamePlay gamePlay = level.createGame(chapterType, difficulty, user, plants, zombies, boosted);
            if (gamePlay != null) {
                gamePlay.setLevelObject(level);
            }
            return gamePlay;
        }
        return null;
    }
}
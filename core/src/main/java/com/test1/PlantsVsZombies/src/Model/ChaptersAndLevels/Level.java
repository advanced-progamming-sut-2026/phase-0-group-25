package com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.GamePlayType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlayFactory;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.ArrayList;
import java.util.Set;

public class Level {

    private ChapterType chapterType;
    private final int levelNumber;
    private final GamePlayType gamePlayType;
    private final ArrayList<PlantType> plantRewards;

    public Level(ChapterType chapterType, int levelNumber, GamePlayType gamePlayType) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
        this.gamePlayType = gamePlayType;
        this.plantRewards = new ArrayList<>();
    }

    public Level(int levelNumber, GamePlayType gamePlayType) {
        this(null, levelNumber, gamePlayType);
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public void setChapterType(ChapterType chapterType) {
        this.chapterType = chapterType;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public GamePlayType getGamePlayType() {
        return gamePlayType;
    }

    public ArrayList<PlantType> getPlantRewards() {
        return plantRewards;
    }

    public void addPlantReward(PlantType plantType) {
        if (!plantRewards.contains(plantType)) {
            plantRewards.add(plantType);
        }
    }

    public GamePlay createGame(ChapterType chapterType, int difficulty, User user, ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        this.chapterType = chapterType;
        GamePlay gamePlay = GamePlayFactory.createGamePlay(gamePlayType, chapterType, levelNumber, difficulty, user, plants, zombies, boosted);
        if (gamePlay != null) {
            gamePlay.setLevelObject(this);
        }
        return gamePlay;
    }

    public void completeLevel() {
        UsersManager.getInstance().handleLevelWin(this.chapterType, this.levelNumber, this.plantRewards);
    }
}

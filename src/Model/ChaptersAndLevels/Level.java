// file: src/Model/ChaptersAndLevels/Level.java
package src.Model.ChaptersAndLevels;

import src.Enums.ChapterType;
import src.Enums.GamePlayType;
import src.Enums.PlantType;
import src.Enums.ZombieType;
import src.Model.GamePlayType.GamePlay;
import src.Model.GamePlayType.GamePlayFactory;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.User.User;
import src.Model.User.UsersManager;

import java.util.ArrayList;

public class Level {
    private ChapterType chapterType;
    private int levelNumber;
    private GamePlayType gamePlayType;
    private ArrayList<PlantType> plantRewards;
    private ArrayList<ZombieType> zombieRewards;

    public Level(ChapterType chapterType, int levelNumber, GamePlayType gamePlayType) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
        this.gamePlayType = gamePlayType;
        this.plantRewards = new ArrayList<>();
        this.zombieRewards = new ArrayList<>();
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

    public ArrayList<ZombieType> getZombieRewards() {
        return zombieRewards;
    }

    public void addZombieReward(ZombieType zombieType) {
        if (!zombieRewards.contains(zombieType)) {
            zombieRewards.add(zombieType);
        }
    }

    public GamePlay createGame(ChapterType chapterType, int difficulty, User user, ArrayList<String> plants, ArrayList<String > zombies) {
        this.chapterType = chapterType;
        GamePlay gamePlay = GamePlayFactory.createGamePlay(gamePlayType, chapterType, levelNumber, difficulty, user, plants, zombies);
        if (gamePlay != null) {
            gamePlay.setLevelObject(this);
        }
        return gamePlay;
    }

    public void completeLevel() {
        UsersManager.getInstance().handleLevelWin(this.chapterType, this.levelNumber, this.plantRewards, this.zombieRewards);
    }
}
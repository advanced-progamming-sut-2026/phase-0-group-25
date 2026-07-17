package Model.ChaptersAndLevels;

import Model.GamePlayType.GamePlay;

public class Level {
    private GamePlay gameToPlay;
    private int levelNumber;

    public Level(int levelNumber, GamePlay gameToPlay) {
        this.gameToPlay = gameToPlay;
        this.levelNumber = levelNumber;
    }

    public void createGame(){

    }
}

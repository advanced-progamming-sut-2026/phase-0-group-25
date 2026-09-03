package com.test1.PlantsVsZombies.src.Menu;

import com.badlogic.gdx.Screen;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.VaseBreaker;
import com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame.WalnutBowling;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.GamePlayScreen;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.VasebreakerScreen;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.WallnutBowlingScreen;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;

public class GamePlayMenu extends Menu {
    private static GamePlay gamePlay;
    private GamePlayMenuView gamePlayMenuView;

    public GamePlayMenu() {
        super(MenuType.Game);
    }

    public static GamePlay getGamePlay() {
        return gamePlay;
    }

    public static void setGamePlay(GamePlay gamePlay) {
        GamePlayMenu.gamePlay = gamePlay;
    }

    /**
     * Initializes a new gameplay session, disposes of previous screen resources,
     * and attaches a fresh GamePlayScreen.
     */
    public void startSession(GamePlay gamePlay) {
        if (this.gamePlayMenuView instanceof Screen) {
            ((Screen) this.gamePlayMenuView).dispose();
        }
        GamePlayMenu.gamePlay = gamePlay;
        if (gamePlay instanceof VaseBreaker) {
            this.gamePlayMenuView = new VasebreakerScreen((VaseBreaker) gamePlay);
        } else if (gamePlay instanceof WalnutBowling) {
            this.gamePlayMenuView = new WallnutBowlingScreen((WalnutBowling) gamePlay);
        } else {
            this.gamePlayMenuView = new GamePlayScreen(gamePlay);
        }
    }

    public void startSession(GamePlay gamePlay, GamePlayMenuView gamePlayMenuView) {
        if (this.gamePlayMenuView instanceof Screen) {
            ((Screen) this.gamePlayMenuView).dispose();
        }
        GamePlayMenu.gamePlay = gamePlay;
        this.gamePlayMenuView = gamePlayMenuView;
    }

    public void setGamePlayMenuView(GamePlayMenuView gamePlayMenuView) {
        this.gamePlayMenuView = gamePlayMenuView;
    }

    private void checkWinCondition() {
        if (gamePlay != null && gamePlay.checkingTheEndOfTheGame()) {
            System.out.println("Level completed successfully! Victory!");
            if (gamePlay.getLevelObject() != null) {
                gamePlay.getLevelObject().completeLevel();
            }
            MenuManager.getInstance().changeMenu(MenuType.Game);
        }
    }

    public void handleSpecificCommands(String input) {
    }

    @Override
    public BaseView getView() {
        return gamePlayMenuView;
    }
}

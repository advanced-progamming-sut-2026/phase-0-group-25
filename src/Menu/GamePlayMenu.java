package src.Menu;

import src.View.ViewInterfaces.GameMenuView;
import src.View.ViewInterfaces.GamePlayMenuView;

public class GamePlayMenu extends Menu{
    private GamePlayMenuView gamePlayMenuView;

    public GamePlayMenu(GamePlayMenuView gamePlayMenuView) {
        this.gamePlayMenuView = gamePlayMenuView;
    }
}

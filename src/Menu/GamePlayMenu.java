package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GameMenuView;
import src.View.ViewInterfaces.GamePlayMenuView;

public class GamePlayMenu extends Menu{
    private final GamePlayMenuView gamePlayMenuView;

    public GamePlayMenu(GamePlayMenuView gamePlayMenuView) {
        super(MenuType.Game);
        this.gamePlayMenuView = gamePlayMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return gamePlayMenuView;
    }
}

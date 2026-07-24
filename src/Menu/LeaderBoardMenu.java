package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.LeaderBoardMenuView;

public class LeaderBoardMenu extends Menu {
    private final LeaderBoardMenuView leaderBoardMenuView;

    public LeaderBoardMenu(LeaderBoardMenuView leaderBoardMenuView) {
        super(MenuType.Game);
        this.leaderBoardMenuView = leaderBoardMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return leaderBoardMenuView;
    }
}

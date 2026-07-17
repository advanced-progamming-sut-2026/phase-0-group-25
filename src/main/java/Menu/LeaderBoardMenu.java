package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.LeaderBoardMenuView;

public class LeaderBoardMenu extends Menu{
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

package src.View.ConcreteViews;

import src.View.ViewInterfaces.LeaderBoardMenuView;

public class LeaderBoardMenuTerminalView extends AbstractTerminalView implements LeaderBoardMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("leader board menu");
    }
}

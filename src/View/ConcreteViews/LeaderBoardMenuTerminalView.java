package src.View.ConcreteViews;

import src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.Scanner;

public class LeaderBoardMenuTerminalView extends AbstractTerminalView implements LeaderBoardMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("leader board menu");
    }
}

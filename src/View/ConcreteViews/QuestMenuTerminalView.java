package src.View.ConcreteViews;

import src.View.ViewInterfaces.QuestMenuView;

import java.util.Scanner;

public class QuestMenuTerminalView implements QuestMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("quest menu");
    }
}

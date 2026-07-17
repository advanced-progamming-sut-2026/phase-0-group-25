package View.ConcreteViews;

import View.ViewInterfaces.QuestMenuView;

import java.util.Scanner;

public class QuestMenuTerminalView extends AbstractTerminalView implements QuestMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("quest menu");
    }
}

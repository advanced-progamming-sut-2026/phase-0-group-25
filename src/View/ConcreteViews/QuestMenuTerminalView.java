package src.View.ConcreteViews;

import src.View.ViewInterfaces.QuestMenuView;

public class QuestMenuTerminalView extends AbstractTerminalView implements QuestMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("quest menu");
    }
}

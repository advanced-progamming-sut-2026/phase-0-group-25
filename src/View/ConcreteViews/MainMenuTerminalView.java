package src.View.ConcreteViews;

import src.View.ViewInterfaces.MainMenuView;

public class MainMenuTerminalView extends AbstractTerminalView implements MainMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("main menu");
    }
}

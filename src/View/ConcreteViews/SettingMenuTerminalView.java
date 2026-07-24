package src.View.ConcreteViews;

import src.View.ViewInterfaces.SettingMenuView;

public class SettingMenuTerminalView extends AbstractTerminalView implements SettingMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("settings menu");
    }
}

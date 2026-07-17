package View.ConcreteViews;

import View.ViewInterfaces.SettingMenuView;

import java.util.Scanner;

public class SettingMenuTerminalView extends AbstractTerminalView implements SettingMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("settings menu");
    }
}

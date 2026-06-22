package src.View.ConcreteViews;

import src.View.ViewInterfaces.SettingMenuView;

import java.util.Scanner;

public class SettingMenuTerminalView implements SettingMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("settings menu");
    }
}

package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.SettingMenuView;

public class SettingMenuTerminalView extends AbstractTerminalView implements SettingMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("settings menu");
    }
}

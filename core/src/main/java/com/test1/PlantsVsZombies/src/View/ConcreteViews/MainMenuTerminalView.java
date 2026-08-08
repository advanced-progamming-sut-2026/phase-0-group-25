package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.MainMenuView;

public class MainMenuTerminalView extends AbstractTerminalView implements MainMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("main menu");
    }
}

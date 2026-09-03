package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;

public class GamePlayMenuTerminalView extends AbstractTerminalView implements GamePlayMenuView {


    @Override
    public void showCurrentMenu() {
        System.out.println("game play menu");
    }
}

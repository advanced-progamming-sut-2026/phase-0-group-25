package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.QuestMenuView;

public class QuestMenuTerminalView extends AbstractTerminalView implements QuestMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("quest menu");
    }
}

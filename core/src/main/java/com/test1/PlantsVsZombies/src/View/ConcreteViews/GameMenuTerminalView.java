package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GameMenuView;

public class GameMenuTerminalView extends AbstractTerminalView implements GameMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("game menu");
    }

    @Override
    public void showChapterEnterSuccess(String chapterName) {
        System.out.println("Chapter entered successfully: " + chapterName);
    }
}

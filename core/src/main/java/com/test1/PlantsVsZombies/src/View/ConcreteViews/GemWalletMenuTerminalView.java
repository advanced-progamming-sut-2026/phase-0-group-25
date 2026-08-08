package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GemWalletMenuView;

public class GemWalletMenuTerminalView extends AbstractTerminalView implements GemWalletMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("gem wallet menu");
    }

    @Override
    public void showGemsCount(int gemsCount) {
        System.out.println("Gems: " + gemsCount);
    }
}

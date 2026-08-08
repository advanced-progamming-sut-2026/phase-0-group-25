package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.NetworkMenuView;

public class NetworkMenuTerminalView extends AbstractTerminalView implements NetworkMenuView {
    @Override
    public void showCurrentMenu() {
        System.out.println("network menu");
    }
}

package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;

public abstract class AbstractTerminalView implements BaseView {
    @Override
    public void showError(String errorMessage) {
        System.out.println("ERROR: " + errorMessage);
    }
}

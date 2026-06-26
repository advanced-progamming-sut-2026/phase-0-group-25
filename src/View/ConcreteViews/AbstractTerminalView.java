package src.View.ConcreteViews;

import src.View.ViewInterfaces.BaseView;

public abstract class AbstractTerminalView implements BaseView {
    @Override
    public void showError(String errorMessage) {
        System.out.println("ERROR: " + errorMessage);
    }
}

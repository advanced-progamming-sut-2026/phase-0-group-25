package View.ConcreteViews;

import View.ViewInterfaces.BaseView;

public abstract class AbstractTerminalView implements BaseView {
    @Override
    public void showError(String errorMessage) {
        System.out.println("ERROR: " + errorMessage);
    }
}

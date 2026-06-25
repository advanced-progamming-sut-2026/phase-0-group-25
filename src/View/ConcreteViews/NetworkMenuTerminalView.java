package src.View.ConcreteViews;

import src.View.ViewInterfaces.NetworkMenuView;

public class NetworkMenuTerminalView extends AbstractTerminalView implements NetworkMenuView {
    @Override
    public void showCurrentMenu() {
        System.out.println("network menu");
    }
}

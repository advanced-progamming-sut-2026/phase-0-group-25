package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.NetworkMenuView;

public class NetworkMenu extends Menu {
    private final NetworkMenuView networkMenuView;

    public NetworkMenu(NetworkMenuView networkMenuView) {
        super(MenuType.Main);
        this.networkMenuView = networkMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return networkMenuView;
    }
}

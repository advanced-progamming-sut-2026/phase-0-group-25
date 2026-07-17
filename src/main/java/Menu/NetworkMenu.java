package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.NetworkMenuView;

public class NetworkMenu extends Menu{
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

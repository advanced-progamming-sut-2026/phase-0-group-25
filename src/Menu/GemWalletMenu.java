package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GemWalletMenuView;

public class GemWalletMenu extends Menu{

    private final GemWalletMenuView gemWalletMenuView;

    public GemWalletMenu(GemWalletMenuView gemWalletMenuView) {
        super(MenuType.Game);
        this.gemWalletMenuView = gemWalletMenuView;
    }

    public void cheat(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return gemWalletMenuView;
    }
}

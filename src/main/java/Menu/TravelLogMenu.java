package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.TravelLogMenuView;

public class TravelLogMenu extends Menu{
    private final TravelLogMenuView travelLogMenuView;

    public TravelLogMenu(TravelLogMenuView travelLogMenuView) {
        super(MenuType.Game);
        this.travelLogMenuView = travelLogMenuView;
    }

    public void enterMiniGame(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return travelLogMenuView;
    }
}

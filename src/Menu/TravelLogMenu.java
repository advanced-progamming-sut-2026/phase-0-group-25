package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.TravelLogMenuView;

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

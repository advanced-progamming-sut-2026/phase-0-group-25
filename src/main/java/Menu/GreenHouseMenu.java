package Menu;

import Enums.MenuType;
import Model.Greenhouse.Greenhouse;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.GreenHouseMenuView;

public class GreenHouseMenu extends Menu{
    private final GreenHouseMenuView greenHouseMenuView;
    private Greenhouse greenhouse;

    public GreenHouseMenu(GreenHouseMenuView greenHouseMenuView) {
        super(MenuType.Game);
        this.greenHouseMenuView = greenHouseMenuView;
    }

    public void updateGreenhouse() {

    }

    public void growPlant(){

    }
    public void collectPlant(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return greenHouseMenuView;
    }
}

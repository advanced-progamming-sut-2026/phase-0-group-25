package src.Menu;

import src.Enums.MenuType;
import src.Model.Greenhouse.Greenhouse;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GreenHouseMenuView;

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

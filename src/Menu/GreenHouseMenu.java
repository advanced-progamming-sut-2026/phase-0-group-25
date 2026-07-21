package src.Menu;

import src.Enums.Command;          // added import
import src.Enums.MenuType;
import src.Model.Greenhouse.Greenhouse;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GreenHouseMenuView;

import java.util.regex.Matcher;    // added import

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
        Matcher matcher;

        // Handle entering the shop from the greenhouse
        if ((matcher = getMatcher(input, Command.EnterShop)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.Shop);
            return;
        }

        // If no valid command, show error
        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return greenHouseMenuView;
    }
}
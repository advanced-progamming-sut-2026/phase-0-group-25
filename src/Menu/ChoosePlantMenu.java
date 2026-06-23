package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ChoosePlantMenuView;

public class ChoosePlantMenu extends Menu{
    private final ChoosePlantMenuView choosePlantMenuView;

    public ChoosePlantMenu(ChoosePlantMenuView choosePlantMenuView) {
        super(MenuType.Game);
        this.choosePlantMenuView = choosePlantMenuView;
    }

    public void addPlant(){

    }
    public void removePlant(){

    }
    public void boostPlant(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return choosePlantMenuView;
    }
}

package src.Menu;

import src.Enums.MenuType;
import src.Model.PlantsAndZombies.Plant;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ChoosePlantMenuView;

import java.util.ArrayList;

public class ChoosePlantMenu extends Menu{
    private final ChoosePlantMenuView choosePlantMenuView;
    private ArrayList<Plant> plants;

    public ChoosePlantMenu(ChoosePlantMenuView choosePlantMenuView, ArrayList<Plant> plants) {
        super(MenuType.Game);
        this.plants = plants;
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

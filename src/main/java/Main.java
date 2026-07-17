import Enums.GenderType;
import Menu.MenuManager;
import Model.PlantsAndZombies.GameDataLoader;
import Model.PlantsAndZombies.PlantStats;
import Model.User.User;
import Model.User.UsersManager;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MenuManager menuManager = MenuManager.getInstance();
        menuManager.startAppLoop();


    }
}
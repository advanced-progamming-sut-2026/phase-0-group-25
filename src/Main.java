package src;

import src.Menu.MenuManager;
import src.Model.PlantsAndZombies.GameDataLoader;
import src.Model.PlantsAndZombies.PlantStats;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MenuManager menuManager = MenuManager.getInstance();
        menuManager.startAppLoop();
    }
}
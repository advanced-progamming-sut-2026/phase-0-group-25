package src.View.ConcreteViews;

import src.View.ViewInterfaces.ChoosePlantMenuView;

import java.util.List;
import java.util.Scanner;

public class ChoosePlantMenuTerminalView extends AbstractTerminalView implements ChoosePlantMenuView{
    @Override
    public void showAllPlants(List<String> plantNames) {
        System.out.println("--- All Defined Plants ---");
        for (String plant : plantNames) {
            System.out.println("- " + plant);
        }
    }
    @Override
    public void showPlantBoosted(String plantName) {
        System.out.println("Plant " + plantName + " is boosted for this stage!");
    }

    @Override
    public void showAvailablePlants(List<String> plantNames) {
        System.out.println("--- Available Plants ---");
        if (plantNames.isEmpty()) {
            System.out.println("No plants available.");
            return;
        }
        for (String plant : plantNames) {
            System.out.println("- " + plant);
        }
    }

    @Override
    public void showPlantAddedSuccess(String plantName) {
        System.out.println("Plant " + plantName + " added successfully!");
    }

    @Override
    public void showPlantRemovedSuccess(String plantName) {
        System.out.println("Plant " + plantName + " removed successfully!");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("choose plant menu");
    }
}

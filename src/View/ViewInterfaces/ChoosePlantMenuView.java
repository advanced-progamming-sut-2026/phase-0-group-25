package src.View.ViewInterfaces;

import java.util.List;

public interface ChoosePlantMenuView extends BaseView {
    void showAllPlants(List<String> plantNames);

    void showAvailablePlants(List<String> plantNames);

    void showPlantAddedSuccess(String plantName);

    void showPlantRemovedSuccess(String plantName);

    void showPlantBoosted(String plantName);
}


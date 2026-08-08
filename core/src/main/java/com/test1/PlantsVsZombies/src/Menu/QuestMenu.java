package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.QuestMenuView;

public class QuestMenu extends Menu {
    private final QuestMenuView questMenuView;

    public QuestMenu(QuestMenuView questMenuView) {
        super(MenuType.Main);
        this.questMenuView = questMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return questMenuView;
    }
}

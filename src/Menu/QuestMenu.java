package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.QuestMenuView;

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

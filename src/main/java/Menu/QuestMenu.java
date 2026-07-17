package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.QuestMenuView;

public class QuestMenu extends Menu{
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

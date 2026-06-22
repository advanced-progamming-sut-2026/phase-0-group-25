package src.Menu;

import src.View.ViewInterfaces.QuestMenuView;

public class QuestMenu extends Menu{
    private QuestMenuView questMenuView;

    public QuestMenu(QuestMenuView questMenuView) {
        this.questMenuView = questMenuView;
    }
}

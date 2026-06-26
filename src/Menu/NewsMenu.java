package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.NewsMenuView;

public class NewsMenu extends Menu{
    private final NewsMenuView newsMenuView;

    public NewsMenu(NewsMenuView newsMenuView) {
        super(MenuType.Main);
        this.newsMenuView = newsMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return newsMenuView;
    }
}

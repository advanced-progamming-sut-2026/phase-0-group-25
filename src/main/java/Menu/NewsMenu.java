package Menu;

import Enums.Command;
import Enums.MenuType;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.NewsMenuView;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class NewsMenu extends Menu{
    private final NewsMenuView newsMenuView;

    public NewsMenu(NewsMenuView newsMenuView) {
        super(MenuType.Main);
        this.newsMenuView = newsMenuView;
    }




    @Override
    public void handleSpecificCommands(String input) {

        Matcher matcher;

        if((matcher = getMatcher(input, Command.ShowUnreadNews)) != null){
            ArrayList<String > unreadNews = UsersManager.getInstance().getUnreadNews()
                    ;
            newsMenuView.showUnreadNew(unreadNews);
            return;
        }
        if((matcher = getMatcher(input, Command.ShowAllNews)) != null){
            ArrayList<String > allNews = UsersManager.getInstance().getAllNews();
            newsMenuView.showAllNews(allNews);
            return;
        }

        getView().showError("Invalid command format for this menu state.");


    }

    @Override
    public BaseView getView() {
        return newsMenuView;
    }
}

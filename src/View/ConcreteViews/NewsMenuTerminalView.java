package src.View.ConcreteViews;

import src.View.ViewInterfaces.NewsMenuView;

import java.util.ArrayList;

public class NewsMenuTerminalView extends AbstractTerminalView implements NewsMenuView {


    @Override
    public void showCurrentMenu() {
        System.out.println("news menu");
    }

    @Override
    public void showUnreadNew(ArrayList<String> news) {
        for (String string : news)
            System.out.println(string);
    }

    @Override
    public void showAllNews(ArrayList<String> news) {

        for (String string : news)
            System.out.println(string);
    }
}

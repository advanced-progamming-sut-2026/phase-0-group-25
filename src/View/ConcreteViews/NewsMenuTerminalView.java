package src.View.ConcreteViews;

import src.View.ViewInterfaces.NewsMenuView;

import java.util.Scanner;

public class NewsMenuTerminalView extends AbstractTerminalView implements NewsMenuView {

    @Override
    public void showUnreadNew() {

    }

    @Override
    public void showAllNews() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("news menu");
    }
}

package View.ConcreteViews;

import View.ViewInterfaces.GameMenuView;

import java.util.Scanner;

public class GameMenuTerminalView extends AbstractTerminalView implements GameMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("game menu");
    }

    @Override
    public void showChapterEnterSuccess(String chapterName) {
        System.out.println("Chapter entered successfully: " + chapterName);
    }
}

package src.View.ConcreteViews;

import src.View.ViewInterfaces.GemWalletMenuView;

import java.util.Scanner;

public class GemWalletMenuTerminalView extends AbstractTerminalView implements GemWalletMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("gem wallet menu");
    }
}

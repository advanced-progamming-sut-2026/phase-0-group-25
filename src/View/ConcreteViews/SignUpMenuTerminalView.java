package src.View.ConcreteViews;

import src.View.ViewInterfaces.SignUpMenuView;

import java.util.Scanner;

public class SignUpMenuTerminalView extends AbstractTerminalView implements SignUpMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("sign up menu");
    }
}

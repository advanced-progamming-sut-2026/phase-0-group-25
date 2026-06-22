package src.View.ConcreteViews;

import src.View.ViewInterfaces.SignUpMenuView;

import java.util.Scanner;

public class SignUpMenuTerminalView implements SignUpMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("sign up menu");
    }
}

package View.ConcreteViews;

import View.ViewInterfaces.LoginMenuView;

import java.util.Scanner;

public class LoginMenuTerminalView extends AbstractTerminalView implements LoginMenuView {

    @Override
    public void showPromptForNewPassword() {
        System.out.println("Security challenge accepted! Please enter your new password:");
    }

    @Override
    public void showPasswordResetSuccess() {
        System.out.println("Password successfully updated! You can now log into your account.");
    }

    @Override
    public void showLoginSuccess(String nickname) {
        System.out.println("Login successful! Welcome back, " + nickname + ".");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("login menu");
    }
}

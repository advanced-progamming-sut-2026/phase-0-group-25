package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.LoginMenuView;

import java.util.regex.Matcher;

public class LoginMenu extends Menu{
    private final LoginMenuView loginMenuView;
    private boolean awaitingNewPassword = false;
    private String resettingUsername = null;

    public LoginMenu(LoginMenuView loginMenuView) {
        super(MenuType.Signup);
        this.loginMenuView = loginMenuView;
    }




    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.LoginAccount)) != null) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            boolean stayLoggedIn = (matcher.group(3) != null);
            loginUser(username, password, stayLoggedIn);
            return;
        }

        if ((matcher = getMatcher(input, Command.ForgetPassword)) != null) {
            String username = matcher.group(1);
            String email = matcher.group(2);
            String answer = matcher.group(3);
            forgetPassword(username, email, answer);
            return;
        }

        if ((matcher = getMatcher(input, Command.SetNewPassword)) != null) {
            String newPassword = matcher.group(1);
            setNewPassword(newPassword);
            return;
        }

        getView().showError("Invalid command signature matching this menu context.");
    }


    private void loginUser(String username, String password, boolean stayLoggedIn) {
        if (awaitingNewPassword) {
            getView().showError("Password update active. Please finalize your new password first.");
            return;
        }

        String authError = UsersManager.getInstance().authenticateUser(username, password, stayLoggedIn);
        if (authError != null) {
            getView().showError(authError);
            return;
        }

        loginMenuView.showLoginSuccess(UsersManager.getInstance().getLoggedInUser().getNickName());
        MenuManager.getInstance().changeMenu(MenuType.Main);
    }

    private void forgetPassword(String username, String email, String answer) {
        if (awaitingNewPassword) {
            getView().showError("Password recovery is already in progress.");
            return;
        }

        String error = UsersManager.getInstance().validateForgetPasswordRequest(username, email, answer);
        if (error != null) {
            getView().showError(error);
            return;
        }

        this.resettingUsername = username;
        this.awaitingNewPassword = true;
        loginMenuView.showPromptForNewPassword();
    }

    private void setNewPassword(String newPassword) {
        if (!awaitingNewPassword || resettingUsername == null) {
            getView().showError("Please pass the 'forget password' security query check first.");
            return;
        }

        String updateError = UsersManager.getInstance().updateUserPassword(resettingUsername, newPassword);
        if (updateError != null) {
            getView().showError(updateError);
            return;
        }

        this.awaitingNewPassword = false;
        this.resettingUsername = null;
        loginMenuView.showPasswordResetSuccess();
    }

    @Override
    public BaseView getView() {
        return loginMenuView;
    }
}

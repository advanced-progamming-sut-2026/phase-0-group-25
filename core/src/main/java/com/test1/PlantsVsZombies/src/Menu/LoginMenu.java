package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.LoginMenuView;
import java.util.regex.Matcher;

public class LoginMenu extends Menu {
    private final LoginMenuView loginMenuView;
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

    public void loginUser(String username, String password, boolean stayLoggedIn) {

        String authError = UsersManager.getInstance().authenticateUser(username, password, stayLoggedIn);
        if (authError != null) {
            getView().showError(authError);
            return;
        }
        loginMenuView.showLoginSuccess(UsersManager.getInstance().getLoggedInUser().getNickName());
        MenuManager.getInstance().changeMenu(MenuType.Main);
    }

    public void forgetPassword(String username, String email, String answer) {
        String error = UsersManager.getInstance().validateForgetPasswordRequest(username, email, answer);
        if (error != null) {
            getView().showError(error);
            return;
        }
        this.resettingUsername = username;
        loginMenuView.showPromptForNewPassword();
    }

    public void setNewPassword(String newPassword, String confirmPassword) {

        if (newPassword == null || newPassword.trim().isEmpty()) {
            getView().showError("Password cannot be empty.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            getView().showError("Security answer verification does not match original field.");
            return;
        }
        String updateError = UsersManager.getInstance().updateUserPassword(resettingUsername, newPassword);
        if (updateError != null) {
            getView().showError(updateError);
            return;
        }
        this.resettingUsername = null;
        loginMenuView.showPasswordResetSuccess();
    }

    public void setNewPassword(String newPassword) {
        setNewPassword(newPassword, newPassword);
    }

    public void cancelPasswordReset() {
        this.resettingUsername = null;
    }

    @Override
    public BaseView getView() {
        return loginMenuView;
    }
}

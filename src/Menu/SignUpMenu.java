package src.Menu;

import src.Enums.Command;
import src.Enums.GenderType;
import src.Enums.MenuType;
import src.Enums.SecurityQuestionType;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.SignUpMenuView;

import java.util.regex.Matcher;

public class SignUpMenu extends Menu{
    private final SignUpMenuView signUpMenuView;
    private final UsersManager usersManager;

    private boolean awaitingSecurityQuestion = false;
    private User pendingUser;

    public SignUpMenu(SignUpMenuView signUpMenuView) {
        super(null);
        this.signUpMenuView = signUpMenuView;
        this.usersManager = new UsersManager();
        addChangeableMenuType(MenuType.Login);
    }

    @Override
    public void exit() {
        MenuManager.getInstance().setMustExit();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.RegisterAccount)) != null) {
            if (awaitingSecurityQuestion) {
                getView().showError("Account details already submitted. Please answer the security question first.");
                return;
            }

            String username = matcher.group(1);
            String password = matcher.group(2);
            String passwordConfirm = matcher.group(3);
            String nickname = matcher.group(4);
            String email = matcher.group(5);
            String genderStr = matcher.group(6);

            String validationError = usersManager.validateRegistration(
                    username, password, passwordConfirm, nickname, email, genderStr
            );

            if (validationError != null) {
                getView().showError(validationError);
                return;
            }

            GenderType gender = genderStr.equalsIgnoreCase("Male") ? GenderType.Male : GenderType.Female;
            pendingUser = new User(username, nickname, password, email, gender);
            awaitingSecurityQuestion = true;

            signUpMenuView.showSecurityQuestions();
            return;
        }

        if ((matcher = getMatcher(input, Command.PickQuestion)) != null) {
            if (!awaitingSecurityQuestion || pendingUser == null) {
                getView().showError("Please enter your registration details first using the 'register' command.");
                return;
            }

            int questionId = Integer.parseInt(matcher.group(1));
            String answer = matcher.group(2);
            String answerConfirm = matcher.group(3);

            SecurityQuestionType chosenQuestion = SecurityQuestionType.getById(questionId);
            if (chosenQuestion == null) {
                getView().showError("Invalid choice! Please select a valid number from the listed options.");
                return;
            }

            if (!answer.equals(answerConfirm)) {
                getView().showError("Security answer verification does not match original field.");
                return;
            }

            pendingUser.setSecurityQuestion(chosenQuestion);
            pendingUser.setSecurityAnswer(answer);

            usersManager.addUser(pendingUser);
            usersManager.writeUsers();

            awaitingSecurityQuestion = false;
            pendingUser = null;

            signUpMenuView.showRegistrationSuccess();
            MenuManager.getInstance().changeMenu(MenuType.Login);
            return;
        }

        // If the input didn't match either command pattern
        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return signUpMenuView;
    }

    public void registerUser(){

    }
}

package Menu;

import Enums.Command;
import Enums.GenderType;
import Enums.MenuType;
import Enums.SecurityQuestionType;
import Model.User.User;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.SignUpMenuView;

import java.util.regex.Matcher;

public class SignUpMenu extends Menu{
    private final SignUpMenuView signUpMenuView;
    private final UsersManager usersManager;

    private boolean awaitingSecurityQuestion = false;
    private User pendingUser;

    public SignUpMenu(SignUpMenuView signUpMenuView) {
        super(null);
        this.signUpMenuView = signUpMenuView;
        this.usersManager = UsersManager.getInstance();
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
            String username = matcher.group(1);
            String password = matcher.group(2);
            String passwordConfirm = matcher.group(3);
            String nickname = matcher.group(4);
            String email = matcher.group(5);
            String genderStr = matcher.group(6);
            registerUser(username, password, passwordConfirm, nickname, email, genderStr);
            return;
        }

        if ((matcher = getMatcher(input, Command.PickQuestion)) != null) {
            int questionId = Integer.parseInt(matcher.group(1));
            String answer = matcher.group(2);
            String answerConfirm = matcher.group(3);
            pickQuestion(questionId, answer, answerConfirm);
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    // --- Core Operations: Completely Agnostic to Regex ---

    private void registerUser(String username, String password, String passwordConfirm,
                              String nickname, String email, String genderStr) {
        if (awaitingSecurityQuestion) {
            getView().showError("Account details already submitted. Please answer the security question.");
            return;
        }

        String validationError = UsersManager.getInstance().validateRegistration(
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
    }

    private void pickQuestion(int questionId, String answer, String answerConfirm) {
        if (!awaitingSecurityQuestion || pendingUser == null) {
            getView().showError("Please enter your registration details first using the 'register' command.");
            return;
        }

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

        UsersManager.getInstance().addUser(pendingUser);

        awaitingSecurityQuestion = false;
        pendingUser = null;

        signUpMenuView.showRegistrationSuccess();
        MenuManager.getInstance().changeMenu(MenuType.Login);
    }

    @Override
    public BaseView getView() {
        return signUpMenuView;
    }


}

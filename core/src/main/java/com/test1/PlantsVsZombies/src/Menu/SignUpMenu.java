package com.test1.PlantsVsZombies.src.Menu;

import com.badlogic.gdx.Gdx;
import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.GenderType;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.SecurityQuestionType;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.SignUpMenuView;
import java.util.regex.Matcher;

public class SignUpMenu extends Menu {
    private final SignUpMenuView signUpMenuView;
    private final UsersManager usersManager;
    private User pendingUser;

    public SignUpMenu(SignUpMenuView signUpMenuView) {
        super(null);
        this.signUpMenuView = signUpMenuView;
        this.usersManager = UsersManager.getInstance();
        addChangeableMenuType(MenuType.Login);
    }

    @Override
    public void exit() {
        Gdx.app.exit();
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

    public void registerUser(String username, String password, String passwordConfirm,
                             String nickname, String email, String genderStr) {

        String validationError = UsersManager.getInstance().validateRegistration(
            username, password, passwordConfirm, nickname, email, genderStr
        );
        if (validationError != null) {
            getView().showError(validationError);
            return;
        }
        GenderType gender = genderStr.equalsIgnoreCase("Male") ? GenderType.Male : GenderType.Female;
        pendingUser = new User(username, nickname, password, email, gender);
        signUpMenuView.showSecurityQuestions();
    }

    public void pickQuestion(int questionId, String answer, String answerConfirm) {

        SecurityQuestionType chosenQuestion = SecurityQuestionType.getById(questionId);
        if (chosenQuestion == null) {
            getView().showError("Invalid choice! Please select a valid number from the listed options.");
            return;
        }
        if(answer.equals("")){
            getView().showError("You must enter an answer.");
            return;
        }
        if (!answer.equals(answerConfirm)) {
            getView().showError("Security answer verification does not match original field.");
            return;
        }
        pendingUser.setSecurityQuestion(chosenQuestion);
        pendingUser.setSecurityAnswer(answer);
        UsersManager.getInstance().addUser(pendingUser);
        pendingUser = null;
        signUpMenuView.showRegistrationSuccess();
        MenuManager.getInstance().changeMenu(MenuType.Login);
    }

    @Override
    public BaseView getView() {
        return signUpMenuView;
    }
}

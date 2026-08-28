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
        String registrationError = usersManager.addUser(pendingUser);
        if (registrationError != null) {
            getView().showError(registrationError);
            return;
        }
        pendingUser = null;
        signUpMenuView.showRegistrationSuccess();
        MenuManager.getInstance().changeMenu(MenuType.Login);
    }

    @Override
    public BaseView getView() {
        return signUpMenuView;
    }
}

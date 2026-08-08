package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.Enums.SecurityQuestionType;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.SignUpMenuView;

public class SignUpMenuTerminalView extends AbstractTerminalView implements SignUpMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("sign up menu");
    }

    @Override
    public void showSecurityQuestions() {
        System.out.println("Please pick a security question matching form configuration details:");
        for (SecurityQuestionType q : SecurityQuestionType.values()) {
            System.out.println(q.getId() + ". " + q.getDescription());
        }
    }

    @Override
    public void showRegistrationSuccess() {
        System.out.println("Account created successfully! Navigating towards login systems.");
    }
}

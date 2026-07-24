package src.View.ConcreteViews;

import src.Enums.SecurityQuestionType;
import src.View.ViewInterfaces.SignUpMenuView;

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

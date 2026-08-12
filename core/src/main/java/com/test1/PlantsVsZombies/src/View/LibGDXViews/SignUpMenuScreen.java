package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.SecurityQuestionType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Menu.SignUpMenu;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.SignUpMenuView;
import pvz.skin.BorderedTable;

public class SignUpMenuScreen extends AbstractScreen implements SignUpMenuView {
    private static final String DEFAULT_BUTTON_BG_ASSET_ID = "IMAGE_UI_GENERIC_GREENBUTTON_DOWN";
    private static final String TOGGLE_BUTTON_BG_ASSET_ID = "IMAGE_UI_HUD_INGAME_PROGRESS_BAR_UPGRADE_GREEN_FILL_SHARP";
    private static final String EXIT_BUTTON_BG_ASSET_ID = "IMAGE_UI_GENERIC_BROWNBUTTON_DOWN";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    private static final String LEFT_ARROW_ASSET_ID = "IMAGE_UI_GENERIC_ARROW_LEFT_GREEN";
    private static final String RIGHT_ARROW_ASSET_ID = "IMAGE_UI_GENERIC_ARROW_RIGHT_GREEN";

    private SignUpMenu menuController;

    // Containers
    private Table mainContainer;
    private BorderedTable registrationTable;
    private BorderedTable securityQuestionTable;

    // Registration UI elements
    private TextField usernameField;
    private TextField passwordField;
    private TextField passwordConfirmField;
    private TextField nicknameField;
    private TextField emailField;

    // Gender Buttons
    private String selectedGender = "Male";
    private TextButton maleButton;
    private TextButton femaleButton;

    // Security Question Selector State
    private int selectedQuestionIndex = 0;
    private final String[] securityQuestions = new String[]{
        SecurityQuestionType.FAVORITE_COLOR.getDescription(),
        SecurityQuestionType.FIRST_PET.getDescription(),
        SecurityQuestionType.BORN_CITY.getDescription()
    };
    private Label questionLabel;
    private TextField answerField;
    private TextField answerConfirmField;

    public SignUpMenuScreen() {
    }

    public void setMenuController(SignUpMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        TextureRegion bgRegion = textureBank.region("IMAGE_TITLEBACKGROUNDS_BACKDROP_A");
        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setScaling(Scaling.fill);
            screenStack.add(bgImage);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        mainContainer = new Table();
        uiTable.add(mainContainer).expand().center().row();

        buildRegistrationBox();
        buildSecurityQuestionBox();

        mainContainer.add(registrationTable);

        buildBottomBar(uiTable);

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }



    private void updateGenderSelection() {
        TextureRegion bgRegion = textureBank.region(TOGGLE_BUTTON_BG_ASSET_ID);
        Drawable activeDrawable = bgRegion != null ? new NinePatchDrawable(new NinePatch(bgRegion, 15, 15, 15, 15)) : null;

        TextButton.TextButtonStyle maleStyle = new TextButton.TextButtonStyle(maleButton.getStyle());
        maleStyle.up = selectedGender.equals("Male") ? activeDrawable : null;
        maleButton.setStyle(maleStyle);
        maleButton.getLabel().setColor(Color.BLACK);

        TextButton.TextButtonStyle femaleStyle = new TextButton.TextButtonStyle(femaleButton.getStyle());
        femaleStyle.up = selectedGender.equals("Female") ? activeDrawable : null;
        femaleButton.setStyle(femaleStyle);
        femaleButton.getLabel().setColor(Color.BLACK);
    }

    private void buildRegistrationBox() {
        registrationTable = new BorderedTable();
        registrationTable.pad(30);

        Label titleLabel = createBlackLabel("REGISTER ACCOUNT");

        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        passwordConfirmField = new TextField("", skin);
        passwordConfirmField.setPasswordMode(true);
        passwordConfirmField.setPasswordCharacter('*');

        nicknameField = new TextField("", skin);
        emailField = new TextField("", skin);

        maleButton = createStretchedButton("Male", TOGGLE_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedGender = "Male";
                updateGenderSelection();
            }
        });

        femaleButton = createStretchedButton("Female", null, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedGender = "Female";
                updateGenderSelection();
            }
        });

        updateGenderSelection();

        Table genderContainer = new Table();
        genderContainer.add(maleButton).width(115).padRight(10);
        genderContainer.add(femaleButton).width(115);

        TextButton registerButton = createStretchedButton("Register", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.registerUser(
                        usernameField.getText().trim(),
                        passwordField.getText().trim(),
                        passwordConfirmField.getText().trim(),
                        nicknameField.getText().trim(),
                        emailField.getText().trim(),
                        selectedGender
                    );
                }
            }
        });

        registrationTable.add(titleLabel).colspan(2).padBottom(20).row();

        registrationTable.add(createBlackLabel("Username:")).right().pad(5);
        registrationTable.add(usernameField).width(250).pad(5).row();

        registrationTable.add(createBlackLabel("Password:")).right().pad(5);
        registrationTable.add(passwordField).width(250).pad(5).row();

        registrationTable.add(createBlackLabel("Confirm Password:")).right().pad(5);
        registrationTable.add(passwordConfirmField).width(250).pad(5).row();

        registrationTable.add(createBlackLabel("Nickname:")).right().pad(5);
        registrationTable.add(nicknameField).width(250).pad(5).row();

        registrationTable.add(createBlackLabel("Email:")).right().pad(5);
        registrationTable.add(emailField).width(250).pad(5).row();

        registrationTable.add(createBlackLabel("Gender:")).right().pad(5);
        registrationTable.add(genderContainer).left().pad(5).row();

        registrationTable.add(registerButton).colspan(2).center().padTop(20);
    }

    private Actor createArrowButton(String bgAssetId, String fallbackText, ClickListener listener) {
        TextureRegion arrowRegion = textureBank.region(bgAssetId);
        if (arrowRegion != null) {
            Image arrowImage = new Image(arrowRegion);
            arrowImage.setTouchable(Touchable.enabled);
            if (listener != null) {
                arrowImage.addListener(listener);
            }
            return arrowImage;
        } else {
            return createStretchedButton(fallbackText, DEFAULT_BUTTON_BG_ASSET_ID, listener);
        }
    }

    private void buildSecurityQuestionBox() {
        securityQuestionTable = new BorderedTable();
        securityQuestionTable.pad(30);

        Label titleLabel = createBlackLabel("SECURITY QUESTION");

        questionLabel = createBlackLabel(securityQuestions[selectedQuestionIndex]);
        questionLabel.setAlignment(Align.center);
        questionLabel.setWrap(true);

        Actor leftArrow = createArrowButton(LEFT_ARROW_ASSET_ID, "<", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedQuestionIndex = (selectedQuestionIndex - 1 + securityQuestions.length) % securityQuestions.length;
                questionLabel.setText(securityQuestions[selectedQuestionIndex]);
            }
        });

        Actor rightArrow = createArrowButton(RIGHT_ARROW_ASSET_ID, ">", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedQuestionIndex = (selectedQuestionIndex + 1) % securityQuestions.length;
                questionLabel.setText(securityQuestions[selectedQuestionIndex]);
            }
        });

        Table questionNavigationRow = new Table();
        questionNavigationRow.add(leftArrow).padRight(10);
        questionNavigationRow.add(questionLabel).width(300).center();
        questionNavigationRow.add(rightArrow).padLeft(10);

        answerField = new TextField("", skin);
        answerConfirmField = new TextField("", skin);

        TextButton submitQuestionButton = createStretchedButton("Submit Security Question", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    int questionId = selectedQuestionIndex + 1;
                    menuController.pickQuestion(
                        questionId,
                        answerField.getText().trim(),
                        answerConfirmField.getText().trim()
                    );
                }
            }
        });

        securityQuestionTable.add(titleLabel).colspan(2).padBottom(20).row();

        securityQuestionTable.add(createBlackLabel("Question:")).right().pad(5);
        securityQuestionTable.add(questionNavigationRow).width(380).pad(5).row();

        securityQuestionTable.add(createBlackLabel("Answer:")).right().pad(5);
        securityQuestionTable.add(answerField).width(380).pad(5).row();

        securityQuestionTable.add(createBlackLabel("Confirm Answer:")).right().pad(5);
        securityQuestionTable.add(answerConfirmField).width(380).pad(5).row();

        securityQuestionTable.add(submitQuestionButton).colspan(2).center().padTop(20);
    }

    private void buildBottomBar(Table uiTable) {
        Table bottomTable = new Table();

        TextButton loginButton = createStretchedButton("Go to Login", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Login);
            }
        });

        TextButton exitButton = createStretchedButton("Exit", EXIT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.exit();
                }
            }
        });

        bottomTable.add(loginButton).left().expandX().pad(20);
        bottomTable.add(exitButton).right().pad(20);

        uiTable.add(bottomTable).fillX().bottom();
    }

    @Override
    public void showSecurityQuestions() {
        mainContainer.clearChildren();
        mainContainer.add(securityQuestionTable);
    }

    @Override
    public void showRegistrationSuccess() {
        showToast("Account registered successfully!", SUCCESS_BG_ASSET_ID);
    }

    @Override
    public void showError(String error) {
        showToast(error, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
    }
}

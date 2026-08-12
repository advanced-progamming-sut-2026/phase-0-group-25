package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.LoginMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.LoginMenuView;
import pvz.skin.BorderedTable;

public class LoginMenuScreen extends AbstractScreen implements LoginMenuView {
    private static final String DEFAULT_BUTTON_BG_ASSET_ID = "IMAGE_UI_GENERIC_GREENBUTTON_DOWN";
    private static final String FORGET_BUTTON_BG_ASSET_ID = "IMAGE_UI_GENERIC_BROWNBUTTON_DOWN";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    private static final String CHECKMARK_ASSET_ID = "IMAGE_UI_HUD_INGAME_CHALLENGE_SUCCESS";
    private static final String XMARK_ASSET_ID = "IMAGE_UI_HUD_INGAME_CHALLENGE_FAILED";

    private LoginMenu menuController;

    // Main Containers
    private Table mainContainer;
    private BorderedTable loginTable;
    private BorderedTable forgetPasswordTable;

    // Login Fields
    private TextField usernameField;
    private TextField passwordField;

    // Stay Logged In Toggle State
    private boolean stayLoggedIn = true;
    private Image checkImage;
    private Image xImage;
    private Cell<Image> checkCell;
    private Cell<Image> xCell;
    private Table stayLoggedInRow;

    // Forgot Password Fields
    private TextField forgetUsernameField;
    private TextField forgetEmailField;
    private TextField forgetAnswerField;

    public LoginMenuScreen() {
    }

    public void setMenuController(LoginMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        TextureRegion bgRegion = textureBank.region("IMAGE_TITLEBACKGROUNDS_BACKDROP_I");
        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setScaling(Scaling.fill);
            screenStack.add(bgImage);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        mainContainer = new Table();
        uiTable.add(mainContainer).expand().center().row();

        buildLoginBox();
        buildForgotPasswordBox();

        mainContainer.add(loginTable);

        buildBottomBar(uiTable);

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }


    private void updateStayLoggedInSelection() {
        TextureRegion checkRegion = textureBank.region(CHECKMARK_ASSET_ID);
        TextureRegion xRegion = textureBank.region(XMARK_ASSET_ID);

        if (checkRegion != null && checkImage != null) {
            checkImage.setDrawable(new TextureRegionDrawable(checkRegion));
        }
        if (xRegion != null && xImage != null) {
            xImage.setDrawable(new TextureRegionDrawable(xRegion));
        }

        if (stayLoggedIn) {
            if (checkCell != null) checkCell.size(36, 36);
            if (xCell != null) xCell.size(26, 26);
        } else {
            if (checkCell != null) checkCell.size(26, 26);
            if (xCell != null) xCell.size(36, 36);
        }

        if (stayLoggedInRow != null) {
            stayLoggedInRow.invalidateHierarchy();
        }
    }

    private void buildLoginBox() {
        loginTable = new BorderedTable();
        loginTable.pad(30);

        Label titleLabel = createBlackLabel("USER LOGIN");

        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        checkImage = new Image();
        xImage = new Image();

        checkImage.setScaling(Scaling.fit);
        xImage.setScaling(Scaling.fit);

        checkImage.setTouchable(Touchable.enabled);
        xImage.setTouchable(Touchable.enabled);

        checkImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stayLoggedIn = true;
                updateStayLoggedInSelection();
            }
        });

        xImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stayLoggedIn = false;
                updateStayLoggedInSelection();
            }
        });

        stayLoggedInRow = new Table();
        checkCell = stayLoggedInRow.add(checkImage).padRight(10);
        xCell = stayLoggedInRow.add(xImage).padRight(15);
        stayLoggedInRow.add(createBlackLabel("Stay Logged In"));

        updateStayLoggedInSelection();

        TextButton loginButton = createStretchedButton("Login", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.loginUser(
                        usernameField.getText().trim(),
                        passwordField.getText().trim(),
                        stayLoggedIn
                    );
                }
            }
        });

        TextButton forgotPassButton = createStretchedButton("Forgot Password?", FORGET_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainContainer.clearChildren();
                mainContainer.add(forgetPasswordTable);
            }
        });

        loginTable.add(titleLabel).colspan(2).padBottom(20).row();

        loginTable.add(createBlackLabel("Username:")).right().pad(5);
        loginTable.add(usernameField).width(250).pad(5).row();

        loginTable.add(createBlackLabel("Password:")).right().pad(5);
        loginTable.add(passwordField).width(250).pad(5).row();

        loginTable.add(stayLoggedInRow).colspan(2).center().pad(10).row();

        loginTable.add(loginButton).colspan(2).center().padTop(10).row();

        loginTable.add(forgotPassButton).colspan(2).center().padTop(15);
    }

    private void buildForgotPasswordBox() {
        forgetPasswordTable = new BorderedTable();
        forgetPasswordTable.pad(30);

        Label titleLabel = createBlackLabel("RECOVER PASSWORD");

        forgetUsernameField = new TextField("", skin);
        forgetEmailField = new TextField("", skin);
        forgetAnswerField = new TextField("", skin);

        TextButton submitRecoveryButton = createStretchedButton("Submit Recovery", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.forgetPassword(
                        forgetUsernameField.getText().trim(),
                        forgetEmailField.getText().trim(),
                        forgetAnswerField.getText().trim()
                    );
                }
            }
        });

        TextButton backToLoginButton = createStretchedButton("Back to Login", FORGET_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainContainer.clearChildren();
                mainContainer.add(loginTable);
            }
        });

        forgetPasswordTable.add(titleLabel).colspan(2).padBottom(20).row();

        forgetPasswordTable.add(createBlackLabel("Username:")).right().pad(5);
        forgetPasswordTable.add(forgetUsernameField).width(280).pad(5).row();

        forgetPasswordTable.add(createBlackLabel("Email:")).right().pad(5);
        forgetPasswordTable.add(forgetEmailField).width(280).pad(5).row();

        forgetPasswordTable.add(createBlackLabel("Security Answer:")).right().pad(5);
        forgetPasswordTable.add(forgetAnswerField).width(280).pad(5).row();

        forgetPasswordTable.add(submitRecoveryButton).colspan(2).center().padTop(15).row();

        forgetPasswordTable.add(backToLoginButton).colspan(2).center().padTop(10);
    }

    private void buildBottomBar(Table uiTable) {
        Table bottomTable = new Table();

        TextButton backToSignupButton = createStretchedButton("Back to Signup", FORGET_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Signup);
            }
        });

        bottomTable.add(backToSignupButton).left().expandX().pad(20);

        uiTable.add(bottomTable).fillX().bottom();
    }

    @Override
    public void showLoginSuccess(String nickname) {
        showToast("Welcome back, " + nickname + "!", SUCCESS_BG_ASSET_ID);
    }

    @Override
    public void showPromptForNewPassword() {
        BorderedTable modal = new BorderedTable();
        modal.pad(25);

        Label titleLabel = createBlackLabel("SET NEW PASSWORD");
        TextField newPasswordField = new TextField("", skin);
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');

        TextButton submitNewPasswordButton = createStretchedButton("Set Password", DEFAULT_BUTTON_BG_ASSET_ID, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.setNewPassword(newPasswordField.getText().trim());
                    modal.remove();
                }
            }
        });

        modal.add(titleLabel).colspan(2).padBottom(15).row();
        modal.add(createBlackLabel("New Password:")).right().pad(5);
        modal.add(newPasswordField).width(250).pad(5).row();
        modal.add(submitNewPasswordButton).colspan(2).center().padTop(15);

        modal.pack();
        modal.setPosition(
            (stage.getWidth() - modal.getWidth()) / 2f,
            (stage.getHeight() - modal.getHeight()) / 2f
        );

        stage.addActor(modal);
    }

    @Override
    public void showPasswordResetSuccess() {
        showToast("Password reset successfully!", SUCCESS_BG_ASSET_ID);
        mainContainer.clearChildren();
        mainContainer.add(loginTable);
    }

    @Override
    public void showError(String error) {
        showToast(error, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
    }
}

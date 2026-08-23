package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MainMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.News.News;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.MainMenuView;
import pvz.skin.BorderedTable;

import java.util.ArrayList;

public class MainMenuScreen extends AbstractScreen implements MainMenuView {

    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String LOGO_ASSET_ID = "IMAGE_UI_MAINMENU_PVZ2_LOGO_HORIZONTAL";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    private static final String USER_BADGE_BG_ASSET_ID = "IMAGE_UI_IF_BUNDLE_REWARD5_BG";

    private static final String NEWS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_NEWSBUTTON_BUTTONS_HUD_NEWS_SELECTED_COPY_2";
    private static final String EXCLAMATION_MARK_ASSET_ID = "IMAGE_UI_CLAIM_SMALL";
    private static final String SETTINGS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_SETTINGSBUTTON_BUTTONS_HUD_SETTINGS_NORMAL";

    private MainMenu menuController;
    private Table topLeftTable;
    private Table bottomTable;

    // Store unread messages snapshot before marking them read
    private ArrayList<String> unreadMessages = new ArrayList<>();

    public void setMenuController(MainMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        TextureRegion bgRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setScaling(Scaling.fill);
            screenStack.add(bgImage);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        Table topTable = new Table();

        topLeftTable = createTopLeftTable();

        topTable.add(topLeftTable).left().pad(15);
        topTable.add().expandX();

        TextButton logoutButton = new TextButton("Logout", skin, "brown");
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.logout();
                }
            }
        });
        topTable.add(logoutButton).right().top().pad(15);

        uiTable.add(topTable).fillX().top().row();

        Table mainContainer = new Table();

        TextureRegion logoRegion = textureBank.region(LOGO_ASSET_ID);
        if (logoRegion != null) {
            Image logoImage = new Image(logoRegion);
            mainContainer.add(logoImage).center().padBottom(20).row();
        } else {
            Label logoLabel = new Label("PLANTS vs ZOMBIES", skin);
            mainContainer.add(logoLabel).center().padBottom(20).row();
        }

        TextButton playButton = new TextButton("Play", skin, "purple");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Game);
            }
        });
        mainContainer.add(playButton).center();

        uiTable.add(mainContainer).expand().center().padBottom(40).row();

        bottomTable = new Table();
        rebuildBottomTable();

        uiTable.add(bottomTable).fillX().bottom().padBottom(20);

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }

    // ============================================================
    // TOP BAR
    // ============================================================

    private Table createTopLeftTable() {
        Table table = new Table();
        table.add(createCurrencyHud()).left().row();
        table.add(createUserBadge()).left().padTop(8).row();
        return table;
    }

    private void refreshTopBar() {
        if (topLeftTable != null) {
            topLeftTable.clearChildren();
            topLeftTable.add(createCurrencyHud()).left().row();
            topLeftTable.add(createUserBadge()).left().padTop(8).row();
            topLeftTable.invalidateHierarchy();
        }
    }

    private Actor createUserBadge() {
        Table userBadgeTable = new Table();

        String username = "Guest";
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user != null && user.getUserName() != null) {
            username = user.getUserName();
        }

        TextureRegion badgeBgRegion = textureBank.region(USER_BADGE_BG_ASSET_ID);
        if (badgeBgRegion != null) {
            NinePatch patch = new NinePatch(badgeBgRegion, 12, 12, 12, 12);
            userBadgeTable.setBackground(new NinePatchDrawable(patch));
        }

        Label userLabel = createLabel(username, "FBUSV8C5EI_1_outline", Color.WHITE);
        userLabel.setFontScale(0.75f);
        userBadgeTable.add(userLabel).pad(6, 14, 6, 14);

        return userBadgeTable;
    }

    // ============================================================
    // BOTTOM BAR & NEWS BUTTON
    // ============================================================

    private boolean hasUnreadNews() {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user != null && user.getNewsManager() != null && user.getNewsManager().getNews() != null) {
            for (News newsItem : user.getNewsManager().getNews()) {
                if (!newsItem.isRead()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Actor createNewsButtonStack() {
        Stack newsStack = new Stack();

        TextureRegion newsRegion = textureBank.region(NEWS_BUTTON_ASSET_ID);
        Actor newsActor;
        if (newsRegion != null) {
            TextureRegionDrawable newsDrawable = new TextureRegionDrawable(newsRegion);

            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = newsDrawable;
            style.down = newsDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));

            Button newsButton = new Button(style);
            newsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showNewsDialog();
                }
            });
            newsActor = newsButton;
        } else {
            TextButton fallbackNewsButton = new TextButton("News", skin);
            fallbackNewsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showNewsDialog();
                }
            });
            newsActor = fallbackNewsButton;
        }

        newsStack.add(newsActor);

        Table exclamationOverlay = new Table();
        if (hasUnreadNews()) {
            TextureRegion exclRegion = textureBank.region(EXCLAMATION_MARK_ASSET_ID);
            if (exclRegion != null) {
                Image exclImage = new Image(exclRegion);
                exclImage.setTouchable(Touchable.disabled);
                exclamationOverlay.add(exclImage).size(24, 24).top().right().expand();
            } else {
                Label exclLabel = new Label("!", skin);
                exclLabel.setColor(Color.RED);
                exclamationOverlay.add(exclLabel).top().right().expand().padTop(-5).padRight(-5);
            }
        }
        newsStack.add(exclamationOverlay);

        return newsStack;
    }

    private void rebuildBottomTable() {
        if (bottomTable == null) return;
        bottomTable.clearChildren();

        TextButton profileButton = new TextButton("Profile", skin, "brown");
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showProfileDialog();
            }
        });

        bottomTable.add(profileButton).left().padLeft(20).padRight(10);
        bottomTable.add(createNewsButtonStack()).left().padRight(20);
        bottomTable.add().expandX();

        TextButton leaderboardButton = new TextButton("Leaderboard", skin, "brown");
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openLeaderBoardDialog();
            }
        });

        bottomTable.add(createSettingsButton()).right().padRight(10);
        bottomTable.add(leaderboardButton).right().padRight(20);
        bottomTable.invalidateHierarchy();
    }

    private void refreshNewsButton() {
        rebuildBottomTable();
    }

    // ============================================================
    // SETTINGS BUTTON
    // ============================================================

    private Actor createSettingsButton() {
        TextureRegion settingsRegion = textureBank.region(SETTINGS_BUTTON_ASSET_ID);
        if (settingsRegion != null) {
            TextureRegionDrawable settingsDrawable = new TextureRegionDrawable(settingsRegion);

            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = settingsDrawable;
            style.down = settingsDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));

            Button settingsButton = new Button(style);
            settingsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showSettingsDialog();
                }
            });
            return settingsButton;
        } else {
            TextButton fallbackSettings = new TextButton("Settings", skin);
            fallbackSettings.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showSettingsDialog();
                }
            });
            return fallbackSettings;
        }
    }

    // ================================================================
    // SETTINGS MODAL
    // ================================================================

    private void showSettingsDialog() {
        BorderedTable box = new BorderedTable();
        box.pad(30);

        UsersManager um = UsersManager.getInstance();
        User loggedUser = um.getLoggedInUser();
        if (loggedUser == null) {
            showError("No user logged in.");
            return;
        }

        Label title = createLabel("SETTINGS", "FBUSV8C5EI_2", Color.BLACK);
        box.add(title).colspan(2).center().padBottom(20).row();

        // Difficulty slider (1-5)
        box.add(createBlackLabel("Difficulty:")).left().padRight(20).row();
        Table diffRow = new Table();
        Slider diffSlider = new Slider(1, 5, 1, false, skin, "default-horizontal");
        diffSlider.setValue(loggedUser.getUserProgress().getGameDifficulty());
        Label diffValueLabel = createBlackLabel(String.valueOf((int) diffSlider.getValue()));
        diffSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                diffValueLabel.setText(String.valueOf((int) diffSlider.getValue()));
            }
        });
        diffRow.add(diffSlider).width(200).padRight(10);
        diffRow.add(diffValueLabel).width(30);
        box.add(diffRow).left().padBottom(15).row();

        // Speed slider (1-3)
        box.add(createBlackLabel("Game Speed:")).left().padRight(20).row();
        Table speedRow = new Table();
        Slider speedSlider = new Slider(1, 3, 1, false, skin, "default-horizontal");
        speedSlider.setValue(loggedUser.getUserProgress().getGameSpeed());
        Label speedValueLabel = createBlackLabel(String.valueOf((int) speedSlider.getValue()));
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedValueLabel.setText(String.valueOf((int) speedSlider.getValue()));
            }
        });
        speedRow.add(speedSlider).width(200).padRight(10);
        speedRow.add(speedValueLabel).width(30);
        box.add(speedRow).left().padBottom(15).row();

        // Show Tile Grid
        box.add(createBlackLabel("Show Tile Grid:")).left().padRight(20).row();
        Table gridRow = new Table();
        ButtonGroup<CheckBox> gridGroup = new ButtonGroup<>();
        gridGroup.setMinCheckCount(1);
        gridGroup.setMaxCheckCount(1);

        boolean currentGrid = loggedUser.getUserProgress().isShowTileGrid();
        CheckBox gridOn = new CheckBox(" On", skin);
        gridOn.getLabel().setColor(Color.BLACK);
        CheckBox gridOff = new CheckBox(" Off", skin);
        gridOff.getLabel().setColor(Color.BLACK);

        if (currentGrid) gridOn.setChecked(true);
        else gridOff.setChecked(true);

        gridGroup.add(gridOn, gridOff);
        gridRow.add(gridOn).padRight(10);
        gridRow.add(gridOff).padRight(10);
        box.add(gridRow).left().padBottom(15).row();

        // Debug Mode
        box.add(createBlackLabel("Debug Mode:")).left().padRight(20).row();
        Table debugRow = new Table();
        ButtonGroup<CheckBox> debugGroup = new ButtonGroup<>();
        debugGroup.setMinCheckCount(1);
        debugGroup.setMaxCheckCount(1);

        boolean currentDebug = loggedUser.isDebugMode();
        CheckBox debugOn = new CheckBox(" On", skin);
        debugOn.getLabel().setColor(Color.BLACK);
        CheckBox debugOff = new CheckBox(" Off", skin);
        debugOff.getLabel().setColor(Color.BLACK);

        if (currentDebug) debugOn.setChecked(true);
        else debugOff.setChecked(true);

        debugGroup.add(debugOn, debugOff);
        debugRow.add(debugOn).padRight(10);
        debugRow.add(debugOff).padRight(10);
        box.add(debugRow).left().padBottom(25).row();

        // Buttons: OK / Cancel
        Table buttonRow = new Table();
        TextButton okButton = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int diff = (int) diffSlider.getValue();
                um.changeDifficulty(String.valueOf(diff));

                int speed = (int) speedSlider.getValue();
                um.setGameSpeed(speed);

                um.setShowTileGrid(gridOn.isChecked());
                um.setDebugMode(debugOn.isChecked());

                refreshTopBar();
                closeModal();
                showToast("Settings saved", "IMAGE_UI_GENERIC_VTB");
            }
        });

        TextButton cancelButton = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        buttonRow.add(okButton).padRight(10);
        buttonRow.add(cancelButton);
        box.add(buttonRow).colspan(2).center();

        ScrollPane scrollPane = new ScrollPane(box);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(true);

        Table wrapper = new Table();
        wrapper.add(scrollPane).size(480, 500);
        showModal(wrapper);
    }

    // ================================================================
    // NEWS MODAL
    // ================================================================

    private boolean showUnread = true;

    private void showNewsDialog() {
        User user = UsersManager.getInstance().getLoggedInUser();
        unreadMessages.clear();
        if (user != null) {
            var unread = UsersManager.getInstance().getUnreadNews();
            if (unread != null) {
                unreadMessages.addAll(unread);
            }
        }

        refreshNewsButton();

        BorderedTable box = new BorderedTable();
        box.pad(20);

        Label title = createLabel("NEWS", "FBUSV8C5EI_2", Color.BLACK);
        title.setFontScale(0.8f);
        box.add(title).center().padBottom(15).row();

        Table newsTable = new Table();
        newsTable.top().left();
        populateNewsTable(newsTable, showUnread);

        ScrollPane scrollPane = new ScrollPane(newsTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);

        Table newsContainer = new Table();
        newsContainer.setBackground(skin.getDrawable("image_ui_powerups_powerup_cost_10"));
        newsContainer.pad(8, 12, 8, 12);
        newsContainer.add(scrollPane).grow();

        box.add(newsContainer).size(500, 300).padBottom(15).row();

        TextButton toggleButton = new TextButton(showUnread ? "Show all news" : "Show unread news", skin, "green_small");
        toggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showUnread = !showUnread;
                toggleButton.setText(showUnread ? "Show all news" : "Show unread news");
                newsTable.clearChildren();
                populateNewsTable(newsTable, showUnread);
                newsTable.invalidateHierarchy();
            }
        });

        TextButton closeButton = new TextButton("Close", skin, "brown");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(toggleButton).padRight(10);
        buttonRow.add(closeButton);
        box.add(buttonRow).center();

        Table wrapper = new Table();
        wrapper.add(box);
        showModal(wrapper);
    }

    private void populateNewsTable(Table newsTable, boolean unreadOnly) {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null || user.getNewsManager() == null) {
            Label noNews = createLabel("No news available.", "FBUSV8C5EI_1", Color.GRAY);
            noNews.setFontScale(0.6f);
            newsTable.add(noNews).expandX().center().pad(10);
            return;
        }

        ArrayList<String> messagesToShow;
        if (unreadOnly) {
            messagesToShow = new ArrayList<>(unreadMessages);
        } else {
            messagesToShow = UsersManager.getInstance().getAllNews();
        }
        //messagesToShow = new ArrayList<>(messagesToShow.reversed());

        if (messagesToShow.isEmpty()) {
            String text = unreadOnly ? "No unread news" : "No news";
            Label noNews = createLabel(text, "FBUSV8C5EI_1", Color.GRAY);
            noNews.setFontScale(0.6f);
            newsTable.add(noNews)
                .expandX()
                .top()
                .center()
                .pad(10);
            return;
        }

        float labelWidth = 460f;
        for (String msg : messagesToShow) {
            Label label = createLabel(msg, "FBUSV8C5EI_1", Color.BLACK);
            label.setWrap(true);
            label.setFontScale(0.5f);
            label.setAlignment(Align.center);
            newsTable.add(label).center().pad(4, 6, 4, 6).width(labelWidth).row();
        }
    }

    // ================================================================
    // PROFILE MODAL
    // ================================================================

    private void showProfileDialog() {
        UsersManager um = UsersManager.getInstance();
        User loggedUser = um.getLoggedInUser();
        if (loggedUser == null) {
            showError("No user logged in.");
            return;
        }

        UserProgress progress = loggedUser.getUserProgress();

        int totalLevels = 0;
        for (Integer level : progress.getUnlockedChaptersAndLevels().values()) {
            totalLevels += level;
        }

        BorderedTable box = new BorderedTable();
        box.pad(30);

        Label title = createLabel("PROFILE", "FBUSV8C5EI_2", Color.BLACK);
        box.add(title).colspan(3).center().padBottom(20).row();

        box.add(createBlackLabel("Username:")).left().padRight(10);
        Label usernameLabel = createBlackLabel(loggedUser.getUserName());
        box.add(usernameLabel).left().expandX();
        TextButton editUsernameBtn = createSkinButton("Edit", "green_small", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showEditUsernameDialog(usernameLabel);
            }
        });
        box.add(editUsernameBtn).right().padLeft(10).padBottom(20).row();

        box.add(createBlackLabel("Nickname:")).left().padRight(10);
        Label nicknameLabel = createBlackLabel(loggedUser.getNickName());
        box.add(nicknameLabel).left().expandX();
        TextButton editNicknameBtn = createSkinButton("Edit", "green_small", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showEditNicknameDialog(nicknameLabel);
            }
        });
        box.add(editNicknameBtn).right().padLeft(10).padBottom(20).row();

        box.add(createBlackLabel("Total Levels Passed:")).left().padBottom(20).padRight(10);
        box.add(createBlackLabel(String.valueOf(totalLevels))).left().padLeft(10).padBottom(20).row();

        box.add(createBlackLabel("Gems:")).left().padBottom(20).padRight(10);
        box.add(createBlackLabel(String.valueOf(loggedUser.getUserProgress().getGemsCount()))).left().padLeft(10).padBottom(20).row();

        box.add(createBlackLabel("Coins:")).left().padBottom(20).padRight(10);
        box.add(createBlackLabel(String.valueOf(loggedUser.getUserProgress().getCoinsCount()))).left().padLeft(10).padBottom(20).row();

        box.add(createBlackLabel("Games Played:")).padBottom(20).left().padRight(10);
        box.add(createBlackLabel(String.valueOf(progress.getGamesPlayed()))).left().padLeft(10).padBottom(20).row();

        TextButton changePasswordBtn = createSkinButton("Change Password", "green_small", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showChangePasswordDialog();
            }
        });
        box.add(changePasswordBtn).colspan(3).center().padTop(15).row();

        TextButton closeBtn = createSkinButton("Close", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        box.add(closeBtn).colspan(3).center().padTop(10);

        showModal(box);
    }

    private void showEditUsernameDialog(Label targetLabel) {
        BorderedTable subBox = new BorderedTable();
        subBox.pad(25);

        Label title = createBlackLabel("Change Username");
        subBox.add(title).colspan(2).center().padBottom(15).row();

        subBox.add(createBlackLabel("New Username:")).right().padRight(10);
        TextField newUsernameField = new TextField("", skin);
        subBox.add(newUsernameField).width(200).padBottom(15).row();

        TextButton okBtn = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newUsername = newUsernameField.getText().trim();
                if (newUsername.isEmpty()) {
                    showError("Username cannot be empty.");
                    return;
                }
                String error = UsersManager.getInstance().validateAndChangeUsername(newUsername);
                if (error != null) {
                    showError(error);
                } else {
                    showToast("Username changed successfully!", "IMAGE_UI_GENERIC_VTB");
                    targetLabel.setText(newUsername);
                    refreshTopBar();
                    closeModal();
                    showProfileDialog();
                }
            }
        });

        TextButton cancelBtn = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
                showProfileDialog();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(okBtn).padRight(10);
        buttonRow.add(cancelBtn);
        subBox.add(buttonRow).colspan(2).center();

        showModal(subBox);
    }

    private void showEditNicknameDialog(Label targetLabel) {
        BorderedTable subBox = new BorderedTable();
        subBox.pad(25);

        Label title = createBlackLabel("Change Nickname");
        subBox.add(title).colspan(2).center().padBottom(15).row();

        subBox.add(createBlackLabel("New Nickname:")).right().padRight(10);
        TextField newNicknameField = new TextField("", skin);
        subBox.add(newNicknameField).width(200).padBottom(15).row();

        TextButton okBtn = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newNickname = newNicknameField.getText().trim();
                if (newNickname.isEmpty()) {
                    showError("Nickname cannot be empty.");
                    return;
                }
                String error = UsersManager.getInstance().validateAndChangeNickname(newNickname);
                if (error != null) {
                    showError(error);
                } else {
                    showToast("Nickname changed successfully!", "IMAGE_UI_GENERIC_VTB");
                    targetLabel.setText(newNickname);
                    refreshTopBar();
                    closeModal();
                    showProfileDialog();
                }
            }
        });

        TextButton cancelBtn = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
                showProfileDialog();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(okBtn).padRight(10);
        buttonRow.add(cancelBtn);
        subBox.add(buttonRow).colspan(2).center();

        showModal(subBox);
    }

    private void showChangePasswordDialog() {
        BorderedTable subBox = new BorderedTable();
        subBox.pad(25);

        Label title = createBlackLabel("Change Password");
        subBox.add(title).colspan(2).center().padBottom(15).row();

        subBox.add(createBlackLabel("Old Password:")).right().padRight(10);
        TextField oldPasswordField = new TextField("", skin);
        oldPasswordField.setPasswordMode(true);
        oldPasswordField.setPasswordCharacter('*');
        subBox.add(oldPasswordField).width(200).padBottom(10).row();

        subBox.add(createBlackLabel("New Password:")).right().padRight(10);
        TextField newPasswordField = new TextField("", skin);
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');
        subBox.add(newPasswordField).width(200).padBottom(10).row();

        subBox.add(createBlackLabel("Confirm Password:")).right().padRight(10);
        TextField confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        subBox.add(confirmPasswordField).width(200).padBottom(20).row();

        TextButton okBtn = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String oldPw = oldPasswordField.getText();
                String newPw = newPasswordField.getText();
                String confirmPw = confirmPasswordField.getText();
                String error = UsersManager.getInstance().validateAndChangePassword(newPw, confirmPw, oldPw);
                if (error != null) {
                    showError(error);
                } else {
                    showToast("Password changed successfully!", "IMAGE_UI_GENERIC_VTB");
                    closeModal();
                    showProfileDialog();
                }
            }
        });

        TextButton cancelBtn = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
                showProfileDialog();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(okBtn).padRight(10);
        buttonRow.add(cancelBtn);
        subBox.add(buttonRow).colspan(2).center();

        showModal(subBox);
    }

    private void openLeaderBoardDialog() {
        LeaderBoardDialog dialog = new LeaderBoardDialog(skin, new Runnable() {
            @Override
            public void run() {
                closeModal();
            }
        });
        showModal(dialog);
    }

    // ================================================================
    // BaseView methods
    // ================================================================

    @Override
    public void showError(String error) {
        showToast(error, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
    }
}

package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MainMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.News.News;
import com.test1.PlantsVsZombies.src.Model.User.User;
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

        TextButton profileButton = new TextButton("Profile", skin, "brown");
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Profile);
            }
        });

        bottomTable.add(profileButton).left().padLeft(20).padRight(10);
        bottomTable.add(createNewsButtonStack()).left().padRight(20);

        bottomTable.add().expandX();

        TextButton leaderboardButton = new TextButton("Leaderboard", skin, "brown");
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.LeaderBoard);
            }
        });

        bottomTable.add(createSettingsButton()).right().padRight(10);
        bottomTable.add(leaderboardButton).right().padRight(20);

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
    // NEWS BUTTON WITH EXCLAMATION MARK
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

        // Overlay for exclamation mark (only if unread news exist)
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

    private void refreshNewsButton() {
        if (bottomTable == null) return;
        bottomTable.clearChildren();
        TextButton profileButton = new TextButton("Profile", skin, "brown");
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Profile);
            }
        });
        bottomTable.add(profileButton).left().padLeft(20).padRight(10);
        bottomTable.add(createNewsButtonStack()).left().padRight(20);
        bottomTable.add().expandX();
        TextButton leaderboardButton = new TextButton("Leaderboard", skin, "brown");
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.LeaderBoard);
            }
        });
        bottomTable.add(createSettingsButton()).right().padRight(10);
        bottomTable.add(leaderboardButton).right().padRight(20);
        bottomTable.invalidateHierarchy();
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
        title.setFontScale(0.8f);
        box.add(title).colspan(2).center().padBottom(20).row();

        box.add(createBlackLabel("Difficulty:")).left().padRight(20).row();
        Table difficultyRow = new Table();
        ButtonGroup<CheckBox> diffGroup = new ButtonGroup<>();
        diffGroup.setMinCheckCount(1);
        diffGroup.setMaxCheckCount(1);

        int currentDiff = loggedUser.getUserProgress().getGameDifficulty();
        for (int i = 1; i <= 5; i++) {
            CheckBox cb = new CheckBox(" " + i, skin);
            cb.getLabel().setColor(Color.BLACK);
            if (i == currentDiff) cb.setChecked(true);
            diffGroup.add(cb);
            difficultyRow.add(cb).padRight(10);
        }
        box.add(difficultyRow).left().padBottom(15).row();

        box.add(createBlackLabel("Game Speed:")).left().padRight(20).row();
        Table speedRow = new Table();
        ButtonGroup<CheckBox> speedGroup = new ButtonGroup<>();
        speedGroup.setMinCheckCount(1);
        speedGroup.setMaxCheckCount(1);

        int currentSpeed = loggedUser.getUserProgress().getGameSpeed();
        for (int i = 1; i <= 3; i++) {
            CheckBox cb = new CheckBox(" " + i, skin);
            cb.getLabel().setColor(Color.BLACK);
            if (i == currentSpeed) cb.setChecked(true);
            speedGroup.add(cb);
            speedRow.add(cb).padRight(10);
        }
        box.add(speedRow).left().padBottom(15).row();

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

        Table buttonRow = new Table();
        TextButton okButton = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int diff = 1;
                for (CheckBox cb : diffGroup.getButtons()) {
                    if (cb.isChecked()) {
                        diff = Integer.parseInt(cb.getText().toString().trim());
                        break;
                    }
                }
                um.changeDifficulty(String.valueOf(diff));

                int speed = 1;
                for (CheckBox cb : speedGroup.getButtons()) {
                    if (cb.isChecked()) {
                        speed = Integer.parseInt(cb.getText().toString().trim());
                        break;
                    }
                }
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
        wrapper.add(scrollPane).size(450, 500);
        showModal(wrapper);
    }

    // ================================================================
    // NEWS MODAL – CORRECT UNREAD SNAPSHOT + CENTERED TEXT
    // ================================================================

    private boolean showUnread = true;   // default: show unread news

    private void showNewsDialog() {
        // 1. Take a snapshot of unread messages BEFORE marking them read
        User user = UsersManager.getInstance().getLoggedInUser();
        unreadMessages.clear();
        if (user != null) {
            unreadMessages = UsersManager.getInstance().getUnreadNews();
        }

        // 2. Mark all unread as read (this updates the user and removes exclamation)
//        UsersManager.getInstance().getUnreadNews();
        refreshNewsButton();

        // 3. Build the modal
        BorderedTable box = new BorderedTable();
        box.pad(20);

        Label title = createLabel("NEWS", "FBUSV8C5EI_2", Color.BLACK);
        title.setFontScale(0.8f);
        box.add(title).center().padBottom(15).row();

        Table newsTable = new Table();
        newsTable.setBackground(skin.getDrawable("image_ui_mainmenu_mm_settings_tab_10"));
        newsTable.top().left(); // we'll center the content via cell alignment
        populateNewsTable(newsTable, showUnread);

        ScrollPane scrollPane = new ScrollPane(newsTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setOverscroll(false, false);
        box.add(scrollPane).size(500, 300).padBottom(15).row();

        // Toggle button – text depends on current state
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

        // The list of messages to display:
        // If unreadOnly, use the snapshot we took; otherwise, use all messages.
        ArrayList<String> messagesToShow;
        if (unreadOnly) {
            messagesToShow = new ArrayList<>(unreadMessages);
        } else {
            messagesToShow = UsersManager.getInstance().getAllNews();
        }

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

        // Set a uniform width for the labels to force wrapping, and center them.
        float labelWidth = 460f;
        for (String msg : messagesToShow) {
            Label label = createLabel(msg, "FBUSV8C5EI_1", Color.BLACK);
            label.setWrap(true);
            label.setFontScale(0.5f);
            label.setAlignment(Align.center);  // center text within the label
            // Add with center alignment so the label itself is centered in the table cell
            newsTable.add(label).center().pad(4, 6, 4, 6).width(labelWidth).row();
        }
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

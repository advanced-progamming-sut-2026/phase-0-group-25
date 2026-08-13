package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MainMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.News.News;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.MainMenuView;

public class MainMenuScreen extends AbstractScreen implements MainMenuView {

    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String LOGO_ASSET_ID = "IMAGE_UI_MAINMENU_PVZ2_LOGO_HORIZONTAL";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    // --- Bottom Bar Asset IDs ---
    private static final String NEWS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_NEWSBUTTON_BUTTONS_HUD_NEWS_SELECTED_COPY_2";
    private static final String EXCLAMATION_MARK_ASSET_ID = "IMAGE_UI_CLAIM_SMALL";
    private static final String SETTINGS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_SETTINGSBUTTON_BUTTONS_HUD_SETTINGS_NORMAL"; // TODO: Replace with your exact settings texture ID

    private MainMenu menuController;

    public void setMenuController(MainMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        // Background Image
        TextureRegion bgRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setScaling(Scaling.fill);
            screenStack.add(bgImage);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        // --- Top Bar (Logout Button at Top-Right) ---
        Table topTable = new Table();
        TextButton logoutButton = new TextButton("Logout", skin, "brown");
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuController != null) {
                    menuController.logout();
                }
            }
        });

        topTable.add().expandX(); // Pushes logout button to top-right
        topTable.add(logoutButton).right().pad(20);

        uiTable.add(topTable).fillX().top().row();

        // --- Main Container (Logo & Play Button) ---
        Table mainContainer = new Table();

        // Logo Image (falls back to label if texture not found)
        TextureRegion logoRegion = textureBank.region(LOGO_ASSET_ID);
        if (logoRegion != null) {
            Image logoImage = new Image(logoRegion);
            mainContainer.add(logoImage).center().padBottom(20).row();
        } else {
            Label logoLabel = new Label("PLANTS vs ZOMBIES", skin);
            mainContainer.add(logoLabel).center().padBottom(20).row();
        }

        // Play Button right beneath the logo
        TextButton playButton = new TextButton("Play", skin, "purple");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Game);
            }
        });
        mainContainer.add(playButton).center();

        uiTable.add(mainContainer).expand().center().padBottom(40).row();

        // --- Bottom Bar ---
        Table bottomTable = new Table();

        // Profile Button (Left of News button)
        TextButton profileButton = new TextButton("Profile", skin, "brown");
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Profile);
            }
        });

        // Add Profile & News buttons on the bottom left
        bottomTable.add(profileButton).left().padLeft(20).padRight(10);
        bottomTable.add(createNewsButtonStack()).left().padRight(20);

        // Spacer pushes left controls to bottom-left and right controls to bottom-right
        bottomTable.add().expandX();

        // Leaderboard Button (Right of Settings button)
        TextButton leaderboardButton = new TextButton("Leaderboard", skin, "brown");
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.LeaderBoard);
            }
        });

        // Add Settings & Leaderboard buttons on the bottom right
        bottomTable.add(createSettingsButton()).right().padRight(10);
        bottomTable.add(leaderboardButton).right().padRight(20);

        uiTable.add(bottomTable).fillX().bottom().padBottom(20);

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }

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

        // News Button Image with Darkened Pressed/Shadow Effect
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
                    MenuManager.getInstance().changeMenu(MenuType.News);
                }
            });
            newsActor = newsButton;
        } else {
            TextButton fallbackNewsButton = new TextButton("News", skin);
            fallbackNewsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.News);
                }
            });
            newsActor = fallbackNewsButton;
        }

        newsStack.add(newsActor);

        // Exclamation Mark Overlay on Top-Right (if unread news exists)
        if (hasUnreadNews()) {
            TextureRegion exclRegion = textureBank.region(EXCLAMATION_MARK_ASSET_ID);
            Table overlayTable = new Table();
            if (exclRegion != null) {
                Image exclImage = new Image(exclRegion);
                exclImage.setTouchable(Touchable.disabled);
                overlayTable.add(exclImage).size(24, 24).top().right().expand();
            } else {
                Label exclLabel = new Label("!", skin);
                exclLabel.setColor(Color.RED);
                overlayTable.add(exclLabel).top().right().expand().padTop(-5).padRight(-5);
            }
            newsStack.add(overlayTable);
        }

        return newsStack;
    }

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
                    MenuManager.getInstance().changeMenu(MenuType.Setting);
                }
            });
            return settingsButton;
        } else {
            TextButton fallbackSettings = new TextButton("Settings", skin);
            fallbackSettings.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.Setting);
                }
            });
            return fallbackSettings;
        }
    }

    @Override
    public void showError(String error) {
        showToast(error, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
    }
}

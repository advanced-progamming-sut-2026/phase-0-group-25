package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.ChapterIslandAsset;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.GameMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GameMenuView;

public class ChooseChapterScreen extends AbstractScreen implements GameMenuView {

    // --- Background Asset ID ---
    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String ERROR_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    private GameMenu menuController;

    public void setMenuController(GameMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        // 1. Background Image Layer
        TextureRegion bgRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setScaling(Scaling.fill);
            screenStack.add(bgImage);
        }

        // 2. UI Content Layer
        Table uiTable = new Table();
        uiTable.setFillParent(true);

        // --- Top Bar (Currency HUD on top-left) ---
        Table topTable = new Table();
        topTable.add(createCurrencyHud()).left().pad(15);
        topTable.add().expandX();
        uiTable.add(topTable).fillX().top().row();

        // --- Middle Section: Horizontal Island ScrollPane ---
        Table islandsTable = new Table();
        islandsTable.defaults().pad(20);

        islandsTable.add(createIslandButton(ChapterType.ANCIENT_EGYPT));
        islandsTable.add(createIslandButton(ChapterType.DARK_AGE));
        islandsTable.add(createIslandButton(ChapterType.FROSTBITE_CAVES));
        islandsTable.add(createIslandButton(ChapterType.BIG_WAVE_BEACH));

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = null;

        ScrollPane scrollPane = new ScrollPane(islandsTable, scrollStyle);
        scrollPane.setScrollingDisabled(false, true); // Enable horizontal scroll only
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(true);

        uiTable.add(scrollPane).expand().fill().center().row();

        // --- Bottom Bar (Back button at bottom-left) ---
        Table bottomTable = new Table();
        bottomTable.add(createBackButton(MenuType.Main)).left().pad(15);
        bottomTable.add().expandX();

        uiTable.add(bottomTable).fillX().bottom().padBottom(10);

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }

    private Actor createIslandButton(ChapterType chapterType) {
        String assetId = ChapterIslandAsset.getAssetId(chapterType);
        Table container = new Table();

        User user = UsersManager.getInstance().getLoggedInUser();
        boolean isUnlocked = (user != null && user.getUserProgress() != null
            && user.getUserProgress().getUnlockedChaptersAndLevels().containsKey(chapterType));

        // Calculate completed levels for this chapter -- the map now stores
        // the last COMPLETED level directly (0 = none done yet), so no
        // "-1" adjustment is needed here anymore.
        int levelsDone = 0;
        if (isUnlocked) {
            int lastCompletedLevel = user.getUserProgress().getUnlockedChaptersAndLevels().getOrDefault(chapterType, 0);
            levelsDone = Math.min(Math.max(0, lastCompletedLevel), ChapterType.LEVELS_PER_CHAPTER);
        }

        TextureRegion region = textureBank.region(assetId);
        Actor buttonActor;

        if (region != null) {
            TextureRegionDrawable drawable = new TextureRegionDrawable(region);
            Button.ButtonStyle style = new Button.ButtonStyle();

            if (isUnlocked) {
                style.up = drawable;
                style.down = drawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));
            } else {
                // Locked shadow tint (darkened)
                style.up = drawable.tint(new Color(0.35f, 0.35f, 0.35f, 1f));
                style.down = drawable.tint(new Color(0.2f, 0.2f, 0.2f, 1f));
            }

            Button islandButton = new Button(style);
            buttonActor = islandButton;
        } else {
            TextButton textBtn = new TextButton(chapterType.getName() + (isUnlocked ? "" : " (Locked)"), skin);
            buttonActor = textBtn;
        }

        buttonActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isUnlocked) {
                    showError("This chapter is locked!");
                    return;
                }
                if (menuController != null) {
                    menuController.enterChapter(chapterType.getName());
                }
            }
        });

        // 1. Island Image Button
        container.add(buttonActor).size(260, 320).row();

        // 2. Levels Completed Ratio (e.g., "1/4")
        Label progressLabel = createLabel(levelsDone + "/" + ChapterType.LEVELS_PER_CHAPTER, "FBUSV8C5EI_2_outline", isUnlocked ? Color.YELLOW : Color.LIGHT_GRAY);
        container.add(progressLabel).padTop(10).row();

        // 3. Chapter Name Label
        Label nameLabel = createLabel(chapterType.getName().toUpperCase(), "FBUSV8C5EI_2_outline", isUnlocked ? Color.WHITE : Color.GRAY);
        container.add(nameLabel).padTop(4);

        return container;
    }

    @Override
    public void showChapterEnterSuccess(String chapterName) {
        showToast("Entering " + chapterName, SUCCESS_BG_ASSET_ID);
    }

    @Override
    public void showError(String error) {
        showToast(error, ERROR_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {}
}

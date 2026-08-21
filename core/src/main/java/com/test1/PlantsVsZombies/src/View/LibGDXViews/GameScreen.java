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
import com.test1.PlantsVsZombies.src.Enums.LevelIslandAsset;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.GameMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.Chapter;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.Level;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GameMenuView;

public class GameScreen extends AbstractScreen implements GameMenuView {




    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    private static final String TRAVEL_LOG_BUTTON_ASSET_ID =
        "IMAGE_UI_HUD_TASKLIST_BUTTONS_HUD_TASK_LIST_NORMAL";

    private static final String GREENHOUSE_BUTTON_ASSET_ID =
        "IMAGE_UI_GENERIC_BUTTONS_HUD_ZG_NORMAL";




    private static final float DEFAULT_ICON_BUTTON_SIZE = 70f;
    private static final float CHAPTER_ICON_WIDTH = 260f;
    private static final float CHAPTER_ICON_HEIGHT = 320f;



    private static final float CENTER_BLOCK_UPWARD_SHIFT = 60f;

    private final GameMenu menuController;

    public GameScreen(GameMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);




        TextureRegion backgroundRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (backgroundRegion != null) {
            Image background = new Image(backgroundRegion);
            background.setScaling(Scaling.fill);
            screenStack.add(background);
        }




        Table uiTable = new Table();
        uiTable.setFillParent(true);




        Table topBar = new Table();

        topBar.add(createCurrencyHud())
            .left()
            .padLeft(15)
            .padTop(15);

        topBar.add()
            .expandX();


        topBar.add(createBackButton(MenuType.Game))
            .right()
            .size(70, 70)
            .padRight(15)
            .padTop(15);

        uiTable.add(topBar)
            .fillX()
            .top()
            .row();




        Chapter chapter = menuController.getChapter();

        Table centerBlock = new Table();

        if (chapter != null) {
            Label title = createLabel(chapter.getChapterType().getName().toUpperCase(), "FBUSV8C5EI_1", Color.WHITE);
            centerBlock.add(title).padBottom(10).row();

            Actor chapterIcon = createChapterIcon(chapter.getChapterType());
            centerBlock.add(chapterIcon)
                .size(CHAPTER_ICON_WIDTH, CHAPTER_ICON_HEIGHT)
                .padBottom(10)
                .row();

            Table levelsTable = new Table();
            levelsTable.defaults().pad(25);
            addLevels(levelsTable, chapter);
            centerBlock.add(levelsTable).row();
        }



        centerBlock.padBottom(CENTER_BLOCK_UPWARD_SHIFT);







        ScrollPane.ScrollPaneStyle centerScrollStyle = new ScrollPane.ScrollPaneStyle();
        centerScrollStyle.background = null;

        ScrollPane centerScrollPane = new ScrollPane(centerBlock, centerScrollStyle);
        centerScrollPane.setScrollingDisabled(true, false);
        centerScrollPane.setOverscroll(false, false);
        centerScrollPane.setFadeScrollBars(true);

        uiTable.add(centerScrollPane)
            .expand()
            .fill()
            .row();





        TextureRegion travelLogRegion = textureBank.region(TRAVEL_LOG_BUTTON_ASSET_ID);
        float iconBtnWidth = (travelLogRegion != null)
            ? travelLogRegion.getRegionWidth()
            : DEFAULT_ICON_BUTTON_SIZE;
        float iconBtnHeight = (travelLogRegion != null)
            ? travelLogRegion.getRegionHeight()
            : DEFAULT_ICON_BUTTON_SIZE;

        Table bottomBar = new Table();


        Actor greenhouseButton = createAssetButton(
            GREENHOUSE_BUTTON_ASSET_ID,
            "Greenhouse",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.GreenHouse);
                }
            }
        );

        TextButton collectionButton = new TextButton("Collection", skin, "brown");
        collectionButton.pad(8, 16, 8, 16);
        collectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Collection);
            }
        });

        Table leftButtons = new Table();
        leftButtons.add(greenhouseButton)
            .size(iconBtnWidth, iconBtnHeight)
            .padRight(12);
        leftButtons.add(collectionButton);


        TextButton choosePlantButton = new TextButton("Choose Plant", skin, "brown");
        choosePlantButton.pad(8, 16, 8, 16);
        choosePlantButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.ChoosePlant);
            }
        });

        Actor travelLogButton = createAssetButton(
            TRAVEL_LOG_BUTTON_ASSET_ID,
            "Travel Log",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.TravelLog);
                }
            }
        );

        Table rightButtons = new Table();
        rightButtons.add(choosePlantButton).padRight(12);
        rightButtons.add(travelLogButton)
            .size(iconBtnWidth, iconBtnHeight);


        bottomBar.add(leftButtons).left();
        bottomBar.add().expandX();
        bottomBar.add(rightButtons).right();

        uiTable.add(bottomBar)
            .fillX()
            .bottom()
            .padLeft(15)
            .padRight(15)
            .padBottom(15)
            .row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();
    }






    private Actor createChapterIcon(ChapterType chapterType) {
        String assetId = ChapterIslandAsset.getAssetId(chapterType);
        TextureRegion region = (assetId != null) ? textureBank.region(assetId) : null;

        if (region != null) {
            Image icon = new Image(region);
            icon.setScaling(Scaling.fit);
            return icon;
        }

        Label fallback = new Label(chapterType.getName().toUpperCase(), skin);
        fallback.setColor(Color.WHITE);
        return fallback;
    }





    private void addLevels(Table levelsTable, Chapter chapter) {
        User user = UsersManager.getInstance().getLoggedInUser();
        ChapterType chapterType = chapter.getChapterType();

        int lastCompletedLevel = 0;
        if (user != null && user.getUserProgress() != null) {
            lastCompletedLevel = user.getUserProgress()
                .getUnlockedChaptersAndLevels()
                .getOrDefault(chapterType, 0);
        }

        int maxPlayableLevel = Math.min(
            lastCompletedLevel + 1,
            ChapterType.LEVELS_PER_CHAPTER
        );

        for (Level level : chapter.getLevels()) {
            int levelNumber = level.getLevelNumber();
            boolean isUnlocked = levelNumber <= maxPlayableLevel;

            levelsTable.add(
                createLevelButton(level, isUnlocked)
            );

            if (levelNumber % 4 == 0) {
                levelsTable.row();
            }
        }
    }

    private Actor createLevelButton(Level level, boolean isUnlocked) {
        String assetId = LevelIslandAsset.getAssetId(level.getChapterType());
        TextureRegion region = textureBank.region(assetId);

        if (region == null) {
            return createFallbackLevelButton(level, isUnlocked);
        }

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(region);
        Button.ButtonStyle style = new Button.ButtonStyle();

        if (isUnlocked) {
            style.up = normalDrawable;
            style.down = normalDrawable.tint(new Color(0.70f, 0.70f, 0.70f, 1f));
        } else {
            style.up = normalDrawable.tint(new Color(0.35f, 0.35f, 0.35f, 1f));
            style.down = normalDrawable.tint(new Color(0.20f, 0.20f, 0.20f, 1f));
        }

        Button levelButton = new Button(style);
        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isUnlocked) {
                    showError(
                        "This level is locked. You must beat level "
                            + (level.getLevelNumber() - 1)
                            + " first."
                    );
                    return;
                }
                menuController.startGame(level.getLevelNumber());
            }
        });




        Label levelNumberLabel = createLabel(
            String.valueOf(level.getLevelNumber()),
            "FBUSV8C5EI_1",
            isUnlocked ? Color.WHITE : Color.LIGHT_GRAY
        );



        Table levelContainer = new Table();
        levelContainer.add(levelButton).row();
        levelContainer.add(levelNumberLabel).padTop(8);

        return levelContainer;
    }

    private Actor createFallbackLevelButton(Level level, boolean isUnlocked) {
        String text = "Level " + level.getLevelNumber();
        if (!isUnlocked) {
            text += "\nLOCKED";
        }

        TextButton fallbackButton = new TextButton(text, skin);
        if (!isUnlocked) {
            fallbackButton.getLabel().setColor(Color.GRAY);
        }

        fallbackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isUnlocked) {
                    showError(
                        "This level is locked. You must beat level "
                            + (level.getLevelNumber() - 1)
                            + " first."
                    );
                    return;
                }
                menuController.startGame(level.getLevelNumber());
            }
        });

        return fallbackButton;
    }





    private Actor createAssetButton(
        String assetId,
        String fallbackText,
        ClickListener listener
    ) {
        if (assetId != null && !assetId.isEmpty()) {
            TextureRegion region = textureBank.region(assetId);
            if (region != null) {
                TextureRegionDrawable drawable = new TextureRegionDrawable(region);
                Button.ButtonStyle style = new Button.ButtonStyle();
                style.up = drawable;
                style.down = drawable.tint(new Color(0.70f, 0.70f, 0.70f, 1f));

                Button button = new Button(style);
                if (listener != null) {
                    button.addListener(listener);
                }
                return button;
            }
        }

        TextButton fallback = new TextButton(fallbackText, skin);
        fallback.pad(8, 16, 8, 16);
        if (listener != null) {
            fallback.addListener(listener);
        }
        return fallback;
    }





    @Override
    public void showChapterEnterSuccess(String chapterName) {
        showToast("Entering " + chapterName, "IMAGE_UI_GENERIC_VTB");
    }

    @Override
    public void showError(String error) {
        showToast(error, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {}
}

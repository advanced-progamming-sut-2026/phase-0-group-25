// file: core/src/main/java/com/test1/PlantsVsZombies/src/View/LibGDXViews/GreenHouseScreen.java
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
import com.test1.PlantsVsZombies.src.Menu.GreenHouseMenu;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenhousePlant;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GreenHouseMenuView;
import pvz.skin.BorderedTable;

public class GreenHouseScreen extends AbstractScreen implements GreenHouseMenuView {

    // IMPORTANT: Swap these with the actual Asset IDs from your TextureBank!
    private static final String BACKGROUND_ASSET_ID = "IMAGE_BACKGROUNDS_ZEN_GARDEN";
    private static final String SHOP_BUTTON_ASSET_ID = "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL";
    private static final String POT_ASSET_ID = "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
    private static final String LOCK_ASSET_ID = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
    private static final String TIMER_BOX_ASSET_ID = "IMAGE_ZEN_GARDEN_FINISH_TIMER_BACKGROUND";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    // Absolute positions for the 3x4 grid pots (Set based on 1280x720 background scaling)
    private static final float[][] POT_X = {
        { 613f, 786f, 961f, 1128f }, // Row 1
        { 613f, 786f, 961f, 1128f }, // Row 2
        { 613f, 786f, 961f, 1128f }  // Row 3
    };
    private static final float[][] POT_Y = {
        { 473f, 473f, 473f, 473f }, // Row 1
        { 305f, 305f, 305f, 305f }, // Row 2
        { 150f, 150f, 150f, 150f }  // Row 3
    };

    private GreenHouseMenu menuController;
    private WidgetGroup potGroup;
    private Label[][] timerLabels = new Label[3][4];

    public void setMenuController(GreenHouseMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        // Background
        TextureRegion backgroundRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (backgroundRegion != null) {
            Image background = new Image(backgroundRegion);
            background.setScaling(Scaling.fill);
            screenStack.add(background);
        }

        // Pot group container using WidgetGroup (supports setFillParent)
        potGroup = new WidgetGroup();
        potGroup.setFillParent(true);
        screenStack.add(potGroup);

        // UI Layer
        Table uiTable = new Table();
        uiTable.setFillParent(true);

        // Top bar
        Table topBar = new Table();
        topBar.add(createCurrencyHud()).left().top().padLeft(15).padTop(15);
        topBar.add().expandX().fillX();

        // Shop button
        TextureRegion shopRegion = textureBank.region(SHOP_BUTTON_ASSET_ID);
        if (shopRegion != null) {
            ImageButton shopBtn = new ImageButton(new TextureRegionDrawable(shopRegion));
            shopBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showToast("Shop opened!", SUCCESS_BG_ASSET_ID);
                }
            });
            topBar.add(shopBtn).right().top().padRight(15).padTop(15);
        } else {
            TextButton shopBtn = createSkinButton("Shop", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showToast("Shop opened!", SUCCESS_BG_ASSET_ID);
                }
            });
            topBar.add(shopBtn).right().top().padRight(15).padTop(15);
        }
        uiTable.add(topBar).expandX().fillX().top().row();

        uiTable.add().expandY().fillY().row();

        // Bottom bar
        Table bottomBar = new Table();
        bottomBar.add(createBackButton(MenuType.Game)).left().bottom().size(70, 70).padLeft(15).padBottom(15);
        bottomBar.add().expandX().fillX();
        uiTable.add(bottomBar).expandX().fillX().bottom().row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        refreshPots();
    }

    private void refreshPots() {
        potGroup.clearChildren();
        UserProgress progress = UsersManager.getInstance().getLoggedInUser().getUserProgress();
        boolean[][] unlocked = progress.getUnlockedPots();
        GreenhousePlant[][] plants = progress.getPotPlants();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                final int gridX = x + 1;
                final int gridY = y + 1;

                Stack potStack = new Stack();
                potStack.setSize(120f, 150f);
                potStack.setPosition(POT_X[y][x], POT_Y[y][x]);

                // Render based on pot state
                if (!unlocked[y][x]) {
                    // Locked
                    TextureRegion lockRegion = textureBank.region(LOCK_ASSET_ID);
                    if (lockRegion != null) {
                        ImageButton lockBtn = new ImageButton(new TextureRegionDrawable(lockRegion));
                        lockBtn.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float ex, float ey) {
                                openBuyPotDialog(gridX, gridY);
                            }
                        });
                        potStack.add(lockBtn);
                    } else {
                        TextButton lockBtn = createSkinButton("Unlock", "brown", new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float ex, float ey) {
                                openBuyPotDialog(gridX, gridY);
                            }
                        });
                        potStack.add(lockBtn);
                    }
                } else {
                    GreenhousePlant plant = plants[y][x];
                    if (plant == null) {
                        // Unlocked but Empty
                        TextureRegion potRegion = textureBank.region(POT_ASSET_ID);
                        if (potRegion != null) {
                            ImageButton potBtn = new ImageButton(new TextureRegionDrawable(potRegion));
                            potBtn.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    String result = menuController.plantPot(gridX, gridY);
                                    if (result.startsWith("Planted")) {
                                        showToast("Plant successful!", SUCCESS_BG_ASSET_ID);
                                        refreshPots();
                                    } else {
                                        showError(result);
                                    }
                                }
                            });
                            potStack.add(potBtn);
                        } else {
                            TextButton potBtn = createSkinButton("Plant", "green", new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    String result = menuController.plantPot(gridX, gridY);
                                    if (result.startsWith("Planted")) {
                                        showToast("Plant successful!", SUCCESS_BG_ASSET_ID);
                                        refreshPots();
                                    } else {
                                        showError(result);
                                    }
                                }
                            });
                            potStack.add(potBtn);
                        }
                    } else {
                        // Unlocked and Planted
                        TextureRegion potRegion = textureBank.region(POT_ASSET_ID);
                        if (potRegion != null) {
                            potStack.add(new Image(potRegion));
                        }

                        // Plant Animation
                        Actor anim = createAnimationActor(plant.getType().getIdleAnimationPath(), plant.getType().getStateName());
                        Table animContainer = new Table();
                        animContainer.add(anim).size(120f, 120f).center().padBottom(30f);
                        animContainer.setTouchable(Touchable.disabled);
                        potStack.add(animContainer);

                        if (plant.isReady()) {
                            // Ready to collect overlay button
                            ImageButton collectBtn = new ImageButton(skin);
                            collectBtn.setColor(1f, 1f, 1f, 0f); // Transparent clickable area
                            collectBtn.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    String result = menuController.collectPot(gridX, gridY);
                                    if (result.startsWith("Collected") || result.contains("already have a boost")) {
                                        openRewardDialog(result);
                                        refreshPots();
                                        updateCurrencyHud();
                                    } else {
                                        showError(result);
                                    }
                                }
                            });
                            potStack.add(collectBtn);

                            Table labelContainer = new Table();
                            labelContainer.bottom();
                            labelContainer.add(createLabel("READY", "FBUSV8C5EI_1_outline", Color.YELLOW)).padBottom(5);
                            labelContainer.setTouchable(Touchable.disabled);
                            potStack.add(labelContainer);
                        } else {
                            // Growing state (Timer + Grow button)
                            Table overlay = new Table();
                            overlay.bottom();

                            // Timer Label
                            Label timeLbl = createLabel(String.format("%.1fh", plant.getRemainingHours()), "FBUSV8C5EI_1_outline", Color.WHITE);
                            timeLbl.setFontScale(0.6f);
                            timerLabels[y][x] = timeLbl;

                            Table timerBox = new Table();
                            TextureRegion boxRegion = textureBank.region(TIMER_BOX_ASSET_ID);
                            if (boxRegion != null) {
                                timerBox.setBackground(new NinePatchDrawable(new NinePatch(boxRegion, 8, 8, 8, 8)));
                            }
                            timerBox.add(timeLbl).pad(4, 8, 4, 8);
                            overlay.add(timerBox).padBottom(5).row();

                            // Grow button
                            int cost = (int) Math.ceil(plant.getRemainingHours());
                            TextButton growBtn = createSkinButton("Grow (" + cost + ")", "green", new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    openGrowDialog(gridX, gridY, cost);
                                }
                            });
                            growBtn.getLabel().setFontScale(0.6f);
                            growBtn.pad(5, 10, 5, 10);
                            overlay.add(growBtn);

                            potStack.add(overlay);
                        }
                    }
                }
                potGroup.addActor(potStack);
            }
        }
    }

    private void openBuyPotDialog(int gridX, int gridY) {
        Table box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel("Unlock New Pot?");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(20).row();

        box.add(createBlackLabel("Cost: 50 Gems")).colspan(2).left().padBottom(20).row();

        TextButton buyBtn = createSkinButton("Buy", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String error = menuController.buyPot(gridX, gridY);
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast("Pot Unlocked!", SUCCESS_BG_ASSET_ID);
                    updateCurrencyHud();
                    refreshPots();
                }
            }
        });

        TextButton cancelBtn = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        Table buttons = new Table();
        buttons.add(buyBtn).padRight(10);
        buttons.add(cancelBtn);
        box.add(buttons).colspan(2);

        showModal(box);
    }

    private void openGrowDialog(int gridX, int gridY, int cost) {
        Table box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel("Accelerate Growth?");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(20).row();

        box.add(createBlackLabel("Cost: " + cost + " Gems")).colspan(2).left().padBottom(20).row();

        TextButton growBtn = createSkinButton("Grow", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String error = menuController.growPot(gridX, gridY);
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast("Growth Accelerated!", SUCCESS_BG_ASSET_ID);
                    updateCurrencyHud();
                    refreshPots();
                }
            }
        });

        TextButton cancelBtn = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        Table buttons = new Table();
        buttons.add(growBtn).padRight(10);
        buttons.add(cancelBtn);
        box.add(buttons).colspan(2);

        showModal(box);
    }

    private void openRewardDialog(String message) {
        Table box = new BorderedTable();
        box.pad(40);

        Label title = createBlackLabel("Reward!");
        title.setFontScale(1.2f);
        box.add(title).padBottom(20).row();

        Label msgLbl = createBlackLabel(message);
        msgLbl.setWrap(true);
        msgLbl.setAlignment(Align.center);
        box.add(msgLbl).width(300f).padBottom(30).row();

        TextButton okBtn = createSkinButton("Awesome!", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        box.add(okBtn);

        showModal(box);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // Live update timers without full UI rebuilds every frame
        UserProgress progress = UsersManager.getInstance().getLoggedInUser().getUserProgress();
        GreenhousePlant[][] plants = progress.getPotPlants();
        boolean needsRefresh = false;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                if (timerLabels[y][x] != null && plants[y][x] != null) {
                    GreenhousePlant plant = plants[y][x];
                    if (plant.isReady()) {
                        needsRefresh = true;
                    } else {
                        timerLabels[y][x].setText(String.format("%.1fh", plant.getRemainingHours()));
                    }
                }
            }
        }
        if (needsRefresh) refreshPots();
    }

    // Terminal view implementations left empty since we use GUI now
    @Override public void showGreenhouseStatus(String status) {}
    @Override public void showPlantPlanted(String plantName, int x, int y) {}
    @Override public void showCollectedMarigold(int amount) {}
    @Override public void showCollectedBoost(String plantName) {}
    @Override public void showAlreadyHasBoost(String plantName) {}

    @Override
    public void showPotCleared() {}

    @Override public void showGrowthAccelerated() {}

    @Override
    public void showError(String errorMessage) {
        showToast(errorMessage, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
        updateCurrencyHud();
        refreshPots();
    }
}

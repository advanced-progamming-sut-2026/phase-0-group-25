// file: core/src/main/java/com/test1/PlantsVsZombies/src/View/LibGDXViews/GreenHouseScreen.java
package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenHouseManager;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenhousePlant;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GreenHouseMenuView;
import pvz.skin.BorderedTable;

public class GreenHouseScreen extends AbstractScreen implements GreenHouseMenuView {

    // ==========================================
    // ASSET IDENTIFIERS
    // ==========================================
    private static final String BACKGROUND_ASSET_ID = "IMAGE_BACKGROUNDS_ZEN_GARDEN";
    private static final String SHOP_BUTTON_ASSET_ID = "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL";
    private static final String POT_ASSET_ID = "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
    private static final String LOCK_ASSET_ID = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
    private static final String TIMER_BOX_ASSET_ID = "IMAGE_ZEN_GARDEN_FINISH_TIMER_BACKGROUND";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    // ==========================================
    // 3 ROWS x 4 COLUMNS POT COORDINATES (X, Y)
    // Measured from bottom-left of the original background
    // ==========================================
    private static final float[][] ORIGINAL_POT_X = {
        { 560f, 734f, 900f, 1067f }, // Row 1 (y = 1)
        { 560f, 734f, 900f, 1067f }, // Row 2 (y = 2)
        { 560f, 734f, 900f, 1067f }  // Row 3 (y = 3)
    };

    private static final float[][] POT_Y = {
        { 420f, 420f, 420f, 420f }, // Row 1 (y = 1)
        { 260f, 260f, 260f, 260f }, // Row 2 (y = 2)
        { 95f, 95f, 95f, 95f }      // Row 3 (y = 3)
    };

    // Crop the background: remove 190px from left and right
    private static final float CROP_LEFT = 190f;
    private static final float CROP_RIGHT = 190f;

    // Fixed unscaled sizes
    private static final float POT_WIDTH = 120f;
    private static final float POT_HEIGHT = 90f;
    private static final float ANIM_SIZE = 120f;

    private GreenHouseMenu menuController;
    private Group potGroup;
    private Label[][] timerLabels = new Label[UserProgress.getPotRowCount()][UserProgress.getPotColumnCount()];

    // Background projection metrics for exact anchoring
    private float bgWidth;
    private float bgHeight;
    private float bgStartX = 0f;
    private float bgStartY = 0f;
    private float bgScale = 1f;
    private TextureRegion croppedBg;

    public void setMenuController(GreenHouseMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        // 1. Get background texture and crop it
        TextureRegion bgRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (bgRegion == null) {
            showError("Background not found!");
            return;
        }
        int cropX = (int) CROP_LEFT;
        int cropWidth = bgRegion.getRegionWidth() - (int) CROP_LEFT - (int) CROP_RIGHT;
        croppedBg = new TextureRegion(bgRegion, cropX, 0, cropWidth, bgRegion.getRegionHeight());
        bgWidth = croppedBg.getRegionWidth();
        bgHeight = croppedBg.getRegionHeight();

        // 2. Compute background projection metrics
        updateBackgroundMetrics();

        // 3. Root stack
        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        // 4. Background image – fills the screen with Scaling.fill
        Image bgImage = new Image(croppedBg);
        bgImage.setScaling(Scaling.fill);
        bgImage.setFillParent(true);
        screenStack.add(bgImage);

        // 5. Pots group – pinned on top of the background
        potGroup = new Group();
        potGroup.setTouchable(Touchable.childrenOnly);
        screenStack.add(potGroup);

        // 6. UI overlay (currency, shop, back button)
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.setTouchable(Touchable.childrenOnly);

        // Top bar
        Table topBar = new Table();
        topBar.add(createCurrencyHud()).left().top().padLeft(15).padTop(15);
        topBar.add().expandX().fillX();

        // Shop button (Top Right)
        TextureRegion shopRegion = textureBank.region(SHOP_BUTTON_ASSET_ID);
        if (shopRegion != null) {
            ImageButton shopBtn = new ImageButton(new TextureRegionDrawable(shopRegion));
            shopBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.Shop);
                }
            });
            topBar.add(shopBtn).right().top().padRight(15).padTop(15);
        } else {
            TextButton shopBtn = createSkinButton("Shop", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuManager.getInstance().changeMenu(MenuType.Shop);
                }
            });
            topBar.add(shopBtn).right().top().padRight(15).padTop(15);
        }
        uiTable.add(topBar).expandX().fillX().top().row();

        uiTable.add().expandY().fillY().row();

        // Bottom bar (Back Button on bottom-left)
        Table bottomBar = new Table();
        bottomBar.add(createBackButton(MenuType.Game)).left().bottom().size(70, 70).padLeft(15).padBottom(15);
        bottomBar.add().expandX().fillX();
        uiTable.add(bottomBar).expandX().fillX().bottom().row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        // 7. Populate pots
        refreshPots();
    }

    private void updateBackgroundMetrics() {
        if (croppedBg == null || stage == null) return;
        float stageW = stage.getWidth() > 0 ? stage.getWidth() : 1280f;
        float stageH = stage.getHeight() > 0 ? stage.getHeight() : 720f;

        // Matches LibGDX Scaling.fill positioning
        float targetRatio = stageH / stageW;
        float sourceRatio = bgHeight / bgWidth;
        bgScale = (targetRatio < sourceRatio) ? (stageW / bgWidth) : (stageH / bgHeight);

        float drawnW = bgWidth * bgScale;
        float drawnH = bgHeight * bgScale;
        bgStartX = (stageW - drawnW) / 2f;
        bgStartY = (stageH - drawnH) / 2f;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateBackgroundMetrics();
        refreshPots();
    }

    private void refreshPots() {
        if (potGroup == null) return;
        potGroup.clearChildren();

        if (UsersManager.getInstance().getLoggedInUser() == null) return;
        UserProgress progress = UsersManager.getInstance().getLoggedInUser().getUserProgress();
        if (progress == null) return;

        boolean[][] unlocked = progress.getUnlockedPots();
        GreenhousePlant[][] plants = progress.getPotPlants();

        timerLabels = new Label[UserProgress.getPotRowCount()][UserProgress.getPotColumnCount()];

        for (int y = 0; y < UserProgress.getPotRowCount(); y++) {
            for (int x = 0; x < UserProgress.getPotColumnCount(); x++) {
                final int gridX = x + 1;
                final int gridY = y + 1;

                // Fixed coordinates anchored to the background texture
                float potX = bgStartX + (ORIGINAL_POT_X[y][x] - CROP_LEFT) * bgScale;
                float potY = bgStartY + POT_Y[y][x] * bgScale;

                // Fixed pot bounds (no stretching or scaling of elements)
                Group potContainer = new Group();
                potContainer.setPosition(potX, potY);
                potContainer.setSize(POT_WIDTH, POT_HEIGHT);

                if (!unlocked[y][x]) {
                    // ==========================================
                    // 1. LOCKED POT
                    // ==========================================
                    TextureRegion potRegion = textureBank.region(POT_ASSET_ID);
                    if (potRegion != null) {
                        Image potImg = new Image(potRegion);
                        potImg.setSize(POT_WIDTH, POT_HEIGHT);
                        potImg.setColor(0.5f, 0.5f, 0.5f, 0.6f);
                        potContainer.addActor(potImg);
                    }

                    TextureRegion lockRegion = textureBank.region(LOCK_ASSET_ID);
                    if (lockRegion != null) {
                        Image lockImg = new Image(lockRegion);
                        lockImg.setPosition((POT_WIDTH - 48f) / 2f, (POT_HEIGHT - 48f) / 2f);
                        potContainer.addActor(lockImg);
                    }

                    potContainer.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float ex, float ey) {
                            openBuyPotDialog(gridX, gridY);
                        }
                    });
                } else {
                    GreenhousePlant plant = plants[y][x];

                    if (plant == null) {
                        // ==========================================
                        // 2. UNLOCKED & EMPTY POT
                        // ==========================================
                        TextureRegion potRegion = textureBank.region(POT_ASSET_ID);
                        if (potRegion != null) {
                            Image potImg = new Image(potRegion);
                            potImg.setSize(POT_WIDTH, POT_HEIGHT);
                            potContainer.addActor(potImg);
                        }

                        TextButton plantBtn = createSkinButton("Plant", "green", new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float ex, float ey) {
                                String result = menuController.plantPot(gridX, gridY);
                                if (result != null && result.startsWith("Planted")) {
                                    showToast("Plant successful!", SUCCESS_BG_ASSET_ID);
                                    refreshPots();
                                } else {
                                    showError(result);
                                }
                            }
                        });
                        plantBtn.getLabel().setFontScale(0.75f);
                        plantBtn.setSize(90f, 35f);
                        plantBtn.setPosition((POT_WIDTH - 90f) / 2f, (POT_HEIGHT - 10f) / 2f);
                        potContainer.addActor(plantBtn);
                    } else {
                        // ==========================================
                        // 3. UNLOCKED & PLANTED (GROWING / READY)
                        // ==========================================
                        // Pot Graphic
                        TextureRegion potRegion = textureBank.region(POT_ASSET_ID);
                        if (potRegion != null) {
                            Image potImg = new Image(potRegion);
                            potImg.setSize(POT_WIDTH, POT_HEIGHT);
                            potContainer.addActor(potImg);
                        }

                        // Plant Animation (Lowered down to sit cleanly on top rim of the pot)
                        Actor anim = createAnimationActor(plant.getType().getIdleAnimationPath(), plant.getType().getStateName(), plant.getType().getVisibility());
                        anim.setSize(ANIM_SIZE, ANIM_SIZE);
                        anim.setPosition((POT_WIDTH - ANIM_SIZE) / 2f, 50f);
                        anim.setTouchable(Touchable.disabled);
                        potContainer.addActor(anim);

                        if (plant.isReady()) {
                            // READY Banner above the plant head
                            Table readyBadge = new Table();
                            readyBadge.setSize(90f, 26f);
                            readyBadge.setPosition((POT_WIDTH - 90f) / 2f, 125f);
                            Label readyLbl = createLabel("READY!", "FBUSV8C5EI_1_outline", Color.YELLOW);
                            readyLbl.setFontScale(0.6f);
                            readyBadge.add(readyLbl).center();
                            readyBadge.setTouchable(Touchable.disabled);
                            potContainer.addActor(readyBadge);

                            // Collect Button (Placed below the pot)
                            TextButton collectBtn = createSkinButton("Collect", "green", new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    String result = menuController.collectPot(gridX, gridY);
                                    if (result != null && (result.startsWith("Collected") || result.contains("already have a boost"))) {
                                        openRewardDialog(result);
                                        refreshPots();
                                        updateCurrencyHud();
                                    } else {
                                        showError(result);
                                    }
                                }
                            });
                            collectBtn.getLabel().setFontScale(0.75f);
                            collectBtn.setSize(100f, 35f);
                            collectBtn.setPosition((POT_WIDTH - 100f) / 2f, -38f);
                            potContainer.addActor(collectBtn);
                        } else {
                            // Timer Box (Scaled smaller and shifted to the top-left of the plant)
                            Table timerBox = new Table();
                            TextureRegion boxRegion = textureBank.region(TIMER_BOX_ASSET_ID);

                            if (boxRegion != null) {
                                timerBox.setBackground(new NinePatchDrawable(new NinePatch(boxRegion, 6, 6, 6, 6)));
                            }
                            timerBox.getBackground().setMinSize(90, 30);
                            Label timeLbl = createLabel(formatRemainingTime(plant.getRemainingHours()), "FBUSV8C5EI_1_outline", Color.WHITE);
                            timeLbl.setFontScale(0.45f);
                            timerLabels[y][x] = timeLbl;
                            timerBox.add(timeLbl).pad(2, 6, 2, 6);
                            timerBox.pack();
                            timerBox.setPosition(-70f, 100f);
                            potContainer.addActor(timerBox);

                            // Grow Button (Placed below the pot)
                            int cost = (int) Math.ceil(plant.getRemainingHours());
                            TextButton growBtn = createSkinButton("Grow (" + cost + ")", "green", new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float ex, float ey) {
                                    openGrowDialog(gridX, gridY, cost);
                                }
                            });
                            growBtn.getLabel().setFontScale(0.75f);
                            growBtn.setSize(110f, 35f);
                            growBtn.setPosition((POT_WIDTH - 110f) / 2f, -38f);
                            potContainer.addActor(growBtn);
                        }
                    }
                }
                potGroup.addActor(potContainer);
            }
        }
    }

    private void openBuyPotDialog(int gridX, int gridY) {
        Table box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel("Unlock New Pot?");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(20).row();

        box.add(createBlackLabel("Cost: "+ GreenHouseManager.getPotCost() +" Gems")).colspan(2).left().padBottom(20).row();

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

    private String formatRemainingTime(double remainingHours) {
        if (remainingHours <= 0) return "READY!";
        long totalSeconds = (long) (remainingHours * 3600);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (UsersManager.getInstance().getLoggedInUser() == null) return;
        UserProgress progress = UsersManager.getInstance().getLoggedInUser().getUserProgress();
        if (progress == null) return;

        GreenhousePlant[][] plants = progress.getPotPlants();
        boolean needsRefresh = false;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                if (timerLabels[y][x] != null && plants != null && plants[y][x] != null) {
                    GreenhousePlant plant = plants[y][x];
                    if (plant.isReady()) {
                        needsRefresh = true;
                    } else {
                        timerLabels[y][x].setText(formatRemainingTime(plant.getRemainingHours()));
                    }
                }
            }
        }
        if (needsRefresh) refreshPots();
    }

    // ==========================================
    // GreenHouseMenuView callbacks
    // ==========================================

    @Override public void showGreenhouseStatus(String status) {}
    @Override public void showError(String errorMessage) { showToast(errorMessage, ERROR_BG_ASSET_ID); }
    @Override public void showPlantPlanted(String plantName, int x, int y) { refreshPots(); }
    @Override public void showCollectedMarigold(int amount) { openRewardDialog("Collected Marigold: +" + amount + " coins."); }
    @Override public void showCollectedBoost(String plantName) { openRewardDialog("Collected " + plantName + " -> greenhouse boost stored."); }
    @Override public void showAlreadyHasBoost(String plantName) { openRewardDialog("You already have a boost for " + plantName + ". Pot cleared."); }
    @Override public void showPotCleared() { refreshPots(); }
    @Override public void showGrowthAccelerated() { refreshPots(); updateCurrencyHud(); }
    @Override public void showCurrentMenu() { updateCurrencyHud(); refreshPots(); }
}

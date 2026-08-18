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
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.ChoosePlantMenu;
import com.test1.PlantsVsZombies.src.Menu.GameMenu;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantStats;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UserProgressManager;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.ChoosePlantMenuView;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlantScreen extends AbstractScreen implements ChoosePlantMenuView {

    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    // Same box CollectionMenuScreen uses for plant cards, for visual consistency.
    // TODO: verify against your real TextureBank atlas keys.
    private static final String PLANT_ICON_BOX_ASSET_ID = "IMAGE_UI_PACKETS_SELECTED_PREMIUM";
    // TODO: placeholder -- point this at your real "boosted" box variant.
    // Falls back to the normal box (not the plain-color fallback) if this
    // particular id doesn't resolve, so a missing boosted-box asset never
    // makes a boosted plant look worse than an un-boosted one.
    private static final String BOOSTED_PLANT_ICON_BOX_ASSET_ID = "IMAGE_UI_PACKETS_BOOST";

    // Pulled from GamePlayScreen's own working usage, so this one is
    // likely already correct rather than a guess.
    private static final String SUN_ICON_ASSET_ID = "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN";

    private static final float CARD_SIZE = 130f;
    private static final float CARD_CELL_WIDTH = 160f;
    private static final float CARD_CELL_HEIGHT = 190f;
    private static final float ICON_INSET = 14f;
    private static final float ANIMATION_BOX_WIDTH = 350f;
    private static final float ANIMATION_BOX_HEIGHT = 400f;

    private ChoosePlantMenu menuController;
    private GameMenu gameMenu;

    private Table topSlotsContainer;
    private Table gridContainer;

    public void setMenuController(ChoosePlantMenu menuController) {
        this.menuController = menuController;
    }

    public void setGameMenu(GameMenu gameMenu) {
        this.gameMenu = gameMenu;
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

        // --------------------------------------------------------
        // Top bar: currency + back
        // --------------------------------------------------------
        Table topBar = new Table();
        topBar.add(createCurrencyHud()).left().padLeft(15).padTop(15);
        topBar.add().expandX();
        topBar.add(createBackButton(MenuType.Game)).right().size(70, 70).padRight(15).padTop(15);
        uiTable.add(topBar).fillX().top().row();

        // --------------------------------------------------------
        // 8 selected-plant slots, 2 rows of 4. Clicking a filled slot
        // deselects that plant.
        // --------------------------------------------------------
        topSlotsContainer = new Table();
        uiTable.add(topSlotsContainer).padTop(10).row();

        // --------------------------------------------------------
        // Plant grid (reuses the same box/icon/badge building blocks
        // CollectionMenuScreen uses, via AbstractScreen)
        // --------------------------------------------------------
        gridContainer = new Table();
        gridContainer.top().left();

        ScrollPane gridScrollPane = new ScrollPane(gridContainer);
        gridScrollPane.setScrollingDisabled(true, false);
        gridScrollPane.setFadeScrollBars(true);
        gridScrollPane.setOverscroll(false, false);

        uiTable.add(gridScrollPane).expand().fill().pad(10, 20, 10, 20).row();

        // --------------------------------------------------------
        // Bottom bar: Let's Rock
        // --------------------------------------------------------
        Table bottomBar = new Table();
        TextButton letsRockButton = createSkinButton("Let's Rock!", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameMenu != null) {
                    gameMenu.startGame();
                }
            }
        });
        bottomBar.add(letsRockButton).padBottom(15);
        uiTable.add(bottomBar).row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        refreshTopSlots();
        refreshGrid();
    }

    // ============================================================
    // TOP SLOTS (selected plants)
    // ============================================================

    private void refreshTopSlots() {
        if (topSlotsContainer == null || gameMenu == null) return;
        topSlotsContainer.clearChildren();

        List<String> selected = gameMenu.getPlantsStr();
        int maxSelected = ChoosePlantMenu.getMaxSelectedPlants();

        for (int i = 0; i < maxSelected; i++) {
            Actor slot;

            if (i < selected.size()) {
                String plantName = selected.get(i);
                PlantType type = PlantType.fromName(plantName);
                boolean boosted = type != null && UsersManager.getInstance().hasGreenhouseBoost(type);
                String boxAssetId = boosted ? BOOSTED_PLANT_ICON_BOX_ASSET_ID : PLANT_ICON_BOX_ASSET_ID;

                slot = buildIconBoxButton(
                    boxAssetId,
                    type != null ? type.getIconAssetId() : null,
                    CARD_SIZE,
                    ICON_INSET,
                    false,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            String error = menuController.removePlant(plantName);
                            if (error != null) {
                                showError(error);
                            } else {
                                showToast(formatEnumName(plantName) + " removed!", SUCCESS_BG_ASSET_ID);
                                refreshTopSlots();
                                refreshGrid();
                            }
                        }
                    }
                );
            } else {
                // Empty slot: box only, no icon, not clickable.
                slot = buildIconBoxButton(PLANT_ICON_BOX_ASSET_ID, null, CARD_SIZE, ICON_INSET, false, null);
            }

            topSlotsContainer.add(slot).size(CARD_SIZE, CARD_SIZE).pad(6);
            if ((i + 1) % 4 == 0) topSlotsContainer.row();
        }
    }

    // ============================================================
    // GRID
    // ============================================================

    private void refreshGrid() {
        if (gridContainer == null) return;
        gridContainer.clearChildren();

        User user = UsersManager.getInstance().getLoggedInUser();
        UserProgress progress = (user != null) ? user.getUserProgress() : null;

        int columns = 6;
        int count = 0;
        for (PlantType type : PlantType.values()) {
            gridContainer.add(buildChooseGridCard(type, progress))
                .size(CARD_CELL_WIDTH, CARD_CELL_HEIGHT)
                .top()
                .pad(6);
            count++;
            if (count % columns == 0) gridContainer.row();
        }
    }

    private Actor buildChooseGridCard(PlantType type, UserProgress progress) {
        boolean unlocked = progress != null && progress.getUnlockedPlantsAndTheirLevels().containsKey(type);
        int level = unlocked ? progress.getUnlockedPlantsAndTheirLevels().get(type) : 0;
        boolean boosted = unlocked && UsersManager.getInstance().hasGreenhouseBoost(type);

        String boxAssetId = boosted ? BOOSTED_PLANT_ICON_BOX_ASSET_ID : PLANT_ICON_BOX_ASSET_ID;

        Stack contentStack = buildIconBoxButton(
            boxAssetId,
            type.getIconAssetId(),
            CARD_SIZE,
            ICON_INSET,
            !unlocked,
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (type == PlantType.MARIGOLD) {
                        showError("Marigold is only available in greenhouse.");
                        return;
                    }
                    if (unlocked) {
                        openPlantDetailDialog(type);
                    } else {
                        openBuyDialog(type);
                    }
                }
            }
        );

        if (unlocked) {
            // Top-right corner cluster: level badge (smaller than
            // Collection's, to make room) + sun cost + sun icon beside it.
            Table corner = new Table();
            corner.setTouchable(Touchable.disabled);
            corner.top().right();

            Table cluster = new Table();
            cluster.add(buildCornerBadge("Lv" + level, 0.45f)).padRight(2);
            cluster.add(buildSunCostBadge(type, level));

            corner.add(cluster).pad(2);
            contentStack.add(corner);
        }

        Table card = new Table();
        card.add(contentStack).size(CARD_SIZE, CARD_SIZE).row();

        if (unlocked) {
            int maxLevel = UserProgressManager.getMaxPlantLevel();
            if (level < maxLevel) {
                int required = UserProgressManager.getRequiredSeedPacketsForUpgrade(level);
                int current = progress.getSeedPackets().getOrDefault(type, 0);
                Label seedLabel = createLabel(current + "/" + required, "FBUSV8C5EI_1_outline", Color.WHITE);
                seedLabel.setFontScale(0.6f);
                card.add(seedLabel).padTop(4);
            } else {
                Label maxLabel = createLabel("MAX", "FBUSV8C5EI_1_outline", Color.WHITE);
                maxLabel.setFontScale(0.6f);
                card.add(maxLabel).padTop(4);
            }
        }

        return card;
    }

    private Table buildSunCostBadge(PlantType type, int level) {
        Table badge = new Table();
        TextureRegion badgeBg = textureBank.region(CURRENCY_BOX_BG_ASSET_ID);
        if (badgeBg != null) {
            badge.setBackground(new NinePatchDrawable(new NinePatch(badgeBg, 8, 8, 8, 8)));
        }

        Label costLabel = createLabel(String.valueOf(getSunCost(type, level)), "FBUSV8C5EI_1_outline", Color.WHITE);
        costLabel.setFontScale(0.45f);
        badge.add(costLabel).padLeft(4);

        TextureRegion sunIconRegion = textureBank.region(SUN_ICON_ASSET_ID);
        if (sunIconRegion != null) {
            Image sunIcon = new Image(sunIconRegion);
            badge.add(sunIcon).size(14, 14).padLeft(2).padRight(4);
        } else {
            badge.add().padRight(4);
        }

        return badge;
    }

    private int getSunCost(PlantType type, int level) {
        try {
            BattlePlant plant = PlantFactory.createBattlePlant(type.getName(), Math.max(level, 1));
            PlantStats stats = (plant != null) ? plant.getPlantStats() : null;
            return (stats != null) ? stats.getCost() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ============================================================
    // BUY DIALOG
    // ============================================================

    private void openBuyDialog(PlantType type) {
        Table box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel(formatEnumName(type.getName()));
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(14).row();

        int cost = UserProgressManager.getPlantPurchaseCost();
        User user = UsersManager.getInstance().getLoggedInUser();
        int coins = (user != null) ? user.getUserProgress().getCoinsCount() : 0;

        box.add(createBlackLabel("Cost: " + cost + " coins")).colspan(2).left().padBottom(4).row();
        box.add(createBlackLabel("You have: " + coins + " coins")).colspan(2).left().padBottom(20).row();

        TextButton buyButton = createSkinButton("Buy", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String error = UsersManager.getInstance().purchasePlant(type.getName());
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast(formatEnumName(type.getName()) + " purchased!", SUCCESS_BG_ASSET_ID);
                    updateCurrencyHud();
                    refreshGrid();
                }
            }
        });

        TextButton cancelButton = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(buyButton).padRight(10);
        buttonRow.add(cancelButton);
        box.add(buttonRow).colspan(2);

        showModal(box);
    }

    // ============================================================
    // DETAIL DIALOG
    // ============================================================

    private void openPlantDetailDialog(PlantType type) {
        User user = UsersManager.getInstance().getLoggedInUser();
        UserProgress progress = (user != null) ? user.getUserProgress() : null;
        int level = (progress != null) ? progress.getUnlockedPlantsAndTheirLevels().getOrDefault(type, 1) : 1;
        int maxLevel = UserProgressManager.getMaxPlantLevel();

        Table box = new BorderedTable();
        box.pad(50);

        Table leftCell = new Table();
        leftCell.add(createAnimationActor(type.getIdleAnimationPath(), type.getStateName()))
            .size(ANIMATION_BOX_WIDTH, ANIMATION_BOX_HEIGHT);

        // Shared name/level/family/cost/health/tags block (same one
        // CollectionMenuScreen's detail dialog uses).
        Table rightCell = buildPlantStatsBlock(type, level);

        if (level < maxLevel) {
            rightCell.add(createBlackLabel(
                "Upgrade: " + UserProgressManager.getRequiredCoinsForUpgrade(level)
                    + " coins, "
                    + (progress != null ? progress.getSeedPackets().getOrDefault(type, 0) : 0)
                    + "/"
                    + UserProgressManager.getRequiredSeedPacketsForUpgrade(level)
                    + " seed packets"
            )).left().padBottom(10).row();
        } else {
            rightCell.add(createBlackLabel("Max level reached.")).left().padBottom(10).row();
        }

        boolean alreadySelected = false;
        for (String plantStr : gameMenu.getPlantsStr()) {
            if (plantStr.equalsIgnoreCase(type.getName())) {
                alreadySelected = true;
                break;
            }
        }
        boolean boosted = UsersManager.getInstance().hasGreenhouseBoost(type);
        boolean listFull = gameMenu.getPlantsStr().size() >= ChoosePlantMenu.getMaxSelectedPlants();

        Table buttonTable = new Table();

        if (level < maxLevel) {
            TextButton upgradeButton = createSkinButton("Upgrade", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String error = UsersManager.getInstance().upgradePlant(type.getName());
                    closeModal();
                    if (error != null) {
                        showError(error);
                    } else {
                        showToast(formatEnumName(type.getName()) + " upgraded!", SUCCESS_BG_ASSET_ID);
                        updateCurrencyHud();
                        refreshGrid();
                        refreshTopSlots();
                    }
                }
            });
            buttonTable.add(upgradeButton).padRight(6);
        }

        TextButton boostButton;
        if (boosted) {
            boostButton = createSkinButton("Boosted", "purple", null);
        } else {
            boostButton = createSkinButton("Boost (2 gems)", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String error = menuController.boostPlant(type.getName());
                    if (error != null) {
                        showError(error);
                    } else {
                        showToast(formatEnumName(type.getName()) + " boosted!", SUCCESS_BG_ASSET_ID);
                        updateCurrencyHud();
                        closeModal();
                        refreshGrid();
                        refreshTopSlots();
                    }
                }
            });
        }
        buttonTable.add(boostButton).padRight(6);

        TextButton addButton;
        if (alreadySelected) {
            addButton = createSkinButton("Selected", "purple", null);
        } else if (listFull) {
            addButton = createSkinButton("List Full", "brown", null);
        } else {
            addButton = createSkinButton("Add to List", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String error = menuController.addPlant(type.getName());
                    if (error != null) {
                        showError(error);
                    } else {
                        showToast(formatEnumName(type.getName()) + " added!", SUCCESS_BG_ASSET_ID);
                        closeModal();
                        refreshTopSlots();
                        refreshGrid();
                    }
                }
            });
        }
        buttonTable.add(addButton).padRight(6);

        TextButton closeButton = createSkinButton("Close", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        buttonTable.add(closeButton);

        rightCell.add(buttonTable).left().padBottom(20);

        Table content = new Table();
        content.add(leftCell).center().padRight(24);
        content.add(rightCell).center();
        box.add(content);

        showModal(box);
    }

    // ============================================================
    // ChoosePlantMenuView / BaseView
    // ============================================================
    // Same rationale as CollectionMenuScreen: this screen reads
    // UsersManager/UserProgress state directly and drives its own
    // dialogs/refreshes rather than being pushed to by these callbacks.
    // They exist to satisfy ChoosePlantMenuView for the terminal flow.

    @Override
    public void showAllPlants(List<String> plantNames) {}

    @Override
    public void showAvailablePlants(List<String> plantNames) {}

    @Override
    public void showPlantAddedSuccess(String plantName) {
        showToast(formatEnumName(plantName) + " added!", SUCCESS_BG_ASSET_ID);
        refreshTopSlots();
        refreshGrid();
    }

    @Override
    public void showPlantRemovedSuccess(String plantName) {
        showToast(formatEnumName(plantName) + " removed!", SUCCESS_BG_ASSET_ID);
        refreshTopSlots();
        refreshGrid();
    }

    @Override
    public void showPlantBoosted(String plantName) {
        showToast(formatEnumName(plantName) + " boosted!", SUCCESS_BG_ASSET_ID);
        updateCurrencyHud();
        refreshGrid();
        refreshTopSlots();
    }

    @Override
    public void showError(String errorMessage) {
        showToast(errorMessage, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
        updateCurrencyHud();
        refreshTopSlots();
        refreshGrid();
    }
}

package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.Main;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Menu.CollectionMenu;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantStats;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieStats;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UserProgressManager;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.CollectionMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionMenuScreen extends AbstractScreen implements CollectionMenuView {

    private enum Tab { PLANTS, ZOMBIES }

    private enum FilterDimension { NONE, FAMILY, LOCK_STATUS, UPGRADABILITY }

    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";
    private static final String DETAIL_BOX_BG_ASSET_ID = "IMAGE_UI_DIALOG_ASSET_DIALOGBORDER_LUNAR_NEW_YEAR";

    private static final String PLANT_ICON_BOX_ASSET_ID = "IMAGE_UI_PACKETS_SELECTED_PREMIUM";
    private static final String ZOMBIE_ICON_BOX_ASSET_ID = "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_READY";

    private static final float CARD_SIZE = 130f;
    private static final float CARD_CELL_WIDTH = 160f;
    private static final float CARD_CELL_HEIGHT = 190f;
    private static final float ICON_INSET = 14f;

    private static final float ANIMATION_BOX_HEIGHT = 400f;
    private static final float ANIMATION_BOX_WIDTH = 350f;

    private static int maxLevel = 4;

    private CollectionMenu menuController;

    private Tab currentTab = Tab.PLANTS;
    private Table gridContainer;

    private FilterDimension activeFilterDimension = FilterDimension.NONE;
    private final Set<PlantCategory> filterFamilies = new HashSet<>();
    private boolean filterWantsUnlocked = true;
    private boolean filterWantsUpgradable = true;

    private Texture fallbackBoxTexture;

    public void setMenuController(CollectionMenu menuController) {
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

        // --------------------------------------------------------
        // Top bar: currency (far left) + back (far right)
        // --------------------------------------------------------
        Table topBar = new Table();
        topBar.add(createCurrencyHud()).left().top().padLeft(15).padTop(15);
        topBar.add().expandX().fillX();
        topBar.add(createBackButton(MenuType.Game)).right().top().size(70, 70).padRight(15).padTop(15);
        uiTable.add(topBar).expandX().fillX().top().row();

        // --------------------------------------------------------
        // Tabs + filter row (centered and matching the grid width)
        // --------------------------------------------------------
        Table tabRow = new Table();

        TextButton plantsTabButton = new TextButton("Plants", skin, "green");
        TextButton zombiesTabButton = new TextButton("Zombies", skin, "brown");
        plantsTabButton.pad(8, 24, 8, 24);
        zombiesTabButton.pad(8, 24, 8, 24);

        TextButton filterButton = createSkinButton("Filter", "purple", null);
        filterButton.setVisible(currentTab == Tab.PLANTS);
        filterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFilterDialog();
            }
        });

        plantsTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTab != Tab.PLANTS) {
                    currentTab = Tab.PLANTS;
                    plantsTabButton.setStyle(skin.get("green", TextButton.TextButtonStyle.class));
                    zombiesTabButton.setStyle(skin.get("brown", TextButton.TextButtonStyle.class));
                    filterButton.setVisible(true);
                    refreshGrid();
                }
            }
        });
        zombiesTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTab != Tab.ZOMBIES) {
                    currentTab = Tab.ZOMBIES;
                    zombiesTabButton.setStyle(skin.get("green", TextButton.TextButtonStyle.class));
                    plantsTabButton.setStyle(skin.get("brown", TextButton.TextButtonStyle.class));
                    filterButton.setVisible(false);
                    refreshGrid();
                }
            }
        });

        tabRow.add(plantsTabButton).padRight(10);
        tabRow.add(zombiesTabButton).padRight(20);
        tabRow.add().expandX();
        tabRow.add(filterButton);

        uiTable.add(tabRow).width(CARD_CELL_WIDTH * 6).padTop(10).row();

        // --------------------------------------------------------
        // Grid
        // --------------------------------------------------------
        gridContainer = new Table();
        gridContainer.top().left();

        ScrollPane gridScrollPane = new ScrollPane(gridContainer, skin);
        gridScrollPane.setScrollingDisabled(true, false);
        gridScrollPane.setFadeScrollBars(false);
        gridScrollPane.setScrollBarPositions(false, true);
        gridScrollPane.setOverscroll(false, false);

        uiTable.add(gridScrollPane).expandY().fillY().pad(10, 0, 10, 0).row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        refreshGrid();
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

        if (currentTab == Tab.PLANTS) {
            for (PlantType type : PlantType.values()) {
                if (!passesActivePlantFilter(type, progress)) continue;
                gridContainer.add(buildPlantCard(type, progress))
                    .size(CARD_CELL_WIDTH, CARD_CELL_HEIGHT)
                    .top()
                    .pad(6);
                count++;
                if (count % columns == 0) gridContainer.row();
            }
            if (count == 0) {
                gridContainer.add(createLabel("No plants match this filter.", "FBUSV8C5EI_1", Color.WHITE)).pad(20);
            }
        } else {
            for (ZombieType type : ZombieType.values()) {
                gridContainer.add(buildZombieCard(type, progress))
                    .size(CARD_CELL_WIDTH, CARD_CELL_HEIGHT)
                    .top()
                    .pad(6);
                count++;
                if (count % columns == 0) gridContainer.row();
            }
        }
    }

    private Actor buildPlantCard(PlantType type, UserProgress progress) {
        boolean unlocked = progress != null && progress.getUnlockedPlantsAndTheirLevels().containsKey(type);
        int level = unlocked ? progress.getUnlockedPlantsAndTheirLevels().get(type) : 0;

        TextureRegion boxRegion = textureBank.region(PLANT_ICON_BOX_ASSET_ID);
        Button.ButtonStyle style = new Button.ButtonStyle();
        if (boxRegion != null) {
            TextureRegionDrawable boxDrawable = new TextureRegionDrawable(boxRegion);
            style.up = boxDrawable;
            style.down = boxDrawable.tint(new Color(0.75f, 0.75f, 0.75f, 1f));
        } else {
            Drawable fallback = getFallbackBoxDrawable();
            style.up = fallback;
            style.down = fallback;
        }

        Button cardButton = new Button(style);
        cardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (type == PlantType.MARIGOLD) {
                    showError("Marigold is only available in greenhouse.");
                } else {
                    if (unlocked) {
                        openPlantDetailDialog(type);
                    } else {
                        openBuyDialog(type);
                    }
                }
            }
        });

        Stack contentStack = new Stack();
        contentStack.add(cardButton);

        TextureRegion iconRegion = textureBank.region(type.getIconAssetId());
        if (iconRegion != null) {
            Image icon = new Image(iconRegion);
            icon.setScaling(Scaling.fit);
            if (!unlocked) {
                icon.setColor(0.25f, 0.25f, 0.25f, 1f);
            }
            Table iconInset = new Table();
            iconInset.setTouchable(Touchable.disabled);
            iconInset.add(icon).size(CARD_SIZE - ICON_INSET * 2).pad(ICON_INSET);
            contentStack.add(iconInset);
        }

        if (unlocked) {
            Table badgeWrapper = new Table();
            badgeWrapper.setTouchable(Touchable.disabled);
            badgeWrapper.top().right();
            badgeWrapper.add(buildCornerBadge("Lv" + level, 0.45f)).pad(2);
            contentStack.add(badgeWrapper);
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

    private Actor buildZombieCard(ZombieType type, UserProgress progress) {
        boolean unlocked = progress != null && progress.getUnlockedZombies().contains(type);

        TextureRegion boxRegion = textureBank.region(ZOMBIE_ICON_BOX_ASSET_ID);
        Button.ButtonStyle style = new Button.ButtonStyle();
        if (boxRegion != null) {
            TextureRegionDrawable boxDrawable = new TextureRegionDrawable(boxRegion);
            style.up = boxDrawable;
            if (unlocked) {
                style.down = boxDrawable.tint(new Color(0.75f, 0.75f, 0.75f, 1f));
            }
        } else {
            Drawable fallback = getFallbackBoxDrawable();
            style.up = fallback;
        }

        Button cardButton = new Button(style);
        if (unlocked) {
            cardButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    openZombieDetailDialog(type);
                }
            });
        }

        Stack contentStack = new Stack();
        contentStack.add(cardButton);

        if (unlocked) {
            TextureRegion iconRegion = textureBank.region(type.getIconAssetId());
            if (iconRegion != null) {
                Image icon = new Image(iconRegion);
                icon.setScaling(Scaling.fit);
                Table iconInset = new Table();
                iconInset.setTouchable(Touchable.disabled);
                iconInset.add(icon).size(CARD_SIZE - ICON_INSET * 2).pad(ICON_INSET);
                contentStack.add(iconInset);
            }
        }

        Table card = new Table();
        card.add(contentStack).size(CARD_SIZE, CARD_SIZE);
        return card;
    }

    public Drawable getFallbackBoxDrawable() {
        if (fallbackBoxTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.28f, 0.22f, 0.15f, 0.9f);
            pixmap.fill();
            fallbackBoxTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return new TextureRegionDrawable(new TextureRegion(fallbackBoxTexture));
    }

    // ============================================================
    // FILTERING
    // ============================================================

    private boolean passesActivePlantFilter(PlantType type, UserProgress progress) {
        if (activeFilterDimension == FilterDimension.NONE) return true;

        boolean unlocked = progress != null && progress.getUnlockedPlantsAndTheirLevels().containsKey(type);

        switch (activeFilterDimension) {
            case LOCK_STATUS:
                return unlocked == filterWantsUnlocked;

            case UPGRADABILITY: {
                if (!unlocked) return false;
                int level = progress.getUnlockedPlantsAndTheirLevels().get(type);
                return canPlantBeUpgraded(type, level, progress) == filterWantsUpgradable;
            }

            case FAMILY: {
                if (filterFamilies.isEmpty()) return false;
                int levelForLookup = unlocked ? Math.max(progress.getUnlockedPlantsAndTheirLevels().get(type), 1) : 1;
                PlantCategory category = resolveCategory(type, levelForLookup);
                return category != null && filterFamilies.contains(category);
            }

            default:
                return true;
        }
    }

    private boolean canPlantBeUpgraded(PlantType type, int level, UserProgress progress) {
        int maxLevel = UserProgressManager.getMaxPlantLevel();
        if (level >= maxLevel) return false;
        int requiredCoins = UserProgressManager.getRequiredCoinsForUpgrade(level);
        int requiredSeeds = UserProgressManager.getRequiredSeedPacketsForUpgrade(level);
        return progress.getCoinsCount() >= requiredCoins && progress.hasEnoughSeedPackets(type, requiredSeeds);
    }

    private PlantCategory resolveCategory(PlantType type, int level) {
        try {
            BattlePlant plant = PlantFactory.createBattlePlant(type.getName(), level);
            PlantStats stats = (plant != null) ? plant.getPlantStats() : null;
            if (stats == null || stats.getCategory() == null) return null;
            return PlantCategory.findCategoryByString(stats.getCategory());
        } catch (Exception e) {
            return null;
        }
    }

    private void openFilterDialog() {
        Table box = new BorderedTable();
        box.pad(24);

        Label title = createBlackLabel("Filter Plants");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(16).row();

        CheckBox familyDimensionBox = new CheckBox("  Family", skin);
        CheckBox lockDimensionBox = new CheckBox("  Locked / Unlocked", skin);
        CheckBox upgradeDimensionBox = new CheckBox("  Upgradable", skin);
        familyDimensionBox.getLabel().setColor(Color.BLACK);
        lockDimensionBox.getLabel().setColor(Color.BLACK);
        upgradeDimensionBox.getLabel().setColor(Color.BLACK);

        ButtonGroup<CheckBox> dimensionGroup = new ButtonGroup<>();
        dimensionGroup.add(familyDimensionBox);
        dimensionGroup.add(lockDimensionBox);
        dimensionGroup.add(upgradeDimensionBox);
        dimensionGroup.setMinCheckCount(0);
        dimensionGroup.setMaxCheckCount(1);

        if (activeFilterDimension == FilterDimension.FAMILY) familyDimensionBox.setChecked(true);
        else if (activeFilterDimension == FilterDimension.LOCK_STATUS) lockDimensionBox.setChecked(true);
        else if (activeFilterDimension == FilterDimension.UPGRADABILITY) upgradeDimensionBox.setChecked(true);

        // --- Family section ---
        box.add(familyDimensionBox).left().colspan(2).padBottom(6).row();

        Table familyOptions = new Table();
        List<CheckBox> familyCheckBoxes = new ArrayList<>();
        int col = 0;
        for (PlantCategory category : PlantCategory.values()) {
            CheckBox cb = new CheckBox("  " + formatCategory(category.name()), skin);
            cb.getLabel().setColor(Color.BLACK);
            cb.getLabel().setFontScale(0.8f);
            cb.setChecked(filterFamilies.contains(category));
            familyCheckBoxes.add(cb);
            familyOptions.add(cb).left().pad(2, 10, 2, 10);
            col++;
            if (col % 3 == 0) familyOptions.row();
        }
        box.add(familyOptions).left().colspan(2).padLeft(20).padBottom(14).row();

        // --- Lock status section ---
        box.add(lockDimensionBox).left().colspan(2).padBottom(6).row();

        CheckBox lockedOption = new CheckBox("  Locked", skin);
        CheckBox unlockedOption = new CheckBox("  Unlocked", skin);
        lockedOption.getLabel().setColor(Color.BLACK);
        unlockedOption.getLabel().setColor(Color.BLACK);
        ButtonGroup<CheckBox> lockGroup = new ButtonGroup<>(lockedOption, unlockedOption);
        lockGroup.setMinCheckCount(1);
        lockGroup.setMaxCheckCount(1);
        (filterWantsUnlocked ? unlockedOption : lockedOption).setChecked(true);

        Table lockOptions = new Table();
        lockOptions.add(lockedOption).padRight(20);
        lockOptions.add(unlockedOption);
        box.add(lockOptions).left().colspan(2).padLeft(20).padBottom(14).row();

        // --- Upgradability section ---
        box.add(upgradeDimensionBox).left().colspan(2).padBottom(6).row();

        CheckBox canUpgradeOption = new CheckBox("  Can upgrade", skin);
        CheckBox cannotUpgradeOption = new CheckBox("  Cannot upgrade", skin);
        canUpgradeOption.getLabel().setColor(Color.BLACK);
        cannotUpgradeOption.getLabel().setColor(Color.BLACK);
        ButtonGroup<CheckBox> upgradeGroup = new ButtonGroup<>(canUpgradeOption, cannotUpgradeOption);
        upgradeGroup.setMinCheckCount(1);
        upgradeGroup.setMaxCheckCount(1);
        (filterWantsUpgradable ? canUpgradeOption : cannotUpgradeOption).setChecked(true);

        Table upgradeOptions = new Table();
        upgradeOptions.add(canUpgradeOption).padRight(20);
        upgradeOptions.add(cannotUpgradeOption);
        box.add(upgradeOptions).left().colspan(2).padLeft(20).padBottom(20).row();

        TextButton okButton = createSkinButton("OK", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (familyDimensionBox.isChecked()) {
                    activeFilterDimension = FilterDimension.FAMILY;
                    filterFamilies.clear();
                    PlantCategory[] categories = PlantCategory.values();
                    for (int i = 0; i < familyCheckBoxes.size(); i++) {
                        if (familyCheckBoxes.get(i).isChecked()) {
                            filterFamilies.add(categories[i]);
                        }
                    }
                } else if (lockDimensionBox.isChecked()) {
                    activeFilterDimension = FilterDimension.LOCK_STATUS;
                    filterWantsUnlocked = unlockedOption.isChecked();
                } else if (upgradeDimensionBox.isChecked()) {
                    activeFilterDimension = FilterDimension.UPGRADABILITY;
                    filterWantsUpgradable = canUpgradeOption.isChecked();
                } else {
                    activeFilterDimension = FilterDimension.NONE;
                }
                closeModal();
                refreshGrid();
            }
        });

        TextButton clearButton = createSkinButton("Clear", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activeFilterDimension = FilterDimension.NONE;
                filterFamilies.clear();
                closeModal();
                refreshGrid();
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(okButton).padRight(10);
        buttonRow.add(clearButton);
        box.add(buttonRow).colspan(2);

        ScrollPane scrollPane = new ScrollPane(box, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, true);

        Table wrapper = new Table();
        wrapper.add(scrollPane).size(540, 560);

        showModal(wrapper);
    }

    // ============================================================
    // BUY DIALOG
    // ============================================================

    private void openBuyDialog(PlantType type) {
        Table box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel(formatPlantName(type));
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
                String error = menuController.buyPlant(type.getName());
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast(formatPlantName(type) + " purchased!", SUCCESS_BG_ASSET_ID);
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
    // DETAIL DIALOGS
    // ============================================================

    private void openPlantDetailDialog(PlantType type) {
        User user = UsersManager.getInstance().getLoggedInUser();
        UserProgress progress = (user != null) ? user.getUserProgress() : null;
        boolean unlocked = progress != null && progress.getUnlockedPlantsAndTheirLevels().containsKey(type);
        int level = unlocked ? progress.getUnlockedPlantsAndTheirLevels().get(type) : 1;

        PlantStats stats = null;
        try {
            BattlePlant battlePlant = PlantFactory.createBattlePlant(type.getName(), Math.max(level, 1));
            if (battlePlant != null) stats = battlePlant.getPlantStats();
        } catch (Exception ignored) {}

        Table box = new BorderedTable();
        box.pad(50);

        Table leftCell = new Table();
        leftCell.add(createAnimationActor(type.getIdleAnimationPath(), type.getStateName())).size(ANIMATION_BOX_WIDTH, ANIMATION_BOX_HEIGHT);

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

        Table buttonTable = new Table();

        if (unlocked && level < UserProgressManager.getMaxPlantLevel()) {
            TextButton upgradeButton = createSkinButton("Upgrade", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String error = menuController.upgradePlant(type.getName());
                    closeModal();

                    if (error != null) {
                        showError(error);
                    } else {
                        showToast(formatPlantName(type) + " upgraded!", SUCCESS_BG_ASSET_ID);
                        updateCurrencyHud();
                        refreshGrid();
                    }
                }
            });

            buttonTable.add(upgradeButton)
                .left()
                .padRight(2);
        }

        TextButton closeButton = createSkinButton("Close", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });

        buttonTable.add(closeButton).left();
        rightCell.add(buttonTable).left().padBottom(20);

        Table content = new Table();
        content.add(leftCell).center().padRight(24);
        content.add(rightCell).center();
        box.add(content);

        showModal(box);
    }

    private void openZombieDetailDialog(ZombieType type) {
        ZombieStats stats = null;
        try {
            Zombie zombie = ZombieFactory.createZombie(type.getName());
            if (zombie != null) stats = zombie.getZombieStats();
        } catch (Exception ignored) {}

        Table box = new BorderedTable();
        box.pad(50);

        Table leftCell = new Table();
        leftCell.add(createAnimationActor(type.getIdleAnimationPath(), type.getStateName())).size(ANIMATION_BOX_WIDTH, ANIMATION_BOX_HEIGHT);

        Table rightCell = new Table();
        rightCell.top().left();

        Label nameLabel = createBlackLabel(formatZombieName(type));
        nameLabel.setFontScale(1.15f);
        rightCell.add(nameLabel).left().padBottom(8).row();

        if (stats != null) {
            rightCell.add(createBlackLabel("Health: " + stats.getBaseHP())).left().padBottom(4).row();
            rightCell.add(createBlackLabel("Speed: " + stats.getVelocity())).left().padBottom(4).row();
            rightCell.add(createBlackLabel("Category: " + formatCategory(stats.getCategory()))).left().padBottom(4).row();
        } else {
            rightCell.add(createBlackLabel("Details unavailable.")).left().padBottom(4).row();
        }

        TextButton closeButton = createSkinButton("Close", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        rightCell.add(closeButton).left().padBottom(20);

        Table content = new Table();
        content.add(leftCell).center().padRight(24);
        content.add(rightCell).center();
        box.add(content);

        showModal(box);
    }

    // ============================================================
    // IDLE ANIMATION (PamPlayer)
    // ============================================================

    public Actor createAnimationActor(String animationPath, String stateName) {
        return new PamAnimationActor(Main.getInstance().getPamPlayer(), animationPath, stateName);
    }

    private static class PamAnimationActor extends Actor {
        private final PamPlayer player;
        private final String animationPath;
        private final String stateName;
        private float stateTime = 0f;

        PamAnimationActor(PamPlayer player, String animationPath, String stateName) {
            this.player = player;
            this.animationPath = animationPath;
            this.stateName = stateName;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stateTime += delta;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (player == null || animationPath == null) return;
            float centerX = getX() + getWidth() / 2f;
            float centerY = getY() + getHeight() / 4f;
            player.draw(batch, animationPath, stateName, stateTime, centerX, centerY, true);
        }
    }

    // ============================================================
    // FORMATTING HELPERS
    // ============================================================

    private String formatCategory(String rawEnumName) {
        if (rawEnumName == null) return "Unknown";
        String[] parts = rawEnumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)))
                .append(part.substring(1).toLowerCase())
                .append(" ");
        }
        return sb.toString().trim();
    }

    private String formatPlantName(PlantType type) {
        return formatCategory(type.getName());
    }

    private String formatZombieName(ZombieType type) {
        return formatCategory(type.getName());
    }

    // ============================================================
    // CollectionMenuView / BaseView
    // ============================================================

    @Override
    public void showPlants(List<String> plantNames) {}

    @Override
    public void showAllPlants(List<String> plantNames) {}

    @Override
    public void showZombies(List<String> zombieNames) {}

    @Override
    public void showAllZombies(List<String> zombieNames) {}

    @Override
    public void showPlantDetails(String plantName, int cost, int baseHP, String category) {}

    @Override
    public void showZombieDetails(String zombieName, double velocity, int baseHP, String category) {}

    @Override
    public void showPlantPurchased(String plantName) {
        showToast(plantName + " purchased!", SUCCESS_BG_ASSET_ID);
        updateCurrencyHud();
        refreshGrid();
    }

    @Override
    public void showPlantUpgradeSuccess(String plantName) {
        showToast(plantName + " upgraded!", SUCCESS_BG_ASSET_ID);
        updateCurrencyHud();
        refreshGrid();
    }

    @Override
    public void showError(String errorMessage) {
        showToast(errorMessage, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
        updateCurrencyHud();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fallbackBoxTexture != null) {
            fallbackBoxTexture.dispose();
        }
    }
}

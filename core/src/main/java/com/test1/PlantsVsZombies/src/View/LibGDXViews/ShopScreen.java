// file: core/src/main/java/com/test1/PlantsVsZombies/src/View/LibGDXViews/ShopScreen.java
package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ShopItemType;
import com.test1.PlantsVsZombies.src.Enums.WalletType;
import com.test1.PlantsVsZombies.src.Menu.ShopMenu;
import com.test1.PlantsVsZombies.src.Model.Shop.DailyOffer;
import com.test1.PlantsVsZombies.src.Model.Shop.ShopItem;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.ShopMenuView;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;

public class ShopScreen extends AbstractScreen implements ShopMenuView {

    // ==========================================================
    // ASSET IDENTIFIERS
    // Same background as the main menu, per spec. The card background
    // and per-item images below are PLACEHOLDERS -- swap in the real
    // asset ids from the shop mock-up image.
    // ==========================================================
    private static final String BACKGROUND_ASSET_ID = "IMAGE_MAINMENU_BACKGROUND";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    // TODO: replace with the real item-table background asset id from the shop image.
    private static final String ITEM_CARD_BG_ASSET_ID = "IMAGE_UI_CARDS_ALMANAC_PLANT_CARD";

    // TODO: replace each with the real per-item image asset id from the shop image.
    private static final String POT_IMAGE_ASSET_ID = "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
    private static final String PLANT_FOOD_IMAGE_ASSET_ID = "IMAGE_UI_GENERIC_LEAF_BACKDROP";
    private static final String RANDOM_SEED_IMAGE_ASSET_ID = "IMAGE_UI_QUESTS_QUESTICONS_RENT_A_PLANT";
    private static final String SELECTIVE_SEED_IMAGE_ASSET_ID = "IMAGE_UI_QUESTS_QUESTICONS_PLANT";
    private static final String CURRENCY_EXCHANGE_IMAGE_ASSET_ID = "IMAGE_UI_QUESTS_EPIC_REWARD_COINS";

    // Small box used for the "pick a plant" picker, reusing the same look
    // as the plant boxes in Collection / Choose Plant.
    private static final String PLANT_PICK_BOX_ASSET_ID = "IMAGE_UI_PACKETS_SELECTED_PREMIUM";

    private static final float CARD_WIDTH = 210f;
    private static final float CARD_HEIGHT = 260f;
    private static final float CARD_IMAGE_SIZE = 96f;
    private static final float PICK_BOX_SIZE = 70f;

    private ShopMenu menuController;
    private Table itemsContainer;

    public void setMenuController(ShopMenu menuController) {
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
            bgImage.setFillParent(true);
            screenStack.add(bgImage);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        // --------------------------------------------------------
        // Top bar: currency (left) + back to greenhouse (right)
        // --------------------------------------------------------
        Table topBar = new Table();
        topBar.add(createCurrencyHud()).left().top().padLeft(15).padTop(15);
        topBar.add().expandX().fillX();
        topBar.add(createBackButton(MenuType.GreenHouse)).right().top().size(70, 70).padRight(15).padTop(15);
        uiTable.add(topBar).expandX().fillX().top().row();

        Label title = createLabel("SHOP", "FBUSV8C5EI_2", Color.WHITE);
        title.setFontScale(1.1f);
        uiTable.add(title).padTop(6).row();

        // --------------------------------------------------------
        // Horizontal scroll pane of shop item cards
        // --------------------------------------------------------
        itemsContainer = new Table();
        itemsContainer.center();

        ScrollPane scrollPane = new ScrollPane(itemsContainer, skin);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setOverscroll(false, false);

        uiTable.add(scrollPane).expand().fillX().pad(20).row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        refreshItems();
    }

    // ==========================================================
    // ITEM LIST
    // ==========================================================

    private void refreshItems() {
        if (itemsContainer == null || menuController == null) return;
        itemsContainer.clearChildren();

        for (ShopItem item : menuController.getPermanentItems()) {
            itemsContainer.add(buildItemCard(item)).size(CARD_WIDTH, CARD_HEIGHT).pad(12);
        }

        itemsContainer.add(buildDailyOfferCard()).size(CARD_WIDTH, CARD_HEIGHT).pad(12);
    }

    private Table buildItemCard(ShopItem item) {
        Table card = new Table();
        card.pad(14);
        applyCardBackground(card);

        Label nameLabel = createBlackLabel(item.getName());
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center);
        card.add(nameLabel).width(CARD_WIDTH - 30).padBottom(8).row();

        String imageAssetId = getImageAssetIdForType(item.getType());
        TextureRegion imgRegion = (imageAssetId != null) ? textureBank.region(imageAssetId) : null;
        if (imgRegion != null) {
            Image itemImage = new Image(imgRegion);
            itemImage.setScaling(Scaling.fit);
            card.add(itemImage).size(CARD_IMAGE_SIZE).padBottom(8).row();
        } else {
            card.add().size(CARD_IMAGE_SIZE).padBottom(8).row();
        }

        card.add(createBlackLabel(item.getPrice() + " " + currencyText(item.getCurrency())))
            .padBottom(10).row();

        TextButton buyButton = createSkinButton("Buy", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBuyClicked(item);
            }
        });
        card.add(buyButton);

        return card;
    }

    private Table buildDailyOfferCard() {
        DailyOffer offer = menuController.getDailyOffer();

        Table card = new Table();
        card.pad(14);
        applyCardBackground(card);

        String plantName = (offer != null) ? formatEnumName(offer.getPlantType().getName()) : "Daily Offer";
        Label nameLabel = createBlackLabel(plantName + " Seed Packet");
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center);
        card.add(nameLabel).width(CARD_WIDTH - 30).padBottom(8).row();

        // Daily offer shows the plant's idle animation instead of a static image.
        if (offer != null) {
            Actor anim = createAnimationActor(offer.getPlantType().getIdleAnimationPath(), offer.getPlantType().getStateName());
            card.add(anim).size(CARD_IMAGE_SIZE).padBottom(8).row();
        } else {
            card.add().size(CARD_IMAGE_SIZE).padBottom(8).row();
        }

        int price = (offer != null) ? offer.getPrice() : 0;
        card.add(createBlackLabel(price + " Coins")).padBottom(10).row();

        boolean alreadyBought = menuController.isDailyOfferBoughtToday();
        TextButton buyButton;
        if (alreadyBought || offer == null) {
            buyButton = createSkinButton("Bought", "brown", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showError("Daily offer already purchased today.");
                }
            });
        } else {
            buyButton = createSkinButton("Buy", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onBuyDailyOfferClicked(offer);
                }
            });
        }
        card.add(buyButton);

        return card;
    }

    private void applyCardBackground(Table card) {
        TextureRegion bgRegion = textureBank.region(ITEM_CARD_BG_ASSET_ID);
        if (bgRegion != null) {
            NinePatch patch = new NinePatch(bgRegion, 14, 14, 14, 14);
            card.setBackground(new NinePatchDrawable(patch));
        }
    }

    private String getImageAssetIdForType(ShopItemType type) {
        switch (type) {
            case POT: return POT_IMAGE_ASSET_ID;
            case PLANT_FOOD: return PLANT_FOOD_IMAGE_ASSET_ID;
            case RANDOM_SEED_PACKET: return RANDOM_SEED_IMAGE_ASSET_ID;
            case SELECTIVE_SEED_PACKET: return SELECTIVE_SEED_IMAGE_ASSET_ID;
            case CURRENCY_EXCHANGE: return CURRENCY_EXCHANGE_IMAGE_ASSET_ID;
            default: return null;
        }
    }

    private String currencyText(WalletType currency) {
        return currency == WalletType.COIN ? "Coins" : "Gems";
    }

    // ==========================================================
    // PURCHASE FLOW
    // ==========================================================

    private void onBuyClicked(ShopItem item) {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) {
            showError("You must be logged in to purchase.");
            return;
        }
        UserProgress progress = user.getUserProgress();
        int available = (item.getCurrency() == WalletType.COIN) ? progress.getCoinsCount() : progress.getGemsCount();

        if (available < item.getPrice()) {
            showError("Not enough " + currencyText(item.getCurrency()) + ".");
            return;
        }

        if (item.getType() == ShopItemType.SELECTIVE_SEED_PACKET) {
            openPlantPickerDialog(item);
        } else {
            openConfirmDialog(item, null, item.getPrice(), item.getCurrency());
        }
    }

    private void onBuyDailyOfferClicked(DailyOffer offer) {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) {
            showError("You must be logged in to purchase.");
            return;
        }
        int coins = user.getUserProgress().getCoinsCount();
        if (coins < offer.getPrice()) {
            showError("Not enough Coins.");
            return;
        }
        openDailyOfferConfirmDialog(offer);
    }

    // ----------------------------------------------------------
    // Plant picker (for selective seed packet) -- a small bordered
    // table showing only the box for each unlocked plant, no level
    // or sun-cost badges, reusing the same box-button building block
    // used by Collection / Choose Plant.
    // ----------------------------------------------------------
    private void openPlantPickerDialog(ShopItem item) {
        User user = UsersManager.getInstance().getLoggedInUser();
        UserProgress progress = (user != null) ? user.getUserProgress() : null;

        List<PlantType> unlocked = new ArrayList<>();
        if (progress != null) {
            unlocked.addAll(progress.getUnlockedPlantsAndTheirLevels().keySet());
        }

        BorderedTable box = new BorderedTable();
        box.pad(24);

        Label title = createBlackLabel("Choose a Plant");
        title.setFontScale(1.05f);
        box.add(title).padBottom(14).row();

        if (unlocked.isEmpty()) {
            box.add(createBlackLabel("You have no unlocked plants.")).padBottom(14).row();
        } else {
            Table grid = new Table();
            int columns = 5;
            int count = 0;
            for (PlantType type : unlocked) {
                Stack plantBox = buildIconBoxButton(
                    PLANT_PICK_BOX_ASSET_ID,
                    type.getIconAssetId(),
                    4f,
                    false,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            closeModal();
                            openConfirmDialog(item, type.getName(), item.getPrice(), item.getCurrency());
                        }
                    }
                );
                grid.add(plantBox).size(PICK_BOX_SIZE).pad(5);
                count++;
                if (count % columns == 0) grid.row();
            }

            ScrollPane gridScroll = new ScrollPane(grid, skin);
            gridScroll.setScrollingDisabled(true, false);
            gridScroll.setFadeScrollBars(false);
            gridScroll.setOverscroll(false, false);
            box.add(gridScroll).size(PICK_BOX_SIZE * columns + 60, PICK_BOX_SIZE * 2 + 30).padBottom(14).row();
        }

        TextButton cancelButton = createSkinButton("Cancel", "brown", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        box.add(cancelButton);

        showModal(box);
    }

    // ----------------------------------------------------------
    // "Are you sure?" confirmation, shared by every purchasable item.
    // ----------------------------------------------------------
    private void openConfirmDialog(ShopItem item, String plantTypeName, int price, WalletType currency) {
        BorderedTable box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel("Confirm Purchase");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(16).row();

        String itemDescription = item.getName() + (plantTypeName != null ? " (" + formatEnumName(plantTypeName) + ")" : "");
        Label msgLabel = createBlackLabel("Buy " + itemDescription + " for " + price + " " + currencyText(currency) + "?");
        msgLabel.setWrap(true);
        msgLabel.setAlignment(Align.center);
        box.add(msgLabel).width(320).colspan(2).padBottom(20).row();

        TextButton confirmButton = createSkinButton("Confirm", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String error = menuController.buyItem(item.getId(), item.getUnitSize(), plantTypeName);
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast(item.getName() + " purchased!", SUCCESS_BG_ASSET_ID);
                    updateCurrencyHud();
                    refreshItems();
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
        buttonRow.add(confirmButton).padRight(10);
        buttonRow.add(cancelButton);
        box.add(buttonRow).colspan(2);

        showModal(box);
    }

    private void openDailyOfferConfirmDialog(DailyOffer offer) {
        BorderedTable box = new BorderedTable();
        box.pad(30);

        Label title = createBlackLabel("Confirm Purchase");
        title.setFontScale(1.1f);
        box.add(title).colspan(2).padBottom(16).row();

        String plantName = formatEnumName(offer.getPlantType().getName());
        Label msgLabel = createBlackLabel("Buy " + plantName + " Seed Packet for " + offer.getPrice() + " Coins?");
        msgLabel.setWrap(true);
        msgLabel.setAlignment(Align.center);
        box.add(msgLabel).width(320).colspan(2).padBottom(20).row();

        TextButton confirmButton = createSkinButton("Confirm", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Daily offer is item id 6 by convention in ShopManager.purchaseItem;
                // count/plantTypeName are unused on that branch.
                String error = menuController.buyItem(6, offer.getSeedPacketCount(), null);
                closeModal();
                if (error != null) {
                    showError(error);
                } else {
                    showToast(plantName + " Seed Packet purchased!", SUCCESS_BG_ASSET_ID);
                    updateCurrencyHud();
                    refreshItems();
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
        buttonRow.add(confirmButton).padRight(10);
        buttonRow.add(cancelButton);
        box.add(buttonRow).colspan(2);

        showModal(box);
    }

    // ==========================================================
    // ShopMenuView / BaseView
    // ==========================================================

    @Override
    public void showShopList(List<ShopItem> items) {
        refreshItems();
    }

    @Override
    public void showDailyOffer(DailyOffer offer) {
        refreshItems();
    }

    @Override
    public void showPurchaseSuccess() {
        showToast("Purchased!", SUCCESS_BG_ASSET_ID);
        updateCurrencyHud();
        refreshItems();
    }

    @Override
    public void showError(String errorMessage) {
        showToast(errorMessage, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
        updateCurrencyHud();
        refreshItems();
    }
}

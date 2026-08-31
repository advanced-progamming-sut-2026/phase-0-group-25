// file: core/src/main/java/com/test1/PlantsVsZombies/src/View/LibGDXViews/TravelLogScreen.java
package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Menu.TravelLogMenu;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.TravelLogMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TravelLogScreen extends AbstractScreen implements TravelLogMenuView {

    private enum Tab { QUESTS, MINIGAMES }

    private static final String BACKGROUND_ASSET_ID = "IMAGE_UI_QUESTS_TRAVEL_LOG_FINAL";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private static final String SUCCESS_BG_ASSET_ID = "IMAGE_UI_GENERIC_VTB";

    private static final String QUEST_CARD_BG_SKIN_DRAWABLE = "image_ui_powerups_powerup_cost_10";

    private static final String GO_TO_MINIGAMES_BUTTON_ASSET_ID = "IMAGE_UI_GENERIC_BUTTON_HUD_MINIGAMES_NORMAL";
    private static final String BACK_TO_QUESTS_BUTTON_ASSET_ID = "IMAGE_UI_GENERIC_BUTTONS_HUD_QUESTS_NORMAL";
    private static final String VASEBREAKER_ICON_ASSET_ID = "IMAGE_VASEBREAKER_VASE_BROWN_VASE_BROWN_115X150";
    private static final String WALNUT_BOWLING_ICON_ASSET_ID = "IMAGE_UI_PACKETS_WALLNUT";
    private static final String IZOMBIE_ICON_ASSET_ID = "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL";

    private static final float DEFAULT_ICON_BUTTON_SIZE = 70f;

    private TravelLogMenu menuController;
    private Tab currentTab = Tab.QUESTS;

    private Table questsContentTable;
    private Table minigamesContentTable;
    private Table dailyQuestsSection;
    private Table regularQuestsContainer;
    private ScrollPane questsScrollPane;
    private Actor goToMinigamesButton;
    private Actor backToQuestsButton;

    public void setMenuController(TravelLogMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Stack screenStack = new Stack();
        screenStack.setFillParent(true);

        // 1. Background image stretched to fit screen bounds
        TextureRegion backgroundRegion = textureBank.region(BACKGROUND_ASSET_ID);
        if (backgroundRegion != null) {
            Image background = new Image(backgroundRegion);
            background.setScaling(Scaling.stretch);
            screenStack.add(background);
        }

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        // --------------------------------------------------------
        // 2. Top bar: currency HUD (left) + back button (right)
        // --------------------------------------------------------
        Table topBar = new Table();
        topBar.add(createCurrencyHud())
            .left()
            .padLeft(15)
            .padTop(15);

        topBar.add().expandX();

        topBar.add(createBackButton(MenuType.Game))
            .right()
            .size(70, 70)
            .padRight(15)
            .padTop(15);

        uiTable.add(topBar)
            .fillX()
            .top()
            .row();

        // --------------------------------------------------------
        // 3. Center Content Stack (Quests vs Mini-Games)
        // --------------------------------------------------------
        Stack contentStack = new Stack();

        questsContentTable = buildQuestsView();
        minigamesContentTable = buildMinigamesView();

        contentStack.add(questsContentTable);
        contentStack.add(minigamesContentTable);

        uiTable.add(contentStack)
            .expand()
            .fill()
            .minHeight(0)
            .pad(10, 20, 10, 20)
            .row();

        // --------------------------------------------------------
        // 4. Bottom bar (Tab switch button aligned to bottom-right)
        // --------------------------------------------------------
        Table bottomBar = new Table();
        bottomBar.add().expandX();

        goToMinigamesButton = createAssetButton(
            GO_TO_MINIGAMES_BUTTON_ASSET_ID,
            "Mini Games",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    switchTab(Tab.MINIGAMES);
                }
            }
        );

        backToQuestsButton = createAssetButton(
            BACK_TO_QUESTS_BUTTON_ASSET_ID,
            "Quests",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    switchTab(Tab.QUESTS);
                }
            }
        );

        Stack switchButtonStack = new Stack();
        switchButtonStack.add(goToMinigamesButton);
        switchButtonStack.add(backToQuestsButton);

        TextureRegion minigamesRegion = textureBank.region(GO_TO_MINIGAMES_BUTTON_ASSET_ID);
        float iconBtnWidth = (minigamesRegion != null)
            ? minigamesRegion.getRegionWidth()
            : 110f;
        float iconBtnHeight = (minigamesRegion != null)
            ? minigamesRegion.getRegionHeight()
            : DEFAULT_ICON_BUTTON_SIZE;

        bottomBar.add(switchButtonStack)
            .size(iconBtnWidth, iconBtnHeight)
            .right();

        uiTable.add(bottomBar)
            .fillX()
            .bottom()
            .padLeft(15)
            .padRight(15)
            .padBottom(15)
            .row();

        screenStack.add(uiTable);
        rootTable.add(screenStack).grow();

        updateTabVisibility();
        refreshQuests();
    }

    // ============================================================
    // TAB SWITCHING
    // ============================================================

    private void switchTab(Tab tab) {
        currentTab = tab;
        updateTabVisibility();
        if (tab == Tab.QUESTS) {
            refreshQuests();
        }
    }

    private void updateTabVisibility() {
        questsContentTable.setVisible(currentTab == Tab.QUESTS);
        minigamesContentTable.setVisible(currentTab == Tab.MINIGAMES);
        goToMinigamesButton.setVisible(currentTab == Tab.QUESTS);
        backToQuestsButton.setVisible(currentTab == Tab.MINIGAMES);
    }

    // ============================================================
    // QUESTS TAB (SCROLLABLE & SEPARATED)
    // ============================================================

    private Table buildQuestsView() {
        Table mainContainer = new Table();
        mainContainer.top();

        Table scrollInnerContainer = new Table();
        scrollInnerContainer.top().pad(5);

        dailyQuestsSection = new Table();
        dailyQuestsSection.top();
        scrollInnerContainer.add(dailyQuestsSection).fillX().padBottom(15).row();

        regularQuestsContainer = new Table();
        regularQuestsContainer.top();
        scrollInnerContainer.add(regularQuestsContainer).fillX().padBottom(15).row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = null;

        questsScrollPane = new ScrollPane(scrollInnerContainer, scrollStyle);
        questsScrollPane.setScrollingDisabled(true, false);
        questsScrollPane.setFadeScrollBars(false);
        questsScrollPane.setOverscroll(false, false);

        questsScrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (stage != null) {
                    stage.setScrollFocus(questsScrollPane);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (stage != null && (toActor == null || !toActor.isDescendantOf(questsScrollPane))) {
                    stage.setScrollFocus(null);
                }
            }
        });

        mainContainer.add(questsScrollPane).expand().fill().minHeight(0).row();
        return mainContainer;
    }

    private Table createSectionHeader(String title) {
        Table header = new Table();
        header.left();

        Label label = createBlackLabel(title);
        header.add(label).left().padLeft(5).padBottom(8);

        return header;
    }

    private void refreshQuests() {
        if (dailyQuestsSection == null || regularQuestsContainer == null) return;

        // Sync user progress before rendering
        QuestManager.getInstance().loadProgress();

        dailyQuestsSection.clearChildren();
        regularQuestsContainer.clearChildren();

        List<Quest> notYetClaimed = new ArrayList<>();
        notYetClaimed.addAll(QuestManager.getInstance().getActiveQuests());
        notYetClaimed.addAll(QuestManager.getInstance().getCompletedQuests());

        List<Quest> dailyQuests = new ArrayList<>();
        List<Quest> regularQuests = new ArrayList<>();

        for (Quest quest : notYetClaimed) {
            if (quest.isDailyReset()) {
                dailyQuests.add(quest);
            } else {
                regularQuests.add(quest);
            }
        }

        // Render Daily Quests Section
        if (!dailyQuests.isEmpty()) {
            dailyQuestsSection.add(createSectionHeader("--- DAILY QUESTS ---")).left().padBottom(6).row();
            for (Quest quest : dailyQuests) {
                Table card = buildQuestCard(quest);
                dailyQuestsSection.add(card).fillX().padBottom(10).row();
            }
        }

        // Render Regular Quests Section
        if (!regularQuests.isEmpty()) {
            regularQuestsContainer.add(createSectionHeader("--- REGULAR QUESTS ---")).left().padBottom(6).row();
            for (Quest quest : regularQuests) {
                Table card = buildQuestCard(quest);
                regularQuestsContainer.add(card).fillX().padBottom(10).row();
            }
        }
    }

    private Table buildQuestCard(Quest quest) {
        Table card = new Table();
        Drawable cardBg = skin.getDrawable(QUEST_CARD_BG_SKIN_DRAWABLE);
        if (cardBg != null) {
            card.setBackground(cardBg);
        }
        card.pad(8, 15, 8, 15);

        // Quest icon (left)
        TextureRegion iconRegion = (quest.getIcon() != null) ? textureBank.region(quest.getIcon()) : null;
        if (iconRegion != null) {
            Image icon = new Image(iconRegion);
            icon.setScaling(Scaling.fit);
            card.add(icon).size(64, 64).padRight(15);
        } else {
            card.add().size(64, 64).padRight(15);
        }

        // Description and progress bar (middle)
        Table middle = new Table();

        Label descLabel = createBlackLabel(quest.getDescription());
        descLabel.setWrap(true);
        descLabel.setFontScale(0.8f);
        middle.add(descLabel).width(450).left().padBottom(6).row();

        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, skin, "xp_green");
        int required = Math.max(quest.getRequiredCount(), 1);
        float percent = Math.min(100f, (quest.getCurrentProgress() / (float) required) * 100f);
        progressBar.setValue(percent);
        middle.add(progressBar).width(450).height(20).left();

        card.add(middle).expandX().left().padRight(15);

        // Reward icon & count
        String rewardAssetId = null;
        if (quest.getReward() != null && quest.getReward().getType() != null) {
            rewardAssetId = quest.getReward().getType().getId();
        }
        TextureRegion rewardRegion = (rewardAssetId != null && !rewardAssetId.isEmpty())
            ? textureBank.region(rewardAssetId) : null;
        if (rewardRegion != null) {
            Image rewardIcon = new Image(rewardRegion);
            rewardIcon.setScaling(Scaling.fit);
            card.add(rewardIcon).size(48, 48).padRight(8);
        } else {
            card.add().size(48, 48).padRight(8);
        }

        Label rewardAmountLabel = createBlackLabel("x" + (quest.getReward() != null ? quest.getReward().getAmount() : 0));
        rewardAmountLabel.setFontScale(0.85f);
        card.add(rewardAmountLabel).padRight(15);

        // Claim button or placeholder
        if (quest.isCompleted() && !quest.isClaimed()) {
            TextButton claimButton = createSkinButton("CLAIM", "green", new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (menuController != null) {
                        menuController.claimReward(quest.getId());
                        showToast(quest.getName() + " claimed!", SUCCESS_BG_ASSET_ID);
                        updateCurrencyHud();
                        refreshQuests();
                    }
                }
            });
            card.add(claimButton).width(110);
        } else {
            card.add().width(110);
        }

        return card;
    }

    // ============================================================
    // MINI GAMES TAB
    // ============================================================

    private Table buildMinigamesView() {
        Table container = new Table();
        container.center();

        container.add(buildMiniGameCard(
            MiniGameType.VASEBREAKER,
            VASEBREAKER_ICON_ASSET_ID,
            "Break vases to find and battle zombies hiding inside!"
        )).padBottom(15).row();

        container.add(buildMiniGameCard(
            MiniGameType.WALNUT_BOWLING,
            WALNUT_BOWLING_ICON_ASSET_ID,
            "Roll explosive walnuts to bowl over waves of zombies!"
        )).padBottom(15).row();

        container.add(buildMiniGameCard(
            MiniGameType.I_ZOMBIE,
            IZOMBIE_ICON_ASSET_ID,
            "Switch sides and lead the zombie horde to victory!"
        )).row();

        return container;
    }

    private Table buildMiniGameCard(MiniGameType type, String iconAssetId, String description) {
        Table card = new Table();
        Drawable cardBg = skin.getDrawable(QUEST_CARD_BG_SKIN_DRAWABLE);
        if (cardBg != null) {
            card.setBackground(cardBg);
        }
        card.pad(10, 15, 10, 15);

        TextureRegion iconRegion = textureBank.region(iconAssetId);
        if (iconRegion != null) {
            Image icon = new Image(iconRegion);
            icon.setScaling(Scaling.fit);
            card.add(icon).size(64, 64).padRight(15);
        } else {
            card.add().size(64, 64).padRight(15);
        }

        Label descLabel = createBlackLabel(description);
        descLabel.setWrap(true);
        descLabel.setFontScale(0.8f);
        card.add(descLabel).width(350).left().padRight(20);

        TextButton playButton = createSkinButton("Play", "green", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (type == MiniGameType.I_ZOMBIE) {
                    showModal(new IZombieLobbyDialog(skin, () -> closeModal()));
                } else {
                    if (menuController != null) {
                        menuController.startMiniGame(type.getDisplayName());
                    }
                }
            }
        });
        card.add(playButton).width(110);

        return card;
    }

    // ============================================================
    // TravelLogMenuView callbacks
    // ============================================================

    @Override
    public void showQuests(List<Quest> activeQuests, List<Quest> completedQuests, QuestPage page) {
        refreshQuests();
    }

    @Override
    public void showMinigames() {
        switchTab(Tab.MINIGAMES);
    }

    @Override
    public void showMiniGameLaunched(String miniGameName) {}

    @Override
    public void showRewardClaimed(String questId) {
        showToast("Reward claimed!", SUCCESS_BG_ASSET_ID);
        updateCurrencyHud();
        refreshQuests();
    }

    @Override
    public void showError(String errorMessage) {
        showToast(errorMessage, ERROR_BG_ASSET_ID);
    }

    @Override
    public void showCurrentMenu() {
        updateCurrencyHud();
        refreshQuests();
    }
}

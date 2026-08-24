package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantStats;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgressManager;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;
    protected TextureBank textureBank;
    protected Table rootTable;
    private Stack mainStack;
    private Stack modalStack;
    private Stack toastStack;
    private com.badlogic.gdx.graphics.Texture modalScrimTexture;
    private Texture fallbackBoxTexture;

    private int lastWidth = -1;
    private int lastHeight = -1;

    protected Label coinCountLabel;
    protected Label gemCountLabel;

    protected static final String CURRENCY_BOX_BG_ASSET_ID = "IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE";
    protected static final String COIN_ICON_ASSET_ID = "IMAGE_UI_THYMED_EVENTS_ECS_CONVRT_COIN";
    protected static final String GEM_ICON_ASSET_ID = "IMAGE_EFFECTS_COIN_DIAMOND_COIN_DIAMOND_141X146";
    protected static final String PLUS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_INGAME_COIN_BUY";
    protected static final String BACK_BUTTON_ASSET_ID = "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED";

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = Main.getInstance().getSkin();
        textureBank = Main.getInstance().getTextureBank();

        mainStack = new Stack();
        mainStack.setFillParent(true);

        modalStack = new Stack();
        toastStack = new Stack();

        modalStack.setTouchable(Touchable.childrenOnly);
        toastStack.setTouchable(Touchable.childrenOnly);

        rootTable = new Table();
        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);

        stage.addActor(mainStack);
        Gdx.input.setInputProcessor(stage);
    }

    public Actor createBackButton(MenuType targetMenu) {
        return createBackButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (targetMenu != null) {
                    MenuManager.getInstance().changeMenu(targetMenu);
                }
            }
        });
    }

    public Actor createBackButton(ClickListener clickListener) {
        TextureRegion backRegion = textureBank.region(BACK_BUTTON_ASSET_ID);
        if (backRegion != null) {
            TextureRegionDrawable backDrawable = new TextureRegionDrawable(backRegion);
            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = backDrawable;
            style.down = backDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));

            Button backButton = new Button(style);
            if (clickListener != null) {
                backButton.addListener(clickListener);
            }
            return backButton;
        } else {
            TextButton fallbackBackButton = new TextButton("Back", skin, "brown");
            if (clickListener != null) {
                fallbackBackButton.addListener(clickListener);
            }
            return fallbackBackButton;
        }
    }

    protected Label createBlackLabel(String text) {
        Label label = createLabel(text, "FBUSV8C5EI_2", Color.BLACK);
        label.setColor(Color.BLACK);
        return label;
    }

    protected void showModal(Actor content) {
        modalStack.clearChildren();

        if (modalScrimTexture == null) {
            com.badlogic.gdx.graphics.Pixmap pixmap =
                new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0f, 0f, 0f, 0.6f);
            pixmap.fill();
            modalScrimTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
            pixmap.dispose();
        }

        Image scrim = new Image(new TextureRegionDrawable(new TextureRegion(modalScrimTexture)));
        scrim.setFillParent(true);
        scrim.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeModal();
            }
        });
        modalStack.addActor(scrim);

        Table centerWrapper = new Table();
        centerWrapper.setFillParent(true);
        centerWrapper.add(content);
        modalStack.addActor(centerWrapper);
    }

    protected void closeModal() {
        modalStack.clearChildren();
    }

    protected void showToast(String message, String bgAssetId) {
        UIManager.showToast(message, bgAssetId);
    }

    public TextButton createStretchedButton(String text, String bgAssetId, ClickListener listener) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.get(Label.LabelStyle.class).font;
        style.fontColor = Color.BLACK;

        if (bgAssetId != null && !bgAssetId.isEmpty()) {
            TextureRegion bgRegion = textureBank.region(bgAssetId);
            if (bgRegion != null) {
                NinePatch patch = new NinePatch(bgRegion, 15, 15, 15, 15);
                style.up = new NinePatchDrawable(patch);
            }
        }

        TextButton button = new TextButton(text, style);
        button.getLabel().setColor(Color.BLACK);
        button.pad(10, 20, 10, 20);

        if (listener != null) {
            button.addListener(listener);
        }
        return button;
    }

    public TextButton createSkinButton(String text, String skinStyleName, ClickListener listener) {
        TextButton button = new TextButton(text, skin, skinStyleName);
        button.pad(10, 20, 10, 20);

        if (listener != null) {
            button.addListener(listener);
        }
        return button;
    }

    @Override
    public void render(float delta) {
        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();
        if (currentWidth != lastWidth || currentHeight != lastHeight) {
            resize(currentWidth, currentHeight);
        }

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (textureBank != null) {
            textureBank.update();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        lastWidth = width;
        lastHeight = height;

        float baseWidth = 1280f;
        float baseHeight = 720f;
        float scaleX = width / baseWidth;
        float scaleY = height / baseHeight;
        float scale = Math.min(scaleX, scaleY);
        float maxScale = 1.25f;
        float minScale = 0.75f;
        if (scale > maxScale) scale = maxScale;
        if (scale < minScale) scale = minScale;

        if (stage.getViewport() instanceof ScreenViewport) {
            ((ScreenViewport) stage.getViewport()).setUnitsPerPixel(1f / scale);
        }
        stage.getViewport().update(width, height, true);
    }

    protected Actor createCurrencyHud() {
        Table hudTable = new Table();
        User user = UsersManager.getInstance().getLoggedInUser();
        boolean debug = (user != null && user.isDebugMode());

        int coins = (user != null && user.getUserProgress() != null) ? user.getUserProgress().getCoinsCount() : 0;
        int gems = (user != null && user.getUserProgress() != null) ? user.getUserProgress().getGemsCount() : 0;

        Table coinBadge = buildCurrencyBadge(
            COIN_ICON_ASSET_ID,
            String.valueOf(coins),
            true,
            debug
        );

        Table gemBadge = buildCurrencyBadge(
            GEM_ICON_ASSET_ID,
            String.valueOf(gems),
            false,
            debug
        );

        hudTable.add(coinBadge).left().padRight(12);
        hudTable.add(gemBadge).left();

        return hudTable;
    }

    private Table buildCurrencyBadge(String iconAssetId, String initialValue, boolean isCoin, boolean isDebug) {
        Table badge = new Table();
        Stack stack = new Stack();

        Table boxTable = new Table();
        TextureRegion boxRegion = textureBank.region(CURRENCY_BOX_BG_ASSET_ID);
        if (boxRegion != null) {
            NinePatch patch = new NinePatch(boxRegion, 8, 8, 8, 8);
            boxTable.setBackground(new NinePatchDrawable(patch));
        }

        Label countLabel = createLabel(initialValue, "FBUSV8C5EI_1_outline", Color.WHITE);
        countLabel.setFontScale(0.5f);
        if (isCoin) {
            coinCountLabel = countLabel;
        } else {
            gemCountLabel = countLabel;
        }

        boxTable.add(countLabel).center().pad(4, 35, 4, isDebug ? 28 : 12).minWidth(60);
        stack.add(boxTable);

        Table overlayTable = new Table();
        overlayTable.setTouchable(Touchable.childrenOnly);

        TextureRegion iconRegion = textureBank.region(iconAssetId);
        if (iconRegion != null) {
            Image icon = new Image(iconRegion);
            overlayTable.add(icon).size(34, 34).left().padLeft(5f);
        } else {
            Label fallbackIcon = new Label(isCoin ? "C:" : "G:", skin);
            overlayTable.add(fallbackIcon).left().padLeft(-6f);
        }

        overlayTable.add().expandX();

        if (isDebug) {
            TextureRegion plusRegion = textureBank.region(PLUS_BUTTON_ASSET_ID);
            Actor plusActor;

            if (plusRegion != null) {
                TextureRegionDrawable plusDrawable = new TextureRegionDrawable(plusRegion);
                Button.ButtonStyle style = new Button.ButtonStyle();
                style.up = plusDrawable;
                style.down = plusDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));

                Button plusBtn = new Button(style);
                plusActor = plusBtn;
            } else {
                TextButton plusBtn = new TextButton("+", skin);
                plusActor = plusBtn;
            }

            plusActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isCoin) {
                        UsersManager.getInstance().addCoins(1000);
                    } else {
                        UsersManager.getInstance().addGems(100);
                    }
                    updateCurrencyHud();
                }
            });

            overlayTable.add(plusActor).size(35, 35).right().padRight(5);
        }

        stack.add(overlayTable);
        badge.add(stack).padLeft(10f).padRight(isDebug ? 10f : 0f);
        return badge;
    }

    public void updateCurrencyHud() {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user != null && user.getUserProgress() != null) {
            if (coinCountLabel != null) {
                coinCountLabel.setText(String.valueOf(user.getUserProgress().getCoinsCount()));
            }
            if (gemCountLabel != null) {
                gemCountLabel.setText(String.valueOf(user.getUserProgress().getGemsCount()));
            }
        }
    }

    protected Actor createAssetButton(
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

    protected Drawable getFallbackBoxDrawable() {
        if (fallbackBoxTexture == null) {
            Pixmap pixmap = new Pixmap(110, 140, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.28f, 0.22f, 0.15f, 0.9f);
            pixmap.fill();
            fallbackBoxTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return new TextureRegionDrawable(new TextureRegion(fallbackBoxTexture));
    }

    protected Stack buildIconBoxButton(
        String boxAssetId,
        String iconAssetId,
        float iconPadLeft,
        boolean tintIconDark,
        ClickListener clickListener
    ) {
        TextureRegion boxRegion = (boxAssetId != null && textureBank != null) ? textureBank.region(boxAssetId) : null;
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
        if (clickListener != null) {
            cardButton.addListener(clickListener);
        }

        Stack contentStack = new Stack();
        contentStack.add(cardButton);

        if (iconAssetId != null && textureBank != null) {
            TextureRegion iconRegion = textureBank.region(iconAssetId);
            if (iconRegion != null) {
                Image icon = new Image(iconRegion);
                icon.setScaling(Scaling.none);
                icon.setAlign(Align.left);
                if (tintIconDark) {
                    icon.setColor(0.25f, 0.25f, 0.25f, 1f);
                }
                Table iconTable = new Table();
                iconTable.setTouchable(Touchable.disabled);
                iconTable.left();
                iconTable.add(icon).left().padLeft(iconPadLeft);
                contentStack.add(iconTable);
            }
        }

        return contentStack;
    }

    protected Table buildCornerBadge(String text, float fontScale) {
        Table badgeInner = new Table();
        TextureRegion badgeBg = textureBank.region(CURRENCY_BOX_BG_ASSET_ID);
        if (badgeBg != null) {
            NinePatchDrawable patchDrawable = new NinePatchDrawable(new NinePatch(badgeBg, 4, 4, 4, 4));
            patchDrawable.setMinWidth(0);
            patchDrawable.setMinHeight(0);
            badgeInner.setBackground(patchDrawable);
        }
        Label label = createLabel(text, "FBUSV8C5EI_1_outline", Color.WHITE);
        label.setFontScale(fontScale);
        badgeInner.add(label).padLeft(3).padRight(3);
        badgeInner.pad(1, 2, 1, 2);
        badgeInner.pack();
        return badgeInner;
    }

    protected String formatEnumName(String rawName) {
        if (rawName == null) return "Unknown";
        String[] parts = rawName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)))
                .append(part.substring(1).toLowerCase())
                .append(" ");
        }
        return sb.toString().trim();
    }

    protected Table buildPlantStatsBlock(PlantType type, int level) {
        Table block = new Table();
        block.top().left();

        PlantStats stats = null;
        try {
            BattlePlant battlePlant = PlantFactory.createBattlePlant(type.getName(), Math.max(level, 1));
            if (battlePlant != null) stats = battlePlant.getPlantStats();
        } catch (Exception ignored) {
        }

        Label nameLabel = createBlackLabel(formatEnumName(type.getName()));
        nameLabel.setFontScale(1.15f);
        block.add(nameLabel).left().padBottom(8).row();

        block.add(createBlackLabel("Level: " + level + " / " + UserProgressManager.getMaxPlantLevel()))
            .left().padBottom(4).row();

        if (stats != null) {
            block.add(createBlackLabel("Family: " + formatEnumName(stats.getCategory()))).left().padBottom(4).row();
            block.add(createBlackLabel("Sun Cost: " + stats.getCost())).left().padBottom(4).row();
            block.add(createBlackLabel("Health: " + stats.getBaseHP())).left().padBottom(4).row();
            if (stats.getTags() != null && !stats.getTags().isEmpty()) {
                block.add(createBlackLabel("Tags: " + String.join(", ", stats.getTags())))
                    .left().padBottom(4).row();
            }
        } else {
            block.add(createBlackLabel("Details unavailable.")).left().padBottom(4).row();
        }

        return block;
    }

    protected Actor createAnimationActor(String animationPath, String stateName) {
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

    public Label createLabel(
        String text,
        String fontName,
        Color fontColor
    ) {
        BitmapFont font = skin.get(fontName, BitmapFont.class);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.fontColor = fontColor;

        return new Label(text, style);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (modalScrimTexture != null) {
            modalScrimTexture.dispose();
        }
        if (fallbackBoxTexture != null) {
            fallbackBoxTexture.dispose();
        }
    }
}

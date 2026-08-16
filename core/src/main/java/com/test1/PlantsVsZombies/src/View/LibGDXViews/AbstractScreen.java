package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import pvz.libpvz.textures.TextureBank;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;
    protected TextureBank textureBank;
    protected Table rootTable;
    private Stack mainStack;
    private Stack modalStack;
    private Stack toastStack;






    private int lastWidth = -1;
    private int lastHeight = -1;

    protected Label coinCountLabel;
    protected Label gemCountLabel;


    protected static final String CURRENCY_BOX_BG_ASSET_ID = "IMAGE_UI_GENERIC_BUTTON_GENERIC_LTECURRENCY";
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
        TextureRegion backRegion = textureBank.region(BACK_BUTTON_ASSET_ID);
        if (backRegion != null) {
            TextureRegionDrawable backDrawable = new TextureRegionDrawable(backRegion);
            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = backDrawable;
            style.down = backDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f));

            Button backButton = new Button(style);
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (targetMenu != null) {
                        MenuManager.getInstance().changeMenu(targetMenu);
                    }
                }
            });
            return backButton;
        } else {
            TextButton fallbackBackButton = new TextButton("Back", skin, "brown");
            fallbackBackButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (targetMenu != null) {
                        MenuManager.getInstance().changeMenu(targetMenu);
                    }
                }
            });
            return fallbackBackButton;
        }
    }

    protected Label createBlackLabel(String text) {
        Label label = new Label(text, skin);
        label.setColor(Color.BLACK);
        return label;
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
    }
}

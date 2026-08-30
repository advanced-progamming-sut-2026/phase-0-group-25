package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Network.IZombieNetworkBridge;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class IZombieScreen extends ScreenAdapter {
    private final IZombie gamePlay;
    private final boolean isCouchPlay;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private BitmapFont hudFont;

    private IZombieWorldRenderer worldRenderer;
    private IZombieHudRenderer hudRenderer;
    private IZombieNetworkBridge networkBridge;
    private GamePlayModals modals;

    private float stateTime = 0f;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;

    public IZombieScreen(IZombie gamePlay, boolean isCouchPlay) {
        this.gamePlay = gamePlay;
        this.isCouchPlay = isCouchPlay;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new ScreenViewport();
        textureBank = new TextureBank("768", Gdx.files.local("assets/Assets"));
        player = new PamPlayer(textureBank, Gdx.files.local("assets/Assets"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("pvz.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 40;
        param.color = Color.WHITE;
        param.borderColor = Color.BLACK;
        param.borderWidth = 2;
        hudFont = generator.generateFont(param);
        generator.dispose();

        worldRenderer = new IZombieWorldRenderer(textureBank, player);
        hudRenderer = new IZombieHudRenderer(textureBank, hudFont);
        networkBridge = new IZombieNetworkBridge(gamePlay);


        networkBridge.setOnReactionReceived(data -> {
            String category = (String) data.get("category");
            String reactionId = (String) data.get("reactionId");
            if ("TEXT".equals(category)) {
                hudRenderer.triggerReactionText(reactionId);
            } else if ("STICKER".equals(category)) {
                hudRenderer.triggerSticker(reactionId);
            }
        });

        modals = new GamePlayModals(
            gamePlay,
            () -> MenuManager.getInstance().changeMenu(MenuType.TravelLog),
            () -> MenuManager.getInstance().getTravelLogMenu().startMiniGame("I Zombie")
        );

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(modals.getStage());
        if (isCouchPlay) {
            multiplexer.addProcessor(new IZombieCouchPlayInputHandler(gamePlay, camera));
        } else {
            multiplexer.addProcessor(new IZombieInputHandler(gamePlay, camera, networkBridge));
        }
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        textureBank.update();
        ScreenUtils.clear(0.1f, 0.35f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            stateTime += delta;
            timeAccumulator += delta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
            modals.checkAndMaybeShowEndGameModal();
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);


        worldRenderer.render(batch, gamePlay, stateTime);


        hudRenderer.render(batch, gamePlay, player, stateTime, delta);


        modals.getStage().act(delta);
        modals.getStage().draw();
        UIManager.renderToasts(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(1920 / 2f, 1200 / 2f, 0);
        camera.update();
        if (modals != null) modals.resize(width, height);
        UIManager.resizeToasts(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        hudFont.dispose();
        if (modals != null) modals.dispose();
    }
}

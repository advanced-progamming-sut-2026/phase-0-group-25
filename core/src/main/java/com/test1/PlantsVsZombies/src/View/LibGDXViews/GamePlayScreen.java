package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.*;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class GamePlayScreen extends ScreenAdapter implements GamePlayMenuView {
    public static final String PAUSE_BTN_ASSET_ID = "IMAGE_UI_HUD_INGAME_PAUSE_BUTTON";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";

    private final GamePlay gamePlay;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private BitmapFont hudFont;

    private float stateTime = 0;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;

    private GamePlayModals modals;
    private IntroDialogueCutscene introCutscene;
    private GamePlayWorldRenderer worldRenderer;
    private GamePlayHudRenderer hudRenderer;
    private GamePlayInputHandler inputHandler;

    public GamePlayScreen(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new ScreenViewport();
        textureBank = new TextureBank("768", Gdx.files.local("assets/Assets"));
        player = new PamPlayer(textureBank, Gdx.files.local("assets/Assets"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("pvz.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3;
        hudFont = generator.generateFont(parameter);
        generator.dispose();

        if (gamePlay.getChapterType() == ChapterType.ANCIENT_EGYPT && gamePlay.getLevel() == 1) {
            introCutscene = new IntroDialogueCutscene(textureBank);
        }

        modals = new GamePlayModals(
            gamePlay,
            () -> MenuManager.getInstance().changeMenu(MenuType.Game),
            () -> MenuManager.getInstance().getGameMenu().startGame(gamePlay.getLevel())
        );

        worldRenderer = new GamePlayWorldRenderer(textureBank, player, gamePlay.getChapterType());
        hudRenderer = new GamePlayHudRenderer(textureBank, player, hudFont);
        inputHandler = new GamePlayInputHandler(gamePlay, camera, modals, introCutscene, this);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(modals.getStage());
        multiplexer.addProcessor(inputHandler);
        Gdx.input.setInputProcessor(multiplexer);

        modals.showObjectivesModal(() -> {
            if (introCutscene == null || introCutscene.isFinished()) {
                gamePlay.isPaused = false;
            }
        });
    }

    @Override
    public void render(float delta) {
        int gameSpeed = 1;
        User user = gamePlay.getThisUser();
        if (user != null && user.getUserProgress() != null) {
            gameSpeed = Math.max(1, Math.min(3, user.getUserProgress().getGameSpeed()));
        }
        float effectiveDelta = delta * gameSpeed;

        textureBank.update();
        ScreenUtils.clear(0.1f, 0.4f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            stateTime += effectiveDelta;
            gamePlay.setTotalTimePassed(stateTime);

            timeAccumulator += effectiveDelta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }

            modals.checkAndMaybeShowEndGameModal();

            for (Mower mower : gamePlay.getMowers()) mower.update(effectiveDelta, gamePlay);
            for (Sun sun : gamePlay.getActiveSuns()) sun.update(effectiveDelta);
            if (gamePlay instanceof ConveyorBelt cb) {
                for (ConveyorCard card : cb.getConveyorCards()) card.update(effectiveDelta);
            }
        }

        ScreenShake.update(delta, camera);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        float renderDelta = gamePlay.isPaused() ? 0f : effectiveDelta;

        batch.begin();
        worldRenderer.render(batch, gamePlay, stateTime, renderDelta);
        batch.end();

        hudRenderer.render(batch, shapeRenderer, gamePlay, inputHandler, stateTime);

        if (introCutscene != null && !introCutscene.isFinished()) {
            introCutscene.render(batch, shapeRenderer, player, hudFont, stateTime);
        }

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
        shapeRenderer.dispose();
        batch.dispose();
        hudFont.dispose();
        if (modals != null) modals.dispose();
    }

    @Override
    public void showCurrentMenu() {}

    @Override
    public void showError(String errorMessage) {
        UIManager.showToast(errorMessage, ERROR_BG_ASSET_ID);
    }
}

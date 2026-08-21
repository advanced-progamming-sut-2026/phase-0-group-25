package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.Simple;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class GamePlayScreen extends ScreenAdapter implements GamePlayMenuView {
    private GamePlay gamePlay;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private TextureRegion region;
    private PamPlayer player;
    private float stateTime = 0;
    private BitmapFont hudFont;
    private TextureRegion sunIcon;
    private TextureRegion plantFoodIcon;
    private TextureRegion bgHud;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;
    private TextureRegion flagIcon;
    private TextureRegion zombieHeadIcon;
    private TextureRegion progressBarFrame;


    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";


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

        String bgKey = getBgPath(gamePlay.getChapterType());
        region = textureBank.region(bgKey);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("assets/pvz.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3;
        hudFont = generator.generateFont(parameter);
        generator.dispose();

        sunIcon = textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
        plantFoodIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON");
        bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        flagIcon = textureBank.region("IMAGE_ZOMBIE_ZOMBIE_FEASTIVUS_FLAG_ZOMBIE_FEASTIVUS_FLAG_123X95");
        zombieHeadIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        progressBarFrame = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));

                gamePlay.tryCollectSunByClick(worldPos.x, worldPos.y);
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                gamePlay.tryCollectSunByClick(worldPos.x, worldPos.y);
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        gamePlay.setTotalTimePassed(stateTime);
        textureBank.update();

        ScreenUtils.clear(0.1f, 0.4f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            timeAccumulator += delta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
        }

        camera.update();

        for (Mower mower : gamePlay.getMowers()) {
            mower.update(delta, gamePlay);
        }


        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPosition() != null && p.getPlantStats().getAnimation() != null) {

                float drawX = (float) p.getPosition().getX();
                float drawY = (float) p.getPosition().getY();

                player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(stateTime),
                    stateTime, drawX, drawY, true, p.getVisibilities());
            }
        }

        for (Sun sun : gamePlay.getActiveSuns()) {
            sun.update(delta);
        }

        for (Sun sun : gamePlay.getActiveSuns()) {
            if (!sun.isCollected()) {
                float x = (float) sun.getPosition().getX() + 40;
                float y = (float) sun.getPosition().getY() + 40;

                if (sun.getNumberOfSun() >= 100) {
                    float scale = 1.35f;

                    batch.setTransformMatrix(batch.getTransformMatrix().idt()
                        .translate(x, y, 0)
                        .scale(scale, scale, 1)
                        .translate(-x, -y, 0));

                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);

                    batch.setTransformMatrix(batch.getTransformMatrix().idt());
                } else {
                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);
                }
            }
        }

        for (Zombie zombie : gamePlay.getGameZombies()) {
            if (zombie.isAlive()) {
                float px = (float) zombie.getPosition().getX();
                float py = (float) zombie.getPosition().getY();
                batch.setColor(zombie.getColor());
                player.draw(batch, zombie.getZombieStats().getAnimation(), zombie.getCurrentAnimationName(),
                    stateTime, px, py, true, zombie.getVisibility());
                batch.setColor(Color.WHITE);
            }
        }

        for (Mower mower : gamePlay.getMowers()) {
            if (!mower.isDone()) {
                player.draw(batch, mower.getAnimationPath(), mower.getCurrentAnimState(),
                    stateTime, mower.getX(), mower.getY(), true);
            }
        }

        batch.draw(bgHud, 20, 1100, 200, 80);
        batch.draw(bgHud, 240, 1100, 200, 80);
        batch.draw(sunIcon, 30, 1110, 60, 60);
        batch.draw(plantFoodIcon, 250, 1110, 60, 60);

        String sunCount = String.valueOf(gamePlay.getMySuns());
        String pfCount = String.valueOf(gamePlay.getNumOfPlantFood());
        hudFont.draw(batch, sunCount, 110, 1160);
        hudFont.draw(batch, pfCount, 330, 1160);

        batch.end();


        int totalWaves = gamePlay.calculateWaves(gamePlay.getChapterType(), gamePlay.getLevel());
        float progress = gamePlay.getProgressPercentage();


        float barWidth = 450f;
        float barHeight = 45f;


        float barLeftX = (1920f - barWidth) / 2f;
        float barRightX = barLeftX + barWidth;
        float barY = 1130f;


        float headX = barRightX - (barWidth * progress);
        float greenWidth = barRightX - headX;


        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(barLeftX + 15, barY + 10, barWidth - 30, barHeight - 20);

        if (greenWidth > 0) {
            shapeRenderer.setColor(new Color(0.2f, 0.9f, 0.2f, 1f));
            shapeRenderer.rect(Math.max(headX, barLeftX + 15), barY + 10, greenWidth - 15, barHeight - 20);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


        batch.begin();
        batch.draw(progressBarFrame, barLeftX, barY, barWidth, barHeight);


        for (int i = 0; i < totalWaves; i++) {
            float flagProgressPercent = (float) i / totalWaves;
            float flagX = barRightX - (barWidth * flagProgressPercent);


            if (i == totalWaves - 1) {
                batch.draw(flagIcon, flagX - 15, barY + 5, 45, 55);
            } else {
                batch.draw(flagIcon, flagX - 10, barY + 10, 30, 40);
            }
        }


        batch.draw(zombieHeadIcon, headX - 25, barY - 5, 50, 50);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(1920 / 2f, 1200 / 2f, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        hudFont.dispose();
    }

    private String getBgPath(ChapterType chapterType) {
        return switch (chapterType) {
            case MINI_GAME -> "IMAGE_BACKGROUNDS_BACKGROUND_LOD_BIGBRAINZ_TEXTURE";
            case ANCIENT_EGYPT -> "IMAGE_BACKGROUNDS_EGYPT_TEXTURE";
            case DARK_AGE -> "IMAGE_BACKGROUNDS_DARK_TEXTURE";
            case FROSTBITE_CAVES -> "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE";
            case BIG_WAVE_BEACH -> "IMAGE_BACKGROUNDS_BEACH_TEXTURE";
        };
    }

    @Override
    public void showCurrentMenu() {

    }

    @Override
    public void showError(String errorMessage) {
        UIManager.showToast(errorMessage, ERROR_BG_ASSET_ID);
    }
}

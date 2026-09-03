package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;

public class WallnutBowlingScreen extends ScreenAdapter implements GamePlayMenuView {
    public static final String PAUSE_BTN_ASSET_ID = "IMAGE_UI_HUD_INGAME_PAUSE_BUTTON";
    private static final float CARD_X = 45f;
    private static final float CARD_WIDTH = 160f;
    private static final float CARD_HEIGHT = 105f;
    private static final float BELT_SEGMENT_HEIGHT = 20f;
    private static final float BELT_WIDTH = 175f;
    private static final float BELT_SCROLL_SPEED = 60f;
    private static final float RED_LINE_X = 490f + (3 * 152.2f) - 5f;
    private static final String NUT_NORMAL_ANIM = "768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE1/BOWLINGBULB_PROJECTILE1.PAM";
    private static final String NUT_BIG_ANIM = "768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE3/BOWLINGBULB_PROJECTILE3.PAM";
    private static final String NUT_EXPLODE_ANIM = "768/FULL/EFFECTS/BOWLINGBULB_PLANTFOOD_PROJECTILE/BOWLINGBULB_PLANTFOOD_PROJECTILE.PAM";
    private static final float PAUSE_BTN_X = 1810f;
    private static final float PAUSE_BTN_Y = 1105f;
    private static final float PAUSE_BTN_SIZE = 75f;
    private final WalnutBowling gamePlay;
    private final Vector3 mouseWorldPos = new Vector3();
    private final float TICK_RATE = 0.1f;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private TextureRegion bgRegion;
    private TextureRegion conveyorTrackRegion;
    private TextureRegion cardBgRegion;
    private TextureRegion normalNutIcon;
    private TextureRegion explodeNutIcon;
    private TextureRegion bigNutIcon;
    private TextureRegion progressBarFrame;
    private TextureRegion flagIcon;
    private TextureRegion zombieHeadIcon;
    private BowlingCard selectedBowlingCard = null;
    private float stateTime = 0f;
    private float timeAccumulator = 0f;
    private TextureRegion pauseBtnRegion;
    private GamePlayModals modals;

    public WallnutBowlingScreen(WalnutBowling gamePlay) {
        this.gamePlay = gamePlay;
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

        bgRegion = textureBank.region("IMAGE_BACKGROUNDS_LOSTCITY_TEXTURE");
        conveyorTrackRegion = textureBank.region("IMAGE_UI_CONVEYOR_CONVEYOR_BELT");
        cardBgRegion = textureBank.region("IMAGE_UI_PACKETS_SELECTED");

        normalNutIcon = textureBank.region("IMAGE_UI_PACKETS_TOOLS_PROJECTILE_BOWLINGBULB_MEGA");
        explodeNutIcon = textureBank.region("IMAGE_UI_PACKETS_TOOLS_PROJECTILE_BOWLINGBULB_EXPLODE");
        bigNutIcon = textureBank.region("IMAGE_UI_PACKETS_TOOLS_PROJECTILE_BOWLINGBULB3");

        progressBarFrame = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        flagIcon = textureBank.region("IMAGE_ZOMBIE_ZOMBIE_FEASTIVUS_FLAG_ZOMBIE_FEASTIVUS_FLAG_123X95");
        zombieHeadIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");

        pauseBtnRegion = textureBank.region(PAUSE_BTN_ASSET_ID);
        if (pauseBtnRegion == null) {
            pauseBtnRegion = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        }

        UIManager.resizeToasts(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        modals = new GamePlayModals(
            gamePlay,
            () -> MenuManager.getInstance().changeMenu(MenuType.TravelLog),
            () -> MenuManager.getInstance().getTravelLogMenu().startMiniGame(MiniGameType.WALNUT_BOWLING.getDisplayName())
        );

        InputAdapter gameInputAdapter = new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));

                if (button == Input.Buttons.RIGHT) {
                    selectedBowlingCard = null;
                    return true;
                }

                if (mouseWorldPos.x >= PAUSE_BTN_X && mouseWorldPos.x <= PAUSE_BTN_X + PAUSE_BTN_SIZE &&
                    mouseWorldPos.y >= PAUSE_BTN_Y && mouseWorldPos.y <= PAUSE_BTN_Y + PAUSE_BTN_SIZE) {
                    if (!gamePlay.isGameOver()) {
                        modals.showPauseModal();
                        return true;
                    }
                }

                for (BowlingCard card : gamePlay.getConveyorBelt()) {
                    float cardY = card.getCurrentY();
                    if (mouseWorldPos.x >= CARD_X && mouseWorldPos.x <= CARD_X + CARD_WIDTH &&
                        mouseWorldPos.y >= cardY && mouseWorldPos.y <= cardY + CARD_HEIGHT) {
                        selectedBowlingCard = (selectedBowlingCard == card) ? null : card;
                        return true;
                    }
                }

                int col = (int) Math.floor((mouseWorldPos.x - 490) / 152.2) + 1;
                int row = (int) Math.floor((mouseWorldPos.y - 130) / 150) + 1;

                if (col >= 1 && col <= 3 && row >= 1 && row <= 5) {
                    if (selectedBowlingCard != null) {
                        gamePlay.plantWalnut(selectedBowlingCard, col, row);
                        selectedBowlingCard = null;
                        return true;
                    }
                } else if (col > 3 && selectedBowlingCard != null) {
                    UIManager.showToast("Must place behind the red line!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                }

                return false;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(modals.getStage());
        multiplexer.addProcessor(gameInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);

        modals.showObjectivesModal(null);
    }

    @Override
    public void render(float delta) {
        textureBank.update();

        int gameSpeed = 1;
        User user = gamePlay.getThisUser();
        if (user != null && user.getUserProgress() != null) {
            gameSpeed = Math.max(1, Math.min(3, user.getUserProgress().getGameSpeed()));
        }
        float effectiveDelta = delta * gameSpeed;

        if (!gamePlay.isPaused()) {
            stateTime += effectiveDelta;

            timeAccumulator += effectiveDelta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
            gamePlay.updateWithDelta(effectiveDelta);

            modals.checkAndMaybeShowEndGameModal();
        }

        ScreenUtils.clear(0.1f, 0.35f, 0.1f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        if (bgRegion != null) batch.draw(bgRegion, 0, 0, 1920, 1200);

        if (conveyorTrackRegion != null) {
            float scrollOffset = (stateTime * BELT_SCROLL_SPEED) % BELT_SEGMENT_HEIGHT;
            for (float y = 120f - BELT_SEGMENT_HEIGHT; y <= 1110f + BELT_SEGMENT_HEIGHT; y += BELT_SEGMENT_HEIGHT) {
                float drawY = y + scrollOffset;
                if (drawY >= 120f && drawY <= 1110f) {
                    batch.draw(conveyorTrackRegion, CARD_X - 7f, drawY, BELT_WIDTH, BELT_SEGMENT_HEIGHT);
                }
            }
        }

        ArrayList<BowlingCard> cards = gamePlay.getConveyorBelt();
        for (BowlingCard card : cards) {
            float cardY = card.getCurrentY();
            String nutType = card.getNutType();

            if (selectedBowlingCard == card) batch.setColor(0.6f, 1f, 0.6f, 1f);
            if (cardBgRegion != null) batch.draw(cardBgRegion, CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT);

            TextureRegion currentIcon = switch (nutType) {
                case "ExplodingWalnut" -> explodeNutIcon;
                case "BigWalnut" -> bigNutIcon;
                default -> normalNutIcon;
            };

            if (currentIcon != null) {
                float availW = CARD_WIDTH - 20f;
                float availH = CARD_HEIGHT - 35f;
                float scale = Math.min(availW / currentIcon.getRegionWidth(), availH / currentIcon.getRegionHeight());
                float finalW = currentIcon.getRegionWidth() * scale;
                float finalH = currentIcon.getRegionHeight() * scale;
                batch.draw(currentIcon, CARD_X + (CARD_WIDTH - finalW) / 2f, cardY + 15f + (availH - finalH) / 2f, finalW, finalH);
            }
            batch.setColor(Color.WHITE);
        }


        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getZombieStats().getAnimation() != null) {
                player.draw(batch, z.getZombieStats().getAnimation(), z.getCurrentAnimationName(),
                    stateTime, (float) z.getPosition().getX(), (float) z.getPosition().getY(), true, z.getVisibility());
            }
        }


        for (Walnut w : gamePlay.getActiveWalnuts()) {
            float wx = (float) w.getX();
            float wy = (float) w.getY();

            if (w instanceof ExplodingWalnut) {
                player.draw(batch, NUT_EXPLODE_ANIM, "animation", stateTime * 4f, wx, wy, true);
            } else if (w instanceof BigWalnut) {
                player.draw(batch, NUT_BIG_ANIM, "animation", stateTime * 3f, wx, wy, true);
            } else {
                player.draw(batch, NUT_NORMAL_ANIM, "animation", stateTime * 4f, wx, wy, true);
            }
        }


        for (float[] exp : gamePlay.getActiveExplosions()) {
            player.draw(batch, NUT_EXPLODE_ANIM, "explosion", exp[2], exp[0], exp[1], true);
        }


        if (selectedBowlingCard != null) {
            switch (selectedBowlingCard.getNutType()) {
                case "ExplodingWalnut" ->
                    player.draw(batch, NUT_EXPLODE_ANIM, "animation", stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
                case "BigWalnut" -> {
                    player.draw(batch, NUT_BIG_ANIM, "animation", stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
                }
                default ->
                    player.draw(batch, NUT_NORMAL_ANIM, "animation", stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
            }
        }

        batch.end();


        float barWidth = 450f;
        float barHeight = 45f;
        float barLeftX = (1920f - barWidth) / 2f;
        float barRightX = barLeftX + barWidth;
        float barY = 1130f;

        float innerBarWidth = barWidth - 30f;
        float progress = Math.max(0f, Math.min(1.0f, gamePlay.getProgressPercentage()));
        float greenWidth = innerBarWidth * progress;
        float headX = (barRightX - 15f) - greenWidth;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(barLeftX + 15, barY + 10, innerBarWidth, barHeight - 20);

        if (greenWidth > 0) {
            shapeRenderer.setColor(new Color(0.2f, 0.9f, 0.2f, 1f));
            shapeRenderer.rect(headX, barY + 10, greenWidth, barHeight - 20);
        }
        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        float pulse = 0.75f + 0.25f * (float) Math.sin(stateTime * 6f);
        shapeRenderer.setColor(new Color(1f, 0.1f, 0.1f, pulse));
        Gdx.gl.glLineWidth(6);
        shapeRenderer.line(RED_LINE_X, 130f, RED_LINE_X, 130f + (5 * 150f));
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        batch.begin();
        if (progressBarFrame != null) {
            batch.draw(progressBarFrame, barLeftX, barY, barWidth, barHeight);
        }

        int totalWaves = Math.max(1, gamePlay.calculateWaves(gamePlay.getChapterType(), gamePlay.getLevel()));
        for (int i = 0; i < totalWaves; i++) {
            float flagProgressPercent = (float) (i + 1) / totalWaves;
            float flagX = barRightX - (innerBarWidth * flagProgressPercent) - 15f;

            if (flagIcon != null) {
                if (i == totalWaves - 1) {
                    batch.draw(flagIcon, flagX - 15, barY + 5, 45, 55);
                } else {
                    batch.draw(flagIcon, flagX - 10, barY + 10, 30, 40);
                }
            }
        }

        if (zombieHeadIcon != null) {
            batch.draw(zombieHeadIcon, headX - 25, barY - 5, 50, 50);
        }

        if (pauseBtnRegion != null) {
            batch.draw(pauseBtnRegion, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE);
        }

        batch.end();

        modals.getStage().act(delta);
        modals.getStage().draw();

        UIManager.renderToasts(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(1920 / 2f, 1200 / 2f, 0);
        camera.update();
        if (modals != null) {
            modals.resize(width, height);
        }
        UIManager.resizeToasts(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (modals != null) {
            modals.dispose();
        }
    }

    @Override
    public void showCurrentMenu() {
    }

    @Override
    public void showError(String errorMessage) {
        UIManager.showToast(errorMessage, "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
    }
}

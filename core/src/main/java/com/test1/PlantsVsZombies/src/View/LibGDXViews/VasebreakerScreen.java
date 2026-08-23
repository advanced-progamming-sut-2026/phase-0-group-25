package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.*;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;

public class VasebreakerScreen extends ScreenAdapter implements GamePlayMenuView {
    private VaseBreaker gamePlay;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private TextureRegion bgRegion;
    private BattlePlant heldPlant = null;
    private TextureRegion orangeJarIcon;
    private TextureRegion greenJarIcon;
    private TextureRegion purpleJarIcon;
    private TextureRegion progressBarFrame;
    private TextureRegion peaRegion;
    private BitmapFont hudFont;

    private float stateTime = 0;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;
    private int selectedCardIndex = -1;
    private Vector3 mouseWorldPos = new Vector3();

    public VasebreakerScreen(VaseBreaker gamePlay) {
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

        bgRegion = textureBank.region("IMAGE_BACKGROUNDS_BACKGROUND_LOD_BIGBRAINZ_TEXTURE");
        orangeJarIcon = textureBank.region("IMAGE_VASEBREAKER_VASE_BROWN_VASE_BROWN_115X150");
        greenJarIcon = textureBank.region("IMAGE_VASEBREAKER_VASE_GREEN_VASE_GREEN_115X150");
        purpleJarIcon = textureBank.region("IMAGE_VASEBREAKER_VASE_GARGANTUAR_VASE_GARGANTUAR_115X150");
        progressBarFrame = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        peaRegion = textureBank.region("IMAGE_PROJECTILES_PEA");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("assets/pvz.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        hudFont = generator.generateFont(parameter);
        generator.dispose();

        UIManager.resizeToasts(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setupInputProcessor();
    }

    private void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                float wx = mouseWorldPos.x;
                float wy = mouseWorldPos.y;

                if (button == Input.Buttons.RIGHT) {
                    heldPlant = null;
                    return true;
                }

                int gridX = (int) Math.round((wx - 566.1) / 152.2) + 1;
                int gridY = (int) Math.round((wy - 205) / 150) + 1;

                if (gridX >= 1 && gridX <= 9 && gridY >= 1 && gridY <= 5) {
                    if (heldPlant != null) {

                        if (gamePlay.plantOnTile(heldPlant, gridX, gridY)) {
                            heldPlant = null;
                        } else {
                            UIManager.showToast("Cannot plant here!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        }
                    } else {

                        BattlePlant releasedPlant = gamePlay.breakJar(gridX, gridY);
                        if (releasedPlant != null) {
                            heldPlant = releasedPlant;
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        textureBank.update();

        if (!gamePlay.isPaused()) {
            timeAccumulator += delta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgRegion, 0, 0, 1920, 1200);

        for (Jar jar : gamePlay.getJars()) {
            if (!jar.isBroken()) {
                float jX = (float) jar.getPosition().getX();
                float jY = (float) jar.getPosition().getY();

                TextureRegion tex = orangeJarIcon;
                if (jar instanceof GargantuarJar) {
                    tex = (purpleJarIcon != null) ? purpleJarIcon : orangeJarIcon;
                } else if (jar instanceof PlantJar) {
                    tex = (greenJarIcon != null) ? greenJarIcon : orangeJarIcon;
                }

                if (tex != null) {
                    batch.draw(tex, jX - 60f, jY - 25f, 120f, 150f);
                }
            }
        }

        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPlantStats().getAnimation() != null) {
                player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(),
                    stateTime, (float) p.getPosition().getX(), (float) p.getPosition().getY(), true);
            }
        }

        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getZombieStats().getAnimation() != null) {
                player.draw(batch, z.getZombieStats().getAnimation(), z.getCurrentAnimationName(),
                    stateTime, (float) z.getPosition().getX(), (float) z.getPosition().getY(), true, z.getVisibility());
            }
        }

        for (Projectile proj : gamePlay.getProjectiles()) {
            if (proj.isActive() && peaRegion != null) {
                batch.draw(peaRegion, (float) proj.getPosition().getX(), (float) proj.getPosition().getY() + 30f, 32f, 32f);
            }
        }

        batch.end();

        int totalJars = gamePlay.getJars().size();
        long brokenJars = gamePlay.getJars().stream().filter(Jar::isBroken).count();
        float jarProgress = (totalJars > 0) ? (float) brokenJars / totalJars : 0f;

        float barWidth = 450f;
        float barHeight = 45f;
        float barLeftX = (1920f - barWidth) / 2f;
        float barY = 1130f;
        float fillWidth = (barWidth - 30f) * jarProgress;

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (heldPlant != null) {
            int hoverCol = (int) Math.round((mouseWorldPos.x - 566.1) / 152.2) + 1;
            int hoverRow = (int) Math.round((mouseWorldPos.y - 205) / 150) + 1;

            if (hoverCol >= 1 && hoverCol <= 9 && hoverRow >= 1 && hoverRow <= 5) {
                float tileX = (float) (566.1 + (hoverCol - 1) * 152.2) - 75f;
                float tileY = (float) (205 + (hoverRow - 1) * 150) - 25f;

                shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.35f));
                shapeRenderer.rect(tileX-13 , tileY-35, 145f, 145f);
            }
        }

        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.85f));
        shapeRenderer.rect(barLeftX + 15, barY + 10, barWidth - 30, barHeight - 20);

        if (fillWidth > 0) {
            shapeRenderer.setColor(new Color(0.2f, 0.9f, 0.2f, 1f));
            shapeRenderer.rect(barLeftX + 15, barY + 10, fillWidth, barHeight - 20);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        batch.begin();

        if (progressBarFrame != null) {
            batch.draw(progressBarFrame, barLeftX, barY, barWidth, barHeight);
        }

        if (orangeJarIcon != null) {
            batch.draw(orangeJarIcon, barLeftX + fillWidth - 10f, barY - 5f, 40f, 50f);
        }

        if (hudFont != null) {
            String progressText = brokenJars + " / " + totalJars + " Jars";
            hudFont.draw(batch, progressText, barLeftX + (barWidth / 2f) - 75f, barY + 32f);
        }

        if (heldPlant != null && heldPlant.getPlantStats().getAnimation() != null) {
            String strOfidle = PlantType.fromName(heldPlant.getName()).getStateName();
            player.draw(batch, heldPlant.getPlantStats().getAnimation(), strOfidle,
                stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
        }

        batch.end();

        UIManager.renderToasts(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(1920 / 2f, 1200 / 2f, 0);
        camera.update();
        UIManager.resizeToasts(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (hudFont != null) {
            hudFont.dispose();
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

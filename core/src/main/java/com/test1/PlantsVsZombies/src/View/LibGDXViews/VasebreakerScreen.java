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

import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class VasebreakerScreen extends ScreenAdapter {
    private VaseBreaker gamePlay;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private TextureRegion bgRegion;


    private TextureRegion orangeJarIcon;
    private TextureRegion greenJarIcon;
    private TextureRegion purpleJarIcon;

    private float stateTime = 0;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;
    private int selectedCardIndex = -1;

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

        setupInputProcessor();
    }

    private void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                float wx = worldPos.x;
                float wy = worldPos.y;


                if (wy >= 1050) {
                    int index = (int) ((wx - 20) / 110);
                    if (index >= 0 && index < gamePlay.getInventory().size()) {
                        selectedCardIndex = index;
                    }
                    return true;
                }

                int gridX = (int) Math.round((wx - 566.1) / 152.2) + 1;
                int gridY = (int) Math.round((wy - 205) / 150) + 1;

                if (gridX >= 1 && gridX <= 9 && gridY >= 1 && gridY <= 5) {

                    if (selectedCardIndex != -1) {
                        gamePlay.plantFromInventory(selectedCardIndex, gridX, gridY);
                        selectedCardIndex = -1;
                    }

                    else {
                        gamePlay.breakJar(gridX, gridY);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        textureBank.update();

        if (!gamePlay.isPaused) {
            timeAccumulator += delta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgRegion, 0, 0, camera.viewportWidth, camera.viewportHeight);


        for (Jar jar : gamePlay.getJars()) {
            if (!jar.isBroken()) {
                float jX = (float) jar.getPosition().getX();
                float jY = (float) jar.getPosition().getY();

                TextureRegion tex;
                if (jar instanceof GargantuarJar) {
                    tex = purpleJarIcon;
                } else if (jar instanceof PlantJar) {
                    tex = greenJarIcon;
                } else {
                    tex = orangeJarIcon;
                }

                batch.draw(tex, jX - 75, jY-25, 120, 150);
            }
        }


        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPlantStats().getAnimation() != null) {
                player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(),
                    stateTime, (float)p.getPosition().getX(), (float)p.getPosition().getY(), true);
            }
        }
        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getZombieStats().getAnimation() != null) {
                player.draw(batch, z.getZombieStats().getAnimation(), z.getCurrentAnimationName(),
                    stateTime, (float)z.getPosition().getX(), (float)z.getPosition().getY(), true);
            }
        }
        batch.end();


        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.6f));
        int totalItems = gamePlay.getInventory().size();
        if (totalItems > 0) {

            shapeRenderer.rect(10, 1050, totalItems * 110 + 20, 140);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


        batch.begin();
        int slotX = 20;
        int index = 0;

        for (BattlePlant invPlant : gamePlay.getInventory()) {

            if (index == selectedCardIndex) {
                batch.setColor(Color.YELLOW);
            }


            if (invPlant.getPlantStats().getAnimation() != null) {
                player.draw(batch, invPlant.getPlantStats().getAnimation(), "idle", 0f, slotX + 45, 1070, true);
            }

            batch.setColor(Color.WHITE);
            slotX += 110;
            index++;
        }
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
        batch.dispose();
        shapeRenderer.dispose();
    }
}

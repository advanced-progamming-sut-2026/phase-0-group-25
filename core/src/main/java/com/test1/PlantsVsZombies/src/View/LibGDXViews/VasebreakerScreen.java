package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
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
                    selectedCardIndex = -1;
                    return true;
                }


                if (wy >= 1040 && wy <= 1190) {
                    int index = (int) ((wx - 20) / 110);
                    if (index >= 0 && index < gamePlay.getInventory().size()) {
                        if (selectedCardIndex == index) {
                            selectedCardIndex = -1;
                        } else {
                            selectedCardIndex = index;
                        }
                    }
                    return true;
                }


                int gridX = (int) Math.round((wx - 566.1) / 152.2) + 1;
                int gridY = (int) Math.round((wy - 205) / 150) + 1;

                if (gridX >= 1 && gridX <= 9 && gridY >= 1 && gridY <= 5) {
                    if (selectedCardIndex != -1) {
                        gamePlay.plantFromInventory(selectedCardIndex, gridX, gridY);
                        selectedCardIndex = -1;
                    } else {
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

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        camera.update();


        for (Mower mower : gamePlay.getMowers()) {
            mower.update(delta, gamePlay);
        }




        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgRegion, 0, 0, 1920, 1200);


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

                batch.draw(tex, jX - 75, jY - 25, 120, 150);
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
                    stateTime, (float) z.getPosition().getX(), (float) z.getPosition().getY(), true);
            }
        }


        for (Mower mower : gamePlay.getMowers()) {
            if (!mower.isDone()) {
                player.draw(batch, mower.getAnimationPath(), mower.getCurrentAnimState(),
                    stateTime, mower.getX(), mower.getY(), true);
            }
        }

        batch.end();




        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        if (selectedCardIndex != -1) {
            int hoverCol = (int) Math.round((mouseWorldPos.x - 566.1) / 152.2) + 1;
            int hoverRow = (int) Math.round((mouseWorldPos.y - 205) / 150) + 1;

            if (hoverCol >= 1 && hoverCol <= 9 && hoverRow >= 1 && hoverRow <= 5) {
                float tileX = (float) (566.1 + (hoverCol - 1) * 152.2) - 75f;
                float tileY = (float) (205 + (hoverRow - 1) * 150) - 25f;

                shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.35f));
                shapeRenderer.rect(tileX, tileY, 145f, 145f);
            }
        }


        int totalItems = gamePlay.getInventory().size();
        if (totalItems > 0) {
            shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.65f));
            shapeRenderer.rect(10, 1040, totalItems * 110 + 20, 145);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);




        batch.begin();
        int slotX = 20;
        int index = 0;

        for (BattlePlant invPlant : gamePlay.getInventory()) {

            if (index == selectedCardIndex) {
                batch.setColor(0.6f, 1f, 0.6f, 1f);
            } else {
                batch.setColor(Color.WHITE);
            }

            if (invPlant.getPlantStats().getAnimation() != null) {
                player.draw(batch, invPlant.getPlantStats().getAnimation(), "idle", stateTime, slotX + 50, 1070, true);
            }

            batch.setColor(Color.WHITE);
            slotX += 110;
            index++;
        }


        if (selectedCardIndex >= 0 && selectedCardIndex < gamePlay.getInventory().size()) {
            BattlePlant previewPlant = gamePlay.getInventory().get(selectedCardIndex);
            if (previewPlant.getPlantStats().getAnimation() != null) {
                player.draw(batch, previewPlant.getPlantStats().getAnimation(), "idle",
                    stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
            }
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
    }
}

package com.test1.PlantsVsZombies.src.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.Simple;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class GamePlayScreen extends ScreenAdapter {
    private Simple gamePlay;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private PamPlayer player;
    private float stateTime = 0;

    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f; // 1 tick = 0.1 seconds

    public GamePlayScreen(Simple gamePlay) {
        this.gamePlay = gamePlay;
    }



    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        shapeRenderer = new ShapeRenderer();

        // 1. Initialize your heavy rendering tools ONCE here
        batch = new SpriteBatch();
        viewport = new ScreenViewport();
        textureBank = new TextureBank("768", Gdx.files.absolute("assets/Assets"));
        player = new PamPlayer(textureBank, Gdx.files.absolute("assets/Assets"));

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                Position gridPos = Position.getRowAndColumn(worldPos.x, worldPos.y);
                int gridX = (int) gridPos.getX();
                int gridY = (int) gridPos.getY();

                if (gridX >= 1 && gridX <= 9 && gridY >= 1 && gridY <= 5) {
                    System.out.println("Clicked on grid: " + gridX + ", " + gridY);
                    gamePlay.collectSun(gridX, gridY);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        ScreenUtils.clear(0.1f, 0.4f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            timeAccumulator += delta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }
        }

        camera.update();

        // --- DRAW SHAPES FIRST ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Tile tile : gamePlay.getTiles()) {
            int gridX = (int) tile.getPosition().getX();
            int gridY = (int) tile.getPosition().getY();
            float px = gamePlay.getRealX(gridX);
            float py = gamePlay.getRealY(gridY);

            if (tile.isArable()) {
                shapeRenderer.setColor((gridX + gridY) % 2 == 0 ? new Color(0.2f, 0.8f, 0.2f, 1) : new Color(0.15f, 0.7f, 0.15f, 1));
            } else {
                shapeRenderer.setColor(Color.GRAY);
            }
            shapeRenderer.rect(px, py, 190, 190);
        }

        shapeRenderer.setColor(Color.RED);
        for (Zombie zombie : gamePlay.getGameZombies()) {
            if (zombie.isAlive()) {
                float px = (float) zombie.getPosition().getX();
                float py = (float) zombie.getPosition().getY();
                shapeRenderer.rect(px + 40, py + 20, 80, 150);
            }
        }

        shapeRenderer.setColor(Color.YELLOW);
        for (Projectile proj : gamePlay.getProjectiles()) {
            if (proj.isActive()) {
                float px = (float) proj.getPosition().getX();
                float py = (float) proj.getPosition().getY();
                shapeRenderer.circle(px + 90, py + 90, 15);
            }
        }

        shapeRenderer.setColor(Color.ORANGE);
        for (Sun sun : gamePlay.getActiveSuns()) {
            float px = (float) sun.getPosition().getX();
            float py = (float) sun.getPosition().getY();
            shapeRenderer.circle(px + 90, py + 90, 30);
        }
        shapeRenderer.end();

        // --- DRAW ANIMATED TEXTURES SECOND ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 2. Loop through gamePlants (actual board plants), NOT plants (seed packets)
        for (BattlePlant p : gamePlay.getGamePlants()) {
            // 3. Null check to prevent crashes if a plant is missing JSON animation data
            if (p.isAlive() && p.getPosition() != null && p.getPlantStats().getAnimation() != null) {
                player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(),
                    stateTime, (float) p.getPosition().getX(), (float) p.getPosition().getY(), true);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // This forces the camera to recalculate its aspect ratio when the window opens
        camera.setToOrtho(false, 1920, 1200);
        camera.update();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}

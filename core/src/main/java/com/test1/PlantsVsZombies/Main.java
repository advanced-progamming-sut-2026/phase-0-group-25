package com.test1.PlantsVsZombies;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.HashMap;
import java.util.Map;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ScreenViewport viewport;

    private TextureBank textureBank;
    private PamPlayer player;

    private float stateTime = 0f;
    private float sunTimer = 0f;

    // Entity existence flags
    private boolean sunflowerAlive = true;
    private boolean sunActive = false;
    private boolean isEating = false;

    // Coordinates
    private final float SUNFLOWER_X = 200f;
    private final float SUNFLOWER_Y = 250f;

    private final float PEASHOOTER_X = 200f;
    private final float PEASHOOTER_Y = 380f;
    private final float PROJECTILE_SPEED = 150f;
    private float PROJECTILE_X = 235f;

    private float zombieX = 700f;
    private final float ZOMBIE_Y = 250f;
    private final float ZOMBIE_SPEED = 100f; // Pixels per second

    private float sunX = 0f;
    private float sunY = 0f;

    private BattlePlant plant;
    private BattlePlant TALLNUGT;
    private Zombie zombie;

    // PAM Asset paths
    private static final String SUNFLOWER_PAM = "768/INITIAL/PLANT/SUNFLOWER/SUNFLOWER.PAM";
    private static final String PEASHOOTER_PAM = "768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM";
    private static final String ZOMBIE_PAM = "768/FULL/ZOMBIE/ZOMBIE_MODERN_NEWSPAPER/ZOMBIE_MODERN_NEWSPAPER.PAM";
    private static final String SUN_PAM = "768/INITIAL/EFFECTS/SUN/SUN.PAM";
    private static final String PROJECTILE_PAW = "768/INITIAL/EFFECTS/SLINGPEA_PROJECTILE/SLINGPEA_PROJECTILE.PAM";
    private static final String ZOMBOSS = "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_ZOMBOSS/ZOMBIE_EGYPT_ZOMBOSS.PAM";

    Map<String, Boolean> visibilities = new HashMap<>();

    @Override
    public void create() {
        GameDataLoader.loadGameData();
        batch = new SpriteBatch();
        viewport = new ScreenViewport();

        plant = PlantFactory.createBattlePlant("ARMA_MINT", 1);
        TALLNUGT = PlantFactory.createBattlePlant("EXPLODE_O_NUT", 1);
        zombie = ZombieFactory.createZombie("BRICK_HEAD");

        textureBank = new TextureBank("768", Gdx.files.absolute("assets/Assets"));
        player = new PamPlayer(textureBank, Gdx.files.absolute("assets/Assets"));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        stateTime += delta;
        textureBank.update();

        // 1. ZOMBIE MOVEMENT LOGIC
        if (!isEating) {
            zombieX -= ZOMBIE_SPEED * delta; // Moves zombie to the left
        }
        PROJECTILE_X += PROJECTILE_SPEED * delta;


        // 2. SUNFLOWER & SUN SPAWN LOGIC
        if (sunflowerAlive) {
            sunTimer += delta;
            if (sunTimer >= 5.0f) {
                sunActive = true;
                sunX = SUNFLOWER_X + 20f;
                sunY = SUNFLOWER_Y + 40f;
                sunflowerAlive = false; // Deletes sunflower after producing sun
            }
        }

        // 3. RENDER SCENE
        ScreenUtils.clear(0.15f, 0.4f, 0.15f, 1f);

        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw Peashooter
        System.out.println(stateTime);

        // Draw Sunflower (only if alive)
        if ((5 <= stateTime) && (stateTime <= 5.5)) {
            player.draw(batch, PEASHOOTER_PAM, "attack", stateTime, SUNFLOWER_X, SUNFLOWER_Y, true);
        } else {
            player.draw(batch, PEASHOOTER_PAM, "idle", stateTime, SUNFLOWER_X, SUNFLOWER_Y, true);
        }


        player.draw(batch, plant.getPlantStats().getAnimation(),
            plant.getCurrentAnimationName(stateTime), stateTime, 400, 700, true);


        System.out.println(TALLNUGT.getPlantStats().getAnimation());
        player.draw(batch, TALLNUGT.getPlantStats().getAnimation(),
            TALLNUGT.getCurrentAnimationName(stateTime), stateTime, 300,
            600, true, TALLNUGT.getVisibilities());


        player.draw(batch, zombie.getZombieStats().getAnimation(),
            zombie.getCurrentAnimationName(), stateTime, 350,
            800, true, zombie.getVisibility());


        // Draw Sun (once active)
        if (sunActive) {
            player.draw(batch, SUN_PAM, "animation", stateTime, sunX + 100, sunY + 100, true);
        }


        // Draw Zombie moving left
        float x = PEASHOOTER_X - zombieX;
        float y = PEASHOOTER_Y - ZOMBIE_Y;
        if (Math.hypot(x, y) <= 200) {
            isEating = true;
        }

        player.draw(batch, PROJECTILE_PAW, "tier1", stateTime, PROJECTILE_X, PEASHOOTER_Y + 45, true);

        player.draw(batch, ZOMBOSS, "idle", stateTime, zombieX + 400, ZOMBIE_Y + 500, true);

        String animation = isEating ? "eat_newspaper" : "walk_newspaper";


        if (stateTime >= 10) {
            visibilities.put("_zombie_newspaper_dmg1", false);
            visibilities.put("_zombie_newspaper_dmg2", false);
        } else if (stateTime >= 5) {
            visibilities.put("_zombie_newspaper", false);
        }

        player.draw(batch, ZOMBIE_PAM, animation, stateTime, zombieX, ZOMBIE_Y,
            true, visibilities);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Brain;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.ProjectileConfig;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.List;

public class IZombieWorldRenderer {
    private static final String BG_ASSET_ID = "IMAGE_BACKGROUNDS_BACKGROUND_LOD_BIGBRAINZ_TEXTURE";
    private static final String BRAIN_ICON_ASSET_ID = "IMAGE_UI_CURRENCY_VALENBRAINZ_STACK_0";
    private static final String BRAIN_ICON_FALLBACK = "IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD";

    private static final float BRAIN_SIZE = 75f;

    private final TextureBank textureBank;
    private final PamPlayer player;
    private final TextureRegion bgRegion;
    private final TextureRegion brainRegion;

    public IZombieWorldRenderer(TextureBank textureBank, PamPlayer player) {
        this.textureBank = textureBank;
        this.player = player;
        this.bgRegion = textureBank.region(BG_ASSET_ID);

        TextureRegion brain = textureBank.region(BRAIN_ICON_ASSET_ID);
        if (brain == null) brain = textureBank.region(BRAIN_ICON_FALLBACK);
        this.brainRegion = brain;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, IZombie gamePlay,
                       float stateTime, List<ActiveReaction> activeReactions) {
        batch.begin();
        if (bgRegion != null) batch.draw(bgRegion, 0, 0, 1920, 1200);


        for (int row = 5; row >= 1; row--) {
            renderPlantsInRow(batch, gamePlay, row, stateTime);
            renderZombiesInRow(batch, gamePlay, row, stateTime);
        }

        renderProjectiles(batch, gamePlay, stateTime);
        batch.end();

        renderBrains(batch, shapeRenderer, gamePlay, stateTime);
    }

    private void renderBrains(SpriteBatch batch, ShapeRenderer shapeRenderer, IZombie gamePlay, float stateTime) {
        Brain[] brains = gamePlay.getBrains();

        if (brainRegion != null) {
            batch.begin();
            for (Brain brain : brains) {
                if (brain.isEaten()) {
                    batch.setColor(0.35f, 0.35f, 0.35f, 0.45f);
                }
                batch.draw(brainRegion, brain.getX() - BRAIN_SIZE / 2f, brain.getY() - BRAIN_SIZE / 2f, BRAIN_SIZE, BRAIN_SIZE);
                batch.setColor(Color.WHITE);
            }
            batch.end();
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Brain brain : brains) {
                shapeRenderer.setColor(brain.isEaten() ? new Color(0.3f, 0.25f, 0.25f, 0.6f) : new Color(0.95f, 0.75f, 0.75f, 1f));
                shapeRenderer.circle(brain.getX(), brain.getY(), BRAIN_SIZE / 2.4f, 24);
            }
            shapeRenderer.end();
        }
    }

    private void renderPlantsInRow(SpriteBatch batch, IZombie gamePlay, int currentRow, float stateTime) {
        ArrayList<BattlePlant> rowPlants = new ArrayList<>();
        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPosition() != null && p.getRow() == currentRow) rowPlants.add(p);
        }
        rowPlants.sort((p1, p2) -> Integer.compare(getPlantLayerPriority(p1), getPlantLayerPriority(p2)));

        for (BattlePlant p : rowPlants) {
            float drawX = (float) p.getPosition().getX();
            float drawY = (float) p.getPosition().getY();
            if (p.isEffected()) {
                player.draw(batch, "768/INITIAL/EFFECTS/PLANTFOOD_FX/PLANTFOOD_FX.PAM", "plantfood", stateTime, drawX, drawY + 95, true);
            }
            player.draw(batch, p.getAnimationPath(), p.getCurrentAnimationName(), stateTime, drawX, drawY, true, p.getVisibilities());
        }
    }

    private void renderZombiesInRow(SpriteBatch batch, IZombie gamePlay, int currentRow, float stateTime) {
        ArrayList<Zombie> rowZombies = new ArrayList<>();
        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getRow() == currentRow) rowZombies.add(z);
        }
        rowZombies.sort((z1, z2) -> Double.compare(z2.getPosition().getX(), z1.getPosition().getX()));

        for (Zombie z : rowZombies) {
            if (z.getZombieStats().getAnimation() == null) continue;
            float drawX = (float) z.getPosition().getX();
            float drawY = (float) z.getPosition().getY();
            batch.setColor(z.getColor());
            player.draw(batch, z.getAnimationPath(), z.getCurrentAnimationName(), stateTime, drawX, drawY + 15, true, z.getVisibility());
            batch.setColor(Color.WHITE);
        }
    }

    private int getPlantLayerPriority(BattlePlant plant) {
        if (plant == null || plant.getName() == null) return 1;
        String name = plant.getName().toUpperCase();
        if (name.contains("LILY_PAD")) return 0;
        if (name.contains("PUMPKIN") || name.contains("HOT_POTATO")) return 2;
        return 1;
    }

    private void renderProjectiles(SpriteBatch batch, IZombie gamePlay, float stateTime) {
        for (Projectile projectile : gamePlay.getProjectiles()) {
            if (!projectile.isActive()) continue;

            String name = projectile.getName();
            ProjectileConfig config = ProjectileConfig.fromName(name);
            if ("pea".equals(name)) {
                if (projectile.isIcy()) config = ProjectileConfig.ICY_PEA;
                else if (projectile.isFiring()) config = ProjectileConfig.FIRING_PEA;
            }
            if (config == null) continue;

            player.draw(batch, config.getAnimation(), config.getClip(), stateTime,
                (float) projectile.getPosition().getX(), (float) projectile.getPosition().getY(), true);
        }
    }
}

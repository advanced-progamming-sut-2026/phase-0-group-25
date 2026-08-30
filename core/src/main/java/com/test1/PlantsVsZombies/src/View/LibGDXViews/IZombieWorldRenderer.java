package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Brain;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.ProjectileConfig;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;

public class IZombieWorldRenderer {
    private final TextureRegion bgRegion;
    private final TextureRegion brainTexture;
    private final PamPlayer player;

    public IZombieWorldRenderer(TextureBank textureBank, PamPlayer player) {
        this.player = player;
        this.bgRegion = textureBank.region("IMAGE_BACKGROUNDS_BACKGROUND_LOD_BIGBRAINZ_TEXTURE");
        TextureRegion brain = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        this.brainTexture = (brain != null) ? brain : textureBank.region("IMAGE_UI_HUD_INGAME_COIN_BUY");
    }

    public void render(SpriteBatch batch, IZombie gamePlay, float stateTime) {
        if (bgRegion != null) {
            batch.draw(bgRegion, 0, 0, 1920, 1200);
        }


        for (Brain brain : gamePlay.getBrains()) {
            if (!brain.isEaten() && brainTexture != null) {
                float pulse = 0.85f + 0.15f * (float) Math.sin(stateTime * 4f);
                batch.setColor(1f, 1f, 1f, pulse);
                batch.draw(brainTexture, brain.getX() - 30f, brain.getY() - 30f, 65f, 65f);
                batch.setColor(Color.WHITE);
            }
        }


        for (int row = 5; row >= 1; row--) {
            final int curRow = row;


            for (BattlePlant p : gamePlay.getGamePlants()) {
                if (p.isAlive() && p.getRow() == curRow && p.getPlantStats().getAnimation() != null) {
                    player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(),
                        stateTime, (float) p.getPosition().getX(), (float) p.getPosition().getY(), true);
                }
            }


            ArrayList<Zombie> rowZombies = new ArrayList<>();
            for (Zombie z : gamePlay.getGameZombies()) {
                if (z.isAlive() && z.getRow() == curRow) rowZombies.add(z);
            }
            rowZombies.sort((z1, z2) -> Double.compare(z2.getPosition().getX(), z1.getPosition().getX()));

            for (Zombie z : rowZombies) {
                if (z.getZombieStats().getAnimation() != null) {
                    player.draw(batch, z.getZombieStats().getAnimation(), z.getCurrentAnimationName(),
                        stateTime, (float) z.getPosition().getX(), (float) z.getPosition().getY(), true, z.getVisibility());
                }
            }
        }


        for (Projectile projectile : gamePlay.getProjectiles()) {
            ProjectileConfig config = ProjectileConfig.fromName(projectile.getName());
            if (config != null) {
                player.draw(batch, config.getAnimation(), config.getClip(), stateTime,
                    (float) projectile.getPosition().getX(), (float) projectile.getPosition().getY(), true);
            }
        }
    }
}

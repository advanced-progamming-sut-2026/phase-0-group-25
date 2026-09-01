package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.DroppedPlantFood;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.SaveOurSeeds;
import com.test1.PlantsVsZombies.src.Model.IcyWindEffect;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.ProjectileConfig;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.SandstormEffect;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.Iterator;

public class GamePlayWorldRenderer {
    private final TextureBank textureBank;
    private final PamPlayer player;
    private final TextureRegion region;


    private final TextureRegion sosTileRegion;
    private final TextureRegion iceSliderRegion;
    private final TextureRegion iceBlockTexture;
    private final TextureRegion lowTideRuneRegion;
    private final TextureRegion necromancyRuneRegion;
    private final TextureRegion getPlantFoodIconInGame;
    private final TextureRegion[] egyptGraveRegions = new TextureRegion[5];
    private final TextureRegion[] iceStageRegions = new TextureRegion[3];
    private final TextureRegion[] darkNormalGraveRegions = new TextureRegion[5];
    private final TextureRegion[] darkPlantFoodGraveRegions = new TextureRegion[5];
    private final TextureRegion[] darkSunGraveRegions = new TextureRegion[5];

    private static final String JALAPENO_FIRE_ANIM_PATH = "768/INITIAL/EFFECTS/JALAPENO_FIRE/JALAPENO_FIRE.PAM";
    private static final String BEACH_WATER_ANIM_PATH = "768/FULL/BACKGROUNDS/WAVE_UPPERLAYER/WAVE_UPPERLAYER.PAM";
    private static final String BEACH_TIDELINE_ANIM_PATH = "768/FULL/BACKGROUNDS/WATER_TIDE_LINE/WATER_TIDE_LINE.PAM";
    private static final String ICY_WIND_ANIM_PATH = "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM";
    private static final String SANDSTORM_ANIM_PATH = "768/INITIAL/EFFECTS/SANDSTORM_TOP/SANDSTORM_TOP.PAM";
    private static final String PLANT_FOOD_GLOW_ANIM_PATH = "768/INITIAL/EFFECTS/PLANTFOOD_FX/PLANTFOOD_FX.PAM";

    private static final float GRAVE_MAX_HP = 700f;
    private static final float TIDELINE_X = 1595f, TIDELINE_Y = 505f;
    private static final float WATER_BASE_X = 2220f, WATER_BASE_Y = 505f, WATER_MOVE_RANGE = 76f;

    public GamePlayWorldRenderer(TextureBank textureBank, PamPlayer player, ChapterType chapterType) {
        this.textureBank = textureBank;
        this.player = player;
        this.region = textureBank.region(getBgPath(chapterType));

        sosTileRegion = textureBank.region("IMAGE_BACKGROUNDS_PROTECT_TILE_PROTECT_TILE_112X125");
        iceSliderRegion = textureBank.region("IMAGE_EFFECTS_TILESLIDER_ICEAGE_UP_TILESLIDER_ICEAGE_UP_116X140");
        iceBlockTexture = textureBank.region("IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_ZOMBIE_FROSTBITE_ICE_BLOCK_ZOMBIE_153X243");
        lowTideRuneRegion = textureBank.region("IMAGE_PLANT_WATERRABBIT_WATERRABBIT_82X82");
        necromancyRuneRegion = textureBank.region("IMAGE_EFFECTS_GRIMROSE_UNDERZOMBIE_EFFECT_GRIMROSE_UNDERZOMBIE_EFFECT_167X52");
        getPlantFoodIconInGame = textureBank.region("IMAGE_EFFECTS_PLANTFOOD_PICKUP_PLANTFOOD_PICKUP_79X79");

        String[] egyptIds = {
            "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_118X148", "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_118X148_2",
            "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_113X145", "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_110X145",
            "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_109X119"
        };
        for (int i = 0; i < egyptIds.length; i++) egyptGraveRegions[i] = textureBank.region(egyptIds[i]);

        String[] iceIds = {
            "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X62",
            "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X79",
            "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_164X169"
        };
        for (int i = 0; i < iceIds.length; i++) iceStageRegions[i] = textureBank.region(iceIds[i]);

        String[] darkNormal = {"IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160_2", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X156", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_125X149", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_93X89"};
        String[] darkPf = {"IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X160", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X160_2", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X157", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_129X144", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_93X95"};
        String[] darkSun = {"IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X160", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X160_2", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X157", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X144", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_93X91"};
        for (int i = 0; i < 5; i++) {
            darkNormalGraveRegions[i] = textureBank.region(darkNormal[i]);
            darkPlantFoodGraveRegions[i] = textureBank.region(darkPf[i]);
            darkSunGraveRegions[i] = textureBank.region(darkSun[i]);
        }
    }

    public void render(SpriteBatch batch, GamePlay gamePlay, float stateTime, float delta) {
        if (region != null) batch.draw(region, 0, 0, 1920, 1200);


        if (gamePlay instanceof SaveOurSeeds) {
            int[][] protectedCoords = {{5, 2}, {5, 4}};
            float pulse = 0.75f + 0.25f * (float) Math.sin(stateTime * 5f);
            for (int[] coord : protectedCoords) {
                float realX = gamePlay.getRealX(coord[0]);
                float realY = gamePlay.getRealY(coord[1]);
                if (sosTileRegion != null) {
                    batch.setColor(1f, 0.9f, 0.2f, pulse);
                    batch.draw(sosTileRegion, realX - 79.5f, realY - 60f, 145f, 140f);
                    batch.setColor(Color.WHITE);
                }
            }
        }


        if (gamePlay.getChapterType() == ChapterType.BIG_WAVE_BEACH) {
            float waterOffset = (float) Math.sin(stateTime * 0.8f) * WATER_MOVE_RANGE;
            player.draw(batch, BEACH_WATER_ANIM_PATH, "water", stateTime, WATER_BASE_X + waterOffset, WATER_BASE_Y - 225, true);
            player.draw(batch, BEACH_TIDELINE_ANIM_PATH, "idle", stateTime, TIDELINE_X, TIDELINE_Y, true);
        }


        for (int row = 5; row >= 1; row--) {
            final int currentRow = row;
            renderChapterTiles(batch, gamePlay, currentRow, stateTime);
            renderPlantsInRow(batch, gamePlay, currentRow, stateTime, delta);
            renderZombiesInRow(batch, gamePlay, currentRow, stateTime, delta);
        }


        for (DroppedPlantFood pf : gamePlay.getActivePlantFoods()) {
            float floatOffset = (float) Math.sin(stateTime * 5f) * 7f;
            batch.draw(getPlantFoodIconInGame, (float) pf.getPosition().getX(), (float) pf.getPosition().getY() + floatOffset, 65, 65);
        }


        for (Sun sun : gamePlay.getActiveSuns()) {
            if (!sun.isCollected()) {
                float x = (float) sun.getPosition().getX() + 40;
                float y = (float) sun.getPosition().getY() + 40;
                if (sun.getNumberOfSun() >= 100) {
                    batch.setTransformMatrix(batch.getTransformMatrix().idt().translate(x, y, 0).scale(1.35f, 1.35f, 1).translate(-x, -y, 0));
                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);
                    batch.setTransformMatrix(batch.getTransformMatrix().idt());
                } else {
                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);
                }
            }
        }


        for (Projectile projectile : gamePlay.getProjectiles()) {
            double offsetX = 0;
            double offsetY = 0;
            if (projectile instanceof LobbedProjectile) {
                offsetX = 0;
                offsetY = 50;
            } else {
                offsetX = 0;
                offsetY = projectile.getOffset().getY();
            }
            float px = (float) (projectile.getPosition().getX() + offsetX);
            float py = (float) (projectile.getPosition().getY() + offsetY);
            String name = projectile.getName();
            ProjectileConfig projectileConfig = ProjectileConfig.fromName(name);
            if (name.equals("pea")) {
                if (projectile.isIcy()) {
                    projectileConfig = ProjectileConfig.ICY_PEA;
                } else if (projectile.isFiring()) {
                    projectileConfig = ProjectileConfig.FIRING_PEA;
                } else if (projectile.isBlueFiring()) {
                    projectileConfig = ProjectileConfig.BLUE_FIRING_PEA;
                }
            }
            player.draw(batch, projectileConfig.getAnimation(), projectileConfig.getClip(),
                stateTime, px, py, true);
        }


        for (Mower mower : gamePlay.getMowers()) {
            if (!mower.isDone()) {
                player.draw(batch, mower.getAnimationPath(), mower.getCurrentAnimState(), stateTime, mower.getX(), mower.getY(), true);
            }
        }


        Iterator<SandstormEffect> it = gamePlay.getActiveSandstorms().iterator();
        while (it.hasNext()) {
            SandstormEffect storm = it.next();
            if (!gamePlay.isPaused()) storm.update(delta);
            if (storm.isFinished()) it.remove();
            else
                player.draw(batch, SANDSTORM_ANIM_PATH, "loop", storm.getAnimTime(), storm.getX(), storm.getY() + 40, true);
        }


        if (gamePlay.getChapterType() == ChapterType.FROSTBITE_CAVES) {
            Iterator<IcyWindEffect> windIt = gamePlay.getActiveIcyWinds().iterator();
            while (windIt.hasNext()) {
                IcyWindEffect wind = windIt.next();
                if (!gamePlay.isPaused()) wind.update(delta);
                if (wind.isFinished()) windIt.remove();
                else
                    player.draw(batch, ICY_WIND_ANIM_PATH, "animation", wind.getAnimTime(), 960f, gamePlay.getRealY(wind.getRow()) + 25, true);
            }
        }
    }

    private void renderChapterTiles(SpriteBatch batch, GamePlay gamePlay, int currentRow, float stateTime) {
        ChapterType chapter = gamePlay.getChapterType();
        for (Tile tile : gamePlay.getTiles()) {
            if ((int) tile.getPosition().getY() != currentRow) continue;
            float realX = gamePlay.getRealX((int) tile.getPosition().getX());
            float realY = gamePlay.getRealY(currentRow);

            if (chapter == ChapterType.ANCIENT_EGYPT && !tile.isArable() && tile.getHP() > 0) {
                int stage = 4 - (int) Math.min(4, Math.floor((tile.getHP() / GRAVE_MAX_HP) * 5));
                TextureRegion grave = egyptGraveRegions[stage];
                if (grave != null)
                    batch.draw(grave, realX - (grave.getRegionWidth() * 0.7f) - 7f, realY - 30f, grave.getRegionWidth() * 1.4f, grave.getRegionHeight() * 1.4f);
            } else if (chapter == ChapterType.FROSTBITE_CAVES && !tile.isArable()) {
                if (tile.getHP() == 0 && iceSliderRegion != null)
                    batch.draw(iceSliderRegion, realX - 72.5f, realY - 47f, 145f, 140f);
                else if (tile.getHP() > 0 && iceBlockTexture != null)
                    batch.draw(iceBlockTexture, realX - 72.5f, realY - 25f, 130f, 155f);
            } else if (chapter == ChapterType.DARK_AGE && !tile.isArable() && tile.getHP() > 0) {
                if (tile.isNecromancy() && !tile.isNecromancyTriggered() && necromancyRuneRegion != null) {
                    batch.draw(necromancyRuneRegion, realX - 60f, realY - 50f, 120f, 60f);
                }
                int stage = 4 - (int) Math.min(4, Math.floor((tile.getHP() / GRAVE_MAX_HP) * 4.99f));
                TextureRegion grave = switch (tile.getGraveType()) {
                    case PLANT_FOOD -> darkPlantFoodGraveRegions[stage];
                    case SUN -> darkSunGraveRegions[stage];
                    default -> darkNormalGraveRegions[stage];
                };
                if (grave != null)
                    batch.draw(grave, realX - (grave.getRegionWidth() * 0.7f) - 7f, realY - 30f, grave.getRegionWidth() * 1.4f, grave.getRegionHeight() * 1.4f);
            } else if (chapter == ChapterType.BIG_WAVE_BEACH && !tile.isArable() && tile.isLowTide() && !tile.isLowTideTriggered() && lowTideRuneRegion != null) {
                float pulse = 0.6f + 0.4f * (float) Math.sin(stateTime * 4f);
                batch.setColor(0.1f, 0.7f, 1f, pulse);
                batch.draw(lowTideRuneRegion, realX - 60f, realY - 45f, 120f, 60f);
                batch.setColor(Color.WHITE);
            }

            if (tile.isFiring()) {
                player.draw(batch, JALAPENO_FIRE_ANIM_PATH, "idle", stateTime, realX, realY, true);
            }
        }
    }

    private void renderPlantsInRow(SpriteBatch batch, GamePlay gamePlay, int currentRow, float stateTime, float delta) {
        ArrayList<BattlePlant> rowPlants = new ArrayList<>();
        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPosition() != null && p.getRow() == currentRow) rowPlants.add(p);
        }
        rowPlants.sort((p1, p2) -> Integer.compare(getPlantLayerPriority(p1), getPlantLayerPriority(p2)));

        for (BattlePlant p : rowPlants) {
            float drawX = (float) p.getPosition().getX();
            float drawY = (float) p.getPosition().getY();

            int iceStage = (p.isFrozen() || p.getIceTime() >= 3) ? 3 : p.getIceTime();
            if (iceStage > 0) batch.setColor(0.65f, 0.85f, 1.0f, 1.0f);
            else batch.setColor(Color.WHITE);


            if (p.isEffected()) {
                player.draw(batch, PLANT_FOOD_GLOW_ANIM_PATH, "plantfood", stateTime, drawX, drawY + 95, true);
            }


            p.getAnimationState().update(p.getCurrentAnimationName(), delta);

            player.draw(batch, p.getAnimationPath(), p.getCurrentAnimationName(),
                p.getAnimationState().getStateTime(), drawX, drawY, true, p.getVisibilities());

            if (p.isOctopusated()) {
                player.draw(batch, "768/FULL/EFFECTS/ZOMBIE_OCTOPUS_PROJECTILE/ZOMBIE_OCTOPUS_PROJECTILE.PAM",
                    "animation4", stateTime, drawX, drawY, true);
            }

            batch.setColor(Color.WHITE);

            if (iceStage > 0) {
                TextureRegion iceTex = iceStageRegions[iceStage - 1];
                if (iceTex != null) {
                    float iceW = (iceStage == 3) ? 140f : 120f;
                    float iceH = (iceStage == 3) ? 160f : 135f;
                    batch.draw(iceTex, drawX - (iceW / 2f) - 2f, drawY - 50f, iceW, iceH);
                }
            }
        }
    }

    private void renderZombiesInRow(SpriteBatch batch, GamePlay gamePlay, int currentRow, float stateTime, float delta) {
        ArrayList<Zombie> rowZombies = new ArrayList<>();
        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getRow() == currentRow) rowZombies.add(z);
        }
        rowZombies.sort((z1, z2) -> Double.compare(z2.getPosition().getX(), z1.getPosition().getX()));

        for (Zombie z : rowZombies) {
            if (z.getZombieStats().getAnimation() != null) {
                float drawX = (float) z.getPosition().getX();
                float drawY = (float) z.getPosition().getY();

                if (z.isHypnotized()) {
                    float pulse = 0.75f + 0.25f * (float) Math.sin(stateTime * 7f);
                    batch.setColor(0.35f * pulse, 1.0f, 0.45f * pulse, 1.0f);
                } else {
                    batch.setColor(z.getColor());
                }

                z.getAnimationState().update(z.getCurrentAnimationName(), delta);

                player.draw(batch, z.getAnimationPath(), z.getCurrentAnimationName(),
                    z.getAnimationState().getStateTime(), drawX, drawY + 15, true, z.getVisibility());
                batch.setColor(Color.WHITE);
            }
        }
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

    private int getPlantLayerPriority(BattlePlant plant) {
        if (plant == null || plant.getName() == null) return 1;
        String name = plant.getName().toUpperCase();
        if (name.contains("LILY_PAD")) return 0;
        if (name.contains("PUMPKIN") || name.contains("HOT_POTATO")) return 2;
        return 1;
    }
}

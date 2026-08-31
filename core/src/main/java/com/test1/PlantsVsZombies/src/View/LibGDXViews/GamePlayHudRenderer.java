package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;

public class GamePlayHudRenderer {
    private final TextureBank textureBank;
    private final PamPlayer player;
    private final BitmapFont hudFont;

    private final TextureRegion sunIcon;
    private final TextureRegion plantFoodIcon;
    private final TextureRegion bgHud;
    private final TextureRegion flagIcon;
    private final TextureRegion zombieHeadIcon;
    private final TextureRegion progressBarFrame;
    private final TextureRegion cardBgRegion;
    private final TextureRegion cardBoostedBgRegion;
    private final TextureRegion plusIcon;
    private final TextureRegion pfBankSlotRegion;
    private final TextureRegion pfLockedSlotRegion;
    private final TextureRegion shovelIcon;
    private final TextureRegion shovelIconInGame;
    private final TextureRegion getPlantFoodIconInGame;
    private final TextureRegion pauseBtnRegion;
    private final TextureRegion conveyorTrackRegion;

    private static final float CARD_X = 45f, CARD_START_Y = 980f, CARD_WIDTH = 160f, CARD_HEIGHT = 105f, CARD_SPACING = 11f;
    private static final float SUN_PLUS_X = 190f, SUN_PLUS_Y = 1120f, PF_PLUS_X = 269f, PF_PLUS_Y = 1120f, PLUS_BTN_SIZE = 40f;
    private static final float PF_BTN_X = 1675f, PF_BTN_Y = 30f, PF_BTN_SIZE = 100f;
    private static final float SHOVEL_BTN_X = 1770f, SHOVEL_BTN_Y = 30f, SHOVEL_BTN_SIZE = 100f;
    private static final float PAUSE_BTN_X = 1810f, PAUSE_BTN_Y = 1105f, PAUSE_BTN_SIZE = 75f;
    private static final float START_WAVE_BTN_X = 1450f, START_WAVE_BTN_Y = 1100f, START_WAVE_BTN_W = 220f, START_WAVE_BTN_H = 75f;
    private static final float DEADLINE_X = 943f;
    private static final float BELT_SEGMENT_HEIGHT = 20f, BELT_WIDTH = 175f, BELT_SCROLL_SPEED = 60f;

    public GamePlayHudRenderer(TextureBank textureBank, PamPlayer player, BitmapFont hudFont) {
        this.textureBank = textureBank;
        this.player = player;
        this.hudFont = hudFont;

        sunIcon = textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
        plantFoodIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON");
        bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        flagIcon = textureBank.region("IMAGE_ZOMBIE_ZOMBIE_FEASTIVUS_FLAG_ZOMBIE_FEASTIVUS_FLAG_123X95");
        zombieHeadIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        progressBarFrame = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        cardBgRegion = textureBank.region("IMAGE_UI_PACKETS_SELECTED");
        cardBoostedBgRegion = textureBank.region("IMAGE_UI_PACKETS_BOOST");
        plusIcon = textureBank.region("IMAGE_UI_HUD_INGAME_COIN_BUY");
        pfBankSlotRegion = textureBank.region("IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK_FILLED_SLOT");
        pfLockedSlotRegion = textureBank.region("IMAGE_ZEN_GARDEN_LOCKED_POT_ICON");
        shovelIcon = textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON");
        shovelIconInGame = textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_ICON");
        getPlantFoodIconInGame = textureBank.region("IMAGE_EFFECTS_PLANTFOOD_PICKUP_PLANTFOOD_PICKUP_79X79");
        pauseBtnRegion = textureBank.region(GamePlayScreen.PAUSE_BTN_ASSET_ID);
        conveyorTrackRegion = textureBank.region("IMAGE_UI_CONVEYOR_CONVEYOR_BELT");
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, GamePlay gamePlay,
                       GamePlayInputHandler input, float stateTime) {
        Vector3 mouse = input.getMouseWorldPos();
        BattlePlant selectedPlant = input.getSelectedPlant();

        batch.begin();


        if (!(gamePlay instanceof ConveyorBelt)) {
            batch.draw(bgHud, 20, 1100, 215, 80);
            batch.draw(sunIcon, 30, 1110, 60, 60);
            hudFont.draw(batch, String.valueOf(gamePlay.getMySuns()), 95, 1160);
        }
        TextureRegion foodBank = textureBank.region("IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK");
        batch.draw((foodBank != null) ? foodBank : bgHud, 240, 1100, 230, 80);
        if (pauseBtnRegion != null) batch.draw(pauseBtnRegion, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE);


        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser != null && currentUser.isDebugMode() && plusIcon != null) {
            if (!(gamePlay instanceof ConveyorBelt)) batch.draw(plusIcon, SUN_PLUS_X, SUN_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE);
            batch.draw(plusIcon, PF_PLUS_X, PF_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE);
        }


        for (int i = 0; i < 5; i++) {
            float sx = 325f + (i * 27f);
            if (i < 3) {
                if (i < gamePlay.getNumOfPlantFood() && pfBankSlotRegion != null) {
                    batch.draw(pfBankSlotRegion, sx, 1127f, 25f, 25f);
                } else if (pfBankSlotRegion != null) {
                    batch.setColor(0.3f, 0.3f, 0.3f, 0.45f);
                    batch.draw(pfBankSlotRegion, sx, 1127f, 25f, 25f);
                    batch.setColor(Color.WHITE);
                }
            } else if (pfLockedSlotRegion != null) {
                batch.setColor(0.7f, 0.7f, 0.7f, 0.75f);
                batch.draw(pfLockedSlotRegion, sx + 3f, 1130f, 19f, 19f);
                batch.setColor(Color.WHITE);
            }
        }


        if (plantFoodIcon != null) {
            if (input.isPlantFoodSelected()) batch.setColor(0.6f, 1f, 0.6f, 1f);
            else if (gamePlay.getNumOfPlantFood() == 0) batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            batch.draw(plantFoodIcon, PF_BTN_X, PF_BTN_Y, PF_BTN_SIZE, PF_BTN_SIZE);
            batch.setColor(Color.WHITE);
        }


        if (!(gamePlay instanceof ConveyorBelt)) {
            ArrayList<BattlePlant> deck = gamePlay.getPlants();
            for (int i = 0; i < deck.size(); i++) {
                BattlePlant p = deck.get(i);
                PlantType pType = PlantType.fromName(p.getName());
                if (pType == null) continue;

                TextureRegion plantIcon = textureBank.region(pType.getIconAssetId());
                float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
                boolean canAfford = gamePlay.getMySuns() >= p.getPlantStats().getCost();
                boolean isReady = p.getCurrentCoolDown() <= 0 || !p.getActiveCooldown();

                if (!canAfford || !isReady) batch.setColor(0.4f, 0.4f, 0.4f, 0.85f);
                else if (selectedPlant == p) batch.setColor(0.6f, 1f, 0.6f, 1f);

                TextureRegion curBg = (gamePlay.isPlantBoosted(p.getName()) && cardBoostedBgRegion != null) ? cardBoostedBgRegion : cardBgRegion;
                if (curBg != null) batch.draw(curBg, CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT);

                if (plantIcon != null) {
                    float availW = CARD_WIDTH - 20f, availH = CARD_HEIGHT - 35f;
                    float scale = Math.min(availW / plantIcon.getRegionWidth(), availH / plantIcon.getRegionHeight());
                    float finalW = plantIcon.getRegionWidth() * scale, finalH = plantIcon.getRegionHeight() * scale;
                    batch.draw(plantIcon, CARD_X + (CARD_WIDTH - finalW) / 2f, cardY + 22f + (availH - finalH) / 2f, finalW, finalH);
                }
                batch.setColor(Color.WHITE);
                hudFont.getData().setScale(0.40f);
                hudFont.draw(batch, String.valueOf(p.getPlantStats().getCost()), CARD_X + CARD_WIDTH - 42, cardY + 22);
                hudFont.getData().setScale(1f);
            }
        }


        if (gamePlay instanceof ConveyorBelt && conveyorTrackRegion != null) {
            float scrollOffset = (stateTime * BELT_SCROLL_SPEED) % BELT_SEGMENT_HEIGHT;
            for (float y = 120f - BELT_SEGMENT_HEIGHT; y <= 1110f + BELT_SEGMENT_HEIGHT; y += BELT_SEGMENT_HEIGHT) {
                float drawY = y + scrollOffset;
                if (drawY >= 120f && drawY <= 1110f) batch.draw(conveyorTrackRegion, CARD_X - 7f, drawY, BELT_WIDTH, BELT_SEGMENT_HEIGHT);
            }
            for (ConveyorCard card : ((ConveyorBelt) gamePlay).getConveyorCards()) {
                BattlePlant p = card.getPlant();
                PlantType pType = PlantType.fromName(p.getName());
                if (pType == null) continue;
                TextureRegion plantIcon = textureBank.region(pType.getIconAssetId());
                if (selectedPlant == p) batch.setColor(0.6f, 1f, 0.6f, 1f);
                if (cardBgRegion != null) batch.draw(cardBgRegion, CARD_X, card.getCurrentY(), CARD_WIDTH, CARD_HEIGHT);
                if (plantIcon != null) {
                    float scale = Math.min((CARD_WIDTH - 20f) / plantIcon.getRegionWidth(), (CARD_HEIGHT - 35f) / plantIcon.getRegionHeight());
                    batch.draw(plantIcon, CARD_X + (CARD_WIDTH - (plantIcon.getRegionWidth() * scale)) / 2f, card.getCurrentY() + 22f + ((CARD_HEIGHT - 35f) - (plantIcon.getRegionHeight() * scale)) / 2f, plantIcon.getRegionWidth() * scale, plantIcon.getRegionHeight() * scale);
                }
                batch.setColor(Color.WHITE);
            }
        }

        if (shovelIcon != null) {
            if (input.isShovelSelected()) batch.setColor(0.6f, 1f, 0.6f, 1f);
            batch.draw(shovelIcon, SHOVEL_BTN_X, SHOVEL_BTN_Y, SHOVEL_BTN_SIZE, SHOVEL_BTN_SIZE);
            batch.setColor(Color.WHITE);
        }

        renderMiniGamePanels(batch, gamePlay, stateTime);

        batch.end();


        renderShapeElements(shapeRenderer, gamePlay, input, stateTime);


        batch.begin();
        float barWidth = 450f, barLeftX = (1920f - barWidth) / 2f, barRightX = barLeftX + barWidth, barY = 1130f;
        float headX = barRightX - (barWidth * gamePlay.getProgressPercentage());
        batch.draw(progressBarFrame, barLeftX, barY, barWidth, 45f);

        int totalWaves = gamePlay.calculateWaves(gamePlay.getChapterType(), gamePlay.getLevel());
        for (int i = 0; i < totalWaves; i++) {
            float flagX = barRightX - (barWidth * ((float) i / totalWaves));
            if (i == totalWaves - 1) batch.draw(flagIcon, flagX - 15, barY + 5, 45, 55);
            else batch.draw(flagIcon, flagX - 10, barY + 10, 30, 40);
        }
        batch.draw(zombieHeadIcon, headX - 25, barY - 5, 50, 50);


        if (selectedPlant != null && selectedPlant.getPlantStats().getAnimation() != null) {
            String strOfIdle = PlantType.fromName(selectedPlant.getName()).getStateName();
            player.draw(batch, selectedPlant.getPlantStats().getAnimation(), strOfIdle, stateTime, mouse.x, mouse.y, true);
        }
        if (input.isShovelSelected() && shovelIconInGame != null) batch.draw(shovelIconInGame, mouse.x - 40, mouse.y - 10, 80, 80);
        if (input.isPlantFoodSelected() && getPlantFoodIconInGame != null) batch.draw(getPlantFoodIconInGame, mouse.x - 30, mouse.y - 30, 60, 60);

        batch.end();
    }

    private void renderShapeElements(ShapeRenderer shapeRenderer, GamePlay gamePlay, GamePlayInputHandler input, float stateTime) {
        Vector3 mouse = input.getMouseWorldPos();
        float barWidth = 450f, barLeftX = (1920f - barWidth) / 2f, barRightX = barLeftX + barWidth, barY = 1130f;
        float greenWidth = barRightX - (barRightX - (barWidth * gamePlay.getProgressPercentage()));

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        if (input.getSelectedPlant() != null || input.isShovelSelected() || input.isPlantFoodSelected()) {
            int hoverCol = (int) Math.floor((mouse.x - 490) / 152.2) + 1;
            int hoverRow = (int) Math.floor((mouse.y - 130) / 150) + 1;
            if (hoverCol >= 1 && hoverCol <= 9 && hoverRow >= 1 && hoverRow <= 5) {
                shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.35f));
                shapeRenderer.rect(490f + (hoverCol - 1) * 152.2f - 5, 130f + (hoverRow - 1) * 150f + 5, 145f, 140f);
            }
        }


        boolean isSetupPhase = (gamePlay instanceof PlantWhatYouGet && !((PlantWhatYouGet) gamePlay).isWaveStarted());
        if (!isSetupPhase && !(gamePlay instanceof ConveyorBelt)) {
            ArrayList<BattlePlant> deck = gamePlay.getPlants();
            for (int i = 0; i < deck.size(); i++) {
                BattlePlant p = deck.get(i);
                if (p.getCurrentCoolDown() > 0 && p.getPlantStats().getRechargeTime() > 0) {
                    float ratio = (float) Math.min(1.0, p.getCurrentCoolDown() / p.getPlantStats().getRechargeTime());
                    float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
                    shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.65f));
                    shapeRenderer.rect(CARD_X, cardY + (CARD_HEIGHT - (CARD_HEIGHT * ratio)), CARD_WIDTH, CARD_HEIGHT * ratio);
                }
            }
        }


        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(barLeftX + 15, barY + 10, barWidth - 30, 25f);
        if (greenWidth > 0) {
            shapeRenderer.setColor(new Color(0.2f, 0.9f, 0.2f, 1f));
            shapeRenderer.rect(Math.max(barRightX - greenWidth, barLeftX + 15), barY + 10, greenWidth - 15, 25f);
        }
        shapeRenderer.end();


        User user = gamePlay.getThisUser();
        boolean showGrid = (user != null && user.getUserProgress() != null && user.getUserProgress().isShowTileGrid());
        boolean isDeadLineMode = (gamePlay instanceof DeadLine);

        if (showGrid || isDeadLineMode) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if (showGrid) {
                shapeRenderer.setColor(new Color(1f, 0f, 0f, 0.85f));
                Gdx.gl.glLineWidth(2);
                for (int r = 1; r <= 5; r++) {
                    for (int c = 1; c <= 9; c++) {
                        shapeRenderer.rect(490f + (c - 1) * 152.2f - 5, 130f + (r - 1) * 150f + 5, 145f, 140f);
                    }
                }
            }
            if (isDeadLineMode) {
                shapeRenderer.setColor(new Color(1f, 0.1f, 0.1f, 0.7f + 0.3f * (float) Math.sin(stateTime * 6f)));
                Gdx.gl.glLineWidth(6);
                shapeRenderer.line(DEADLINE_X, 130f, DEADLINE_X, 130f + 750f);
            }
            shapeRenderer.end();
        }
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }

    private void renderMiniGamePanels(SpriteBatch batch, GamePlay gamePlay, float stateTime) {
        if (gamePlay instanceof TimedWar tw) {
            float timeLeft = Math.max(0f, (600 - tw.getTotalTicksPassed()) * 0.1f);
            batch.draw(bgHud, 1450f, 1100f, 280, 80);
            hudFont.getData().setScale(0.48f);
            if (tw.getNumOfDeadZombies() >= 7) {
                hudFont.setColor(Color.GREEN);
                hudFont.draw(batch, "GOAL ACHIEVED! (" + tw.getNumOfDeadZombies() + "/7)", 1470f, 1150f);
            } else {
                hudFont.setColor((timeLeft <= 10f) ? Color.RED : Color.YELLOW);
                hudFont.draw(batch, "Kills: " + tw.getNumOfDeadZombies() + " / 7", 1475f, 1160f);
                hudFont.draw(batch, String.format("Time: %.1fs", timeLeft), 1475f, 1130f);
            }
            hudFont.getData().setScale(1f);
            hudFont.setColor(Color.WHITE);
        } else if (gamePlay instanceof LoveYourPlants lyp) {
            batch.draw(bgHud, 1450f, 1100f, 260, 80);
            hudFont.getData().setScale(0.45f);
            hudFont.setColor((lyp.getNumOfLost() >= 4) ? Color.RED : Color.WHITE);
            hudFont.draw(batch, "Plants Lost: " + lyp.getNumOfLost() + " / 5", 1470f, 1148f);
            hudFont.getData().setScale(1f);
            hudFont.setColor(Color.WHITE);
        } else if (gamePlay instanceof PlantWhatYouGet pwyb && !pwyb.isWaveStarted()) {
            batch.setColor(0.3f, 0.9f, 0.3f, 0.85f + 0.15f * (float) Math.sin(stateTime * 6f));
            batch.draw(bgHud, START_WAVE_BTN_X, START_WAVE_BTN_Y, START_WAVE_BTN_W, START_WAVE_BTN_H);
            batch.setColor(Color.WHITE);
            hudFont.getData().setScale(0.55f);
            hudFont.setColor(Color.YELLOW);
            hudFont.draw(batch, "LET'S ROCK!", START_WAVE_BTN_X + 28, START_WAVE_BTN_Y + 50);
            hudFont.getData().setScale(1f);
            hudFont.setColor(Color.WHITE);
        }
    }
}

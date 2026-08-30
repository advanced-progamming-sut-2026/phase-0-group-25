package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;

public class IZombieHudRenderer {
    private final TextureBank textureBank;
    private final BitmapFont hudFont;
    private final TextureRegion bgHud;
    private final TextureRegion sunIcon;
    private final TextureRegion cardBg;
    private final TextureRegion pauseBtn;


    private String activeStickerPath = null;
    private float stickerAnimTime = 0f;
    private String activeReactionText = null;
    private float reactionTextTimer = 0f;

    public IZombieHudRenderer(TextureBank textureBank, BitmapFont hudFont) {
        this.textureBank = textureBank;
        this.hudFont = hudFont;
        this.bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        this.sunIcon = textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
        this.cardBg = textureBank.region("IMAGE_UI_PACKETS_SELECTED");
        this.pauseBtn = textureBank.region(GamePlayScreen.PAUSE_BTN_ASSET_ID);
    }

    public void triggerSticker(String animPath) {
        this.activeStickerPath = animPath;
        this.stickerAnimTime = 0f;
    }

    public void triggerReactionText(String text) {
        this.activeReactionText = text;
        this.reactionTextTimer = 3.0f;
    }

    public void render(SpriteBatch batch, IZombie gamePlay, PamPlayer player, float stateTime, float delta) {
        batch.begin();


        batch.draw(bgHud, 20, 1100, 240, 80);
        batch.draw(sunIcon, 30, 1110, 60, 60);
        int resourceAmount = (gamePlay.getMyFaction() == Faction.PLANT) ? gamePlay.getMySuns() : gamePlay.getZombieSunBudget();
        hudFont.draw(batch, String.valueOf(resourceAmount), 100, 1160);


        float timeLeft = gamePlay.getMatchTimeRemaining();
        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        String timerStr = String.format("%02d:%02d", minutes, seconds);

        batch.draw(bgHud, 860, 1100, 200, 80);
        hudFont.setColor((timeLeft <= 20f) ? Color.RED : Color.YELLOW);
        hudFont.draw(batch, timerStr, 905, 1160);
        hudFont.setColor(Color.WHITE);


        long remainingBrains = gamePlay.getBrains().stream().filter(b -> !b.isEaten()).count();
        batch.draw(bgHud, 1400, 1100, 220, 80);
        hudFont.getData().setScale(0.48f);
        hudFont.draw(batch, "Brains: " + remainingBrains + " / 5", 1425, 1155);
        hudFont.getData().setScale(1.0f);


        if (pauseBtn != null) {
            batch.draw(pauseBtn, 1810, 1105, 75, 75);
        }


        renderCards(batch, gamePlay);


        if (activeStickerPath != null) {
            stickerAnimTime += delta;
            player.draw(batch, activeStickerPath, "animation", stickerAnimTime, 1750f, 950f, true);
            if (stickerAnimTime > 3.0f) activeStickerPath = null;
        }


        if (activeReactionText != null && reactionTextTimer > 0) {
            reactionTextTimer -= delta;
            hudFont.setColor(Color.GREEN);
            hudFont.getData().setScale(0.6f);
            hudFont.draw(batch, "Opponent: " + activeReactionText, 1400, 1050);
            hudFont.getData().setScale(1.0f);
            hudFont.setColor(Color.WHITE);
        }

        batch.end();
    }

    private void renderCards(SpriteBatch batch, IZombie gamePlay) {
        if (gamePlay.getMyFaction() == Faction.PLANT) {
            ArrayList<BattlePlant> deck = gamePlay.getPlants();
            for (int i = 0; i < deck.size(); i++) {
                BattlePlant p = deck.get(i);
                PlantType pType = PlantType.fromName(p.getName());
                if (pType == null) continue;
                float cardY = 980f - (i * 116f);
                if (cardBg != null) batch.draw(cardBg, 45f, cardY, 160f, 105f);
                TextureRegion icon = textureBank.region(pType.getIconAssetId());
                if (icon != null) batch.draw(icon, 65f, cardY + 25f, 120f, 70f);
                hudFont.getData().setScale(0.40f);
                hudFont.draw(batch, String.valueOf(p.getPlantStats().getCost()), 160, cardY + 22);
                hudFont.getData().setScale(1.0f);
            }
        } else {
            ZombieType[] zTypes = { ZombieType.DEFAULT, ZombieType.CONE_HEAD, ZombieType.BUCKET_HEAD, ZombieType.NEWSPAPER };
            int[] costs = { 50, 75, 125, 100 };
            for (int i = 0; i < zTypes.length; i++) {
                float cardY = 980f - (i * 116f);
                if (cardBg != null) batch.draw(cardBg, 45f, cardY, 160f, 105f);
                TextureRegion icon = textureBank.region(zTypes[i].getIconAssetId());
                if (icon != null) batch.draw(icon, 65f, cardY + 25f, 120f, 70f);
                hudFont.getData().setScale(0.40f);
                hudFont.draw(batch, String.valueOf(costs[i]), 160, cardY + 22);
                hudFont.getData().setScale(1.0f);
            }
        }
    }
}

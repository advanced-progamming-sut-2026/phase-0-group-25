package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Brain;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HUD for multiplayer "I, Zombie": the countdown timer, each player's
 * faction-specific card deck (only the local player's deck in network play,
 * both decks side-by-side in local couch play), the five brain status pips,
 * the slide-out reaction drawer, and incoming opponent reaction popups.
 */
public class IZombieHudRenderer {
    // ---- Shared reaction catalog: IZombieInputHandler / IZombieCouchPlayInputHandler hit-test against these same arrays. ----
    public static final String[] REACTION_TEXTS = {"Well played!", "Brains incoming!", "Nice defense!"};
    public static final String[] REACTION_EMOJI_ASSET_IDS = {
        "IMAGE_UI_HUD_INGAME_CHALLENGE_SUCCESS",
        "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN",
        "IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD"
    };
    public static final String[] REACTION_STICKER_LABELS = {"Zombie", "Sunflower", "Wall-nut"};

    // ---- Deck card layout (Plant deck matches the main game's card bar exactly; Zombie deck mirrors it on the right). ----
    public static final float CARD_X = 45f, CARD_START_Y = 980f, CARD_WIDTH = 160f, CARD_HEIGHT = 105f, CARD_SPACING = 11f;
    public static final float ZOMBIE_CARD_X = 1715f;

    public static final float PLANT_CURRENCY_X = 20f, PLANT_CURRENCY_Y = 1100f, CURRENCY_BOX_W = 215f, CURRENCY_BOX_H = 80f;
    public static final float ZOMBIE_CURRENCY_X = 1685f, ZOMBIE_CURRENCY_Y = 1100f;

    public static final float PAUSE_BTN_X = 825f, PAUSE_BTN_Y = 1145f, PAUSE_BTN_SIZE = 55f;

    public static final float TIMER_CENTER_X = 960f, TIMER_Y = 1155f;
    public static final float BRAIN_PIP_Y = 1120f, BRAIN_PIP_SIZE = 26f, BRAIN_PIP_SPACING = 34f;

    // ---- Reaction drawer ----
    public static final float DRAWER_TOGGLE_X = 1780f, DRAWER_TOGGLE_Y = 30f, DRAWER_TOGGLE_SIZE = 100f;
    public static final float DRAWER_PANEL_X = 1160f, DRAWER_PANEL_Y = 150f, DRAWER_PANEL_W = 720f, DRAWER_PANEL_H = 330f;
    public static final float DRAWER_BTN_W = 220f, DRAWER_BTN_H = 90f;
    public static final float DRAWER_COL_GAP = 20f, DRAWER_ROW_GAP = 10f, DRAWER_MARGIN = 20f;

    public static float drawerButtonX(int col) {
        return DRAWER_PANEL_X + DRAWER_MARGIN + col * (DRAWER_BTN_W + DRAWER_COL_GAP);
    }

    /** row 0 = bottom (TEXT), row 1 = middle (EMOJI), row 2 = top (STICKER). */
    public static float drawerButtonY(int row) {
        return DRAWER_PANEL_Y + DRAWER_MARGIN + row * (DRAWER_BTN_H + DRAWER_ROW_GAP);
    }

    private static final float EMOTE_POPUP_CENTER_X = 960f, EMOTE_POPUP_Y = 900f;
    private static final float EMOTE_POPUP_DURATION = 3.0f;

    private final TextureBank textureBank;
    private final PamPlayer player;
    private final BitmapFont hudFont;

    private final TextureRegion sunIcon;
    private final TextureRegion brainPointIcon;
    private final TextureRegion bgHud;
    private final TextureRegion cardBgRegion;
    private final TextureRegion pauseBtnRegion;
    private final TextureRegion drawerToggleRegion;
    private final TextureRegion[] emojiRegions = new TextureRegion[REACTION_EMOJI_ASSET_IDS.length];

    public IZombieHudRenderer(TextureBank textureBank, PamPlayer player, BitmapFont hudFont) {
        this.textureBank = textureBank;
        this.player = player;
        this.hudFont = hudFont;

        sunIcon = textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
        brainPointIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        cardBgRegion = textureBank.region("IMAGE_UI_PACKETS_SELECTED");
        pauseBtnRegion = textureBank.region("IMAGE_UI_HUD_INGAME_PAUSE_BUTTON");

        TextureRegion toggle = textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON");
        drawerToggleRegion = (toggle != null) ? toggle : bgHud;

        for (int i = 0; i < REACTION_EMOJI_ASSET_IDS.length; i++) {
            emojiRegions[i] = textureBank.region(REACTION_EMOJI_ASSET_IDS[i]);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, IZombie gamePlay,
                       IZombieHudInputState input, float stateTime, List<ActiveReaction> activeReactions) {
        boolean showPlantSide = gamePlay.isLocalCouchPlay() || gamePlay.getMyFaction() == Faction.PLANT;
        boolean showZombieSide = gamePlay.isLocalCouchPlay() || gamePlay.getMyFaction() == Faction.ZOMBIE;

        batch.begin();
        if (showPlantSide) renderPlantCurrencyAndDeck(batch, gamePlay, input);
        if (showZombieSide) renderZombieCurrencyAndDeck(batch, gamePlay, input);

        if (pauseBtnRegion != null) batch.draw(pauseBtnRegion, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE);

        renderTimerAndBrainPips(batch, gamePlay, stateTime);
        renderDrawerToggleAndPanel(batch, input);
        renderEmotePopups(batch, activeReactions, stateTime);
        renderDragPreview(batch, gamePlay, input, stateTime);
        batch.end();

        renderCooldownOverlays(shapeRenderer, gamePlay, showPlantSide, showZombieSide);
        renderLaneCursor(shapeRenderer, input);
    }

    private void renderLaneCursor(ShapeRenderer shapeRenderer, IZombieHudInputState input) {
        int lane = input.getSelectedZombieLane();
        if (lane < 1 || lane > 5) return;

        float laneY = 205f + (lane - 1) * 150f;
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.4f, 0.9f, 0.4f, 0.18f));
        shapeRenderer.rect(0, laneY - 75f, 1920, 150f);
        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
    }

    // ------------------------------------------------------------------
    // Plant side
    // ------------------------------------------------------------------

    private void renderPlantCurrencyAndDeck(SpriteBatch batch, IZombie gamePlay, IZombieHudInputState input) {
        if (bgHud != null) batch.draw(bgHud, PLANT_CURRENCY_X, PLANT_CURRENCY_Y, CURRENCY_BOX_W, CURRENCY_BOX_H);
        if (sunIcon != null) batch.draw(sunIcon, PLANT_CURRENCY_X + 10, PLANT_CURRENCY_Y + 10, 60, 60);
        hudFont.draw(batch, String.valueOf(gamePlay.getMySuns()), PLANT_CURRENCY_X + 75, PLANT_CURRENCY_Y + 60);

        ArrayList<BattlePlant> deck = gamePlay.getPlants();
        BattlePlant selected = input.getSelectedPlantCard();
        for (int i = 0; i < deck.size(); i++) {
            BattlePlant card = deck.get(i);
            PlantType type = PlantType.fromName(card.getName());
            if (type == null) continue;

            float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
            boolean canAfford = gamePlay.getMySuns() >= card.getPlantStats().getCost();
            boolean isReady = card.getCurrentCoolDown() <= 0 || !card.getActiveCooldown();

            if (!canAfford || !isReady) batch.setColor(0.4f, 0.4f, 0.4f, 0.85f);
            else if (selected == card) batch.setColor(0.6f, 1f, 0.6f, 1f);

            if (cardBgRegion != null) batch.draw(cardBgRegion, CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT);
            drawCardIcon(batch, textureBank.region(type.getIconAssetId()), CARD_X, cardY);
            batch.setColor(Color.WHITE);

            hudFont.getData().setScale(0.4f);
            hudFont.draw(batch, String.valueOf(card.getPlantStats().getCost()), CARD_X + 8, cardY + 22);
            hudFont.getData().setScale(1f);
        }
    }

    // ------------------------------------------------------------------
    // Zombie side
    // ------------------------------------------------------------------

    private void renderZombieCurrencyAndDeck(SpriteBatch batch, IZombie gamePlay, IZombieHudInputState input) {
        if (bgHud != null) batch.draw(bgHud, ZOMBIE_CURRENCY_X, ZOMBIE_CURRENCY_Y, CURRENCY_BOX_W, CURRENCY_BOX_H);
        if (brainPointIcon != null) batch.draw(brainPointIcon, ZOMBIE_CURRENCY_X + 10, ZOMBIE_CURRENCY_Y + 10, 60, 60);
        hudFont.draw(batch, String.valueOf(gamePlay.getZombieBrainPoints()), ZOMBIE_CURRENCY_X + 75, ZOMBIE_CURRENCY_Y + 60);

        String selected = input.getSelectedZombieCardType();
        int i = 0;
        for (Map.Entry<String, Integer> entry : gamePlay.getZombieDeck().entrySet()) {
            String zombieName = entry.getKey();
            int cost = entry.getValue();
            ZombieType type = ZombieType.fromName(zombieName);

            float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
            boolean canAfford = gamePlay.getZombieBrainPoints() >= cost;

            if (!canAfford) batch.setColor(0.4f, 0.4f, 0.4f, 0.85f);
            else if (zombieName.equals(selected)) batch.setColor(0.6f, 1f, 0.6f, 1f);

            if (cardBgRegion != null) batch.draw(cardBgRegion, ZOMBIE_CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT);
            if (type != null) drawCardIcon(batch, textureBank.region(type.getIconAssetId()), ZOMBIE_CARD_X, cardY);
            batch.setColor(Color.WHITE);

            hudFont.getData().setScale(0.4f);
            hudFont.draw(batch, String.valueOf(cost), ZOMBIE_CARD_X + 8, cardY + 22);
            hudFont.getData().setScale(1f);
            i++;
        }
    }

    private void drawCardIcon(SpriteBatch batch, TextureRegion icon, float cardX, float cardY) {
        if (icon == null) return;
        float availW = CARD_WIDTH - 20f, availH = CARD_HEIGHT - 35f;
        float scale = Math.min(availW / icon.getRegionWidth(), availH / icon.getRegionHeight());
        float finalW = icon.getRegionWidth() * scale, finalH = icon.getRegionHeight() * scale;
        batch.draw(icon, cardX + (CARD_WIDTH - finalW) / 2f, cardY + 22f + (availH - finalH) / 2f, finalW, finalH);
    }

    // ------------------------------------------------------------------
    // Timer / brains / drawer / popups
    // ------------------------------------------------------------------

    private void renderTimerAndBrainPips(SpriteBatch batch, IZombie gamePlay, float stateTime) {
        int secondsRemaining = gamePlay.getSecondsRemaining();
        String timeText = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);

        hudFont.getData().setScale(0.6f);
        hudFont.setColor(secondsRemaining <= 15 ? Color.RED : Color.WHITE);
        hudFont.draw(batch, timeText, TIMER_CENTER_X - 45f, TIMER_Y);
        hudFont.setColor(Color.WHITE);
        hudFont.getData().setScale(1f);

        Brain[] brains = gamePlay.getBrains();
        float startX = TIMER_CENTER_X - ((brains.length - 1) * BRAIN_PIP_SPACING) / 2f;
        for (int i = 0; i < brains.length; i++) {
            float px = startX + i * BRAIN_PIP_SPACING - BRAIN_PIP_SIZE / 2f;
            if (brainPointIcon != null) {
                if (brains[i].isEaten()) batch.setColor(0.4f, 0.4f, 0.4f, 0.5f);
                batch.draw(brainPointIcon, px, BRAIN_PIP_Y, BRAIN_PIP_SIZE, BRAIN_PIP_SIZE);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void renderDrawerToggleAndPanel(SpriteBatch batch, IZombieHudInputState input) {
        if (drawerToggleRegion != null) {
            if (input.isReactionDrawerOpen()) batch.setColor(0.6f, 1f, 0.6f, 1f);
            batch.draw(drawerToggleRegion, DRAWER_TOGGLE_X, DRAWER_TOGGLE_Y, DRAWER_TOGGLE_SIZE, DRAWER_TOGGLE_SIZE);
            batch.setColor(Color.WHITE);
        }

        if (!input.isReactionDrawerOpen()) return;

        if (bgHud != null) {
            batch.setColor(0f, 0f, 0f, 0.55f);
            batch.draw(bgHud, DRAWER_PANEL_X, DRAWER_PANEL_Y, DRAWER_PANEL_W, DRAWER_PANEL_H);
            batch.setColor(Color.WHITE);
        }

        hudFont.getData().setScale(0.38f);
        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(0);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);
            hudFont.draw(batch, REACTION_TEXTS[col], bx + 12, by + DRAWER_BTN_H / 2f + 10, DRAWER_BTN_W - 24, com.badlogic.gdx.utils.Align.left, true);
        }
        hudFont.getData().setScale(1f);

        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(1);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);
            if (emojiRegions[col] != null) {
                float size = 55f;
                batch.draw(emojiRegions[col], bx + (DRAWER_BTN_W - size) / 2f, by + (DRAWER_BTN_H - size) / 2f, size, size);
            }
        }

        hudFont.getData().setScale(0.42f);
        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(2);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);
            hudFont.draw(batch, REACTION_STICKER_LABELS[col], bx + 12, by + DRAWER_BTN_H / 2f + 10);
        }
        hudFont.getData().setScale(1f);
    }

    private void renderEmotePopups(SpriteBatch batch, List<ActiveReaction> activeReactions, float stateTime) {
        if (activeReactions == null) return;

        float y = EMOTE_POPUP_Y;
        for (ActiveReaction reaction : activeReactions) {
            if (reaction.category == ActiveReaction.Category.STICKER) continue;

            float age = stateTime - reaction.spawnStateTime;
            if (age < 0 || age > EMOTE_POPUP_DURATION) continue;

            String label;
            if (reaction.category == ActiveReaction.Category.TEXT) {
                label = (reaction.index >= 0 && reaction.index < REACTION_TEXTS.length) ? REACTION_TEXTS[reaction.index] : "";
            } else {
                label = "(emoji)";
            }
            String fullText = reaction.fromLabel + ": " + label;

            float alpha = (age > EMOTE_POPUP_DURATION - 1f) ? Math.max(0f, EMOTE_POPUP_DURATION - age) : 1f;

            if (bgHud != null) {
                batch.setColor(0f, 0f, 0f, 0.6f * alpha);
                batch.draw(bgHud, EMOTE_POPUP_CENTER_X - 220f, y - 10f, 440f, 60f);
            }

            if (reaction.category == ActiveReaction.Category.EMOJI
                && reaction.index >= 0 && reaction.index < emojiRegions.length && emojiRegions[reaction.index] != null) {
                batch.setColor(1f, 1f, 1f, alpha);
                batch.draw(emojiRegions[reaction.index], EMOTE_POPUP_CENTER_X - 210f, y - 2f, 44f, 44f);
            }

            hudFont.getData().setScale(0.42f);
            hudFont.setColor(1f, 1f, 1f, alpha);
            hudFont.draw(batch, fullText, EMOTE_POPUP_CENTER_X - 150f, y + 30f);
            hudFont.setColor(Color.WHITE);
            hudFont.getData().setScale(1f);
            batch.setColor(Color.WHITE);

            y -= 70f;
        }
    }

    private void renderDragPreview(SpriteBatch batch, IZombie gamePlay, IZombieHudInputState input, float stateTime) {
        Vector3 mouse = input.getMouseWorldPos();
        BattlePlant selectedPlant = input.getSelectedPlantCard();
        if (selectedPlant != null && selectedPlant.getPlantStats().getAnimation() != null) {
            PlantType type = PlantType.fromName(selectedPlant.getName());
            String stateName = (type != null) ? type.getStateName() : "idle";
            player.draw(batch, selectedPlant.getPlantStats().getAnimation(), stateName, stateTime, mouse.x, mouse.y, true);
        }
    }

    private void renderCooldownOverlays(ShapeRenderer shapeRenderer, IZombie gamePlay, boolean showPlantSide, boolean showZombieSide) {
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (showPlantSide) {
            ArrayList<BattlePlant> deck = gamePlay.getPlants();
            for (int i = 0; i < deck.size(); i++) {
                BattlePlant card = deck.get(i);
                if (card.getCurrentCoolDown() > 0 && card.getPlantStats().getRechargeTime() > 0) {
                    float ratio = (float) Math.min(1.0, card.getCurrentCoolDown() / card.getPlantStats().getRechargeTime());
                    float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
                    shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.65f));
                    shapeRenderer.rect(CARD_X, cardY + (CARD_HEIGHT - (CARD_HEIGHT * ratio)), CARD_WIDTH, CARD_HEIGHT * ratio);
                }
            }
        }

        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
    }
}

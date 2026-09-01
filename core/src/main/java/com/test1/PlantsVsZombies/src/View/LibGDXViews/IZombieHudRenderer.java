package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IZombieHudRenderer {

    public static final String[] REACTION_TEXTS = {"Well played!", "Nice attack!", "Nice defense!"};


    public static final String[] REACTION_EMOJI_ASSET_IDS = {
        "IMAGE_UI_HUD_LOD_LOD_STRAWBURST",
        "IMAGE_UI_HUD_LOD_LOD_GHOST_PEPPER",
        "IMAGE_UI_HUD_LOD_LOD_FUTURE_LASERBEAN"
    };


    public static final String[] STICKER_ANIM_PATHS = {
        "768/FULL/EFFECTS/ZOMBIE_BIGHEAD_IMP_SHOCK/ZOMBIE_BIGHEAD_IMP_SHOCK.PAM",
        "768/INITIAL/EFFECTS/BLOOMING_HEARTS_ZOMBIE_EFFECT/BLOOMING_HEARTS_ZOMBIE_EFFECT.PAM",
        "768/FULL/ZOMBIE/ZOMBIE_BIGHEAD_SUPERFAN/ZOMBIE_BIGHEAD_SUPERFAN.PAM"
    };

    public static final String[] STICKER_STATE_NAMES = {
        "animation",
        "animation_lrg",
        "eat"
    };


    private static final float PREVIEW_ANIM_SCALE = 0.45f;


    public static final float CARD_X = 45f, CARD_START_Y = 980f, CARD_WIDTH = 160f, CARD_HEIGHT = 105f, CARD_SPACING = 11f;
    public static final float ZOMBIE_CARD_X = 1715f;

    public static final float PLANT_CURRENCY_X = 20f, PLANT_CURRENCY_Y = 1100f, CURRENCY_BOX_W = 215f, CURRENCY_BOX_H = 80f;
    public static final float ZOMBIE_CURRENCY_X = 1685f, ZOMBIE_CURRENCY_Y = 1100f;


    public static final float DRAWER_TOGGLE_X = 1780f, DRAWER_TOGGLE_Y = 30f, DRAWER_TOGGLE_SIZE = 100f;


    public static final float PAUSE_BTN_X = 1670f, PAUSE_BTN_Y = 30f, PAUSE_BTN_SIZE = 100f;


    public static final float TIMER_BOX_X = 850f, TIMER_BOX_Y = 1100f, TIMER_BOX_W = 220f, TIMER_BOX_H = 80f;


    public static final float DRAWER_PANEL_X = 1160f, DRAWER_PANEL_Y = 150f, DRAWER_PANEL_W = 720f, DRAWER_PANEL_H = 330f;
    public static final float DRAWER_BTN_W = 220f, DRAWER_BTN_H = 90f;
    public static final float DRAWER_COL_GAP = 20f, DRAWER_ROW_GAP = 10f, DRAWER_MARGIN = 20f;

    public static float drawerButtonX(int col) {
        return DRAWER_PANEL_X + DRAWER_MARGIN + col * (DRAWER_BTN_W + DRAWER_COL_GAP);
    }
    public static float drawerButtonY(int row) {
        return DRAWER_PANEL_Y + DRAWER_MARGIN + row * (DRAWER_BTN_H + DRAWER_ROW_GAP);
    }

    private static final float EMOTE_POPUP_CENTER_X = 960f, EMOTE_POPUP_Y = 880f;
    private static final float EMOTE_POPUP_DURATION = 3.2f;

    private final TextureBank textureBank;
    private final PamPlayer player;
    private final BitmapFont hudFont;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private final TextureRegion sunIcon;
    private final TextureRegion brainIcon;
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

        TextureRegion brain = textureBank.region("IMAGE_UI_CURRENCY_VALENBRAINZ_STACK_0");
        if (brain == null) brain = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        brainIcon = brain;

        bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        cardBgRegion = textureBank.region("IMAGE_UI_PACKETS_SELECTED");
        pauseBtnRegion = textureBank.region("IMAGE_UI_HUD_INGAME_PAUSE_BUTTON");

        TextureRegion toggle = textureBank.region("IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_SOCIAL_NORMAL");
        if (toggle == null) toggle = textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON");
        drawerToggleRegion = (toggle != null) ? toggle : bgHud;

        for (int i = 0; i < REACTION_EMOJI_ASSET_IDS.length; i++) {
            emojiRegions[i] = textureBank.region(REACTION_EMOJI_ASSET_IDS[i]);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, IZombie gamePlay,
                       IZombieHudInputState input, float stateTime, List<ActiveReaction> activeReactions) {
        boolean showPlantSide = gamePlay.isLocalCouchPlay() || gamePlay.getMyFaction() == Faction.PLANT;
        boolean showZombieSide = gamePlay.isLocalCouchPlay() || gamePlay.getMyFaction() == Faction.ZOMBIE;


        renderLaneCursor(shapeRenderer, input);


        batch.begin();
        if (showPlantSide) renderPlantCurrencyAndDeck(batch, gamePlay, input);
        if (showZombieSide) renderZombieCurrencyAndDeck(batch, gamePlay, input);

        if (pauseBtnRegion != null) {
            batch.draw(pauseBtnRegion, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE);
        }

        renderTopCenterStatus(batch, gamePlay);
        renderDrawerToggleAndPanel(batch, input, stateTime);
        renderEmotePopups(batch, activeReactions, stateTime);
        renderDragPreview(batch, gamePlay, input, stateTime);
        batch.end();


        renderCooldownOverlays(shapeRenderer, gamePlay, showPlantSide, showZombieSide);
    }

    private void renderTopCenterStatus(SpriteBatch batch, IZombie gamePlay) {
        if (bgHud != null) batch.draw(bgHud, TIMER_BOX_X, TIMER_BOX_Y, TIMER_BOX_W, TIMER_BOX_H);
        int secondsRemaining = gamePlay.getSecondsRemaining();
        String timeText = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);

        hudFont.getData().setScale(1.28f);
        hudFont.setColor((secondsRemaining <= 20) ? Color.RED : Color.YELLOW);
        hudFont.draw(batch, timeText, TIMER_BOX_X, TIMER_BOX_Y + 58f, TIMER_BOX_W, Align.center, false);
        hudFont.setColor(Color.WHITE);
        hudFont.getData().setScale(1f);
    }

    private void renderPlantCurrencyAndDeck(SpriteBatch batch, IZombie gamePlay, IZombieHudInputState input) {
        if (bgHud != null) batch.draw(bgHud, PLANT_CURRENCY_X, PLANT_CURRENCY_Y, CURRENCY_BOX_W, CURRENCY_BOX_H);
        if (sunIcon != null) batch.draw(sunIcon, PLANT_CURRENCY_X + 10, PLANT_CURRENCY_Y + 10, 60, 60);

        hudFont.getData().setScale(1.42f);
        String sunText = String.valueOf(gamePlay.getMySuns());
        glyphLayout.setText(hudFont, sunText);

        float textSlotX = PLANT_CURRENCY_X + 70f;
        float textSlotW = CURRENCY_BOX_W - 75f;
        float textX = textSlotX + (textSlotW - glyphLayout.width) / 2f;
        float textY = PLANT_CURRENCY_Y + (CURRENCY_BOX_H + glyphLayout.height) / 2f;

        hudFont.draw(batch, sunText, textX, textY);
        hudFont.getData().setScale(1f);

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

            hudFont.getData().setScale(1.30f);
            hudFont.draw(batch, String.valueOf(card.getPlantStats().getCost()), CARD_X + 12f, cardY + 38f);
            hudFont.getData().setScale(1f);
        }
    }

    private void renderZombieCurrencyAndDeck(SpriteBatch batch, IZombie gamePlay, IZombieHudInputState input) {
        if (bgHud != null) batch.draw(bgHud, ZOMBIE_CURRENCY_X, ZOMBIE_CURRENCY_Y, CURRENCY_BOX_W, CURRENCY_BOX_H);
        if (brainIcon != null) batch.draw(brainIcon, ZOMBIE_CURRENCY_X + 10, ZOMBIE_CURRENCY_Y + 10, 60, 60);

        hudFont.getData().setScale(1.42f);
        String brainText = String.valueOf(gamePlay.getZombieBrainPoints());
        glyphLayout.setText(hudFont, brainText);

        float textSlotX = ZOMBIE_CURRENCY_X + 70f;
        float textSlotW = CURRENCY_BOX_W - 75f;
        float textX = textSlotX + (textSlotW - glyphLayout.width) / 2f;
        float textY = ZOMBIE_CURRENCY_Y + (CURRENCY_BOX_H + glyphLayout.height) / 2f;

        hudFont.draw(batch, brainText, textX, textY);
        hudFont.getData().setScale(1f);

        String selected = input.getSelectedZombieCardType();
        int i = 0;
        for (Map.Entry<String, Integer> entry : gamePlay.getZombieDeck().entrySet()) {
            String zombieName = entry.getKey();
            int cost = entry.getValue();

            float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
            boolean canAfford = gamePlay.getZombieBrainPoints() >= cost;

            if (!canAfford) batch.setColor(0.4f, 0.4f, 0.4f, 0.85f);
            else if (zombieName.equals(selected)) batch.setColor(0.6f, 1f, 0.6f, 1f);

            if (cardBgRegion != null) batch.draw(cardBgRegion, ZOMBIE_CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT);
            drawCardIcon(batch, getZombieCardIcon(zombieName), ZOMBIE_CARD_X, cardY);
            batch.setColor(Color.WHITE);

            hudFont.getData().setScale(1.30f);
            hudFont.draw(batch, String.valueOf(cost), ZOMBIE_CARD_X + CARD_WIDTH - 65f, cardY + 38f);
            hudFont.getData().setScale(1f);
            i++;
        }
    }

    private TextureRegion getZombieCardIcon(String zombieName) {
        ZombieType type = ZombieType.fromName(zombieName);
        TextureRegion region = (type != null && type.getIconAssetId() != null) ? textureBank.region(type.getIconAssetId()) : null;
        if (region != null) return region;

        String name = zombieName.toUpperCase();
        if (name.contains("CONE")) {
            region = textureBank.region("IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_ARMOR1");
        } else if (name.contains("BUCKET")) {
            region = textureBank.region("IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_ARMOR2");
        } else if (name.contains("NEWSPAPER")) {
            region = textureBank.region("IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_NEWSPAPER");
        }
        return (region != null) ? region : textureBank.region("IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK");
    }

    private void drawCardIcon(SpriteBatch batch, TextureRegion icon, float cardX, float cardY) {
        if (icon == null) return;
        float availW = CARD_WIDTH - 20f, availH = CARD_HEIGHT - 35f;
        float scale = Math.min(availW / icon.getRegionWidth(), availH / icon.getRegionHeight());
        float finalW = icon.getRegionWidth() * scale, finalH = icon.getRegionHeight() * scale;
        batch.draw(icon, cardX + (CARD_WIDTH - finalW) / 2f, cardY + 22f + (availH - finalH) / 2f, finalW, finalH);
    }

    private void renderLaneCursor(ShapeRenderer shapeRenderer, IZombieHudInputState input) {
        int lane = input.getSelectedZombieLane();
        if (lane < 1 || lane > 5) return;

        float laneY = 130f + (lane - 1) * 150f + 75f;
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.4f, 0.9f, 0.4f, 0.18f));

        shapeRenderer.rect(490f, laneY - 75f, 1370f, 150f);
        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.Gdx.gl.GL_BLEND);
    }

    private void renderDrawerToggleAndPanel(SpriteBatch batch, IZombieHudInputState input, float stateTime) {
        if (drawerToggleRegion != null) {
            if (input.isReactionDrawerOpen()) batch.setColor(0.6f, 1f, 0.6f, 1f);
            batch.draw(drawerToggleRegion, DRAWER_TOGGLE_X, DRAWER_TOGGLE_Y, DRAWER_TOGGLE_SIZE, DRAWER_TOGGLE_SIZE);
            batch.setColor(Color.WHITE);
        }

        if (!input.isReactionDrawerOpen()) return;

        if (bgHud != null) {
            batch.setColor(0f, 0f, 0f, 0.65f);
            batch.draw(bgHud, DRAWER_PANEL_X, DRAWER_PANEL_Y, DRAWER_PANEL_W, DRAWER_PANEL_H);
            batch.setColor(Color.WHITE);
        }


        hudFont.getData().setScale(1.26f);
        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(0);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);
            hudFont.draw(batch, REACTION_TEXTS[col], bx + 6, by + DRAWER_BTN_H / 2f + 20, DRAWER_BTN_W - 12, Align.center, true);
        }
        hudFont.getData().setScale(1f);


        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(1);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);
            if (emojiRegions[col] != null) {
                float size = 65f;
                batch.draw(emojiRegions[col], bx + (DRAWER_BTN_W - size) / 2f, by + (DRAWER_BTN_H - size) / 2f, size, size);
            }
        }


        for (int col = 0; col < 3; col++) {
            float bx = drawerButtonX(col), by = drawerButtonY(2);
            if (cardBgRegion != null) batch.draw(cardBgRegion, bx, by, DRAWER_BTN_W, DRAWER_BTN_H);

            if (player != null && col < STICKER_ANIM_PATHS.length && STICKER_ANIM_PATHS[col] != null) {
                float animCenterX = bx + DRAWER_BTN_W / 2f;
                float animCenterY = by + 20f;

                batch.setTransformMatrix(batch.getTransformMatrix().idt()
                    .translate(animCenterX, animCenterY, 0)
                    .scale(PREVIEW_ANIM_SCALE, PREVIEW_ANIM_SCALE, 1)
                    .translate(-animCenterX, -animCenterY, 0));

                player.draw(batch, STICKER_ANIM_PATHS[col], STICKER_STATE_NAMES[col], stateTime, animCenterX, animCenterY, true);

                batch.setTransformMatrix(batch.getTransformMatrix().idt());
            }
        }
    }

    private void renderEmotePopups(SpriteBatch batch, List<ActiveReaction> activeReactions, float stateTime) {
        if (activeReactions == null || activeReactions.isEmpty()) return;

        float currentCenterY = EMOTE_POPUP_Y;

        for (ActiveReaction reaction : activeReactions) {
            float age = stateTime - reaction.spawnStateTime;
            if (age < 0 || age > EMOTE_POPUP_DURATION) continue;

            float alpha = (age > EMOTE_POPUP_DURATION - 0.8f)
                ? Math.max(0f, (EMOTE_POPUP_DURATION - age) / 0.8f)
                : 1f;

            if (reaction.category == ActiveReaction.Category.TEXT) {
                String text = (reaction.index >= 0 && reaction.index < REACTION_TEXTS.length)
                    ? REACTION_TEXTS[reaction.index]
                    : "";
                String fullText = reaction.fromLabel + ": " + text;

                float boxW = 820f;
                float boxH = 110f;
                float boxX = EMOTE_POPUP_CENTER_X - (boxW / 2f);
                float boxY = currentCenterY - (boxH / 2f);

                if (bgHud != null) {
                    batch.setColor(0.1f, 0.1f, 0.1f, 0.85f * alpha);
                    batch.draw(bgHud, boxX, boxY, boxW, boxH);
                    batch.setColor(Color.WHITE);
                }

                hudFont.getData().setScale(1.5f);
                hudFont.setColor(1f, 0.95f, 0.2f, alpha);
                hudFont.draw(batch, fullText, boxX, currentCenterY + 18f, boxW, Align.center, false);
                hudFont.setColor(Color.WHITE);
                hudFont.getData().setScale(1f);

                currentCenterY -= 130f;

            } else if (reaction.category == ActiveReaction.Category.EMOJI) {
                if (reaction.index >= 0 && reaction.index < emojiRegions.length && emojiRegions[reaction.index] != null) {
                    TextureRegion icon = emojiRegions[reaction.index];
                    float size = 150f;
                    batch.setColor(1f, 1f, 1f, alpha);
                    batch.draw(icon, EMOTE_POPUP_CENTER_X - (size / 2f), currentCenterY - (size / 2f), size, size);
                    batch.setColor(Color.WHITE);

                    hudFont.getData().setScale(0.6f);
                    hudFont.setColor(1f, 1f, 1f, alpha);
                    hudFont.draw(batch, reaction.fromLabel, EMOTE_POPUP_CENTER_X - 150f, currentCenterY - (size / 2f) - 10f, 300f, Align.center, false);
                    hudFont.setColor(Color.WHITE);
                    hudFont.getData().setScale(1f);

                    currentCenterY -= 180f;
                }

            } else if (reaction.category == ActiveReaction.Category.STICKER) {
                if (player != null && reaction.index >= 0 && reaction.index < STICKER_ANIM_PATHS.length) {
                    batch.setColor(1f, 1f, 1f, alpha);
                    batch.setTransformMatrix(batch.getTransformMatrix().idt()
                        .translate(EMOTE_POPUP_CENTER_X, currentCenterY, 0)
                        .scale(1.8f, 1.8f, 1)
                        .translate(-EMOTE_POPUP_CENTER_X, -currentCenterY, 0));

                    player.draw(batch, STICKER_ANIM_PATHS[reaction.index], STICKER_STATE_NAMES[reaction.index],
                        age, EMOTE_POPUP_CENTER_X, currentCenterY, true);

                    batch.setTransformMatrix(batch.getTransformMatrix().idt());
                    batch.setColor(Color.WHITE);

                    hudFont.getData().setScale(0.6f);
                    hudFont.setColor(1f, 1f, 1f, alpha);
                    hudFont.draw(batch, reaction.fromLabel, EMOTE_POPUP_CENTER_X - 150f, currentCenterY - 90f, 300f, Align.center, false);
                    hudFont.setColor(Color.WHITE);
                    hudFont.getData().setScale(1f);

                    currentCenterY -= 200f;
                }
            }
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

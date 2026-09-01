package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;

import java.util.ArrayList;
import java.util.Map;

public class IZombieInputHandler extends InputAdapter implements IZombieHudInputState {
    private final IZombie gamePlay;
    private final OrthographicCamera camera;
    private final IZombieScreen screen;
    private final Vector3 mouseWorldPos = new Vector3();

    private BattlePlant selectedPlantCard;
    private String selectedZombieCardType;
    private boolean reactionDrawerOpen = false;

    public IZombieInputHandler(IZombie gamePlay, OrthographicCamera camera, IZombieScreen screen) {
        this.gamePlay = gamePlay;
        this.camera = camera;
        this.screen = screen;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));

        gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));

        gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
        float x = mouseWorldPos.x, y = mouseWorldPos.y;

        if (isInside(x, y, IZombieHudRenderer.PAUSE_BTN_X, IZombieHudRenderer.PAUSE_BTN_Y,
            IZombieHudRenderer.PAUSE_BTN_SIZE, IZombieHudRenderer.PAUSE_BTN_SIZE)) {
            screen.openPauseModal();
            return true;
        }

        if (isInside(x, y, IZombieHudRenderer.DRAWER_TOGGLE_X, IZombieHudRenderer.DRAWER_TOGGLE_Y,
            IZombieHudRenderer.DRAWER_TOGGLE_SIZE, IZombieHudRenderer.DRAWER_TOGGLE_SIZE)) {
            reactionDrawerOpen = !reactionDrawerOpen;
            return true;
        }
        if (reactionDrawerOpen) {
            if (handleDrawerClick(x, y)) {
                reactionDrawerOpen = false;
                return true;
            }
            reactionDrawerOpen = false;
        }

        if (gamePlay.isGameOver()) return false;

        if (gamePlay.getMyFaction() == Faction.PLANT) return handlePlantInput(x, y);
        if (gamePlay.getMyFaction() == Faction.ZOMBIE) return handleZombieInput(x, y);
        return false;
    }

    private boolean handlePlantInput(float x, float y) {
        if (gamePlay.tryCollectSunByClick(x, y)) return true;

        ArrayList<BattlePlant> deck = gamePlay.getPlants();
        for (int i = 0; i < deck.size(); i++) {
            float cardY = IZombieHudRenderer.CARD_START_Y - (i * (IZombieHudRenderer.CARD_HEIGHT + IZombieHudRenderer.CARD_SPACING));
            if (isInside(x, y, IZombieHudRenderer.CARD_X, cardY, IZombieHudRenderer.CARD_WIDTH, IZombieHudRenderer.CARD_HEIGHT)) {
                selectedPlantCard = (selectedPlantCard == deck.get(i)) ? null : deck.get(i);
                return true;
            }
        }

        if (selectedPlantCard != null) {
            int col = gridColumn(x);
            int row = gridRow(y);
            if (gamePlay.placePlant(selectedPlantCard, col, row)) {
                screen.sendGameStateAction("PLACE_PLANT", selectedPlantCard.getName(), col, row);
                selectedPlantCard = null;
            }
            return true;
        }
        return false;
    }

    private boolean handleZombieInput(float x, float y) {
        int i = 0;
        for (Map.Entry<String, Integer> entry : gamePlay.getZombieDeck().entrySet()) {
            float cardY = IZombieHudRenderer.CARD_START_Y - (i * (IZombieHudRenderer.CARD_HEIGHT + IZombieHudRenderer.CARD_SPACING));
            if (isInside(x, y, IZombieHudRenderer.ZOMBIE_CARD_X, cardY, IZombieHudRenderer.CARD_WIDTH, IZombieHudRenderer.CARD_HEIGHT)) {
                String key = entry.getKey();
                selectedZombieCardType = key.equals(selectedZombieCardType) ? null : key;
                return true;
            }
            i++;
        }

        if (selectedZombieCardType != null) {
            int row = Math.max(1, Math.min(5, gridRow(y)));
            if (gamePlay.spawnZombie(selectedZombieCardType, row)) {
                screen.sendGameStateAction("SPAWN_ZOMBIE", selectedZombieCardType, -1, row);
                selectedZombieCardType = null;
            }
            return true;
        }
        return false;
    }

    private boolean handleDrawerClick(float x, float y) {
        for (int col = 0; col < 3; col++) {
            if (isInside(x, y, IZombieHudRenderer.drawerButtonX(col), IZombieHudRenderer.drawerButtonY(0),
                IZombieHudRenderer.DRAWER_BTN_W, IZombieHudRenderer.DRAWER_BTN_H)) {
                screen.sendReaction(ActiveReaction.Category.TEXT, col);
                return true;
            }
            if (isInside(x, y, IZombieHudRenderer.drawerButtonX(col), IZombieHudRenderer.drawerButtonY(1),
                IZombieHudRenderer.DRAWER_BTN_W, IZombieHudRenderer.DRAWER_BTN_H)) {
                screen.sendReaction(ActiveReaction.Category.EMOJI, col);
                return true;
            }
            if (isInside(x, y, IZombieHudRenderer.drawerButtonX(col), IZombieHudRenderer.drawerButtonY(2),
                IZombieHudRenderer.DRAWER_BTN_W, IZombieHudRenderer.DRAWER_BTN_H)) {
                screen.sendReaction(ActiveReaction.Category.STICKER, col);
                return true;
            }
        }
        return false;
    }

    private int gridColumn(float worldX) {
        return (int) Math.floor((worldX - 490) / 152.2) + 1;
    }

    private int gridRow(float worldY) {
        return (int) Math.floor((worldY - 130) / 150) + 1;
    }

    private boolean isInside(float x, float y, float rx, float ry, float rw, float rh) {
        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }

    @Override public Vector3 getMouseWorldPos() { return mouseWorldPos; }
    @Override public BattlePlant getSelectedPlantCard() { return selectedPlantCard; }
    @Override public String getSelectedZombieCardType() { return selectedZombieCardType; }
    @Override public boolean isReactionDrawerOpen() { return reactionDrawerOpen; }
}

package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;

import java.util.ArrayList;
import java.util.List;

public class IZombieCouchPlayInputHandler extends InputAdapter implements IZombieHudInputState {
    private final IZombie gamePlay;
    private final OrthographicCamera camera;
    private final IZombieScreen screen;
    private final Vector3 mouseWorldPos = new Vector3();
    private final List<String> zombieCardOrder;

    private BattlePlant selectedPlantCard;
    private String selectedZombieCardType;
    private int selectedZombieLane = 3;
    private boolean reactionDrawerOpen = false;

    public IZombieCouchPlayInputHandler(IZombie gamePlay, OrthographicCamera camera, IZombieScreen screen) {
        this.gamePlay = gamePlay;
        this.camera = camera;
        this.screen = screen;
        this.zombieCardOrder = new ArrayList<>(gamePlay.getZombieDeck().keySet());
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
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
            boolean handled = handleDrawerClick(x, y);
            reactionDrawerOpen = false;
            if (handled) return true;
        }

        if (gamePlay.isGameOver()) return false;
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
            int col = (int) Math.floor((x - 490) / 152.2) + 1;
            int row = (int) Math.floor((y - 130) / 150) + 1;
            if (gamePlay.placePlant(selectedPlantCard, col, row)) {
                selectedPlantCard = null;
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

    @Override
    public boolean keyDown(int keycode) {
        if (gamePlay.isGameOver()) return false;

        int cardIndex = numberKeyIndex(keycode);
        if (cardIndex >= 0 && cardIndex < zombieCardOrder.size()) {
            String card = zombieCardOrder.get(cardIndex);
            selectedZombieCardType = card.equals(selectedZombieCardType) ? null : card;
            return true;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            selectedZombieLane = Math.min(5, selectedZombieLane + 1);
            return true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            selectedZombieLane = Math.max(1, selectedZombieLane - 1);
            return true;
        }
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            if (selectedZombieCardType != null && gamePlay.spawnZombie(selectedZombieCardType, selectedZombieLane)) {
                selectedZombieCardType = null;
            }
            return true;
        }
        return false;
    }

    private int numberKeyIndex(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_1: return 0;
            case Input.Keys.NUM_2: return 1;
            case Input.Keys.NUM_3: return 2;
            case Input.Keys.NUM_4: return 3;
            case Input.Keys.NUM_5: return 4;
            default: return -1;
        }
    }

    private boolean isInside(float x, float y, float rx, float ry, float rw, float rh) {
        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }

    @Override public int getSelectedZombieLane() { return selectedZombieLane; }
    @Override public Vector3 getMouseWorldPos() { return mouseWorldPos; }
    @Override public BattlePlant getSelectedPlantCard() { return selectedPlantCard; }
    @Override public String getSelectedZombieCardType() { return selectedZombieCardType; }
    @Override public boolean isReactionDrawerOpen() { return reactionDrawerOpen; }
}

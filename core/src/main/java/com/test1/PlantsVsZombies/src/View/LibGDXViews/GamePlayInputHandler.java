package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.ConveyorBelt;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.ConveyorCard;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.PlantWhatYouGet;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.util.ArrayList;

public class GamePlayInputHandler extends InputAdapter {
    private static final float SUN_PLUS_X = 190f, SUN_PLUS_Y = 1120f;
    private static final float PF_PLUS_X = 269f, PF_PLUS_Y = 1120f;
    private static final float PLUS_BTN_SIZE = 40f;
    private static final float PAUSE_BTN_X = 1810f, PAUSE_BTN_Y = 1105f, PAUSE_BTN_SIZE = 75f;
    private static final float PF_BTN_X = 1675f, PF_BTN_Y = 30f, PF_BTN_SIZE = 100f;
    private static final float SHOVEL_BTN_X = 1770f, SHOVEL_BTN_Y = 30f, SHOVEL_BTN_SIZE = 100f;
    private static final float START_WAVE_BTN_X = 1450f, START_WAVE_BTN_Y = 1100f, START_WAVE_BTN_W = 220f, START_WAVE_BTN_H = 75f;
    private static final float CARD_X = 45f, CARD_START_Y = 980f, CARD_WIDTH = 160f, CARD_HEIGHT = 105f, CARD_SPACING = 11f;
    private final GamePlay gamePlay;
    private final OrthographicCamera camera;
    private final GamePlayModals modals;
    private final IntroDialogueCutscene introCutscene;
    private final GamePlayScreen screen;
    private final Vector3 mouseWorldPos = new Vector3();
    private BattlePlant selectedPlant = null;
    private ConveyorCard selectedConveyorCard = null;
    private boolean isShovelSelected = false;
    private boolean isPlantFoodSelected = false;

    public GamePlayInputHandler(GamePlay gamePlay, OrthographicCamera camera, GamePlayModals modals,
                                IntroDialogueCutscene introCutscene, GamePlayScreen screen) {
        this.gamePlay = gamePlay;
        this.camera = camera;
        this.modals = modals;
        this.introCutscene = introCutscene;
        this.screen = screen;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
        gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
        gamePlay.tryCollectPlantFoodByHover(mouseWorldPos.x, mouseWorldPos.y);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
        gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
        gamePlay.tryCollectPlantFoodByHover(mouseWorldPos.x, mouseWorldPos.y);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(mouseWorldPos.set(screenX, screenY, 0));

        if (introCutscene != null && !introCutscene.isFinished()) {
            introCutscene.advance();
            if (introCutscene.isFinished()) {
                gamePlay.isPaused = false;
            }
            return true;
        }

        if (button == Input.Buttons.RIGHT) {
            clearSelections();
            return true;
        }

        if (button == Input.Buttons.LEFT) {

            if (isInside(mouseWorldPos.x, mouseWorldPos.y, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE)) {
                if (!gamePlay.isGameOver()) {
                    modals.showPauseModal();
                    return true;
                }
            }


            User user = gamePlay.getThisUser();
            if (user != null && user.isDebugMode()) {
                if (!(gamePlay instanceof ConveyorBelt) && isInside(mouseWorldPos.x, mouseWorldPos.y, SUN_PLUS_X, SUN_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE)) {
                    gamePlay.cheatAddSun(100);
                    return true;
                }
                if (isInside(mouseWorldPos.x, mouseWorldPos.y, PF_PLUS_X, PF_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE)) {
                    gamePlay.addPlantFood();
                    return true;
                }
            }


            if (gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y)) {
                return true;
            }


            if (isInside(mouseWorldPos.x, mouseWorldPos.y, SHOVEL_BTN_X, SHOVEL_BTN_Y, SHOVEL_BTN_SIZE, SHOVEL_BTN_SIZE)) {
                isShovelSelected = !isShovelSelected;
                if (isShovelSelected) {
                    selectedPlant = null;
                    selectedConveyorCard = null;
                    isPlantFoodSelected = false;
                }
                return true;
            }


            if (isInside(mouseWorldPos.x, mouseWorldPos.y, PF_BTN_X, PF_BTN_Y, PF_BTN_SIZE, PF_BTN_SIZE)) {
                if (gamePlay.getNumOfPlantFood() > 0) {
                    isPlantFoodSelected = !isPlantFoodSelected;
                    if (isPlantFoodSelected) {
                        selectedPlant = null;
                        selectedConveyorCard = null;
                        isShovelSelected = false;
                    }
                } else {
                    screen.showError("No Plant Food available!");
                }
                return true;
            }


            if (gamePlay instanceof PlantWhatYouGet pwyb && !pwyb.isWaveStarted()) {
                if (isInside(mouseWorldPos.x, mouseWorldPos.y, START_WAVE_BTN_X, START_WAVE_BTN_Y, START_WAVE_BTN_W, START_WAVE_BTN_H)) {
                    pwyb.startWave();
                    return true;
                }
            }


            if (gamePlay instanceof ConveyorBelt cb) {
                for (ConveyorCard card : cb.getConveyorCards()) {
                    if (isInside(mouseWorldPos.x, mouseWorldPos.y, CARD_X, card.getCurrentY(), CARD_WIDTH, CARD_HEIGHT)) {
                        isShovelSelected = false;
                        isPlantFoodSelected = false;
                        selectedPlant = (selectedPlant == card.getPlant()) ? null : card.getPlant();
                        selectedConveyorCard = (selectedPlant != null) ? card : null;
                        return true;
                    }
                }
            }


            if (!(gamePlay instanceof ConveyorBelt)) {
                ArrayList<BattlePlant> deck = gamePlay.getPlants();
                for (int i = 0; i < deck.size(); i++) {
                    float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
                    if (isInside(mouseWorldPos.x, mouseWorldPos.y, CARD_X, cardY, CARD_WIDTH, CARD_HEIGHT)) {
                        BattlePlant clickedPlant = deck.get(i);
                        boolean canAfford = gamePlay.getMySuns() >= clickedPlant.getPlantStats().getCost();
                        boolean isSetupPhase = (gamePlay instanceof PlantWhatYouGet && !((PlantWhatYouGet) gamePlay).isWaveStarted());
                        boolean isReady = isSetupPhase || (clickedPlant.getCurrentCoolDown() <= 0 || !clickedPlant.getActiveCooldown());

                        if (canAfford && isReady) {
                            isShovelSelected = false;
                            isPlantFoodSelected = false;
                            selectedPlant = (selectedPlant == clickedPlant) ? null : clickedPlant;
                        } else if (!canAfford) {
                            screen.showError("Not enough sun!");
                        } else {
                            screen.showError("Plant is recharging!");
                        }
                        return true;
                    }
                }
            }


            int col = (int) Math.floor((mouseWorldPos.x - 490) / 152.2) + 1;
            int row = (int) Math.floor((mouseWorldPos.y - 130) / 150) + 1;

            if (col >= 1 && col <= 9 && row >= 1 && row <= 5) {
                if (isShovelSelected) {
                    gamePlay.plucking(new Position(col, row));
                    isShovelSelected = false;
                    return true;
                }

                if (isPlantFoodSelected) {
                    Tile tile = gamePlay.getTileByPosition(col, row);
                    if (tile != null && !tile.getPlants().isEmpty()) {
                        if (gamePlay.usePlantFood(col, row)) {
                            isPlantFoodSelected = false;
                        }
                    } else {
                        screen.showError("No plant on this tile!");
                    }
                    return true;
                }

                if (selectedPlant != null) {
                    gamePlay.planting(selectedPlant, new Position(col, row));
                    if (gamePlay instanceof ConveyorBelt && selectedConveyorCard != null) {
                        ((ConveyorBelt) gamePlay).removeCard(selectedConveyorCard);
                        selectedConveyorCard = null;
                    }
                    selectedPlant = null;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInside(float x, float y, float rx, float ry, float rw, float rh) {
        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }

    public void clearSelections() {
        selectedPlant = null;
        selectedConveyorCard = null;
        isShovelSelected = false;
        isPlantFoodSelected = false;
    }

    public Vector3 getMouseWorldPos() {
        return mouseWorldPos;
    }

    public BattlePlant getSelectedPlant() {
        return selectedPlant;
    }

    public boolean isShovelSelected() {
        return isShovelSelected;
    }

    public boolean isPlantFoodSelected() {
        return isPlantFoodSelected;
    }
}

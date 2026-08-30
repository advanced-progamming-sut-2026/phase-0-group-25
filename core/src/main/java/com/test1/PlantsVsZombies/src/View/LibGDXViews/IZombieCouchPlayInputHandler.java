package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;

public class IZombieCouchPlayInputHandler extends InputAdapter {
    private final IZombie gamePlay;
    private final OrthographicCamera camera;
    private final Vector3 mousePos = new Vector3();

    private BattlePlant selectedPlant = null;
    private int selectedZombieIndex = 0;
    private int targetZombieRow = 3;

    private final String[] zombieDeck = {
        ZombieType.DEFAULT.getName(),
        ZombieType.CONE_HEAD.getName(),
        ZombieType.BUCKET_HEAD.getName(),
        ZombieType.NEWSPAPER.getName()
    };

    public IZombieCouchPlayInputHandler(IZombie gamePlay, OrthographicCamera camera) {
        this.gamePlay = gamePlay;
        this.camera = camera;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(mousePos.set(screenX, screenY, 0));

        if (button == Input.Buttons.LEFT) {
            for (int i = 0; i < gamePlay.getPlants().size(); i++) {
                float cardY = 980f - (i * 116f);
                if (mousePos.x >= 45f && mousePos.x <= 205f && mousePos.y >= cardY && mousePos.y <= cardY + 105f) {
                    selectedPlant = gamePlay.getPlants().get(i);
                    return true;
                }
            }

            int col = (int) Math.floor((mousePos.x - 490) / 152.2) + 1;
            int row = (int) Math.floor((mousePos.y - 130) / 150) + 1;
            if (col >= 1 && col <= 8 && row >= 1 && row <= 5 && selectedPlant != null) {
                gamePlay.plantDefenseAction(selectedPlant, col, row);
                selectedPlant = null;
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.NUM_1) selectedZombieIndex = 0;
        else if (keycode == Input.Keys.NUM_2) selectedZombieIndex = 1;
        else if (keycode == Input.Keys.NUM_3) selectedZombieIndex = 2;
        else if (keycode == Input.Keys.NUM_4) selectedZombieIndex = 3;


        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            targetZombieRow = Math.min(5, targetZombieRow + 1);
        } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            targetZombieRow = Math.max(1, targetZombieRow - 1);
        }


        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            gamePlay.spawnZombieAction(zombieDeck[selectedZombieIndex], targetZombieRow);
            return true;
        }

        return false;
    }
}

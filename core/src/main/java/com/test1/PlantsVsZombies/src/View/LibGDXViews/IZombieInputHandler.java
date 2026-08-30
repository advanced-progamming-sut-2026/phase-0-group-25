package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Network.IZombieNetworkBridge;

import java.util.Map;

public class IZombieInputHandler extends InputAdapter {
    private final IZombie gamePlay;
    private final OrthographicCamera camera;
    private final IZombieNetworkBridge networkBridge;
    private final Vector3 mousePos = new Vector3();

    private BattlePlant selectedPlant = null;
    private String selectedZombieType = null;

    public IZombieInputHandler(IZombie gamePlay, OrthographicCamera camera, IZombieNetworkBridge networkBridge) {
        this.gamePlay = gamePlay;
        this.camera = camera;
        this.networkBridge = networkBridge;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(mousePos.set(screenX, screenY, 0));

        if (button == Input.Buttons.RIGHT) {
            selectedPlant = null;
            selectedZombieType = null;
            return true;
        }


        if (gamePlay.getMyFaction() == Faction.PLANT) {
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
                if (gamePlay.plantDefenseAction(selectedPlant, col, row)) {
                    networkBridge.sendAction("PLANT", Map.of("type", selectedPlant.getName(), "col", col, "row", row));
                    selectedPlant = null;
                }
                return true;
            }
        } else {
            ZombieType[] zTypes = { ZombieType.DEFAULT, ZombieType.CONE_HEAD, ZombieType.BUCKET_HEAD, ZombieType.NEWSPAPER };
            for (int i = 0; i < zTypes.length; i++) {
                float cardY = 980f - (i * 116f);
                if (mousePos.x >= 45f && mousePos.x <= 205f && mousePos.y >= cardY && mousePos.y <= cardY + 105f) {
                    selectedZombieType = zTypes[i].getName();
                    return true;
                }
            }

            int row = (int) Math.floor((mousePos.y - 130) / 150) + 1;
            if (mousePos.x >= 1500f && row >= 1 && row <= 5 && selectedZombieType != null) {
                if (gamePlay.spawnZombieAction(selectedZombieType, row)) {
                    networkBridge.sendAction("SPAWN_ZOMBIE", Map.of("type", selectedZombieType, "row", row));
                    selectedZombieType = null;
                }
                return true;
            }
        }
        return false;
    }
}

package com.test1.PlantsVsZombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.GenderType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.Simple;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.GamePlayScreen;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Main extends Game {
    private static Main instance;
    private SpriteBatch batch;
    private Skin skin;
    private TextureBank textureBank;

    @Override
    public void create() {

        GameDataLoader.loadGameData();


        UIManager.init(this);


        User dummyUser = new User("TestUser", "Tester", "123", "test@test.com", GenderType.Male);
        dummyUser.unlockPlant(PlantType.PEASHOOTER);
        dummyUser.unlockPlant(PlantType.SUNFLOWER);
        dummyUser.unlockPlant(PlantType.WALL_NUT);


        ArrayList<String> selectedPlants = new ArrayList<>(Arrays.asList("PEASHOOTER", "SUNFLOWER", "WALL_NUT"));
        ArrayList<String> stageZombies = new ArrayList<>(Arrays.asList("DEFAULT", "CONE_HEAD"));


        Simple testGamePlay = new Simple(
            ChapterType.ANCIENT_EGYPT,
            1,
            1,
            dummyUser,
            selectedPlants,
            stageZombies,
            new HashSet<>()
        );


        GamePlayScreen testScreen = new GamePlayScreen(testGamePlay);
        UIManager.changeScreen(testScreen);
    }


    public static Main getInstance() {
        return instance;
    }

    @Override
    public void render() {
        super.render();
        UIManager.renderToasts(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        UIManager.resizeToasts(width, height);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Skin getSkin() {
        return skin;
    }

    public TextureBank getTextureBank() {
        return textureBank;
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        super.dispose();
    }
}

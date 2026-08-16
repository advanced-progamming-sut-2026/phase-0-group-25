package com.test1.PlantsVsZombies;

import com.badlogic.gdx.Game;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.GenderType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.Simple;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.GamePlayScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main extends Game {

    @Override
    public void create() {
        GameDataLoader.loadGameData();
        instance = this;
        batch = new SpriteBatch();
        skin = PvzSkin.get();

        textureBank = new TextureBank("768", Gdx.files.internal("Assets"));

        GameDataLoader.loadGameData();

        // 2. Create dummy data to instantly test the Simple mode gameplay
        User dummyUser = new User("Player1", "Player", "Password123!", "player@test.com", GenderType.Male);
        UsersManager.getInstance().addUser(dummyUser);

        ArrayList<String> chosenPlants = new ArrayList<>();
        chosenPlants.add("PEASHOOTER");
        chosenPlants.add("SUNFLOWER");

        ArrayList<String> incomingZombies = new ArrayList<>();
        incomingZombies.add("DEFAULT");

        Set<String> boostedPlants = new HashSet<>();

        // 3. Initialize the core model for Simple Mode
        Simple simpleGame = new Simple(
            ChapterType.ANCIENT_EGYPT,
            1,
            3, // difficulty
            dummyUser,
            chosenPlants,
            incomingZombies,
            boostedPlants
        );

        // 4. Boot up the visual GamePlayScreen and pass the model to it
        this.setScreen(new GamePlayScreen(simpleGame));
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
        super.dispose();
    }
}

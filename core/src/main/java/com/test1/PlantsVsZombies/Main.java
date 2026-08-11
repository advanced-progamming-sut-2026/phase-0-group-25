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
        // 1. Load the backend game data (plants and zombies config)
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
        // This is crucial: it tells libGDX to render whatever screen is currently active
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

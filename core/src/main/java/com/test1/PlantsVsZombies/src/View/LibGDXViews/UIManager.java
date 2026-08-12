package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Screen;
import com.test1.PlantsVsZombies.Main;

public class UIManager {
    private static Main main;

    public static void init(Main instance) {
        main = instance;
    }

    public static void changeScreen(Screen screen) {
        if (main != null) {
            main.setScreen(screen);
        }
    }

    public static Screen getCurrentScreen() {
        if (main != null) {
            return main.getScreen();
        }
        return null;
    }
}

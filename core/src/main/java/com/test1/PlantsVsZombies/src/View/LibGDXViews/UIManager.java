package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;

public class UIManager {
    private static Main main;
    private static Stage toastStage;

    public static void init(Main instance) {
        main = instance;
        toastStage = new Stage(new ScreenViewport());
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

    /**
     * Adds a toast to a Stage that is NOT owned by any individual Screen,
     * so it keeps rendering even if the current Screen changes right after
     * this is called (e.g. login/sign up success followed by changeMenu()).
     */
    public static void showToast(String message, String bgAssetId) {
        if (toastStage == null || main == null) return;

        Table popup = new Table();
        popup.pad(15, 20, 15, 20);

        if (bgAssetId != null && !bgAssetId.isEmpty() && main.getTextureBank() != null) {
            TextureRegion region = main.getTextureBank().region(bgAssetId);
            if (region != null) {
                NinePatch patch = new NinePatch(region, 15, 15, 15, 15);
                popup.setBackground(new NinePatchDrawable(patch));
            }
        }

        Label label = new Label(message, main.getSkin());
        label.setColor(Color.BLACK);
        popup.add(label);
        popup.pack();

        float margin = 20f;
        popup.setPosition(
            margin,
            toastStage.getHeight() - popup.getHeight() - margin
        );

        toastStage.addActor(popup);
        popup.addAction(Actions.sequence(
            Actions.delay(2.5f),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));
    }

    public static void renderToasts(float delta) {
        if (toastStage == null) return;
        toastStage.act(delta);
        toastStage.draw();
    }

    public static void resizeToasts(int width, int height) {
        if (toastStage != null) {
            toastStage.getViewport().update(width, height, true);
        }
    }
}

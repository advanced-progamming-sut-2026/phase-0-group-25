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
    private static Table toastContainer;

    public static void init(Main instance) {
        main = instance;
        toastStage = new Stage(new ScreenViewport());

        toastContainer = new Table();
        toastContainer.setFillParent(true);

        toastContainer.top().right().padTop(25).padRight(25);
        toastStage.addActor(toastContainer);

        toastStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
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

    public static void showToast(String message, String bgAssetId) {
        if (toastStage == null || main == null || toastContainer == null) return;

        Table popup = new Table();
        popup.pad(12, 22, 12, 22);

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


        toastContainer.add(popup).padBottom(10).right().row();

        popup.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(0.3f),
                Actions.moveBy(-30f, 0, 0.3f)
            ),
            Actions.delay(3.0f),
            Actions.parallel(
                Actions.fadeOut(0.4f),
                Actions.moveBy(40f, 0, 0.4f)
            ),
            Actions.run(() -> {
                popup.remove();
                toastContainer.invalidateHierarchy();
            })
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

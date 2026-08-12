package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;
import pvz.libpvz.textures.TextureBank;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;
    protected TextureBank textureBank;
    protected Table rootTable;
    private Stack mainStack;
    private Stack modalStack;
    private Stack toastStack;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = Main.getInstance().getSkin();
        textureBank = Main.getInstance().getTextureBank();

        mainStack = new Stack();
        mainStack.setFillParent(true);
        modalStack = new Stack();
        toastStack = new Stack();

        rootTable = new Table();
        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);

        stage.addActor(mainStack);
        Gdx.input.setInputProcessor(stage);
    }

    protected Label createBlackLabel(String text) {
        Label label = new Label(text, skin);
        label.setColor(Color.BLACK);
        return label;
    }

    protected void showToast(String message, String bgAssetId) {
        // Delegate to UIManager's toast stage, which is not tied to this
        // Screen's lifecycle -- so the toast still renders even if the
        // caller immediately switches to a different Screen (e.g. right
        // after a successful login/sign up).
        UIManager.showToast(message, bgAssetId);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (textureBank != null) {
            textureBank.update();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        float baseWidth = 1280f;
        float baseHeight = 720f;
        float scaleX = width / baseWidth;
        float scaleY = height / baseHeight;
        float scale = Math.min(scaleX, scaleY);
        float maxScale = 1.25f;
        float minScale = 0.75f;
        if (scale > maxScale) scale = maxScale;
        if (scale < minScale) scale = minScale;

        if (stage.getViewport() instanceof ScreenViewport) {
            ((ScreenViewport) stage.getViewport()).setUnitsPerPixel(1f / scale);
        }
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}

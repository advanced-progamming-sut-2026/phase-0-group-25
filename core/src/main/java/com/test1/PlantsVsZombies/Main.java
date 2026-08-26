package com.test1.PlantsVsZombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.test1.PlantsVsZombies.src.Audio.SoundManager;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

public class Main extends Game {
    private static Main instance;
    private SpriteBatch batch;
    private Skin skin;
    private TextureBank textureBank;
    private PamPlayer pamPlayer;

    @Override
    public void create() {
        GameDataLoader.loadGameData();
        instance = this;
        batch = new SpriteBatch();
        skin = PvzSkin.get();
        textureBank = new TextureBank("768", Gdx.files.internal("Assets"));
        pamPlayer = new PamPlayer(textureBank, Gdx.files.internal("Assets"));

        SoundManager.getInstance().initSound();

        GameDataLoader.loadGameData();
        UIManager.init(this);

        MenuManager.getInstance().initInitialScreen();
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

    public PamPlayer getPamPlayer() {
        return pamPlayer;
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        SoundManager.getInstance().dispose();
        super.dispose();
    }
}

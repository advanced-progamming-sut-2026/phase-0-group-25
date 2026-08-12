package com.test1.PlantsVsZombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

public class Main extends Game {
    private static Main instance;
    private SpriteBatch batch;
    private Skin skin;
    private TextureBank textureBank;

    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();

        skin = PvzSkin.get();
        textureBank = new TextureBank("768", Gdx.files.absolute("Assets"));

        GameDataLoader.loadGameData();
        UIManager.init(this);

        MenuManager.getInstance().initInitialScreen();
    }

    public static Main getInstance() {
        return instance;
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

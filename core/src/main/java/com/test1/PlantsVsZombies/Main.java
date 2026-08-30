package com.test1.PlantsVsZombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.test1.PlantsVsZombies.src.Audio.SoundManager;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;
import com.test1.PlantsVsZombies.src.Network.NetworkConfig;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import java.io.IOException;

public class Main extends Game {
    private static Main instance;
    private SpriteBatch batch;
    private Skin skin;
    private TextureBank textureBank;
    private PamPlayer pamPlayer;

    @Override
    public void create() {
        try {
            ServerConnection.connect(NetworkConfig.SERVER_HOST, NetworkConfig.SERVER_PORT);
        } catch (IOException e) {
            System.err.println("==========================================================");
            System.err.println("[Client] Could not connect to the game server at "
                + NetworkConfig.SERVER_HOST + ":" + NetworkConfig.SERVER_PORT);
            System.err.println("[Client] Start GameServer (Network.Server.GameServer) first,");
            System.err.println("[Client] then launch the game again.");
            System.err.println("[Client] Details: " + e.getMessage());
            System.err.println("==========================================================");
            Gdx.app.exit();
            return;
        }

        GameDataLoader.loadGameData();
        instance = this;
        batch = new SpriteBatch();
        skin = PvzSkin.get();
        textureBank = new TextureBank("768", Gdx.files.internal("assets/Assets"));
        pamPlayer = new PamPlayer(textureBank, Gdx.files.internal("assets/Assets"));

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

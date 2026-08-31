package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IZombieScreen extends ScreenAdapter implements GamePlayMenuView {
    private static final float TICK_RATE = 0.1f;
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    /** How long a reaction popup/sticker stays in the active list before being pruned (must exceed the renderers' own fade durations). */
    private static final float REACTION_LIFETIME = 3.5f;

    private final IZombie gamePlay;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private TextureBank textureBank;
    private PamPlayer player;
    private BitmapFont hudFont;

    private GamePlayModals modals;
    private IZombieWorldRenderer worldRenderer;
    private IZombieHudRenderer hudRenderer;
    private IZombieHudInputState hudInputState;

    private float stateTime = 0f;
    private float tickAccumulator = 0f;
    private final List<ActiveReaction> activeReactions = new ArrayList<>();

    private final Consumer<NetworkMessage> opponentGameStateListener = this::handleOpponentGameState;
    private final Consumer<NetworkMessage> reactionReceivedListener = this::handleReactionReceived;
    private final Consumer<NetworkMessage> opponentDisconnectedListener = this::handleOpponentDisconnected;
    private boolean networkListenersRegistered = false;

    public IZombieScreen(IZombie gamePlay) {
        this.gamePlay = gamePlay;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        textureBank = new TextureBank("768", Gdx.files.local("assets/Assets"));
        player = new PamPlayer(textureBank, Gdx.files.local("assets/Assets"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("pvz.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3;
        hudFont = generator.generateFont(parameter);
        generator.dispose();

        modals = new GamePlayModals(
            gamePlay,
            () -> MenuManager.getInstance().changeMenu(MenuType.TravelLog),
            () -> MenuManager.getInstance().changeMenu(MenuType.TravelLog)
        );

        worldRenderer = new IZombieWorldRenderer(textureBank, player);
        hudRenderer = new IZombieHudRenderer(textureBank, player, hudFont);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(modals.getStage());
        if (gamePlay.isLocalCouchPlay()) {
            IZombieCouchPlayInputHandler couchInput = new IZombieCouchPlayInputHandler(gamePlay, camera, this);
            hudInputState = couchInput;
            multiplexer.addProcessor(couchInput);
        } else {
            IZombieInputHandler networkInput = new IZombieInputHandler(gamePlay, camera, this);
            hudInputState = networkInput;
            multiplexer.addProcessor(networkInput);
            registerNetworkListeners();
        }
        Gdx.input.setInputProcessor(multiplexer);

        modals.showObjectivesModal(() -> gamePlay.isPaused = false);
    }

    private void registerNetworkListeners() {
        if (networkListenersRegistered) return;
        ServerConnection connection = ServerConnection.getInstance();
        connection.addPushListener(MessageType.OPPONENT_GAME_STATE, opponentGameStateListener);
        connection.addPushListener(MessageType.REACTION_RECEIVED, reactionReceivedListener);
        connection.addPushListener(MessageType.OPPONENT_DISCONNECTED, opponentDisconnectedListener);
        networkListenersRegistered = true;
    }

    private void unregisterNetworkListeners() {
        if (!networkListenersRegistered) return;
        ServerConnection connection = ServerConnection.getInstance();
        connection.removePushListener(MessageType.OPPONENT_GAME_STATE, opponentGameStateListener);
        connection.removePushListener(MessageType.REACTION_RECEIVED, reactionReceivedListener);
        connection.removePushListener(MessageType.OPPONENT_DISCONNECTED, opponentDisconnectedListener);
        networkListenersRegistered = false;
    }







    private void handleOpponentGameState(NetworkMessage message) {
        Map<String, Object> data = message.getData();
        Object actionType = data.get("actionType");
        if ("PLACE_PLANT".equals(actionType)) {
            String plantName = stringValue(data, "entityName");
            int column = intValue(data, "column");
            int row = intValue(data, "row");
            gamePlay.applyRemotePlacePlant(plantName, column, row);
        } else if ("SPAWN_ZOMBIE".equals(actionType)) {
            String zombieType = stringValue(data, "entityName");
            int row = intValue(data, "row");
            gamePlay.applyRemoteSpawnZombie(zombieType, row);
        }
    }

    private void handleReactionReceived(NetworkMessage message) {
        Map<String, Object> data = message.getData();
        try {
            ActiveReaction.Category category = ActiveReaction.Category.valueOf(stringValue(data, "category"));
            int index = intValue(data, "index");
            String fromUsername = stringValue(data, "fromUsername");
            activeReactions.add(new ActiveReaction(category, index, fromUsername != null ? fromUsername : "Opponent", stateTime));
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void handleOpponentDisconnected(NetworkMessage message) {
        if (gamePlay.isGameOver()) return;
        gamePlay.endMatch(gamePlay.getMyFaction());
        String opponent = stringValue(message.getData(), "opponentUsername");
        UIManager.showToast((opponent != null ? opponent : "Your opponent") + " disconnected -- you win!", ERROR_BG_ASSET_ID);
    }

    private String stringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : value.toString();
    }

    private int intValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : 0;
    }





    @Override
    public void render(float delta) {
        textureBank.update();
        ScreenUtils.clear(0.1f, 0.4f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            stateTime += delta;
            gamePlay.setTotalTimePassed(stateTime);

            tickAccumulator += delta;
            while (tickAccumulator >= TICK_RATE) {
                gamePlay.update();
                tickAccumulator -= TICK_RATE;
            }

            modals.checkAndMaybeShowEndGameModal();
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        activeReactions.removeIf(reaction -> stateTime - reaction.spawnStateTime > REACTION_LIFETIME);

        worldRenderer.render(batch, shapeRenderer, gamePlay, stateTime, activeReactions);
        hudRenderer.render(batch, shapeRenderer, gamePlay, hudInputState, stateTime, activeReactions);

        modals.getStage().act(delta);
        modals.getStage().draw();
        UIManager.renderToasts(delta);
    }

    @Override
    public void resize(int width, int height) {
        if (modals != null) modals.resize(width, height);
        UIManager.resizeToasts(width, height);
    }

    @Override
    public void hide() {




        if (!gamePlay.isLocalCouchPlay() && !gamePlay.isGameOver() && ServerConnection.isConnected()) {
            ServerConnection.getInstance().sendRequestAsync(NetworkMessage.request(0, MessageType.CANCEL_MATCHMAKING), null);
        }
        unregisterNetworkListeners();
    }

    @Override
    public void dispose() {
        unregisterNetworkListeners();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (hudFont != null) hudFont.dispose();
        if (modals != null) modals.dispose();
    }





    public void openPauseModal() {
        modals.showPauseModal();
    }

    /** Applies a local action's network echo: sends what the local player just did so the opponent's client can mirror it. */
    public void sendGameStateAction(String actionType, String entityName, int column, int row) {
        if (gamePlay.isLocalCouchPlay() || !ServerConnection.isConnected()) return;
        NetworkMessage message = NetworkMessage.request(0, MessageType.OPPONENT_GAME_STATE)
            .put("actionType", actionType)
            .put("entityName", entityName)
            .put("column", column)
            .put("row", row);
        ServerConnection.getInstance().sendRequestAsync(message, null);
    }

    /** Shows an instant local echo of a reaction the local player just sent, and forwards it to the opponent over the network. */
    public void sendReaction(ActiveReaction.Category category, int index) {
        activeReactions.add(new ActiveReaction(category, index, "You", stateTime));

        if (!gamePlay.isLocalCouchPlay() && ServerConnection.isConnected()) {
            NetworkMessage message = NetworkMessage.request(0, MessageType.SEND_REACTION)
                .put("category", category.name())
                .put("index", index);
            ServerConnection.getInstance().sendRequestAsync(message, null);
        }
    }





    @Override
    public void showCurrentMenu() {
    }

    @Override
    public void showError(String errorMessage) {
        UIManager.showToast(errorMessage, ERROR_BG_ASSET_ID);
    }
}

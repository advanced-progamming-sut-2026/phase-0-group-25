package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;
import pvz.skin.BorderedTable;

import java.util.Random;
import java.util.function.Consumer;

public class IZombieLobbyDialog extends BorderedTable {
    private enum Mode { CHOOSE, SEARCHING, CHALLENGE_INPUT, CHALLENGE_PENDING }

    private static final float BTN_WIDTH = 160f;
    private static final float BTN_HEIGHT = 48f;

    private final Skin skin;
    private final Runnable onClose;

    private Mode mode = Mode.CHOOSE;
    private String statusMessage = "";
    private String pendingChallengeTarget;
    private TextField usernameField;

    private final Consumer<NetworkMessage> challengeResponseListener = this::handleChallengeResponsePush;
    private final Consumer<NetworkMessage> matchFoundListener = this::handleMatchFoundPush;
    private final Consumer<NetworkMessage> challengeReceivedListener = this::handleChallengeReceivedPush;

    public IZombieLobbyDialog(Skin skin, Runnable onClose) {
        super();
        this.skin = skin;
        this.onClose = onClose;

        this.pad(30);
        this.setSize(580, 480);

        ServerConnection.getInstance().addPushListener(MessageType.MATCH_FOUND, matchFoundListener);
        ServerConnection.getInstance().addPushListener(MessageType.CHALLENGE_USER, challengeReceivedListener);

        buildUI();
    }

    private void buildUI() {
        this.clearChildren();

        Label title = new Label("I, Zombie: Multiplayer", skin, "big");
        title.setColor(Color.BLACK);
        title.setFontScale(0.75f);
        title.setAlignment(Align.center);
        this.add(title).padBottom(24).row();

        switch (mode) {
            case CHOOSE:
                buildChooseView();
                break;
            case SEARCHING:
                buildSearchingView();
                break;
            case CHALLENGE_INPUT:
                buildChallengeInputView();
                break;
            case CHALLENGE_PENDING:
                buildChallengePendingView();
                break;
        }
    }

    private void buildChooseView() {
        TextButton randomBtn = new TextButton("Random Matchmaking", skin, "green");
        randomBtn.addListener(clickListener(this::startRandomMatchmaking));
        this.add(randomBtn).width(380).height(52).padBottom(14).row();

        TextButton challengeBtn = new TextButton("Direct Challenge", skin, "green");
        challengeBtn.addListener(clickListener(() -> {
            mode = Mode.CHALLENGE_INPUT;
            statusMessage = "";
            buildUI();
        }));
        this.add(challengeBtn).width(380).height(52).padBottom(14).row();

        TextButton couchBtn = new TextButton("Local Couch Play", skin, "green");
        couchBtn.addListener(clickListener(this::startCouchPlay));
        this.add(couchBtn).width(380).height(52).padBottom(20).row();

        TextButton closeBtn = createCloseButton();
        this.add(closeBtn).width(180).height(BTN_HEIGHT).row();
    }

    private void buildSearchingView() {
        Label searching = new Label("Searching for an opponent...", skin, "big");
        searching.setFontScale(0.55f);
        searching.setColor(Color.BLACK);
        this.add(searching).padBottom(16).row();

        if (statusMessage != null && !statusMessage.isEmpty()) {
            this.add(errorLabel(statusMessage)).width(480).padBottom(16).row();
        }


        Table actionRow = new Table();
        TextButton cancelBtn = new TextButton("Cancel", skin, "brown");
        cancelBtn.addListener(clickListener(this::cancelMatchmaking));
        actionRow.add(cancelBtn).width(BTN_WIDTH).height(BTN_HEIGHT).padRight(16);

        TextButton closeBtn = createCloseButton();
        actionRow.add(closeBtn).width(BTN_WIDTH).height(BTN_HEIGHT);

        this.add(actionRow).padTop(16).row();
    }

    private void buildChallengeInputView() {
        Label prompt = new Label("Enter a username to challenge:", skin, "big");
        prompt.setFontScale(0.55f);
        prompt.setColor(Color.BLACK);
        this.add(prompt).padBottom(12).row();

        usernameField = new TextField("", skin);
        this.add(usernameField).width(340).height(50).padBottom(12).row();

        if (statusMessage != null && !statusMessage.isEmpty()) {
            this.add(errorLabel(statusMessage)).width(480).padBottom(12).row();
        }

        TextButton sendBtn = new TextButton("Send Challenge", skin, "green");
        sendBtn.addListener(clickListener(this::sendChallenge));
        this.add(sendBtn).width(320).height(50).padBottom(16).row();


        Table actionRow = new Table();
        TextButton backBtn = new TextButton("Back", skin, "brown");
        backBtn.addListener(clickListener(() -> {
            mode = Mode.CHOOSE;
            statusMessage = "";
            buildUI();
        }));
        actionRow.add(backBtn).width(BTN_WIDTH).height(BTN_HEIGHT).padRight(16);

        TextButton closeBtn = createCloseButton();
        actionRow.add(closeBtn).width(BTN_WIDTH).height(BTN_HEIGHT);

        this.add(actionRow).row();
    }

    private void buildChallengePendingView() {
        Label waiting = new Label("Waiting for " + pendingChallengeTarget + " to respond...", skin, "big");
        waiting.setFontScale(0.60f);
        waiting.setColor(Color.BLACK);
        waiting.setWrap(true);
        waiting.setAlignment(Align.center);
        this.add(waiting).width(450).padBottom(20).row();

        if (statusMessage != null && !statusMessage.isEmpty()) {
            this.add(errorLabel(statusMessage)).width(480).padBottom(16).row();
        }

        Table actionRow = new Table();
        TextButton cancelBtn = new TextButton("Cancel", skin, "brown");
        cancelBtn.addListener(clickListener(() -> {
            cleanup();
            mode = Mode.CHALLENGE_INPUT;
            statusMessage = "Challenge cancelled.";
            buildUI();
        }));
        actionRow.add(cancelBtn).width(BTN_WIDTH).height(BTN_HEIGHT).padRight(16);

        TextButton closeBtn = createCloseButton();
        actionRow.add(closeBtn).width(BTN_WIDTH).height(BTN_HEIGHT);

        this.add(actionRow).padTop(10).row();
    }

    private TextButton createCloseButton() {
        TextButton closeBtn = new TextButton("Close", skin, "brown");
        closeBtn.addListener(clickListener(() -> {
            cleanup();
            if (onClose != null) onClose.run();
        }));
        return closeBtn;
    }

    private Label errorLabel(String text) {
        Label label = new Label(text, skin);
        label.setColor(Color.FIREBRICK);
        label.setFontScale(1.25f);
        label.setWrap(true);
        label.setAlignment(Align.center);
        return label;
    }

    private void startRandomMatchmaking() {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) {
            statusMessage = "You must log in to play online.";
            buildUI();
            return;
        }

        mode = Mode.SEARCHING;
        statusMessage = "";
        buildUI();

        NetworkMessage request = NetworkMessage.request(0, MessageType.JOIN_MATCHMAKING_QUEUE)
            .put("username", user.getUserName());

        ServerConnection.getInstance().sendRequestAsync(request, response -> {
            if (mode != Mode.SEARCHING) return;
            if (!response.isSuccess()) {
                mode = Mode.CHOOSE;
                statusMessage = response.getErrorMessage();
                buildUI();
            }
        });
    }

    private void cancelMatchmaking() {
        ServerConnection.getInstance().sendRequestAsync(NetworkMessage.request(0, MessageType.CANCEL_MATCHMAKING), null);
        mode = Mode.CHOOSE;
        statusMessage = "";
        buildUI();
    }

    private void sendChallenge() {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) {
            statusMessage = "You must log in to challenge others.";
            buildUI();
            return;
        }

        String target = (usernameField != null && usernameField.getText() != null) ? usernameField.getText().trim() : "";
        if (target.isEmpty()) {
            statusMessage = "Enter a username first.";
            buildUI();
            return;
        }

        if (target.equalsIgnoreCase(user.getUserName())) {
            statusMessage = "You cannot challenge yourself!";
            buildUI();
            return;
        }

        pendingChallengeTarget = target;
        mode = Mode.CHALLENGE_PENDING;
        statusMessage = "";
        ServerConnection.getInstance().addPushListener(MessageType.RESPOND_TO_CHALLENGE, challengeResponseListener);
        buildUI();

        NetworkMessage request = NetworkMessage.request(0, MessageType.CHALLENGE_USER)
            .put("fromUsername", user.getUserName())
            .put("targetUsername", target);

        ServerConnection.getInstance().sendRequestAsync(request, response -> {
            if (mode != Mode.CHALLENGE_PENDING) return;
            if (!response.isSuccess()) {
                ServerConnection.getInstance().removePushListener(MessageType.RESPOND_TO_CHALLENGE, challengeResponseListener);
                mode = Mode.CHALLENGE_INPUT;
                statusMessage = response.getErrorMessage();
                buildUI();
            }
        });
    }

    private void handleMatchFoundPush(NetworkMessage msg) {
        mode = Mode.CHOOSE;
        cleanup();

        String roleStr = (String) msg.getData().get("role");
        String opponent = (String) msg.getData().getOrDefault("opponentUsername", msg.getData().get("opponent"));
        Faction myFaction = "PLANT".equalsIgnoreCase(roleStr) ? Faction.PLANT : Faction.ZOMBIE;

        User currentUser = UsersManager.getInstance().getLoggedInUser();
        int difficulty = (currentUser != null && currentUser.getUserProgress() != null) ? currentUser.getUserProgress().getGameDifficulty() : 0;
        long seed = ((Number) msg.getData().getOrDefault("roomSeed", 0L)).longValue();
        long startTime = ((Number) msg.getData().getOrDefault("startTimeMillis", System.currentTimeMillis())).longValue();

        IZombie gamePlay = new IZombie(ChapterType.MINI_GAME, 1, difficulty, currentUser,
            myFaction, false, opponent, seed, startTime);

        if (onClose != null) onClose.run();

        UIManager.showToast("Matched vs " + opponent + "! Role: " + myFaction, "IMAGE_UI_GENERIC_VTB");
        UIManager.changeScreen(new IZombieScreen(gamePlay));
    }

    private void handleChallengeReceivedPush(NetworkMessage msg) {
        String challenger = (String) msg.getData().get("challenger");
        User user = UsersManager.getInstance().getLoggedInUser();
        String myUname = (user != null) ? user.getUserName() : "Player";


        if (challenger == null || challenger.equalsIgnoreCase(myUname) || mode == Mode.CHALLENGE_PENDING) {
            return;
        }

        BorderedTable modal = new BorderedTable();
        modal.pad(25);

        Label notice = new Label("Challenge from " + challenger + "!", skin, "big");
        notice.setColor(Color.BLACK);
        notice.setFontScale(0.8f);
        modal.add(notice).colspan(2).padBottom(20).row();

        TextButton acceptBtn = new TextButton("Accept", skin, "green");
        acceptBtn.addListener(clickListener(() -> {
            NetworkMessage resp = NetworkMessage.request(0, MessageType.RESPOND_TO_CHALLENGE)
                .put("fromUsername", myUname)
                .put("challenger", challenger)
                .put("accepted", true);
            ServerConnection.getInstance().sendRequestAsync(resp, null);
            modal.remove();
        }));

        TextButton declineBtn = new TextButton("Decline", skin, "brown");
        declineBtn.addListener(clickListener(() -> {
            NetworkMessage resp = NetworkMessage.request(0, MessageType.RESPOND_TO_CHALLENGE)
                .put("fromUsername", myUname)
                .put("challenger", challenger)
                .put("accepted", false);
            ServerConnection.getInstance().sendRequestAsync(resp, null);
            modal.remove();
        }));

        Table btnRow = new Table();
        btnRow.add(acceptBtn).width(130).padRight(15);
        btnRow.add(declineBtn).width(130);
        modal.add(btnRow).colspan(2);

        modal.pack();
        if (getStage() != null) {
            modal.setPosition((getStage().getWidth() - modal.getWidth()) / 2f,
                (getStage().getHeight() - modal.getHeight()) / 2f);
            getStage().addActor(modal);
        }
    }

    private void handleChallengeResponsePush(NetworkMessage message) {
        if (mode != Mode.CHALLENGE_PENDING) return;

        Object opponent = message.getData().get("opponentUsername");
        Object acceptedObj = message.getData().get("accepted");
        boolean accepted = Boolean.parseBoolean(String.valueOf(acceptedObj));
        if (accepted) return;

        ServerConnection.getInstance().removePushListener(MessageType.RESPOND_TO_CHALLENGE, challengeResponseListener);
        mode = Mode.CHALLENGE_INPUT;
        statusMessage = (opponent != null ? opponent : pendingChallengeTarget) + " declined the challenge.";
        buildUI();
    }

    private void startCouchPlay() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null) {
            statusMessage = "You must be logged in to play.";
            mode = Mode.CHOOSE;
            buildUI();
            return;
        }

        int difficulty = (currentUser.getUserProgress() != null) ? currentUser.getUserProgress().getGameDifficulty() : 0;
        long seed = new Random().nextLong();

        IZombie gamePlay = new IZombie(ChapterType.MINI_GAME, 1, difficulty, currentUser,
            Faction.PLANT, true, null, seed, System.currentTimeMillis());

        cleanup();
        if (onClose != null) onClose.run();

        UIManager.changeScreen(new IZombieScreen(gamePlay));
    }

    public void cleanup() {
        ServerConnection.getInstance().removePushListener(MessageType.MATCH_FOUND, matchFoundListener);
        ServerConnection.getInstance().removePushListener(MessageType.CHALLENGE_USER, challengeReceivedListener);
        ServerConnection.getInstance().removePushListener(MessageType.RESPOND_TO_CHALLENGE, challengeResponseListener);
        if (mode == Mode.SEARCHING) {
            ServerConnection.getInstance().sendRequestAsync(NetworkMessage.request(0, MessageType.CANCEL_MATCHMAKING), null);
        }
    }

    private ClickListener clickListener(Runnable action) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        };
    }
}

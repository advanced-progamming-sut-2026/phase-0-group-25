package com.test1.PlantsVsZombies.src.Network.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSession implements Runnable {
    public static final Map<String, ClientSession> onlineSessions = new ConcurrentHashMap<>();

    private final Socket socket;
    private final UserDatabase database;
    private final ObjectMapper mapper = new ObjectMapper();
    private String loggedInUsername;
    private String sessionToken;
    private boolean stayLoggedIn;

    private volatile PrintWriter out;
    private volatile String username;
    private volatile IZombieRoom currentRoom;
    private volatile boolean inQueue = false;

    public ClientSession(Socket socket, UserDatabase database) {
        this.socket = socket;
        this.database = database;
        mapper.registerModule(new JavaTimeModule());
    }

    public ClientSession(Socket socket, UserDatabase database, Object unused1, Object unused2) {
        this(socket, database);
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("[Server] Client connected: " + remote);

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            this.out = writer;
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                NetworkMessage response;
                try {
                    NetworkMessage request = mapper.readValue(line, NetworkMessage.class);
                    response = handle(request);
                } catch (Exception e) {
                    System.err.println("[Server] Error parsing JSON from " + getDisplayName() + ": " + e.getMessage());
                    response = null;
                }
                if (response != null) {
                    sendPush(response);
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] Client disconnected: " + getDisplayName());
        } finally {
            cleanupSession();
        }
    }

    private void cleanupSession() {
        if (username != null) {
            onlineSessions.remove(username.toLowerCase());
        }
        MatchmakingManager.getInstance().cancel(this);

        if (currentRoom != null) {
            currentRoom.handleDisconnect(this);
            currentRoom = null;
        }

        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public void sendPush(NetworkMessage message) {
        if (message == null) return;
        if (out == null) {
            System.err.println("[Server] sendPush(" + message.getType() + ") DROPPED -- no writer for " + getDisplayName());
            return;
        }
        try {
            String json = mapper.writeValueAsString(message);
            synchronized (writeLock) {
                out.println(json);
            }
            System.out.println("[Server] Push sent to " + getDisplayName() + " -> " + message.getType());
        } catch (Exception e) {
            System.err.println("[Server] sendPush(" + message.getType() + ") FAILED for " + getDisplayName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private NetworkMessage handle(NetworkMessage request) {
        try {
            switch (request.getType()) {
                case REGISTER:
                    return handleRegister(request);
                case LOGIN:
                    return handleLogin(request);
                case RESTORE_SESSION:
                    return handleRestoreSession(request);
                case LOGOUT:
                    return handleLogout(request);
                case CHANGE_USERNAME:
                    return handleChangeUsername(request);
                case FORGOT_PASSWORD:
                    return handleForgotPassword(request);
                case RESET_PASSWORD:
                    return handleResetPassword(request);
                case SAVE_PROGRESS:
                    return handleSaveProgress(request);
                case GET_ALL_USERS:
                    return handleGetAllUsers(request);

                case JOIN_MATCHMAKING_QUEUE:
                    return handleJoinQueue(request);
                case CANCEL_MATCHMAKING:
                    MatchmakingManager.getInstance().cancel(this);
                    if (currentRoom != null) {
                        currentRoom.handleDisconnect(this);
                        currentRoom = null;
                    }
                    return NetworkMessage.ok(request.getRequestId(), request.getType());
                case CHALLENGE_USER:
                    return handleChallengeUser(request);
                case RESPOND_TO_CHALLENGE:
                    return handleRespondToChallenge(request);
                case OPPONENT_GAME_STATE:
                    if (currentRoom != null) {
                        currentRoom.forwardGameState(this, request);
                    }
                    return null;
                case SEND_REACTION:
                    if (currentRoom != null) {
                        currentRoom.forwardReaction(this, request);
                    }
                    return null;

                default:
                    return NetworkMessage.error(request.getRequestId(), request.getType(),
                        "This request type isn't handled by the server yet.");
            }
        } catch (Exception e) {
            System.err.println("[Server] Handler error for " + request.getType() + ": " + e.getMessage());
            e.printStackTrace();
            return NetworkMessage.error(request.getRequestId(), request.getType(), "Server error: " + e.getMessage());
        }
    }

    private String str(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : value.toString();
    }

    private NetworkMessage handleRegister(NetworkMessage req) {
        User pendingUser = mapper.convertValue(req.getData().get("user"), User.class);
        String error = database.register(pendingUser);
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleLogin(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String uname = str(d, "username");
        UserDatabase.LoginResult result = database.login(uname, str(d, "password"));
        if (!result.success()) return NetworkMessage.error(req.getRequestId(), req.getType(), result.errorMessage());

        this.username = uname;
        onlineSessions.put(this.username.toLowerCase(), this);
        System.out.println("[Server] Logged in: " + this.username);

        return NetworkMessage.ok(req.getRequestId(), req.getType())
            .put("user", result.user())
            .put("sessionToken", result.sessionToken());
    }

    private NetworkMessage handleRestoreSession(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String uname = str(d, "username");
        UserDatabase.LoginResult result = database.restoreSession(uname, str(d, "sessionToken"));
        if (!result.success()) return NetworkMessage.error(req.getRequestId(), req.getType(), result.errorMessage());

        this.username = uname;
        onlineSessions.put(this.username.toLowerCase(), this);
        System.out.println("[Server] Restored Session: " + this.username);

        return NetworkMessage.ok(req.getRequestId(), req.getType()).put("user", result.user());
    }

    private NetworkMessage handleLogout(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        if (this.username != null) {
            onlineSessions.remove(this.username.toLowerCase());
        }
        MatchmakingManager.getInstance().cancel(this);
        database.logout(str(d, "username"), str(d, "sessionToken"));
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleChangeUsername(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String oldName = str(d, "username");
        String newName = str(d, "newUsername");
        String error = database.changeUsername(oldName, str(d, "sessionToken"), newName);
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);

        onlineSessions.remove(oldName.toLowerCase());
        this.username = newName;
        onlineSessions.put(this.username.toLowerCase(), this);

        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleForgotPassword(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String error = database.checkForgotPassword(str(d, "username"), str(d, "email"), str(d, "answer"));
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleResetPassword(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String error = database.resetPassword(str(d, "username"), str(d, "newPassword"));
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleSaveProgress(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        User updatedUser = mapper.convertValue(d.get("user"), User.class);
        String error = database.saveProgress(str(d, "username"), str(d, "sessionToken"), updatedUser);
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleGetAllUsers(NetworkMessage req) {
        List<User> users = database.getAllUsersSanitized();
        return NetworkMessage.ok(req.getRequestId(), req.getType()).put("users", users);
    }

    private NetworkMessage handleJoinQueue(NetworkMessage req) {
        syncUsernameIfProvided(str(req.getData(), "username"));

        if (currentRoom != null) {
            currentRoom = null;
        }

        String error = MatchmakingManager.getInstance().enqueue(this);
        if (error != null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        }
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleChallengeUser(NetworkMessage req) {
        syncUsernameIfProvided(str(req.getData(), "fromUsername"));

        if (currentRoom != null) {
            currentRoom = null;
        }

        String targetName = str(req.getData(), "targetUsername");
        if (targetName == null || this.username == null || targetName.equalsIgnoreCase(this.username)) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "Cannot challenge yourself.");
        }

        ClientSession targetSession = onlineSessions.get(targetName.toLowerCase());

        if (targetSession == null || !targetSession.isConnected() || targetSession == this) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "User '" + targetName + "' is offline or not found.");
        }

        NetworkMessage challengeNotice = NetworkMessage.request(0, MessageType.CHALLENGE_USER)
            .put("challenger", this.username != null ? this.username : "Opponent");
        targetSession.sendPush(challengeNotice);

        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleRespondToChallenge(NetworkMessage req) {
        syncUsernameIfProvided(str(req.getData(), "fromUsername"));

        if (currentRoom != null) {
            currentRoom = null;
        }

        String challengerName = str(req.getData(), "challenger");
        boolean accepted = Boolean.parseBoolean(String.valueOf(req.getData().get("accepted")));

        ClientSession challenger = onlineSessions.get(challengerName != null ? challengerName.toLowerCase() : "");
        if (challenger == null || !challenger.isConnected()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "Challenger is no longer online.");
        }

        if (accepted) {
            MatchmakingManager.getInstance().cancel(challenger);
            MatchmakingManager.getInstance().cancel(this);

            IZombieRoom room = new IZombieRoom(challenger, this);
            room.start();
        } else {
            NetworkMessage declineMsg = NetworkMessage.request(0, MessageType.RESPOND_TO_CHALLENGE)
                .put("opponentUsername", this.username != null ? this.username : "Opponent")
                .put("accepted", false);
            challenger.sendPush(declineMsg);
        }

        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private void syncUsernameIfProvided(String u) {
        if (u != null && !u.isEmpty() && !u.equalsIgnoreCase("Player")) {
            this.username = u;
            onlineSessions.put(this.username.toLowerCase(), this);
        }
    }

    private String getDisplayName() {
        return (username != null) ? username : ("Session@" + socket.getRemoteSocketAddress());
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public boolean isAvailableForMatch() {
        return isConnected() && username != null && !inQueue && currentRoom == null;
    }

    public String getUsername() { return username; }
    public void setInQueue(boolean inQueue) { this.inQueue = inQueue; }
    public boolean isInQueue() { return inQueue; }
    public void setCurrentRoom(IZombieRoom room) { this.currentRoom = room; }
    public IZombieRoom getCurrentRoom() { return currentRoom; }
}

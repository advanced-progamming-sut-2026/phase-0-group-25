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
    private final Socket socket;
    private final UserDatabase database;
    private final ConcurrentHashMap<String, ClientSession> onlineSessions;
    private final MatchmakingManager matchmakingManager;
    private final ObjectMapper mapper = new ObjectMapper();
    private String loggedInUsername;
    private String sessionToken;
    private boolean stayLoggedIn;

    private final Object writeLock = new Object();

    /** Assigned once LOGIN/RESTORE_SESSION succeeds; null until then. */
    private volatile String username;
    /** Set once the session's writer is up, so sendPush() can be called from other sessions' threads. */
    private volatile PrintWriter out;
    /** The I,Zombie room this session currently belongs to, if any. */
    private volatile IZombieRoom currentRoom;
    /** Whether this session is currently sitting in the random matchmaking queue. */
    private volatile boolean inQueue;

    public ClientSession(Socket socket, UserDatabase database,
                         ConcurrentHashMap<String, ClientSession> onlineSessions,
                         MatchmakingManager matchmakingManager) {
        this.socket = socket;
        this.database = database;
        this.onlineSessions = onlineSessions;
        this.matchmakingManager = matchmakingManager;
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("[Server] Client connected: " + remote);

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            this.out = out;

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                NetworkMessage response;
                try {
                    NetworkMessage request = mapper.readValue(line, NetworkMessage.class);
                    response = handle(request);
                } catch (Exception e) {
                    response = NetworkMessage.error(0, null, "Malformed request: " + e.getMessage());
                }
                writeMessage(response);
            }
        } catch (IOException e) {
            System.out.println("[Server] Client disconnected: " + remote);
        } finally {
            if (loggedInUsername != null
                && sessionToken != null
                && !stayLoggedIn) {

                database.logout(loggedInUsername, sessionToken);
            }

            try {
                socket.close();
            } catch (IOException ignored) {
            }

            System.out.println("[Server] Client session closed: " + remote);
        }
    }

    /** Best-effort cleanup so a dropped connection can't leave the user "stuck" online, queued, or mid-match forever. */
    private void cleanupOnDisconnect() {
        if (username != null) {
            onlineSessions.remove(username, this);
        }
        matchmakingManager.cancel(this);
        IZombieRoom room = this.currentRoom;
        if (room != null) {
            room.handleDisconnect(this);
        }
    }

    private void writeMessage(NetworkMessage message) {
        PrintWriter writer = this.out;
        if (writer == null) return;
        try {
            String json = mapper.writeValueAsString(message);
            synchronized (writeLock) {
                writer.println(json);
            }
        } catch (Exception e) {
            System.err.println("[Server] Failed to write message to "
                + (username != null ? username : "unknown") + ": " + e.getMessage());
        }
    }

    /** Sends an unsolicited push (e.g. MATCH_FOUND, OPPONENT_GAME_STATE) to this session's client. */
    public void sendPush(NetworkMessage message) {
        writeMessage(message);
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
                    return handleJoinMatchmakingQueue(request);
                case CANCEL_MATCHMAKING:
                    return handleCancelMatchmaking(request);
                case CHALLENGE_USER:
                    return handleChallengeUser(request);
                case RESPOND_TO_CHALLENGE:
                    return handleRespondToChallenge(request);
                case OPPONENT_GAME_STATE:
                    return handleOpponentGameState(request);
                case SEND_REACTION:
                    return handleSendReaction(request);
                default:
                    return NetworkMessage.error(request.getRequestId(), request.getType(),
                        "This request type isn't handled by the server yet.");
            }
        } catch (Exception e) {
            return NetworkMessage.error(request.getRequestId(), request.getType(),
                "Server error: " + e.getMessage());
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

        String username = str(d, "username");
        String password = str(d, "password");

        UserDatabase.LoginResult result =
            database.login(username, password);

        if (!result.success()) {
            return NetworkMessage.error(
                req.getRequestId(),
                req.getType(),
                result.errorMessage()
            );
        }

        loggedInUsername = username;
        sessionToken = result.sessionToken();

        Object stayLoggedInValue = d.get("stayLoggedIn");
        stayLoggedIn =
            stayLoggedInValue != null &&
                Boolean.parseBoolean(stayLoggedInValue.toString());

        return NetworkMessage.ok(req.getRequestId(), req.getType())
            .put("user", result.user())
            .put("sessionToken", result.sessionToken());
    }

    private NetworkMessage handleRestoreSession(NetworkMessage req) {
        Map<String, Object> d = req.getData();

        String username = str(d, "username");
        String token = str(d, "sessionToken");

        UserDatabase.LoginResult result =
            database.restoreSession(username, token);

        if (!result.success()) {
            return NetworkMessage.error(
                req.getRequestId(),
                req.getType(),
                result.errorMessage()
            );
        }

        loggedInUsername = username;
        sessionToken = token;

        // A successfully restored session is a remembered session.
        stayLoggedIn = true;

        return NetworkMessage.ok(
            req.getRequestId(),
            req.getType()
        ).put("user", result.user());
    }

    private void registerOnlineSession(String loggedInUsername) {
        this.username = loggedInUsername;
        if (loggedInUsername != null) {
            onlineSessions.put(loggedInUsername, this);
        }
    }

    private NetworkMessage handleLogout(NetworkMessage req) {
        Map<String, Object> d = req.getData();

        String username = str(d, "username");
        String token = str(d, "sessionToken");

        database.logout(username, token);

        loggedInUsername = null;
        sessionToken = null;
        stayLoggedIn = false;

        return NetworkMessage.ok(
            req.getRequestId(),
            req.getType()
        );
    }

    private NetworkMessage handleChangeUsername(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String newUsername = str(d, "newUsername");
        String error = database.changeUsername(str(d, "username"), str(d, "sessionToken"), newUsername);
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        if (username != null) {
            onlineSessions.remove(username, this);
        }
        registerOnlineSession(newUsername);
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





    private NetworkMessage handleJoinMatchmakingQueue(NetworkMessage req) {
        if (username == null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You must be logged in to join matchmaking.");
        }
        String error = matchmakingManager.enqueue(this);
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    /**
     * Doubles as a general "leave whatever I,Zombie state I'm in" signal:
     * besides popping this session out of the matchmaking queue, it also
     * leaves the current room (if any) so a player who backs out mid-match
     * isn't stuck unable to queue or be challenged again, and so their
     * opponent is freed up (and told) via the same OPPONENT_DISCONNECTED
     * path used for an actual dropped connection.
     */
    private NetworkMessage handleCancelMatchmaking(NetworkMessage req) {
        matchmakingManager.cancel(this);
        IZombieRoom room = this.currentRoom;
        if (room != null) {
            room.handleDisconnect(this);
        }
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleChallengeUser(NetworkMessage req) {
        if (username == null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You must be logged in to challenge another player.");
        }
        String targetUsername = str(req.getData(), "targetUsername");
        if (targetUsername == null || targetUsername.isBlank()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "Enter a username to challenge.");
        }
        if (targetUsername.equalsIgnoreCase(this.username)) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You can't challenge yourself.");
        }
        if (!this.isAvailableForMatch()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You're already queued or in a match.");
        }

        ClientSession target = onlineSessions.get(targetUsername);
        if (target == null || !target.isConnected()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), targetUsername + " is not online.");
        }
        if (!target.isAvailableForMatch()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), targetUsername + " is currently busy.");
        }

        target.sendPush(NetworkMessage.request(0, MessageType.CHALLENGE_USER).put("fromUsername", this.username));
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleRespondToChallenge(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String fromUsername = str(d, "fromUsername");
        boolean accepted = Boolean.parseBoolean(String.valueOf(d.get("accepted")));

        if (fromUsername == null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "Missing challenger username.");
        }

        ClientSession challenger = onlineSessions.get(fromUsername);

        if (!accepted) {
            if (challenger != null) {
                challenger.sendPush(NetworkMessage.request(0, MessageType.RESPOND_TO_CHALLENGE)
                    .put("opponentUsername", this.username)
                    .put("accepted", false));
            }
            return NetworkMessage.ok(req.getRequestId(), req.getType());
        }

        if (challenger == null || !challenger.isConnected() || !challenger.isAvailableForMatch()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), fromUsername + " is no longer available.");
        }
        if (!this.isAvailableForMatch()) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You're already queued or in a match.");
        }

        IZombieRoom room = new IZombieRoom(challenger, this);
        room.start();
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }





    private NetworkMessage handleOpponentGameState(NetworkMessage req) {
        IZombieRoom room = this.currentRoom;
        if (room == null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You are not currently in a match.");
        }
        room.forwardGameState(this, req);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleSendReaction(NetworkMessage req) {
        IZombieRoom room = this.currentRoom;
        if (room == null) {
            return NetworkMessage.error(req.getRequestId(), req.getType(), "You are not currently in a match.");
        }
        room.forwardReaction(this, req);
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }





    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setCurrentRoom(IZombieRoom room) {
        this.currentRoom = room;
    }

    public IZombieRoom getCurrentRoom() {
        return currentRoom;
    }

    public boolean isAvailableForMatch() {
        return username != null && !inQueue && currentRoom == null;
    }
}

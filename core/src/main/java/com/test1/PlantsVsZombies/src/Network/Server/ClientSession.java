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


public class ClientSession implements Runnable {
    private final Socket socket;
    private final UserDatabase database;
    private final ObjectMapper mapper = new ObjectMapper();
    private String loggedInUsername;
    private String sessionToken;

    public ClientSession(Socket socket, UserDatabase database) {
        this.socket = socket;
        this.database = database;
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
                out.println(mapper.writeValueAsString(response));
            }
        } catch (IOException e) {
            System.out.println("[Server] Client disconnected: " + remote);
        } finally {
            if (loggedInUsername != null && sessionToken != null) {
                database.logout(loggedInUsername, sessionToken);
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.out.println("[Server] Client session closed: " + remote);
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

        return NetworkMessage.ok(req.getRequestId(), req.getType())
            .put("user", result.user())
            .put("sessionToken", result.sessionToken());
    }

    private NetworkMessage handleRestoreSession(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        UserDatabase.LoginResult result = database.restoreSession(str(d, "username"), str(d, "sessionToken"));
        if (!result.success()) return NetworkMessage.error(req.getRequestId(), req.getType(), result.errorMessage());
        return NetworkMessage.ok(req.getRequestId(), req.getType()).put("user", result.user());
    }

    private NetworkMessage handleLogout(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String username = str(d, "username");
        String token = str(d, "sessionToken");
        database.logout(username, token);
        loggedInUsername = null;
        sessionToken = null;
        return NetworkMessage.ok(req.getRequestId(), req.getType());
    }

    private NetworkMessage handleChangeUsername(NetworkMessage req) {
        Map<String, Object> d = req.getData();
        String error = database.changeUsername(str(d, "username"), str(d, "sessionToken"), str(d, "newUsername"));
        if (error != null) return NetworkMessage.error(req.getRequestId(), req.getType(), error);
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
}

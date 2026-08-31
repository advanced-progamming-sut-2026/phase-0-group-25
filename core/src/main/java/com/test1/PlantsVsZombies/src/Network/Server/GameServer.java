package com.test1.PlantsVsZombies.src.Network.Server;

import com.test1.PlantsVsZombies.src.Network.NetworkConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GameServer {
    private static final int THREAD_POOL_SIZE = 32;

    public static void main(String[] args) {
        UserDatabase database = new UserDatabase();
        ConcurrentHashMap<String, ClientSession> onlineSessions = new ConcurrentHashMap<>();
        MatchmakingManager matchmakingManager = new MatchmakingManager();
        ExecutorService clientThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(NetworkConfig.SERVER_PORT)) {
            System.out.println("[Server] Plants vs Zombies game server listening on port " + NetworkConfig.SERVER_PORT);
            System.out.println("[Server] Waiting for clients... (start the game client now)");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientThreadPool.submit(new ClientSession(clientSocket, database, onlineSessions, matchmakingManager));
            }
        } catch (IOException e) {
            System.err.println("[Server] Failed to start server on port " + NetworkConfig.SERVER_PORT + ": " + e.getMessage());
        } finally {
            clientThreadPool.shutdown();
        }
    }
}

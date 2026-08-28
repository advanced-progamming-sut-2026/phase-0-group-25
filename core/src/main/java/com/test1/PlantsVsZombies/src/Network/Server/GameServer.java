package com.test1.PlantsVsZombies.src.Network.Server;

import com.test1.PlantsVsZombies.src.Network.NetworkConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entry point for the SERVER program. Run this first, before starting
 * any client -- it is the only process that ever touches users.json.
 * Every connected client's requests are handled on a thread borrowed
 * from a fixed-size thread pool, so many clients can be served at once,
 * while UserDatabase's internal ReadWriteLock keeps every read/write of
 * the shared JSON file correctly synchronized regardless of how many
 * client processes are hitting the server at the same time.
 *
 * This class has no LibGDX/graphics dependency at all -- it's a plain
 * Java program. Run it directly (e.g. right-click -> Run in your IDE,
 * or `java -cp <classpath> com.test1.PlantsVsZombies.src.Network.Server.GameServer`).
 */
public class GameServer {
    private static final int THREAD_POOL_SIZE = 32;

    public static void main(String[] args) {
        UserDatabase database = new UserDatabase();
        ExecutorService clientThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(NetworkConfig.SERVER_PORT)) {
            System.out.println("[Server] Plants vs Zombies game server listening on port " + NetworkConfig.SERVER_PORT);
            System.out.println("[Server] Waiting for clients... (start the game client now)");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientThreadPool.submit(new ClientSession(clientSocket, database));
            }
        } catch (IOException e) {
            System.err.println("[Server] Failed to start server on port " + NetworkConfig.SERVER_PORT + ": " + e.getMessage());
        } finally {
            clientThreadPool.shutdown();
        }
    }
}

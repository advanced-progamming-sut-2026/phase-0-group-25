package com.test1.PlantsVsZombies.src.Network.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class ServerConnection {
    private static ServerConnection instance;

    private static final int RESPONSE_TIMEOUT_MS = 15000;

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Object writeLock = new Object();
    private final AtomicLong nextRequestId = new AtomicLong(1);
    private final Map<Long, Mailbox> pending = new ConcurrentHashMap<>();

    private static final class Mailbox {
        NetworkMessage response;
    }

    private ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        mapper.registerModule(new JavaTimeModule());

        Thread readerThread = new Thread(this::readLoop, "server-connection-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }


    public static synchronized void connect(String host, int port) throws IOException {
        if (instance == null) {
            instance = new ServerConnection(host, port);
        }
    }

    public static ServerConnection getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not connected to the server yet. Call ServerConnection.connect(...) first.");
        }
        return instance;
    }

    public static boolean isConnected() {
        return instance != null;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                NetworkMessage response = mapper.readValue(line, NetworkMessage.class);
                Mailbox mailbox = pending.remove(response.getRequestId());
                if (mailbox != null) {
                    synchronized (mailbox) {
                        mailbox.response = response;
                        mailbox.notifyAll();
                    }
                }



            }
        } catch (IOException e) {
            System.err.println("[Client] Lost connection to server: " + e.getMessage());
        }
    }


    public NetworkMessage sendRequest(NetworkMessage request) {
        long id = nextRequestId.getAndIncrement();
        request.setRequestId(id);
        Mailbox mailbox = new Mailbox();
        pending.put(id, mailbox);

        try {
            synchronized (writeLock) {
                out.println(mapper.writeValueAsString(request));
            }
        } catch (Exception e) {
            pending.remove(id);
            return NetworkMessage.error(id, request.getType(), "Failed to send request: " + e.getMessage());
        }

        synchronized (mailbox) {
            long deadline = System.currentTimeMillis() + RESPONSE_TIMEOUT_MS;
            while (mailbox.response == null) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    pending.remove(id);
                    return NetworkMessage.error(id, request.getType(), "Server did not respond in time.");
                }
                try {
                    mailbox.wait(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    pending.remove(id);
                    return NetworkMessage.error(id, request.getType(), "Interrupted while waiting for server response.");
                }
            }
            return mailbox.response;
        }
    }
}

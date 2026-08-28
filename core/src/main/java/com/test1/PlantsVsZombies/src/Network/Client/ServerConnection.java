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

/**
 * Owns exactly one persistent connection to the game server for the
 * lifetime of this game process. The client cannot run at all without
 * successfully connecting first (see connect()).
 *
 * Outgoing requests are correlated to their response through a small
 * "mailbox" per pending request: the calling thread registers a
 * mailbox, sends the request, then wait()s on that specific mailbox
 * until the one background reader thread reads the matching response
 * off the socket and notify()s it. This lets many callers have requests
 * in flight at once over the single shared socket, none of them
 * blocking each other, with no polling anywhere.
 *
 * The same persistent-connection + reader-thread design is what a later
 * phase needs for server-initiated pushes (a match was found, an
 * opponent sent a reaction, ...): those arrive as NetworkMessages with
 * no pending mailbox waiting for their requestId, so readLoop() below
 * already has a clearly marked spot to dispatch them to listeners once
 * that's implemented -- nothing about this class needs to change to add it.
 */
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

    /**
     * Must be called exactly once, before anything else touches the
     * network layer -- Main.create() does this first, before
     * MenuManager/UsersManager are touched. Throws if the server isn't
     * reachable; by design the game cannot proceed without it.
     */
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
                // else: a server-initiated push with no matching pending
                // request (reserved for later phases -- match found,
                // incoming reaction, etc). Nothing consumes these yet.
            }
        } catch (IOException e) {
            System.err.println("[Client] Lost connection to server: " + e.getMessage());
        }
    }

    /**
     * Sends a request and blocks the calling thread until the matching
     * response arrives, or RESPONSE_TIMEOUT_MS elapses.
     */
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

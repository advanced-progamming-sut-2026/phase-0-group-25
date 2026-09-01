package com.test1.PlantsVsZombies.src.Network.Client;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
    private final Map<MessageType, List<Consumer<NetworkMessage>>> pushListeners = new ConcurrentHashMap<>();

    private static final class Mailbox {
        NetworkMessage response;
        Consumer<NetworkMessage> asyncCallback;
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
            throw new IllegalStateException("Not connected to server. Call ServerConnection.connect(...) first.");
        }
        return instance;
    }

    public static boolean isConnected() {
        return instance != null;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void addPushListener(MessageType type, Consumer<NetworkMessage> listener) {
        pushListeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void removePushListener(MessageType type, Consumer<NetworkMessage> listener) {
        List<Consumer<NetworkMessage>> list = pushListeners.get(type);
        if (list != null) {
            list.remove(listener);
        }
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                NetworkMessage response = mapper.readValue(line, NetworkMessage.class);


                Mailbox mailbox = pending.remove(response.getRequestId());
                if (mailbox != null) {
                    if (mailbox.asyncCallback != null) {
                        if (Gdx.app != null) {
                            Gdx.app.postRunnable(() -> mailbox.asyncCallback.accept(response));
                        } else {
                            mailbox.asyncCallback.accept(response);
                        }
                    }
                    synchronized (mailbox) {
                        mailbox.response = response;
                        mailbox.notifyAll();
                    }
                }


                List<Consumer<NetworkMessage>> listeners = pushListeners.get(response.getType());
                if (listeners != null && !listeners.isEmpty()) {
                    for (Consumer<NetworkMessage> listener : listeners) {
                        if (Gdx.app != null) {
                            Gdx.app.postRunnable(() -> listener.accept(response));
                        } else {
                            listener.accept(response);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Client] Lost connection to server: " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void sendRequestAsync(NetworkMessage request, Consumer<NetworkMessage> callback) {
        long id = nextRequestId.getAndIncrement();
        request.setRequestId(id);
        if (callback != null) {
            Mailbox mailbox = new Mailbox();
            mailbox.asyncCallback = callback;
            pending.put(id, mailbox);
        }
        try {
            synchronized (writeLock) {
                out.println(mapper.writeValueAsString(request));
            }
        } catch (Exception e) {
            pending.remove(id);
            if (callback != null) {
                callback.accept(NetworkMessage.error(id, request.getType(), "Failed to send: " + e.getMessage()));
            }
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
                    return NetworkMessage.error(id, request.getType(), "Interrupted.");
                }
            }
            return mailbox.response;
        }
    }

    public void sendFireAndForget(NetworkMessage message) {
        try {
            synchronized (writeLock) {
                out.println(mapper.writeValueAsString(message));
            }
        } catch (Exception ignored) {}
    }
}

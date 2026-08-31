package com.test1.PlantsVsZombies.src.Network.Server;

import java.util.ArrayDeque;
import java.util.Deque;

public class MatchmakingManager {
    private static final MatchmakingManager instance = new MatchmakingManager();
    private final Deque<ClientSession> queue = new ArrayDeque<>();
    private final Object lock = new Object();

    public static MatchmakingManager getInstance() {
        return instance;
    }

    public String enqueue(ClientSession session) {
        synchronized (lock) {
            if (session.getUsername() == null) {
                return "You must be logged in to join matchmaking.";
            }
            if (session.isInQueue()) {
                return "You are already queued.";
            }
            if (!session.isAvailableForMatch()) {
                return "You are currently busy in another match.";
            }

            queue.add(session);
            session.setInQueue(true);
            System.out.println("[Server] " + session.getUsername() + " ENQUEUED. Total in queue: " + queue.size());

            pairWaitingPlayers();
            return null;
        }
    }

    public void cancel(ClientSession session) {
        synchronized (lock) {
            if (queue.remove(session)) {
                session.setInQueue(false);
                System.out.println("[Server] " + session.getUsername() + " DEQUEUED.");
            }
        }
    }

    private void pairWaitingPlayers() {
        while (queue.size() >= 2) {
            ClientSession first = queue.poll();
            if (first != null) first.setInQueue(false);
            if (first == null || !first.isConnected() || first.getUsername() == null) {
                continue;
            }

            ClientSession second = queue.poll();
            if (second != null) second.setInQueue(false);
            if (second == null) {
                queue.addFirst(first);
                first.setInQueue(true);
                break;
            }
            if (!second.isConnected() || second.getUsername() == null) {
                queue.addFirst(first);
                first.setInQueue(true);
                continue;
            }

            System.out.println("[Server] PAIRING SUCCESS: " + first.getUsername() + " vs " + second.getUsername());
            IZombieRoom room = new IZombieRoom(first, second);
            room.start();
        }
    }
}

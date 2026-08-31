package com.test1.PlantsVsZombies.src.Network.Server;

import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pairs two {@link ClientSession}s for a single "I, Zombie" multiplayer
 * match. The server never simulates the match itself -- it only assigns
 * roles, hands out a shared seed/start time so both clients' independent
 * simulations line up, and relays each player's game actions and reactions
 * to their opponent.
 */
public class IZombieRoom {
    /** Small buffer so both clients have time to receive MATCH_FOUND and load the screen before the clock starts. */
    private static final long MATCH_START_DELAY_MS = 3000L;

    private final ClientSession playerA;
    private final ClientSession playerB;
    private final long roomSeed;
    private final AtomicBoolean ended = new AtomicBoolean(false);

    public IZombieRoom(ClientSession playerA, ClientSession playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.roomSeed = new Random().nextLong();
    }

    /** Randomly assigns Plant/Zombie roles and pushes MATCH_FOUND to both players. */
    public void start() {
        playerA.setCurrentRoom(this);
        playerB.setCurrentRoom(this);

        boolean aIsPlant = new Random().nextBoolean();
        Faction factionA = aIsPlant ? Faction.PLANT : Faction.ZOMBIE;
        Faction factionB = aIsPlant ? Faction.ZOMBIE : Faction.PLANT;

        long startTimeMillis = System.currentTimeMillis() + MATCH_START_DELAY_MS;

        playerA.sendPush(NetworkMessage.request(0, MessageType.MATCH_FOUND)
            .put("opponentUsername", playerB.getUsername())
            .put("role", factionA.name())
            .put("roomSeed", roomSeed)
            .put("startTimeMillis", startTimeMillis)
            .put("matchDurationSeconds", IZombie.MATCH_DURATION_SECONDS));

        playerB.sendPush(NetworkMessage.request(0, MessageType.MATCH_FOUND)
            .put("opponentUsername", playerA.getUsername())
            .put("role", factionB.name())
            .put("roomSeed", roomSeed)
            .put("startTimeMillis", startTimeMillis)
            .put("matchDurationSeconds", IZombie.MATCH_DURATION_SECONDS));

        System.out.println("[Server] I,Zombie match started: " + playerA.getUsername() + " (" + factionA
            + ") vs " + playerB.getUsername() + " (" + factionB + ")");
    }

    private ClientSession other(ClientSession sender) {
        if (sender == playerA) return playerB;
        if (sender == playerB) return playerA;
        return null;
    }

    public void forwardGameState(ClientSession sender, NetworkMessage request) {
        ClientSession opponent = other(sender);
        if (opponent == null) return;

        NetworkMessage push = NetworkMessage.request(0, MessageType.OPPONENT_GAME_STATE);
        push.getData().putAll(request.getData());
        push.put("fromUsername", sender.getUsername());
        opponent.sendPush(push);
    }

    public void forwardReaction(ClientSession sender, NetworkMessage request) {
        ClientSession opponent = other(sender);
        if (opponent == null) return;

        NetworkMessage push = NetworkMessage.request(0, MessageType.REACTION_RECEIVED);
        push.getData().putAll(request.getData());
        push.put("fromUsername", sender.getUsername());
        opponent.sendPush(push);
    }

    public void handleDisconnect(ClientSession leavingSession) {
        if (!ended.compareAndSet(false, true)) return;

        ClientSession opponent = other(leavingSession);
        if (opponent != null) {
            opponent.setCurrentRoom(null);
            opponent.sendPush(NetworkMessage.request(0, MessageType.OPPONENT_DISCONNECTED)
                .put("opponentUsername", leavingSession.getUsername() != null ? leavingSession.getUsername() : "Your opponent"));
        }
        leavingSession.setCurrentRoom(null);
    }

    public ClientSession getPlayerA() {
        return playerA;
    }

    public ClientSession getPlayerB() {
        return playerB;
    }
}

package com.test1.PlantsVsZombies.src.Network;

import com.badlogic.gdx.Gdx;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;

import java.util.Map;
import java.util.function.Consumer;

public class IZombieNetworkBridge {
    private final IZombie gamePlay;
    private Consumer<Map<String, Object>> onReactionReceived;

    public IZombieNetworkBridge(IZombie gamePlay) {
        this.gamePlay = gamePlay;
    }

    public void setOnReactionReceived(Consumer<Map<String, Object>> onReactionReceived) {
        this.onReactionReceived = onReactionReceived;
    }


    public void sendAction(String actionType, Map<String, Object> payload) {
        if (!gamePlay.isNetworkGame()) return;

        NetworkMessage msg = NetworkMessage.request(0, MessageType.OPPONENT_GAME_STATE);
        msg.put("subAction", actionType);
        msg.put("payload", payload);

        new Thread(() -> ServerConnection.getInstance().sendRequest(msg)).start();
    }


    public void sendReaction(String reactionCategory, String reactionId) {
        if (!gamePlay.isNetworkGame()) return;

        NetworkMessage msg = NetworkMessage.request(0, MessageType.SEND_REACTION);
        msg.put("category", reactionCategory);
        msg.put("reactionId", reactionId);

        new Thread(() -> ServerConnection.getInstance().sendRequest(msg)).start();
    }


    public void handleIncomingPacket(NetworkMessage message) {
        Gdx.app.postRunnable(() -> {
            if (message.getType() == MessageType.OPPONENT_GAME_STATE) {
                String subAction = (String) message.getData().get("subAction");
                Map<String, Object> payload = (Map<String, Object>) message.getData().get("payload");

                if ("SPAWN_ZOMBIE".equals(subAction)) {
                    String zType = (String) payload.get("type");
                    int row = ((Number) payload.get("row")).intValue();
                    gamePlay.spawnZombieAction(zType, row);
                } else if ("PLANT".equals(subAction)) {
                    String pType = (String) payload.get("type");
                    int col = ((Number) payload.get("col")).intValue();
                    int row = ((Number) payload.get("row")).intValue();
                    BattlePlant plant = PlantFactory.createBattlePlant(pType, 1);
                    gamePlay.plantDefenseAction(plant, col, row);
                }
            } else if (message.getType() == MessageType.REACTION_RECEIVED) {
                if (onReactionReceived != null) {
                    onReactionReceived.accept(message.getData());
                }
            }
        });
    }
}

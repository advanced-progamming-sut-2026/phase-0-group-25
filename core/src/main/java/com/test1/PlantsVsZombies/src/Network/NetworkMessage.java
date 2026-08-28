package com.test1.PlantsVsZombies.src.Network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * The envelope every message -- request or response -- is wrapped in
 * before being written as a single line of JSON to a socket.
 *
 * requestId ties a response back to whichever call is blocked waiting
 * for it (see ServerConnection.sendRequest). The server always echoes
 * the same requestId AND the same type it received on a given request.
 *
 * The payload itself is a loosely-typed Map<String,Object> rather than
 * a rigid per-message-type class hierarchy: it keeps this one envelope
 * class usable for every message type -- including ones reserved for
 * later phases (matchmaking, reactions) -- without needing a new DTO
 * class or Jackson polymorphic-type configuration for each one. Nested
 * POJOs (like a User) stored in the map deserialize back into a
 * LinkedHashMap by default because of type erasure; use
 * objectMapper.convertValue(data.get("key"), TargetClass.class) to get
 * the real type back out.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkMessage {
    private long requestId;
    private MessageType type;
    private boolean success = true;
    private String errorMessage;
    private Map<String, Object> data = new HashMap<>();

    public NetworkMessage() {
        // Jackson needs a no-arg constructor.
    }

    public static NetworkMessage request(long requestId, MessageType type) {
        NetworkMessage message = new NetworkMessage();
        message.requestId = requestId;
        message.type = type;
        return message;
    }

    public static NetworkMessage ok(long requestId, MessageType type) {
        NetworkMessage message = new NetworkMessage();
        message.requestId = requestId;
        message.type = type;
        message.success = true;
        return message;
    }

    public static NetworkMessage error(long requestId, MessageType type, String errorMessage) {
        NetworkMessage message = new NetworkMessage();
        message.requestId = requestId;
        message.type = type;
        message.success = false;
        message.errorMessage = errorMessage;
        return message;
    }

    /** Fluent setter so callers can build a response inline: ok(id, type).put("user", user). */
    public NetworkMessage put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}

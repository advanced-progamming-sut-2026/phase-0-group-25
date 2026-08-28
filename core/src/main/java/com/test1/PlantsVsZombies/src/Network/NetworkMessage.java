package com.test1.PlantsVsZombies.src.Network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkMessage {
    private long requestId;
    private MessageType type;
    private boolean success = true;
    private String errorMessage;
    private Map<String, Object> data = new HashMap<>();

    public NetworkMessage() {

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

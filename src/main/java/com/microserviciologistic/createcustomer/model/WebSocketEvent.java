package com.microserviciologistic.createcustomer.model;

public class WebSocketEvent {
    private String type;
    private String topic;
    private Object payload;

    public WebSocketEvent() { }

    public WebSocketEvent(String type, String topic, Object payload) {
        this.type = type;
        this.topic = topic;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public String getTopic() {
        return topic;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Type: " + type + ", Topic: " + topic + ", Payload: " + payload;
    }
}

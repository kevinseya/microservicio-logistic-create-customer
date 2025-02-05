package com.microserviciologistic.createcustomer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviciologistic.createcustomer.config.PlainWebSocketClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class WebSocketClientService {

    private static final String WEBSOCKET_URL = "ws://3.91.247.144:5002/ws";
    private PlainWebSocketClient webSocketClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        connectWebSocket();
    }

    // Synchronized method to avoid race conditions on reconnection
    private synchronized void connectWebSocket() {
        try {
            System.out.println("Trying to connect to WebSocket on: " + WEBSOCKET_URL);
            webSocketClient = new PlainWebSocketClient(new URI(WEBSOCKET_URL));
        } catch (Exception e) {
            System.err.println("Error to connect to WebSocket: " + e.getMessage());
            scheduleReconnect();
        }
    }

    // Recconection after 5 seconds
    private void scheduleReconnect() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Reconnection to WebSocket...");
                connectWebSocket();
            } catch (InterruptedException e) {
                System.err.println("Error of reconnection : " + e.getMessage());
            }
        }).start();
    }

    public void sendEvent(String operation, Object data) {
        if (webSocketClient == null || !webSocketClient.isConnected()) {
            System.err.println("WebSocket is not connected. Trying reconnect...");
            connectWebSocket();
            return;
        }
        try {
            String jsonMessage = objectMapper.writeValueAsString(new WebSocketMessage(operation, data));
            webSocketClient.sendMessage(jsonMessage);
        } catch (Exception e) {
            System.err.println("Error to sent message: " + e.getMessage());
        }
    }

    // Inner class to structure the message to be sent
    private static class WebSocketMessage {
        private String operation;
        private Object customer;

        public WebSocketMessage(String operation, Object customer) {
            this.operation = operation;
            this.customer = customer;
        }

        public String getOperation() {
            return operation;
        }

        public Object getCustomer() {
            return customer;
        }
    }
}

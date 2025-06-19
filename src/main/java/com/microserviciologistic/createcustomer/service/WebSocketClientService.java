package com.microserviciologistic.createcustomer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviciologistic.createcustomer.websocket.PlainWebSocketClient;
import com.microserviciologistic.createcustomer.websocket.WebSocketMessageHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class WebSocketClientService {
    @Value("${URL_WEBSOCKET}")
    private String WEBSOCKET_URL;
    private PlainWebSocketClient webSocketClient;
    private final WebSocketMessageHandler messageHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReconnectionScheduler reconnectionScheduler;

    public WebSocketClientService(WebSocketMessageHandler messageHandler, ReconnectionScheduler reconnectionScheduler) {
        this.messageHandler = messageHandler;
        this.reconnectionScheduler = reconnectionScheduler;
    }

    @PostConstruct
    public void init() {
        connectWebSocket();
    }

    private synchronized void connectWebSocket() {
        try {
            System.out.println("Connecting to WebSocket at: " + WEBSOCKET_URL);
            webSocketClient = new PlainWebSocketClient(
                    new URI(WEBSOCKET_URL),
                    this::handleMessage,
                    () -> {
                        System.err.println("WebSocket disconnected! Attempting reconnection...");
                        reconnectionScheduler.scheduleReconnect(this::connectWebSocket);
                    }
            );
            reconnectionScheduler.cancelReconnect();
        } catch (Exception e) {
            System.err.println("Error connecting WebSocket: " + e.getMessage());
            reconnectionScheduler.scheduleReconnect(this::connectWebSocket);
        }
    }

    private void handleMessage(String message) {
        System.out.println("Message received: " + message);
        messageHandler.processMessage(message);
    }

    public void sendEvent(String operation, Object data) {
        if (webSocketClient == null || !webSocketClient.isConnected()) {
            System.err.println("WebSocket not connected. Attempting reconnection...");
            reconnectionScheduler.scheduleReconnect(this::connectWebSocket);
            return;
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(data);
            String jsonMessage = String.format(
                    "{\"type\":\"publish\", \"topic\":\"user.%s\", \"payload\":%s}",
                    operation.toLowerCase(), jsonPayload
            );
            webSocketClient.sendMessage(jsonMessage);
            System.out.println("Message Sent: " + jsonMessage);
        } catch (Exception e) {
            System.err.println(" Error serializing JSON: " + e.getMessage());
        }
    }
}

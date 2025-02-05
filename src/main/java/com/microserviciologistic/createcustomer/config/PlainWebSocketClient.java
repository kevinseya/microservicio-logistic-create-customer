package com.microserviciologistic.createcustomer.config;

import jakarta.websocket.*;

import java.net.URI;

@ClientEndpoint
public class PlainWebSocketClient {

    private Session userSession;
    private URI endpointURI;

    // Exception the connection knows if it fails.
    public PlainWebSocketClient(URI endpointURI) throws Exception {
        this.endpointURI = endpointURI;
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to WebSocket");
        this.userSession = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Disconnected to WebSocket: " + reason.getReasonPhrase());
        this.userSession = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on WebSocket: " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message recived: " + message);
    }

    public boolean isConnected() {
        return userSession != null && userSession.isOpen();
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            userSession.getAsyncRemote().sendText(message);
            System.out.println("Message Sent: " + message);
        } else {
            System.err.println("WebSocket is not connected.");
        }
    }
}

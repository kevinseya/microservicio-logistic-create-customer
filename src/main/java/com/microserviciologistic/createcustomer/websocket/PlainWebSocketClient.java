package com.microserviciologistic.createcustomer.websocket;

import jakarta.websocket.*;

import java.net.URI;
import java.util.function.Consumer;

@ClientEndpoint
public class PlainWebSocketClient {

    private Session userSession;
    private final URI endpointURI;
    private final Consumer<String> messageHandler;
    private final Runnable disconnectionHandler;

    public PlainWebSocketClient(URI endpointURI, Consumer<String> messageHandler, Runnable disconnectionHandler) throws Exception {
        this.endpointURI = endpointURI;
        this.messageHandler = messageHandler;
        this.disconnectionHandler = disconnectionHandler;
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
        System.err.println("Disconnected from WebSocket: " + reason.getReasonPhrase());
        this.userSession = null;
        disconnectionHandler.run();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message received: " + message);
        messageHandler.accept(message);
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

package org.abx.wsarch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketServer extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Handle connection established
        System.out.println("Connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming message
        System.out.println("Received message: " + message.getPayload());

        // Parse the incoming message as JSON
        Object json = objectMapper.readValue(message.getPayload(), Object.class);

        // Resend the same JSON message
        String jsonResponse = objectMapper.writeValueAsString(json);
        session.sendMessage(new TextMessage(jsonResponse));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Handle connection closed
        System.out.println("Connection closed: " + session.getId());
    }
}

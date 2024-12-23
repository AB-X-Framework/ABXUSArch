package org.abx.wsarch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketServerTest {

    @Value("${server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testWebSocketConnection() throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CompletableFuture<String> future = new CompletableFuture<>();

        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString("hello world");
                    session.send("/ws", jsonMessage);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                future.complete(payload.toString());
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                future.completeExceptionally(exception);
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }
        }).get(1, TimeUnit.SECONDS);

        String response = future.get(5, TimeUnit.SECONDS);
        assertEquals("{\"message\":\"hello world\"}", response);

        session.disconnect();
    }
}

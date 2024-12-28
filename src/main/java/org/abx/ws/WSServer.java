package org.abx.ws;

import org.abx.util.ExceptionHandler;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class WSServer extends WSEngine {

    public HashMap<String, WSClient> clients;

    public WSServer() {
        clients = new HashMap<>();
    }
    public void start(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try {
                Socket client = serverSocket.accept();
                new Thread(() -> {
                    try {
                        WSClient wsClient = new WSClient(client);
                        String id = wsClient.process
                                (new WSReq("_client/getClientId")).asString();
                        clients.put(id, wsClient);
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    handle(client);
                }).start();
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

}
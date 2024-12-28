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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class WSServer extends WSEngine {

    public HashMap<String, WSClient> clients;

    public ArrayList<WSClientListener> listeners;

    public WSServer() {
        clients = new HashMap<>();
        listeners = new ArrayList<>();
    }
    public void addClientListener(WSClientListener listener) {
        listeners.add(listener);
    }
    public void start(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try {
                Socket client = serverSocket.accept();
                new Thread(() -> {
                    try {
                        WSClient wsClient = new WSClient(client);
                        String clientId = wsClient.process
                                (new WSReq("_client/getClientId")).asString();
                        clientConnected(clientId);
                        clients.put(clientId, wsClient);
                        handle(client);
                        clientDisconnected(clientId);
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                }).start();
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

    private void clientConnected(String clientId) {
        for (WSClientListener listener : listeners) {
            listener.clientConnected(clientId);
        }
    }

    private void clientDisconnected(String clientId) {
        for (WSClientListener listener : listeners) {
            listener.clientDisconnected(clientId);
        }
    }


}
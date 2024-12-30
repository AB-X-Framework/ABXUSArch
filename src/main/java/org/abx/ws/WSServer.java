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
    private ServerSocket serverSocket;
    private final HashMap<String, WSClient> clients;

    private final ArrayList<WSClientListener> listeners;

    public WSServer() {
        clients = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public WSClient getClient(String id) {
        return clients.get(id);
    }

    public void addClientListener(WSClientListener listener) {
        listeners.add(listener);
    }

    public void start(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        new Thread(() -> {
            try {
                while (true) {
                    Socket client = serverSocket.accept();

                    try {
                        WSClient wsClient = new WSClient(client, this);
                        String clientId = wsClient.process
                                (new WSReq("_client/getClientId")).asString();
                        clientConnected(clientId);
                        clients.put(clientId, wsClient);
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }

                }
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }).start();
    }

    private void clientConnected(String clientId) {
        for (WSClientListener listener : listeners) {
            listener.clientConnected(clientId);
        }
    }

    public void clientDisconnected(String clientId) {
        for (WSClientListener listener : listeners) {
            listener.clientDisconnected(clientId);
        }
        clients.remove(clientId);
    }


}
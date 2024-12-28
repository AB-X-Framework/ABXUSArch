package org.abx.ws;

import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;

import java.net.Socket;
import java.util.concurrent.Semaphore;

public class WSClient extends WSEngine {

    private String clientId;
    private Socket client;

    public WSClient(String clientId, String host, int port) throws Exception {
        this.clientId = clientId;
        client = new Socket(host, port);
        addController("_client", client);
    }

    public WSClient(Socket client) throws Exception {
        this.client = client;
    }

    public String getClientId() {
        return clientId;
    }

    public WSRes process(WSReq req) throws Exception {
        Semaphore reqSem = new Semaphore(1);
        reqSem.acquire();
        requests.put(req.id, reqSem);

    }
}

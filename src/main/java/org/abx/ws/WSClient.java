package org.abx.ws;

import org.abx.ws.annotations.WSMethod;
import org.abx.ws.frames.WebSocketFrame;
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
        addController("_client", this);
        handle(client);
    }

    public WSClient(Socket client) throws Exception {
        this.client = client;
        handle(client);
    }

    @WSMethod(params={})
    public String getClientId() {
        return clientId;
    }

    /**
     * Process request by sending it and waiting until result
     * @param req The request
     * @return The response after it was found
     * @throws Exception If connection gets closed
     */
    public WSRes process(WSReq req) throws Exception {
        Semaphore reqSem = new Semaphore(1);
        reqSem.acquire();
        requests.put(req.getID(), reqSem);
        WebSocketFrame.writeFrame(client.getOutputStream(),req.toFrame());
        reqSem.acquire();
        return responses.remove(req.getID());
    }
}

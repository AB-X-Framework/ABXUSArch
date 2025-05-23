package org.abx.ws;

import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;

import java.net.Socket;
import java.util.concurrent.Semaphore;

public class WSClient extends WSEngine implements WSService {

    protected String clientId;
    private final Socket client;

    public WSClient(String clientId, String host, int port) throws Exception {
        this.clientId = clientId;
        client = new Socket(host, port);
        addController("_client", this);
        handle(client);
    }

    public WSClient(Socket client, WSServer server) {
        super(server);
        this.client = client;
        handle(client);
    }

    @WSMethod(params = {})
    public String getClientId() {
        return clientId;
    }

    public void disconnect() throws Exception {
        WebSocketFrame.writeFrame(client.getOutputStream(), CloseFrame.get());
        client.close();
    }

    /**
     * Process request by sending it and waiting until result
     *
     * @param req The request
     * @return The response after it was found
     * @throws Exception If connection gets closed
     */
    public WSRes process(WSReq req) throws Exception {
        Semaphore reqSem = new Semaphore(1);
        reqSem.acquire();
        requests.put(req.getID(), reqSem);
        WebSocketFrame.writeFrame(client.getOutputStream(), req.toFrame());
        reqSem.acquire();
        WSRes res= responses.remove(req.getID());
        if (res.isNotFound()){
            throw new Exception(res.asString());
        }else if (res.isError()){
            throw new Exception(res.asString());
        }
        return res;
    }
}
